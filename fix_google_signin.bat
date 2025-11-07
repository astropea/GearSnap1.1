@echo off
REM Script pour diagnostiquer et résoudre l'erreur Google Sign-In 10
REM Cette erreur se produit généralement à cause d'un problème de configuration SHA-1

echo.
echo ========================================
echo   Diagnostic Google Sign-In Erreur 10
echo ========================================
echo.

echo L'erreur 10 de Google Sign-In signifie que la configuration
echo du SHA-1 ou du Web Client ID n'est pas correcte.
echo.

REM Étape 1: Vérifier le Web Client ID
echo [1/5] Verification du Web Client ID dans strings.xml...
echo.

set STRINGS_FILE=app\src\main\res\values\strings.xml

if exist "%STRINGS_FILE%" (
    findstr /C:"YOUR_GOOGLE_WEB_CLIENT_ID" "%STRINGS_FILE%" > nul
    if errorlevel 1 (
        echo [OK] Web Client ID semble configure
        echo.
        echo Votre Web Client ID actuel:
        findstr /C:"google_web_client_id" "%STRINGS_FILE%"
        echo.
    ) else (
        echo [ERREUR] Le Web Client ID n'est PAS configure!
        echo.
        echo SOLUTION:
        echo 1. Allez sur https://console.firebase.google.com/
        echo 2. Selectionnez votre projet
        echo 3. Authentication ^> Sign-in method ^> Google
        echo 4. Copiez le "Web client ID"
        echo 5. Ouvrez app\src\main\res\values\strings.xml
        echo 6. Remplacez YOUR_GOOGLE_WEB_CLIENT_ID par votre Web Client ID
        echo.
        pause
        exit /b 1
    )
) else (
    echo [ERREUR] strings.xml non trouve!
    pause
    exit /b 1
)

REM Étape 2: Générer le SHA-1
echo [2/5] Generation du SHA-1 pour Debug...
echo.

call gradlew.bat signingReport > sha1_debug.txt 2>&1

echo SHA-1 genere! Extraction des informations...
echo.

REM Extraire le SHA-1 Debug
findstr /C:"SHA1:" sha1_debug.txt > sha1_result.txt

if exist sha1_result.txt (
    echo ========================================
    echo   VOS EMPREINTES SHA-1
    echo ========================================
    echo.
    type sha1_result.txt
    echo.
    echo ========================================
    echo.
) else (
    echo [ATTENTION] Impossible d'extraire le SHA-1
    echo Consultez le fichier sha1_debug.txt manuellement
    echo.
)

REM Étape 3: Vérifier google-services.json
echo [3/5] Verification de google-services.json...
echo.

set DEMO_FILE=app\src\demo\google-services.json
set PROD_FILE=app\src\prod\google-services.json

if exist "%DEMO_FILE%" (
    echo [OK] google-services.json trouve dans app\src\demo\
) else (
    echo [ATTENTION] google-services.json manquant dans app\src\demo\
)

if exist "%PROD_FILE%" (
    echo [OK] google-services.json trouve dans app\src\prod\
) else (
    echo [ATTENTION] google-services.json manquant dans app\src\prod\
)

echo.

REM Étape 4: Instructions de correction
echo [4/5] Instructions pour corriger l'erreur 10...
echo.
echo ========================================
echo   ETAPES DE CORRECTION
echo ========================================
echo.
echo 1. AJOUTER LE SHA-1 DANS FIREBASE CONSOLE
echo    - Copiez le SHA-1 affiche ci-dessus
echo    - Allez sur https://console.firebase.google.com/
echo    - Selectionnez votre projet
echo    - Parametres du projet ^> Vos applications
echo    - Cliquez sur votre app Android
echo    - Descendez a "Empreintes digitales du certificat SHA"
echo    - Cliquez "Ajouter une empreinte"
echo    - Collez le SHA-1
echo    - Cliquez "Enregistrer"
echo.
echo 2. TELECHARGER LE NOUVEAU google-services.json
echo    - Dans Firebase Console, restez dans Parametres du projet
echo    - Descendez a "Vos applications"
echo    - Cliquez sur "google-services.json" pour telecharger
echo    - Remplacez le fichier dans app\src\demo\ ou app\src\prod\
echo.
echo 3. VERIFIER LE WEB CLIENT ID
echo    - Firebase Console ^> Authentication ^> Sign-in method ^> Google
echo    - Copiez le "Web client ID"
echo    - Ouvrez app\src\main\res\values\strings.xml
echo    - Remplacez YOUR_GOOGLE_WEB_CLIENT_ID par votre Web Client ID
echo.
echo 4. NETTOYER ET RECOMPILER
echo    - Dans Android Studio: Build ^> Clean Project
echo    - Puis: Build ^> Rebuild Project
echo    - Synchronisez Gradle: File ^> Sync Project with Gradle Files
echo.
echo 5. TESTER A NOUVEAU
echo    - Lancez l'app
echo    - Essayez de vous connecter avec Google
echo.
echo ========================================
echo.

REM Étape 5: Créer un fichier de résumé
echo [5/5] Creation du fichier de diagnostic...
echo.

echo DIAGNOSTIC GOOGLE SIGN-IN ERREUR 10 > google_signin_diagnostic.txt
echo ======================================= >> google_signin_diagnostic.txt
echo. >> google_signin_diagnostic.txt
echo Date: %date% %time% >> google_signin_diagnostic.txt
echo. >> google_signin_diagnostic.txt
echo SHA-1 DETECTES: >> google_signin_diagnostic.txt
type sha1_result.txt >> google_signin_diagnostic.txt 2>nul
echo. >> google_signin_diagnostic.txt
echo FICHIERS DETECTES: >> google_signin_diagnostic.txt
if exist "%DEMO_FILE%" (
    echo [OK] app\src\demo\google-services.json >> google_signin_diagnostic.txt
) else (
    echo [MANQUANT] app\src\demo\google-services.json >> google_signin_diagnostic.txt
)
if exist "%PROD_FILE%" (
    echo [OK] app\src\prod\google-services.json >> google_signin_diagnostic.txt
) else (
    echo [MANQUANT] app\src\prod\google-services.json >> google_signin_diagnostic.txt
)
echo. >> google_signin_diagnostic.txt
echo CONFIGURATION WEB CLIENT ID: >> google_signin_diagnostic.txt
findstr /C:"google_web_client_id" "%STRINGS_FILE%" >> google_signin_diagnostic.txt 2>nul
echo. >> google_signin_diagnostic.txt

echo [OK] Diagnostic sauvegarde dans google_signin_diagnostic.txt
echo.

echo ========================================
echo   RESUME
echo ========================================
echo.
echo Pour corriger l'erreur Google Sign-In 10:
echo.
echo 1. Copiez le SHA-1 affiche ci-dessus
echo 2. Ajoutez-le dans Firebase Console
echo 3. Telechargez le nouveau google-services.json
echo 4. Verifiez le Web Client ID dans strings.xml
echo 5. Nettoyez et recompilez le projet
echo.
echo Consultez google_signin_diagnostic.txt pour plus de details
echo.

REM Nettoyage
if exist sha1_debug.txt del sha1_debug.txt
if exist sha1_result.txt del sha1_result.txt

pause
