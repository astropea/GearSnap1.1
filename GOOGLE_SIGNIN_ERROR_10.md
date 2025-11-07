# 🔧 Résoudre l'erreur Google Sign-In 10

## 🚨 Qu'est-ce que l'erreur 10 ?

L'erreur 10 de Google Sign-In signifie : **"Developer Error"** (Erreur de développeur)

Cette erreur se produit quand :
- ❌ Le SHA-1 de votre application n'est pas enregistré dans Firebase Console
- ❌ Le Web Client ID est incorrect ou manquant
- ❌ Le fichier `google-services.json` n'est pas à jour

---

## ✅ Solution Rapide (5 minutes)

### Étape 1 : Obtenir votre SHA-1

**Option A : Utiliser le script automatique**
```bash
fix_google_signin.bat
```

**Option B : Commande manuelle**
```bash
./gradlew signingReport
```

Cherchez la ligne qui ressemble à :
```
SHA1: AA:BB:CC:DD:EE:FF:11:22:33:44:55:66:77:88:99:00:AA:BB:CC:DD
```

**Copiez cette valeur !** ✂️

---

### Étape 2 : Ajouter le SHA-1 dans Firebase Console

1. Allez sur **https://console.firebase.google.com/**
2. Sélectionnez votre projet
3. Cliquez sur **⚙️ (Paramètres)** > **Paramètres du projet**
4. Descendez jusqu'à **"Vos applications"**
5. Cliquez sur votre application Android
6. Descendez jusqu'à **"Empreintes digitales du certificat SHA"**
7. Cliquez sur **"Ajouter une empreinte"**
8. **Collez le SHA-1** que vous avez copié
9. Cliquez sur **"Enregistrer"**

---

### Étape 3 : Télécharger le nouveau google-services.json

⚠️ **IMPORTANT** : Après avoir ajouté le SHA-1, vous DEVEZ télécharger à nouveau le fichier !

1. Restez dans **Paramètres du projet**
2. Descendez jusqu'à **"Vos applications"**
3. Cliquez sur **"google-services.json"** pour le télécharger
4. **Remplacez** l'ancien fichier :
   - Pour Demo : `app/src/demo/google-services.json`
   - Pour Prod : `app/src/prod/google-services.json`

---

### Étape 4 : Vérifier le Web Client ID

1. **Obtenir le Web Client ID** :
   - Firebase Console > **Authentication** > **Sign-in method**
   - Cliquez sur **Google**
   - Développez **"Configuration du SDK Web"**
   - **Copiez le "Web client ID"** (format : `123456789-abcdefg.apps.googleusercontent.com`)

2. **Configurer dans l'app** :
   - Ouvrez `app/src/main/res/values/strings.xml`
   - Trouvez la ligne :
     ```xml
     <string name="google_web_client_id">YOUR_GOOGLE_WEB_CLIENT_ID</string>
     ```
   - **Remplacez** `YOUR_GOOGLE_WEB_CLIENT_ID` par votre Web Client ID :
     ```xml
     <string name="google_web_client_id">123456789-abcdefg.apps.googleusercontent.com</string>
     ```
   - **Sauvegardez** le fichier

---

### Étape 5 : Nettoyer et recompiler

Dans Android Studio :

1. **Clean Project** :
   - Menu : **Build** > **Clean Project**
   - Attendez que ça se termine

2. **Rebuild Project** :
   - Menu : **Build** > **Rebuild Project**
   - Attendez la compilation complète

3. **Synchroniser Gradle** :
   - Menu : **File** > **Sync Project with Gradle Files**

4. **Relancer l'app** :
   - Cliquez sur **Run** (▶️) ou **Shift + F10**

---

## 🧪 Tester la connexion Google

1. Lancez l'app
2. Sur l'écran d'authentification, cliquez sur **"Continuer avec Google"**
3. Sélectionnez un compte Google
4. ✅ Vous devriez être connecté sans erreur !

---

## 🔍 Diagnostic Automatique

Utilisez le script de diagnostic pour vérifier votre configuration :

```bash
fix_google_signin.bat
```

Ce script va :
- ✅ Générer votre SHA-1
- ✅ Vérifier le Web Client ID
- ✅ Vérifier la présence de google-services.json
- ✅ Créer un rapport de diagnostic

---

## 🐛 Problèmes persistants ?

### Erreur : "Web Client ID non configuré"

**Solution** :
- Vérifiez que vous avez bien remplacé `YOUR_GOOGLE_WEB_CLIENT_ID` dans `strings.xml`
- Le Web Client ID doit ressembler à : `123456789-abcdefg.apps.googleusercontent.com`

### Erreur : "SHA1 mismatch"

**Solution** :
- Vérifiez que le SHA-1 dans Firebase Console correspond exactement à celui de votre app
- Si vous utilisez plusieurs ordinateurs, ajoutez tous les SHA-1 dans Firebase

### Erreur : "google-services.json manquant"

**Solution** :
- Vérifiez que le fichier est dans le bon dossier :
  - Demo : `app/src/demo/google-services.json`
  - Prod : `app/src/prod/google-services.json`
- Synchronisez Gradle après avoir ajouté le fichier

### L'app crash au démarrage

**Solution** :
1. Vérifiez les logs dans Logcat (View > Tool Windows > Logcat)
2. Cherchez les erreurs Firebase
3. Vérifiez que toutes les dépendances sont synchronisées
4. Invalidez le cache : File > Invalidate Caches / Restart

---

## 📋 Checklist de vérification

Avant de tester, assurez-vous que :

- [ ] Le SHA-1 est ajouté dans Firebase Console
- [ ] Le nouveau `google-services.json` est téléchargé et placé dans le bon dossier
- [ ] Le Web Client ID est configuré dans `strings.xml`
- [ ] Le projet est nettoyé et recompilé
- [ ] Gradle est synchronisé
- [ ] Google Sign-In est activé dans Firebase Console (Authentication > Sign-in method)

---

## 🎯 Résumé en 3 étapes

1. **SHA-1** : Générez-le avec `./gradlew signingReport` et ajoutez-le dans Firebase Console
2. **google-services.json** : Téléchargez le nouveau fichier après avoir ajouté le SHA-1
3. **Web Client ID** : Configurez-le dans `strings.xml`

---

## 📚 Ressources

- [Documentation Firebase Auth](https://firebase.google.com/docs/auth/android/google-signin)
- [Guide de configuration complet](FIREBASE_SETUP.md)
- [Guide de démarrage rapide](QUICKSTART.md)

---

**Besoin d'aide ?** Consultez le fichier `google_signin_diagnostic.txt` généré par le script de diagnostic.
