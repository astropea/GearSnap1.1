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

## 🔐 Firebase Authentication - Configuration complète

### ✅ État actuel
Firebase Auth est **déjà intégré et fonctionnel** dans le projet ! Les fichiers suivants sont en place :
- `auth/AuthRepository.kt` - Gestion des appels Firebase Auth
- `auth/AuthViewModel.kt` - Logique d'authentification
- `auth/screens/AuthScreen.kt` - Interface utilisateur
- `auth/components/AuthComponents.kt` - Composants UI réutilisables
- `MainActivity.kt` - Intégration avec navigation conditionnelle

### 📋 Étapes pour activer Firebase Auth

#### 1. Créer un projet Firebase
1. Allez sur [console.firebase.google.com](https://console.firebase.google.com/)
2. Créez un nouveau projet ou sélectionnez un projet existant
3. Cliquez sur "Ajouter une application" et sélectionnez Android
4. Entrez le nom du package : `com.gearsnap` (pour prod) ou `com.gearsnap.demo` (pour demo)

#### 2. Télécharger google-services.json
1. Téléchargez le fichier `google-services.json` depuis la console Firebase
2. Placez-le dans :
   - **Mode Demo** : `app/src/demo/google-services.json`
   - **Mode Prod** : `app/src/prod/google-services.json`

#### 3. Activer les méthodes d'authentification
Dans la console Firebase, allez dans **Authentication** > **Sign-in method** et activez :
- ✅ **Email/Password** (obligatoire)
- ✅ **Google** (optionnel mais recommandé)
- ✅ **Apple** (optionnel, complexe sur Android)

#### 4. Configurer Google Sign-In (optionnel)
Pour activer la connexion Google :

1. **Obtenir le SHA-1 de votre application** :
   ```bash
   ./gradlew signingReport
   ```
   Copiez le SHA-1 affiché dans la console

2. **Ajouter le SHA-1 dans Firebase** :
   - Console Firebase > Paramètres du projet > Vos applications
   - Cliquez sur votre app Android
   - Ajoutez le SHA-1 dans "Empreintes digitales du certificat"

3. **Télécharger le nouveau google-services.json** :
   - Téléchargez à nouveau le fichier mis à jour
   - Remplacez l'ancien fichier

4. **Obtenir le Web Client ID** :
   - Console Firebase > Authentication > Sign-in method > Google
   - Copiez le "Web client ID"
   - Décommentez et configurez `GoogleSignInService.kt` avec ce client ID

#### 5. Compiler et tester
```bash
# Mode Demo (avec google-services.json demo)
./gradlew assembleDemoDebug

# Mode Prod (avec google-services.json prod)
./gradlew assembleProdDebug
```

### 🎯 Fonctionnalités disponibles

#### ✅ Déjà implémentées
- **Connexion email/mot de passe** - Prêt à l'emploi
- **Inscription** - Création de compte Firebase
- **Déconnexion** - Sécurisée avec Firebase
- **Gestion d'erreurs** - Messages en français
- **Interface Material 3** - Design GearSnap
- **Navigation conditionnelle** - AuthScreen si non connecté, app complète si connecté

#### 🔄 Nécessitent configuration
- **Google Sign-In** - Nécessite SHA-1 et Web Client ID (voir étape 4)
- **Apple Sign-In** - Complexe sur Android, nécessite service tiers
- **Réinitialisation mot de passe** - Méthode disponible dans AuthRepository
- **Suppression de compte** - Méthode disponible dans AuthRepository

### 📱 Utilisation dans le code

L'authentification est déjà intégrée dans `MainActivity.kt` :

```kotlin
val authViewModel = viewModel {
    AuthViewModel(authRepository = AuthRepository())
}

val isAuthenticated = authViewModel.isAuthenticated

if (isAuthenticated) {
    // App complète avec bottom navigation
} else {
    // Écran d'authentification
    AuthScreen(viewModel = authViewModel)
}
```

### 🔍 Tester l'authentification

1. **Lancez l'app** en mode Demo ou Prod
2. **L'écran d'authentification s'affiche** automatiquement
3. **Créez un compte** avec email/mot de passe
4. **Connectez-vous** - L'app complète s'affiche avec la bottom navigation
5. **Déconnectez-vous** - Retour à l'écran d'authentification

### 🐛 Dépannage

**Erreur : "google-services.json manquant"**
- Vérifiez que le fichier est dans `app/src/demo/` ou `app/src/prod/`
- Synchronisez Gradle (File > Sync Project with Gradle Files)

**Erreur : "Default FirebaseApp is not initialized"**
- Vérifiez que `google-services.json` est bien configuré
- Le plugin `com.google.gms.google-services` est déjà appliqué dans `build.gradle.kts`

**Google Sign-In ne fonctionne pas (Erreur 10)**
- 🔧 **Solution rapide** : Exécutez `fix_google_signin.bat` pour diagnostiquer
- 📖 **Guide complet** : Consultez [GOOGLE_SIGNIN_ERROR_10.md](GOOGLE_SIGNIN_ERROR_10.md)
- Vérifiez que le SHA-1 est ajouté dans Firebase Console
- Vérifiez que le Web Client ID est configuré dans `strings.xml`
- Téléchargez à nouveau `google-services.json` après avoir ajouté le SHA-1

**L'app ne compile pas**
- Vérifiez que toutes les dépendances Firebase sont synchronisées
- Nettoyez le projet : Build > Clean Project
- Rebuild : Build > Rebuild Project

### 📚 Documentation complète

Pour plus de détails sur l'implémentation, consultez :
- `app/src/main/java/com/gearsnap/auth/README.md` - Documentation détaillée du système d'auth
- [Documentation Firebase Auth](https://firebase.google.com/docs/auth/android/start)
- [Documentation Google Sign-In](https://developers.google.com/identity/sign-in/android/start)

## Cartes (OpenStreetMap/Leaflet)
- La carte OpenStreetMap est automatiquement affichée dans `MapLibreScreen` en utilisant une WebView avec Leaflet (JavaScript).
- Les permissions de localisation sont gérées automatiquement dans la compose function.
- Les spots sont affichés avec des marqueurs cliquables et popup d'informations.
- Interface épurée avec affichage en plein écran de la carte.

**Fonctionnalités :**
- 🗺️ Carte interactive avec zoom et déplacement en temps réel
- 📍 Affichage automatique des spots avec détails (nom, catégorie)
- 🔍 Zoom en direct affiché dans l'interface
- 📊 Compteur de spots en temps réel
- 🎯 Géolocalisation automatique si l'autorisation est accordée
- ⚡ Chargement automatique de la carte au démarrage

**Technologies utilisées :**
- WebView pour intégrer la carte web
- Leaflet.js pour les fonctionnalités cartographiques
- OpenStreetMap pour les tuiles cartographiques (https://tile.openstreetmap.org/)
- Géolocalisation HTML5 pour la position utilisateur
- Interface Material Design pour l'UI

## Météo (OpenWeather)
- Renseigne la clé API dans `res/values/strings.xml` (`openweather_api_key`).
- Crée une instance Retrofit avec `https://api.openweathermap.org/data/2.5/`.

## IA Recommandations
- Implémente un modèle TFLite ou appelle une API distante dans `ai/RecommendEngine.kt`.

## Design & Thème
- Palette : vert nature (#2E6B4A), bleu horizon (#2E7BBE), orange (#F47B20).
- Material 3, mode clair/sombre via `Theme.Material3.DayNight` (configurable).

## Prochaines étapes
- ✅ Firebase Auth (Email/Password) - **IMPLÉMENTÉ ET FONCTIONNEL**
- ✅ Google Sign-In - **IMPLÉMENTÉ** (nécessite juste configuration SHA-1 - voir guides)
- 🔄 Brancher Firestore et Storage
- 🔄 Implémenter la messagerie (FireStore ou backend temps réel)
- 🔄 Ajout des écrans CRUD (matériel, événements, clubs)
- 🔄 Cache hors-ligne (Room + Paging + DataStore)

## 📖 Guides de configuration Firebase

- **🚀 Démarrage rapide (5 min)** : [QUICKSTART.md](QUICKSTART.md)
- **📚 Guide complet (15 min)** : [FIREBASE_SETUP.md](FIREBASE_SETUP.md)
- **🔧 Script automatique** : Exécutez `setup_firebase.bat` pour obtenir votre SHA-1
- **🐛 Erreur Google Sign-In 10** : [GOOGLE_SIGNIN_ERROR_10.md](GOOGLE_SIGNIN_ERROR_10.md) - Solution complète

---
© 2025 GearSnap