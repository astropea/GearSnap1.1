# GearSnap — Starter Android (Jetpack Compose)

Projet Android Studio prêt à compiler (flavor `demo`) avec :
- Navigation bottom bar (6 onglets)
- Écrans placeholder : Accueil, Carte, Prêt, Événements, Social, Profil
- Modèles (User, Spot, Equipment, Event, Club, Message, Badge)
- Repositories `Demo*` en mémoire
- Placeholders pour Firebase, OpenWeather, FCM, IA (TFLite)

## Prérequis
- Android Studio Koala ou plus récent
- JDK 17
- SDK 35

## Lancer l'app (mode Demo)
1. Ouvrir ce dossier dans Android Studio
2. Sélectionner la configuration `app demoDebug`
3. Lancer sur un émulateur ou un appareil

## Activer Firebase (mode Prod)
1. Crée un projet sur console.firebase.google.com
2. Ajoute une app Android (`com.gearsnap`) et récupère `google-services.json`
3. Place le fichier dans `app/src/prod/google-services.json`
4. Dans `app/build.gradle.kts`, si besoin, applique le plugin `com.google.gms.google-services`
5. Initialise Firebase via `FirebaseApp.initializeApp(context)` ou options manuelles.

## Cartes (Mapbox/OSM)
- Le token Mapbox est défini dans `res/values/strings.xml` (`mapbox_token`). 
  Connecte un SDK de carte (Mapbox, Google Maps, OSMdroid) dans `MapScreen`.

## Météo (OpenWeather)
- Renseigne la clé API dans `res/values/strings.xml` (`openweather_api_key`).
- Crée une instance Retrofit avec `https://api.openweathermap.org/data/2.5/`.

## IA Recommandations
- Implémente un modèle TFLite ou appelle une API distante dans `ai/RecommendEngine.kt`.

## Design & Thème
- Palette : vert nature (#2E6B4A), bleu horizon (#2E7BBE), orange (#F47B20).
- Material 3, mode clair/sombre via `Theme.Material3.DayNight` (configurable).

## Prochaines étapes
- Brancher Firebase Auth (Google/Apple/Facebook), Firestore et Storage.
- Implémenter la messagerie (FireStore ou backend temps réel).
- Ajout des écrans CRUD (matériel, événements, clubs).
- Cache hors-ligne (Room + Paging + DataStore).

---
© 2025 GearSnap