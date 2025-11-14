# Amélioration du bandeau de recherche dans l'onglet Carte

## 📋 Résumé des modifications

Ce document décrit toutes les améliorations apportées au système de recherche et de filtrage dans l'onglet Carte de GearSnap.

## ✅ Fonctionnalités implémentées

### 🔍 1. Recherche par nom de spot

**Fichier modifié:** `MapScreen.kt`

- Ajout d'une barre de recherche extensible sous l'AppBar
- Clic sur l'icône de recherche pour afficher/masquer la barre
- Filtrage dynamique des spots par nom (insensible à la casse)
- Bouton pour effacer la recherche
- Les marqueurs sur la carte sont mis à jour en temps réel

**Utilisation:**
- Cliquez sur l'icône de recherche dans l'AppBar
- Tapez le nom du spot recherché
- Les marqueurs se filtrent automatiquement
- Effacez le texte pour réafficher tous les spots

### 🎚️ 2. Filtre par difficulté

**Fichiers modifiés:**
- `SpotUi.kt` - Ajout de l'enum `SpotDifficulty`
- `MapScreen.kt` - Implémentation du filtre

**Niveaux de difficulté:**
- Facile
- Moyen
- Difficile

**Utilisation:**
- Cliquez sur le chip "Difficulté"
- Sélectionnez un ou plusieurs niveaux de difficulté
- Les cases à cocher indiquent les sélections actives
- Le chip affiche le nombre de filtres actifs: "Difficulté (2)"
- Bouton "Réinitialiser" pour effacer tous les filtres de difficulté

### 📍 3. Filtre par distance (rayon)

**Fichier modifié:** `MapScreen.kt`

**Fonctionnalités:**
- Calcul de distance avec la formule Haversine
- Rayons disponibles: 5 km, 10 km, 20 km, 50 km, 100 km
- Filtrage basé sur la position centrale de la carte
- Support pour la position GPS de l'utilisateur (si disponible)

**Utilisation:**
- Cliquez sur le chip "Distance"
- Sélectionnez un rayon
- Le chip affiche: "< 20km" par exemple
- Seuls les spots dans le rayon sont affichés
- Bouton "Réinitialiser" pour désactiver le filtre

### 🗺️ 4. Mise à jour en temps réel de la carte

**Fichier modifié:** `MapLibreScreen.kt`

**Améliorations:**
- Les marqueurs sont recréés dynamiquement lors de chaque mise à jour
- Le snippet des marqueurs affiche maintenant: "Catégorie - Difficulté"
- Tous les filtres fonctionnent ensemble de manière combinée
- La carte est rafraîchie automatiquement (`invalidate()`)

## 🔧 Détails techniques

### Structure des données

```kotlin
// SpotUi.kt
data class SpotUi(
    val id: String,
    val name: String,
    val lat: Double,
    val lng: Double,
    val category: SpotCategory,
    val isFavorite: Boolean = false,
    val difficulty: SpotDifficulty = SpotDifficulty.MEDIUM
)

enum class SpotDifficulty(val display: String) {
    EASY("Facile"),
    MEDIUM("Moyen"),
    HARD("Difficile")
}
```

### Algorithme de filtrage combiné

Le filtrage est effectué dans l'ordre suivant:
1. **Filtre par catégorie** (Hiking uniquement si activé)
2. **Filtre par nom** (recherche textuelle)
3. **Filtre par difficulté** (un ou plusieurs niveaux)
4. **Filtre par distance** (rayon autour du centre)

```kotlin
val filtered = remember(spots, hikingOnly, searchQuery, selectedDifficulties, selectedRadius, userLocation) {
    var result = spots

    if (hikingOnly) {
        result = result.filter { it.category == SpotCategory.HIKING }
    }

    if (searchQuery.isNotBlank()) {
        result = result.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    if (selectedDifficulties.isNotEmpty()) {
        result = result.filter { it.difficulty in selectedDifficulties }
    }

    if (selectedRadius != null) {
        val centerPoint = userLocation ?: centerLatLng
        result = result.filter { spot ->
            val distance = calculateDistance(centerPoint.first, centerPoint.second, spot.lat, spot.lng)
            distance <= selectedRadius!!
        }
    }

    result
}
```

### Calcul de distance (Haversine)

```kotlin
fun calculateDistance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
    val earthRadius = 6371.0 // Rayon de la Terre en km
    val dLat = Math.toRadians(lat2 - lat1)
    val dLng = Math.toRadians(lng2 - lng1)
    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLng / 2) * sin(dLng / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return earthRadius * c
}
```

## 📁 Fichiers modifiés

1. **SpotUi.kt**
   - Ajout du champ `difficulty` à `SpotUi`
   - Ajout de l'enum `SpotDifficulty`

2. **MapScreen.kt**
   - Ajout de la barre de recherche extensible
   - Implémentation des filtres de difficulté et distance
   - Fonction de calcul Haversine
   - Logique de filtrage combiné
   - Mise à jour des spots d'exemple avec difficulté

3. **MapLibreScreen.kt**
   - Refactorisation de la gestion des marqueurs
   - Mise à jour dynamique dans le bloc `update`
   - Affichage de la difficulté dans le snippet

## 🎨 Interface utilisateur

### Barre de recherche
- Apparaît/disparaît avec animation
- Icône de recherche qui devient une croix quand active
- Champ de texte avec placeholder
- Bouton d'effacement quand du texte est saisi

### Chips de filtres
- **Hiking**: Filtre par catégorie (existant, conservé)
- **Difficulté**: Menu déroulant avec cases à cocher
- **Distance**: Menu déroulant avec sélection de rayon

### Indicateurs visuels
- Les chips actifs changent de couleur (vert primaire)
- Affichage du nombre de filtres actifs
- Icône de validation pour le rayon sélectionné

## 🚀 Performance

- Utilisation de `remember` avec dépendances pour éviter les recalculs inutiles
- Filtrage côté client (performant pour des centaines de spots)
- Mise à jour incrémentale des marqueurs
- Pas de rechargement complet de la carte

## 📝 Notes importantes

1. **Compatibilité**: Toutes les modifications sont dans l'onglet Carte uniquement
2. **Rétrocompatibilité**: Le bandeau de recherche existant a été étendu, pas remplacé
3. **Extensibilité**: Facile d'ajouter de nouveaux rayons ou niveaux de difficulté
4. **Localisation**: Tous les textes sont en français comme demandé

## 🔄 Prochaines étapes possibles

- Ajouter la géolocalisation en temps réel de l'utilisateur
- Sauvegarder les préférences de filtres
- Ajouter un filtre par catégorie multiple (pas seulement Hiking)
- Implémenter le clustering pour de grandes quantités de spots
- Ajouter des animations lors du changement de filtres

## 🐛 Résolution de problèmes

Si vous rencontrez des erreurs de compilation:
1. Nettoyez le projet: `Build > Clean Project`
2. Reconstruisez: `Build > Rebuild Project`
3. Invalidez les caches: `File > Invalidate Caches / Restart`

Les erreurs "Unresolved reference" sont généralement dues au cache de l'IDE et se résolvent après une reconstruction complète.
