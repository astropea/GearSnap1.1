@echo off
REM Script de configuration Firebase pour GearSnap
REM Ce script vous guide dans la configuration de Firebase Authentication

echo.
echo ========================================
echo   Configuration Firebase - GearSnap
echo ========================================
echo.

REM Vérifier si google-services.json existe
echo [1/4] Verification du fichier google-services.json...
echo.

set DEMO_FILE=app\src\demo\google-services.json
set PROD_FILE=app\src\prod\google-services.json

if exist "%DEMO_FILE%" (
    echo [OK] Fichier google-services.json trouve dans app\src\demo\
) else (
    echo [!] ATTENTION: google-services.json manquant dans app\src\demo\
    echo.
    echo Etapes a suivre:
    echo 1. Allez sur https://console.firebase.google.com/
    echo 2. Creez un projet Firebase
    echo 3. Ajoutez une app Android avec le package: com.gearsnap.demo
    echo 4. Telechargez google-services.json
    echo 5. Placez-le dans: app\src\demo\google-services.json
    echo.
    pause
)

echo.
echo [2/4] Generation du SHA-1 pour Google Sign-In...
echo.
echo Execution de: gradlew signingReport
echo.

call gradlew.bat signingReport > sha1_output.txt 2>&1

echo SHA-1 genere! Recherche du SHA-1 dans le fichier...
echo.

findstr /C:"SHA1:" sha1_output.txt > sha1_result.txt

if exist sha1_result.txt (
    echo Votre SHA-1:
    type sha1_result.txt
    echo.
    echo IMPORTANT: Copiez ce SHA-1 et ajoutez-le dans Firebase Console:
    echo 1. Console Firebase ^> Parametres du projet
    echo 2. Vos applications ^> Votre app Android
    echo 3. Empreintes digitales du certificat SHA
    echo 4. Ajouter une empreinte ^> Collez le SHA-1
    echo.
) else (
    echo Impossible de trouver le SHA-1. Verifiez sha1_output.txt manuellement.
)

echo.
echo [3/4] Verification de la configuration strings.xml...
echo.

set STRINGS_FILE=app\src\main\res\values\strings.xml

if exist "%STRINGS_FILE%" (
    findstr /C:"google_web_client_id" "%STRINGS_FILE%" > nul
    if errorlevel 1 (
        echo [!] ATTENTION: google_web_client_id non trouve dans strings.xml
    ) else (
        findstr /C:"YOUR_GOOGLE_WEB_CLIENT_ID" "%STRINGS_FILE%" > nul
        if errorlevel 1 (
            echo [OK] Web Client ID semble configure
        ) else (
            echo [!] ATTENTION: Vous devez configurer le Web Client ID dans strings.xml
            echo.
            echo Etapes:
            echo 1. Firebase Console ^> Authentication ^> Sign-in method ^> Google
            echo 2. Copiez le "Web client ID"
            echo 3. Ouvrez app\src\main\res\values\strings.xml
            echo 4. Remplacez YOUR_GOOGLE_WEB_CLIENT_ID par votre Web Client ID
        )
    )
) else (
    echo [!] ERREUR: strings.xml non trouve
)

echo.
echo [4/4] Synchronisation Gradle...
echo.

echo Pour synchroniser Gradle dans Android Studio:
echo 1. Ouvrez Android Studio
echo 2. File ^> Sync Project with Gradle Files
echo.

echo ========================================
echo   Configuration terminee!
echo ========================================
echo.
echo Prochaines etapes:
echo 1. Verifiez que google-services.json est en place
echo 2. Ajoutez le SHA-1 dans Firebase Console
echo 3. Configurez le Web Client ID dans strings.xml
echo 4. Telechargez a nouveau google-services.json apres avoir ajoute le SHA-1
echo 5. Synchronisez Gradle dans Android Studio
echo 6. Compilez et testez l'app
echo.
echo Documentation complete: FIREBASE_SETUP.md
echo Guide rapide: QUICKSTART.md
echo.

REM Nettoyage
if exist sha1_output.txt del sha1_output.txt
if exist sha1_result.txt del sha1_result.txt

pause
