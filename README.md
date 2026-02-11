# QuPath Extension Orthanc v0.3.1

Extension QuPath permettant l'import d'images DICOM depuis un serveur [Orthanc](https://www.orthanc-server.com/).

## Fonctionnalites

- Connexion a un serveur Orthanc
- Navigation dans les etudes et series DICOM
- Import d'instances uniques ou de series completes
- Creation automatique de projet QuPath
- Suivi de la progression du telechargement

## Prerequis

- **QuPath** >= 0.5.0
- **Java** >= 17
- Un serveur **Orthanc** accessible sur le reseau

---

## Installation

### Option 1 : Utiliser le JAR pre-compile

1. Recuperez le fichier `qupath-extension-test-0.3.1.jar` (depuis les releases ou le dossier `build/libs/`).

2. Localisez le dossier d'extensions de QuPath :
   - **Windows** : `C:\Users\<votre-nom>\AppData\Local\QuPath\v0.5\extensions\`
   - **macOS** : `~/Library/Application Support/QuPath/v0.5/extensions/`
   - **Linux** : `~/.local/share/QuPath/v0.5/extensions/`

   > Vous pouvez aussi trouver ce dossier depuis QuPath : **Edit > Preferences > Extensions directory**

3. Copiez le fichier `.jar` dans ce dossier.

4. Redemarrez QuPath. Un menu **Extension Orthanc** apparait dans la barre de menu.

---

### Option 2 : Compiler depuis les sources

#### 1. Cloner le depot

```bash
git clone <url-du-depot>
cd qupath-extension-orthanc-v0.3.1
```

#### 2. Compiler avec Gradle

**Windows :**
```bash
gradle clean build
```

**macOS / Linux :**
```bash
./gradlew clean build
```

Le JAR sera genere dans :
```
build/libs/qupath-extension-test-0.3.1.jar
```

#### 3. Installer le JAR

Copiez le fichier genere dans le dossier d'extensions de QuPath (voir Option 1, etape 2), puis redemarrez QuPath.

---

## Utilisation

1. Lancez QuPath.
2. Allez dans le menu **Extension Orthanc > Importer une image DICOM...**
3. Renseignez l'adresse de votre serveur Orthanc (par defaut : `http://localhost:8042`).
4. Naviguez dans les etudes et series disponibles.
5. Selectionnez une image ou une serie, puis cliquez sur **Importer**.

> Pour l'import d'une serie complete, un projet QuPath est requis. L'extension proposera d'en creer un si necessaire.
