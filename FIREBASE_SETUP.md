# 🔥 Guide de Configuration Firebase - GearSnap

Ce guide vous accompagne étape par étape pour configurer Firebase Authentication dans votre projet GearSnap.

## ⏱️ Temps estimé : 15-20 minutes

---

## 📋 Prérequis

- [ ] Compte Google
- [ ] Android Studio installé
- [ ] Projet GearSnap ouvert dans Android Studio

---

## 🚀 Étape 1 : Créer un projet Firebase (5 min)

### 1.1 Accéder à Firebase Console
1. Ouvrez votre navigateur et allez sur : **https://console.firebase.google.com/**
2. Connectez-vous avec votre compte Google
3. Cliquez sur **"Ajouter un projet"** ou **"Create a project"**

### 1.2 Configurer le projet
1. **Nom du projet** : Entrez `GearSnap` (ou le nom de votre choix)
2. **Google Analytics** : Vous pouvez désactiver pour l'instant (optionnel)
3. Cliquez sur **"Créer le projet"**
4. Attendez quelques secondes que Firebase crée votre projet
5. Cliquez sur **"Continuer"**

---

## 📱 Étape 2 : Ajouter une application Android (5 min)

### 2.1 Ajouter l'app Android
1. Dans la console Firebase, cliquez sur l'icône **Android** (</>) pour ajouter une app
2. Remplissez les informations :
   - **Nom du package Android** :
     - Pour le mode **Demo** : `com.gearsnap.demo`
     - Pour le mode **Prod** : `com.gearsnap`
   - **Surnom de l'app** : `GearSnap Demo` ou `GearSnap Prod`
   - **Certificat de signature SHA-1** : Laissez vide pour l'instant (on le fera plus tard)
3. Cliquez sur **"Enregistrer l'application"**

### 2.2 Télécharger google-services.json
1. Cliquez sur **"Télécharger google-services.json"**
2. Sauvegardez le fichier sur votre ordinateur
3. **IMPORTANT** : Placez le fichier dans le bon dossier :
   - Pour **Demo** : `app/src/demo/google-services.json`
   - Pour **Prod** : `app/src/prod/google-services.json`

### 2.3 Vérifier l'emplacement du fichier
```
GearSnap1.3/
└── app/
    └── src/
        ├── demo/
        │   └── google-services.json  ← ICI pour le mode Demo
        └── prod/
            └── google-services.json  ← ICI pour le mode Prod
```

4. Cliquez sur **"Suivant"** puis **"Continuer vers la console"**

---

## 🔐 Étape 3 : Activer l'authentification Email/Password (2 min)

### 3.1 Activer Email/Password
1. Dans la console Firebase, cliquez sur **"Authentication"** dans le menu de gauche
2. Cliquez sur **"Get started"** ou **"Commencer"**
3. Allez dans l'onglet **"Sign-in method"**
4. Cliquez sur **"Email/Password"**
5. **Activez** le premier bouton (Email/Password)
6. Cliquez sur **"Enregistrer"**

✅ **L'authentification par email est maintenant active !**

---

## 🔧 Étape 4 : Configurer Google Sign-In (OPTIONNEL - 5 min)

### 4.1 Obtenir le SHA-1 de votre application

1. **Ouvrez un terminal** dans Android Studio (View > Tool Windows > Terminal)
2. Exécutez la commande suivante :
   ```bash
   ./gradlew signingReport
   ```
   Ou sur Windows :
   ```bash
   gradlew.bat signingReport
   ```

3. **Cherchez la ligne SHA-1** dans la sortie :
   ```
   Variant: debug
   Config: debug
   Store: C:\Users\YourName\.android\debug.keystore
   Alias: AndroidDebugKey
   MD5: XX:XX:XX:...
   SHA1: AA:BB:CC:DD:EE:FF:11:22:33:44:55:66:77:88:99:00:AA:BB:CC:DD  ← COPIEZ CETTE LIGNE
   SHA-256: ...
   ```

4. **Copiez le SHA-1** (la longue chaîne de caractères avec des `:`)

### 4.2 Ajouter le SHA-1 dans Firebase

1. Retournez dans la **Console Firebase**
2. Cliquez sur l'icône **⚙️ (Paramètres)** > **Paramètres du projet**
3. Descendez jusqu'à **"Vos applications"**
4. Cliquez sur votre application Android
5. Descendez jusqu'à **"Empreintes digitales du certificat SHA"**
6. Cliquez sur **"Ajouter une empreinte"**
7. **Collez le SHA-1** que vous avez copié
8. Cliquez sur **"Enregistrer"**

### 4.3 Activer Google Sign-In

1. Allez dans **Authentication** > **Sign-in method**
2. Cliquez sur **"Google"**
3. **Activez** le bouton
4. Sélectionnez un **email d'assistance** (votre email)
5. Cliquez sur **"Enregistrer"**

### 4.4 Récupérer le Web Client ID

1. Restez dans **Authentication** > **Sign-in method** > **Google**
2. Développez la section **"Configuration du SDK Web"**
3. **Copiez le "Web client ID"** (format : `123456789-abcdefg.apps.googleusercontent.com`)

### 4.5 Télécharger le nouveau google-services.json

⚠️ **IMPORTANT** : Après avoir ajouté le SHA-1, vous devez télécharger à nouveau le fichier !

1. Allez dans **⚙️ Paramètres du projet**
2. Descendez jusqu'à **"Vos applications"**
3. Cliquez sur **"google-services.json"** pour le télécharger
4. **Remplacez** l'ancien fichier dans `app/src/demo/` ou `app/src/prod/`

### 4.6 Configurer le Web Client ID dans l'app

1. Ouvrez le fichier : `app/src/main/res/values/strings.xml`
2. Trouvez la ligne :
   ```xml
   <string name="google_web_client_id">YOUR_GOOGLE_WEB_CLIENT_ID</string>
   ```
3. **Remplacez** `YOUR_GOOGLE_WEB_CLIENT_ID` par le Web Client ID que vous avez copié :
   ```xml
   <string name="google_web_client_id">123456789-abcdefg.apps.googleusercontent.com</string>
   ```
4. **Sauvegardez** le fichier

---

## ✅ Étape 5 : Compiler et tester (3 min)

### 5.1 Synchroniser Gradle

1. Dans Android Studio, cliquez sur **File** > **Sync Project with Gradle Files**
2. Attendez que la synchronisation se termine

### 5.2 Compiler l'application

1. Sélectionnez la configuration **"app demoDebug"** dans la barre d'outils
2. Cliquez sur le bouton **Run** (▶️) ou appuyez sur **Shift + F10**
3. Sélectionnez un émulateur ou un appareil physique

### 5.3 Tester l'authentification

1. **L'écran d'authentification s'affiche** automatiquement
2. **Testez Email/Password** :
   - Cliquez sur "Continuer avec l'e-mail"
   - Créez un compte avec un email et mot de passe
   - Vous devriez être connecté et voir l'app complète
3. **Testez Google Sign-In** (si configuré) :
   - Déconnectez-vous
   - Cliquez sur "Continuer avec Google"
   - Sélectionnez un compte Google
   - Vous devriez être connecté

---

## 🎉 Félicitations !

Votre application GearSnap est maintenant configurée avec Firebase Authentication !

### ✅ Ce qui fonctionne maintenant :
- ✅ Connexion par email/mot de passe
- ✅ Inscription de nouveaux utilisateurs
- ✅ Déconnexion sécurisée
- ✅ Google Sign-In (si configuré)
- ✅ Navigation conditionnelle (AuthScreen si non connecté)

---

## 🐛 Dépannage

### Erreur : "Default FirebaseApp is not initialized"
**Solution** : Vérifiez que `google-services.json` est bien dans `app/src/demo/` ou `app/src/prod/`

### Erreur : "API key not valid"
**Solution** : Téléchargez à nouveau `google-services.json` depuis Firebase Console

### Google Sign-In ne fonctionne pas
**Solutions** :
1. Vérifiez que le SHA-1 est bien ajouté dans Firebase Console
2. Téléchargez à nouveau `google-services.json` après avoir ajouté le SHA-1
3. Vérifiez que le Web Client ID est correct dans `strings.xml`
4. Nettoyez et recompilez : Build > Clean Project puis Build > Rebuild Project

### L'app crash au démarrage
**Solutions** :
1. Vérifiez les logs dans Logcat (View > Tool Windows > Logcat)
2. Vérifiez que toutes les dépendances sont synchronisées
3. Invalidez le cache : File > Invalidate Caches / Restart

---

## 📚 Ressources supplémentaires

- [Documentation Firebase Auth](https://firebase.google.com/docs/auth/android/start)
- [Documentation Google Sign-In](https://developers.google.com/identity/sign-in/android/start)
- [README du projet](README.md) - Section Firebase Authentication
- [Documentation détaillée](app/src/main/java/com/gearsnap/auth/README.md)

---

## 🔄 Prochaines étapes

Maintenant que Firebase Auth est configuré, vous pouvez :
- [ ] Configurer Firestore pour la base de données
- [ ] Configurer Firebase Storage pour les images
- [ ] Ajouter Firebase Cloud Messaging pour les notifications
- [ ] Implémenter la réinitialisation de mot de passe
- [ ] Ajouter la vérification d'email

---

**Besoin d'aide ?** Consultez le README.md ou la documentation dans `app/src/main/java/com/gearsnap/auth/README.md`
