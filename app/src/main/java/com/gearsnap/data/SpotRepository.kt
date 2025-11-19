package com.gearsnap.data

import android.net.Uri
import com.gearsnap.model.Photo
import com.gearsnap.model.Review
import com.gearsnap.model.Spot
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class SpotRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
){
    private val spotsCollection = firestore.collection("spots")

    suspend fun getSpots(): List<Spot> {
        ensureSignedIn()
        val snapshot = spotsCollection.orderBy("name").get().await()
        return snapshot.documents.mapNotNull { doc ->
            doc.toObject(Spot::class.java)?.copy(id = doc.id)
        }
    }

    suspend fun getSpotById(id: String): Spot? {
        ensureSignedIn()
        val doc = spotsCollection.document(id).get().await()
        return doc.toObject(Spot::class.java)?.copy(id = doc.id)
    }

    suspend fun createSpot(spot: Spot): Spot? {
        val uid = ensureSignedIn()
        val data = spot.copy(id = "", creatorId = uid ?: "")
        val ref = spotsCollection.add(data).await()
        ref.update(mapOf("id" to ref.id)).await()
        return getSpotById(ref.id)
    }

    suspend fun updateSpot(
        spotId: String,
        description: String? = null,
        sport: String? = null,
        difficulty: String? = null
    ): Spot? {
        ensureSignedIn()
        val updates = buildMap<String, Any> {
            description?.let { put("description", it) }
            sport?.let { put("sport", it) }
            difficulty?.let { put("difficulty", it) }
        }
        if (updates.isEmpty()) return getSpotById(spotId)
        spotsCollection.document(spotId).update(updates).await()
        return getSpotById(spotId)
    }

    suspend fun getReviews(spotId: String): List<Review> {
        ensureSignedIn()

        // Helper de mapping robuste
        fun mapDoc(doc: com.google.firebase.firestore.DocumentSnapshot): Review? {
            val data = doc.data ?: emptyMap()
            fun numberValue(key: String): Number? = when (val v = data[key]) {
                is Number -> v
                is String -> v.toDoubleOrNull()
                is com.google.firebase.Timestamp -> v.toDate().time
                else -> null
            }
            return try {
                val ratingNumber = numberValue("rating")
                    ?: numberValue("note")
                    ?: numberValue("score")
                    ?: numberValue("stars")
                    ?: 0
                val commentValue = (data["comment"] ?: data["text"] ?: data["message"]) as? String ?: ""
                val authorValue = (data["authorId"] ?: data["userId"] ?: data["uid"]) as? String
                val authorNameValue = (data["authorName"] ?: data["userName"] ?: data["displayName"] ?: data["email"]) as? String
                val createdAtMs = numberValue("createdAt")?.toLong()
                    ?: doc.getLong("createdAt")
                    ?: doc.getTimestamp("createdAt")?.toDate()?.time
                    ?: System.currentTimeMillis()

                Review(
                    id = doc.id,
                    spotId = (data["spotId"] as? String) ?: spotId,
                    authorId = authorValue,
                    authorName = authorNameValue,
                    rating = ratingNumber.toInt(),
                    comment = commentValue,
                    photoUrl = data["photoUrl"] as? String,
                    createdAt = createdAtMs
                )
            } catch (_: Exception) {
                null
            }
        }

        // Sous-collection reviews du spot (peut demander un index pour l'orderBy)
        try {
            val subSnapshot = spotsCollection.document(spotId)
                .collection("reviews")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            val subReviews = subSnapshot.documents.mapNotNull(::mapDoc)
            if (subReviews.isNotEmpty()) return subReviews
        } catch (_: Exception) {
            // ignore et on tente le fallback
        }

        // Sous-collection sans orderBy (pour Ã©viter un index manquant)
        try {
            val subSnapshot = spotsCollection.document(spotId)
                .collection("reviews")
                .get()
                .await()
            val subReviews = subSnapshot.documents.mapNotNull(::mapDoc)
            if (subReviews.isNotEmpty()) return subReviews.sortedByDescending { it.createdAt }
        } catch (_: Exception) { }

        // Fallback: collection racine "reviews" (sans orderBy pour Ã©viter un index manquant)
        val rootReviews: List<Review> = try {
            val rootSnapshot = firestore.collection("reviews")
                .whereEqualTo("spotId", spotId)
                .get()
                .await()
            rootSnapshot.documents.mapNotNull(::mapDoc)
        } catch (_: Exception) {
            emptyList()
        }
        if (rootReviews.isNotEmpty()) return rootReviews.sortedByDescending { it.createdAt }

        // Dernier recours: lire toute la collection reviews (peut Ãªtre coÃ»teux)
        return try {
            val snapshot = firestore.collection("reviews").get().await()
            snapshot.documents.mapNotNull(::mapDoc)
                .filter { it.spotId == spotId }
                .sortedByDescending { it.createdAt }
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun addReview(
        spotId: String,
        rating: Int,
        comment: String,
        photoUrl: String? = null
    ): Review {
        ensureSignedIn()
        val review = Review(
            spotId = spotId,
            authorId = auth.currentUser?.uid,
            authorName = auth.currentUser?.displayName ?: auth.currentUser?.email,
            rating = rating,
            comment = comment,
            photoUrl = photoUrl,
            createdAt = System.currentTimeMillis()
        )
        val ref = spotsCollection.document(spotId)
            .collection("reviews")
            .add(review)
            .await()
        ref.update("id", ref.id).await()
        refreshSpotAggregates(spotId)
        return review.copy(id = ref.id)
    }

    suspend fun getPhotos(spotId: String): List<Photo> {
        ensureSignedIn()
        // 1) Sous-collection "photos" (support existant)
        try {
            val snapshot = spotsCollection.document(spotId)
                .collection("photos")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            val fromSubCollection = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Photo::class.java)?.copy(id = doc.id)
            }
            if (fromSubCollection.isNotEmpty()) return fromSubCollection
        } catch (_: Exception) {
            // ignore, fallback sur le champ array
        }

        // 2) Champ array "photos" sur le document du spot (déploiement actuel)
        val fromArray = try {
            val doc = spotsCollection.document(spotId).get().await()
            val urls = doc.get("photos") as? List<*>
            urls.orEmpty()
                .mapNotNull { it as? String }
                .mapIndexed { index, url ->
                    Photo(
                        id = "${spotId}_${index}",
                        spotId = spotId,
                        url = url,
                        createdAt = System.currentTimeMillis()
                    )
                }
        } catch (_: Exception) {
            emptyList()
        }
        if (fromArray.isNotEmpty()) return fromArray

        // 3) Fallback: listage du dossier Firebase Storage
        val fromStorage = fetchPhotosFromStorage(spotId)
        if (fromStorage.isNotEmpty()) {
            runCatching {
                val batch = firestore.batch()
                val spotDoc = spotsCollection.document(spotId)
                fromStorage.forEach { photo ->
                    val docId = photo.id.ifBlank { photo.url.hashCode().toString() }
                    val ref = spotDoc.collection("photos").document(docId)
                    batch.set(ref, photo)
                    batch.update(spotDoc, mapOf("photos" to FieldValue.arrayUnion(photo.url)))
                }
                batch.commit().await()
            }
        }
        return fromStorage
    }

    suspend fun addPhoto(spotId: String, uri: Uri): Photo {
        ensureSignedIn()
        val url = uploadSpotPhoto(spotId, uri)
        val photo = Photo(
            spotId = spotId,
            url = url,
            createdAt = System.currentTimeMillis()
        )

        // 1) Sous-collection "photos" (voie principale)
        val subCollectionId = runCatching {
            val ref = spotsCollection.document(spotId)
                .collection("photos")
                .add(photo)
                .await()
            ref.update("id", ref.id).await()
            ref.id
        }.getOrElse {
            android.util.Log.w("SpotRepository", "Add photo to sub-collection failed, fallback to array only", it)
            null
        }

        // 2) Alimente aussi/surtout le champ array "photos" (dÃ©ploiement actuel)
        runCatching {
            spotsCollection.document(spotId)
                .update("photos", FieldValue.arrayUnion(url))
                .await()
        }.onFailure {
            android.util.Log.e("SpotRepository", "Failed to update photos array for $spotId", it)
        }

        return photo.copy(id = subCollectionId ?: url.hashCode().toString())
    }

    private suspend fun uploadSpotPhoto(spotId: String, uri: Uri): String {
        val seg = uri.lastPathSegment?.replace("[^A-Za-z0-9._-]".toRegex(), "_") ?: "photo"
        val filename = "spots/$spotId/${System.currentTimeMillis()}_$seg"
        val ref = storage.reference.child(filename)
        ref.putFile(uri).await()
        return ref.downloadUrl.await().toString()
    }

    private suspend fun fetchPhotosFromStorage(spotId: String): List<Photo> {
        return try {
            val folder = storage.reference.child("spots/$spotId")
            val listResult = folder.listAll().await()
            listResult.items.mapNotNull { item ->
                val url = runCatching { item.downloadUrl.await().toString() }.getOrNull() ?: return@mapNotNull null
                val metadata = runCatching { item.metadata.await() }.getOrNull()
                val createdAt = metadata?.creationTimeMillis ?: System.currentTimeMillis()
                Photo(
                    id = item.name,
                    spotId = spotId,
                    url = url,
                    createdAt = createdAt
                )
            }.sortedByDescending { it.createdAt }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private suspend fun ensureSignedIn(): String? {
        if (auth.currentUser == null) {
            try {
                auth.signInAnonymously().await()
            } catch (_: Exception) {
                // ignore, reads may still work if rules allow public
            }
        }
        return auth.currentUser?.uid
    }

    private suspend fun refreshSpotAggregates(spotId: String) {
        val reviews = getReviews(spotId)
        if (reviews.isEmpty()) return
        val avg = reviews.map { it.rating }.average()
        spotsCollection.document(spotId).update(
            mapOf(
                "rating" to avg,
                "ratingCount" to reviews.size
            )
        ).await()
    }
}



