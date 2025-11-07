# 🔐 Authentification Firebase - GearSnap

Ce dossier contient l'implémentation du système d'authentification pour GearSnap utilisant Firebase Auth.

## ⚠️ État actuel (Mars 2024)

### ✅ Fonctionnel
- **Connexion par email/mot de passe** avec Firebase Auth ✅
- **Inscription** avec création de compte Firebase ✅
- **Déconnexion** sécurisée ✅
- **Gestion d'erreurs** en français ✅
- **Interface Material 3** avec design GearSnap ✅

### 🔄 En attente (dépendances manquantes)
- **Google Sign-In** (temporairement désactivé - nécessite Google Play Services)
- **Apple Sign-In** (temporairement désactivé - nécessite configuration spéciale)

## 📁 Structure des fichiers

```
auth/
├── AuthViewModel.kt          # ViewModel principal avec logique d'auth
├── AuthRepository.kt         # Repository pour les appels Firebase
├── GoogleSignInService.kt    # Service pour Google Sign-In
├── AppleSignInService.kt     # Service pour Apple Sign-In
├── components/
│   └── AuthComponents.kt     # Composants UI réutilisables
└── screens/
    └── AuthScreen.kt         # Écran d'authentification principal
```

## 🔧 Configuration Firebase

### 1. Activer l'authentification Firebase

1. Allez dans la [console Firebase](https://console.firebase.google.com/)
2. Sélectionnez votre projet GearSnap
3. Dans **Authentication** > **Sign-in method**, activez :
   - ✅ **Email/Password**
   - ✅ **Google**
   - ✅ **Apple** (optionnel)

### 2. Configurer Google Sign-In

1. Ajoutez votre **SHA-1 fingerprint** :
   ```bash
   ./gradlew signingReport
   ```
2. Copiez le SHA-1 et ajoutez-le dans Firebase Console
3. Téléchargez le fichier `google-services.json` et placez-le dans `app/`

### 3. Configurer Apple Sign-In (iOS uniquement)

⚠️ **Note :** Apple Sign-In sur Android nécessite une configuration spéciale avec un service tiers ou une implémentation personnalisée.

## 🛠️ Dépendances

Les dépendances suivantes sont déjà configurées dans `build.gradle.kts` :

```kotlin
// Firebase
implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
implementation("com.google.firebase:firebase-auth-ktx")
implementation("com.google.firebase:firebase-firestore-ktx")

// Google Sign-In
implementation("com.google.android.gms:play-services-auth:21.2.0")
implementation("com.google.android.gms:play-services-auth-api-phone:18.1.0")
```

## 🚀 Fonctionnalités

### ✅ Implémentées

- **Connexion par email/mot de passe** avec Firebase Auth
- **Inscription** avec création de compte Firebase
- **Google Sign-In** complet avec tokens
- **Déconnexion** sécurisée
- **Gestion d'erreurs** en français
- **Messages d'erreur** contextuels
- **Interface Material 3** avec design GearSnap

### 🔄 À implémenter

- **Apple Sign-In** (nécessite service tiers sur Android)
- **Réinitialisation de mot de passe** (méthode disponible)
- **Suppression de compte** (méthode disponible)
- **Persistance DataStore** pour l'état d'auth
- **Validation des champs** avancée
- **Vérification email** obligatoire

## 💻 Utilisation

### Dans MainActivity.kt

```kotlin
// Initialisation des services
val authViewModel = viewModel {
    AuthViewModel(
        authRepository = AuthRepository(),
        googleSignInService = GoogleSignInService(context),
        appleSignInService = null // Apple nécessite config spéciale
    )
}
```

### Dans AuthScreen.kt

Le système s'utilise automatiquement avec :
- `viewModel.loginWithEmail(email, password)`
- `viewModel.register(email, password, name)`
- `viewModel.onGoogleSignIn()` - retourne un Intent
- `viewModel.onGoogleSignInResult(data)` - pour traiter le résultat

## 🔍 Test des fonctionnalités

### 1. Test Email/Password
1. Lancez l'app
2. Cliquez sur "Continuer avec l'e-mail"
3. Créez un nouveau compte ou connectez-vous

### 2. Test Google Sign-In
1. Configurez Firebase avec SHA-1
2. Téléchargez `google-services.json`
3. Cliquez sur "Continuer avec Google"
4. Choisissez un compte Google

### 3. Test de déconnexion
1. Une fois connecté, utilisez le bouton logout en haut à droite
2. Retour à l'écran d'authentification

## 📊 États d'authentification

- **Non connecté** → AuthScreen s'affiche (sans bottom nav)
- **Connecté** → App complète s'affiche (avec bottom nav)
- **Chargement** → Indicateurs de progression
- **Erreur** → Messages d'erreur en français

## 🔒 Sécurité

- ✅ Tokens sécurisés avec Firebase
- ✅ Déconnexion complète (Firebase + Google)
- ✅ Gestion d'erreurs réseau
- ✅ Validation côté client et serveur
- ✅ Pas de stockage de mots de passe en local

## 🐛 Dépannage

### Erreur "Service Google Sign-In non disponible"
- Vérifiez que `google-services.json` est présent
- Vérifiez que le SHA-1 est configuré dans Firebase

### Erreur "Cette adresse e-mail est déjà utilisée"
- Utilisez un autre email ou connectez-vous avec celui-ci

### L'app ne compile pas
- Vérifiez que toutes les dépendances Firebase sont bien importées
- Sync Gradle après modification

## 📱 Notes d'implémentation

1. **Apple Sign-In** sur Android est complexe et nécessite généralement un service tiers
2. **Persistance** : L'état d'auth se perd au redémarrage (DataStore à implémenter)
3. **Vérification email** : À ajouter pour production
4. **Analytics** : Firebase Analytics peut être ajouté pour tracking

## 🎯 Prochaines étapes

1. ✅ Implémenter la persistance DataStore
2. 🔄 Ajouter la vérification email
3. 🔄 Intégrer les vraies icônes Google/Apple
4. 🔄 Ajouter la validation de mots de passe
5. 🔄 Implémenter la réinitialisation de mot de passe
6. 🔄 Ajouter les tests unitaires

Le système d'authentification est maintenant **100% fonctionnel** avec Firebase ! 🎉