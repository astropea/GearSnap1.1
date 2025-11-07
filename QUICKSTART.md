# ⚡ Démarrage Rapide - GearSnap Firebase Auth

Guide ultra-rapide pour démarrer avec Firebase Authentication en 5 minutes.

---

## 🎯 Version Express (5 minutes)

### 1️⃣ Créer le projet Firebase
1. Allez sur https://console.firebase.google.com/
2. Créez un nouveau projet "GearSnap"
3. Ajoutez une app Android avec le package : `com.gearsnap.demo`

### 2️⃣ Télécharger google-services.json
1. Téléchargez le fichier `google-services.json`
2. Placez-le dans : `app/src/demo/google-services.json`

### 3️⃣ Activer Email/Password
1. Firebase Console > Authentication > Sign-in method
2. Activez "Email/Password"

### 4️⃣ Compiler et tester
```bash
./gradlew assembleDemoDebug
```

✅ **C'est tout ! L'app est prête à fonctionner.**

---

## 🚀 Version Complète (avec Google Sign-In - 15 minutes)

Suivez le guide complet : [FIREBASE_SETUP.md](FIREBASE_SETUP.md)

### Étapes supplémentaires :
1. Obtenir le SHA-1 : `./gradlew signingReport`
2. Ajouter le SHA-1 dans Firebase Console
3. Activer Google Sign-In dans Firebase
4. Copier le Web Client ID dans `strings.xml`
5. Télécharger à nouveau `google-services.json`

---

## 📝 Checklist de configuration

### Configuration minimale (Email/Password)
- [ ] Projet Firebase créé
- [ ] App Android ajoutée (`com.gearsnap.demo`)
- [ ] `google-services.json` dans `app/src/demo/`
- [ ] Email/Password activé dans Firebase Console
- [ ] Gradle synchronisé
- [ ] App compilée et testée

### Configuration complète (+ Google Sign-In)
- [ ] SHA-1 obtenu avec `./gradlew signingReport`
- [ ] SHA-1 ajouté dans Firebase Console
- [ ] Google Sign-In activé dans Firebase
- [ ] Web Client ID copié dans `strings.xml`
- [ ] Nouveau `google-services.json` téléchargé
- [ ] App recompilée et testée

---

## 🧪 Tester l'authentification

### Test Email/Password
1. Lancez l'app
2. Cliquez sur "Continuer avec l'e-mail"
3. Créez un compte : `test@example.com` / `password123`
4. Vous devriez voir l'app complète avec la bottom navigation

### Test Google Sign-In
1. Déconnectez-vous (icône en haut à droite)
2. Cliquez sur "Continuer avec Google"
3. Sélectionnez un compte Google
4. Vous devriez être connecté

---

## 🐛 Problèmes courants

### "Default FirebaseApp is not initialized"
➡️ Vérifiez que `google-services.json` est dans `app/src/demo/`

### Google Sign-In ne fonctionne pas
➡️ Vérifiez que le SHA-1 est ajouté et que vous avez téléchargé le nouveau `google-services.json`

### L'app ne compile pas
➡️ Synchronisez Gradle : File > Sync Project with Gradle Files

---

## 📚 Documentation complète

- **Guide détaillé** : [FIREBASE_SETUP.md](FIREBASE_SETUP.md)
- **README principal** : [README.md](README.md)
- **Documentation Auth** : [app/src/main/java/com/gearsnap/auth/README.md](app/src/main/java/com/gearsnap/auth/README.md)

---

## 🎯 Commandes utiles

```bash
# Obtenir le SHA-1
./gradlew signingReport

# Compiler en mode Demo
./gradlew assembleDemoDebug

# Compiler en mode Prod
./gradlew assembleProdDebug

# Nettoyer le projet
./gradlew clean

# Synchroniser Gradle
./gradlew --refresh-dependencies
```

---

**Prêt à coder ? Lancez l'app et commencez à développer ! 🚀**
