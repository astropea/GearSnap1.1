package com.gearsnap.data

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class RentalRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) {
    private val collection = firestore.collection("rental_items")

    suspend fun getAllRentalItems(): List<RentalItem> {
        val snapshot = collection.orderBy("datePosted", Query.Direction.DESCENDING).get().await()
        return snapshot.documents.mapNotNull { doc ->
            doc.toObject(RentalItem::class.java)?.copy(id = doc.id)
        }
    }

    suspend fun getRentalItemById(id: String): RentalItem? {
        val doc = collection.document(id).get().await()
        return doc.toObject(RentalItem::class.java)?.copy(id = doc.id)
    }

    suspend fun createRentalItem(item: RentalItem): Boolean {
        return try {
            // Create doc with auto-generated id
            val data = item.copy(id = "")
            val ref = collection.add(data).await()
            // If we want to set the id field inside the document, update it
            ref.update(mapOf("id" to ref.id)).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun uploadPhoto(uri: Uri): String {
        try {
            val seg = uri.lastPathSegment?.replace("[^A-Za-z0-9._-]".toRegex(), "_") ?: "img"
            val filename = "rental_photos/${System.currentTimeMillis()}_${seg}"
            val ref = storage.reference.child(filename)
            // putFile peut lever si URI ou permissions invalides
            ref.putFile(uri).await()
            val url = ref.downloadUrl.await()
            return url.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception("Échec upload image (uri=$uri): ${e.message}", e)
        }
    }

    // Real-time listener helper: returns ListenerRegistration so caller can remove it
    fun listenAll(onUpdate: (List<RentalItem>) -> Unit): ListenerRegistration {
        return collection.orderBy("datePosted", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val items = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(RentalItem::class.java)?.copy(id = doc.id)
                    }
                    onUpdate(items)
                }
            }
    }

    // Send a simple direct message (stores in 'messages' collection)
    suspend fun sendMessage(toId: String, fromId: String, text: String): Boolean {
        return try {
            val data = mapOf(
                "toId" to toId,
                "fromId" to fromId,
                "text" to text,
                "timestamp" to System.currentTimeMillis()
            )
            firestore.collection("messages").add(data).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
