# qupath-extension-orthanc

> **[English](#english) | [Français](#français)**

---

## English

### Overview

A [QuPath](https://qupath.github.io/) extension for importing DICOM images from an [Orthanc](https://www.orthanc-server.com/) server. Supports both single-instance imports and full WSI (Whole Slide Image) pyramidal series streamed on demand directly from Orthanc — no local download required.

### Features

- Connect to an Orthanc server (with or without authentication)
- Browse DICOM studies and series
- Import a single DICOM instance
- Import a complete series as a pyramidal WSI (tiles fetched on demand via the [Orthanc WSI plugin](https://orthanc.uclouvain.be/book/plugins/wsi.html))
- Automatic QuPath project creation
- Display current image metadata

### Requirements

| Dependency | Version |
|---|---|
| [QuPath](https://qupath.github.io/) | >= 0.5.0 |
| Java | >= 17 |
| Orthanc server | any recent version |
| [Orthanc WSI plugin](https://orthanc.uclouvain.be/book/plugins/wsi.html) | required for pyramidal series |

### Installation

#### Option 1 — Pre-built JAR

1. Download `qupath-extension-orthanc-1.0.jar` from the [Releases](../../releases) page or build it yourself (see below).

2. Locate your QuPath extensions folder:
   - **Windows:** `C:\Users\<you>\AppData\Local\QuPath\v0.5\extensions\`
   - **macOS:** `~/Library/Application Support/QuPath/v0.5/extensions/`
   - **Linux:** `~/.local/share/QuPath/v0.5/extensions/`

   > You can also find this path in QuPath via **Edit > Preferences > Extensions directory**.

3. Copy the `.jar` file into that folder.

4. Restart QuPath. An **Extension Orthanc** menu will appear in the menu bar.

#### Option 2 — Build from source

**Clone the repository:**
```bash
git clone <repository-url>
cd qupath-extension-orthanc-v0.3.1
```

**Build with Gradle:**

```bash
gradle clean build
```

The JAR is generated at:
```
build/libs/qupath-extension-orthanc-1.0.jar
```

Then copy it to your QuPath extensions folder (see Option 1, step 2).

### Usage

1. Open QuPath.
2. Go to **Extension Orthanc > Importer une image DICOM...**.
3. Enter your Orthanc server address (default: `http://localhost:8042`) and credentials if required.
4. Browse the available studies and series.
5. Select an image or a series, then click **Importer**.

**Note:** Importing a complete series requires a QuPath project. The extension will offer to create one automatically if none is open.

---

## Français

### Présentation

Extension [QuPath](https://qupath.github.io/) permettant l'import d'images DICOM depuis un serveur [Orthanc](https://www.orthanc-server.com/). Supporte l'import d'instances uniques ainsi que l'import de séries WSI (Whole Slide Image) pyramidales, dont les tuiles sont récupérées à la demande directement depuis Orthanc — aucun téléchargement local requis.

### Fonctionnalités

- Connexion à un serveur Orthanc (avec ou sans authentification)
- Navigation dans les études et séries DICOM
- Import d'une instance DICOM unique
- Import d'une série complète en tant que WSI pyramidale (tuiles récupérées à la demande via le [plugin Orthanc WSI](https://orthanc.uclouvain.be/book/plugins/wsi.html))
- Création automatique de projet QuPath
- Affichage des métadonnées de l'image courante

### Prérequis

| Dépendance | Version |
|---|---|
| [QuPath](https://qupath.github.io/) | >= 0.5.0 |
| Java | >= 17 |
| Serveur Orthanc | toute version récente |
| [Plugin Orthanc WSI](https://orthanc.uclouvain.be/book/plugins/wsi.html) | requis pour les séries pyramidales |

### Installation

#### Option 1 — JAR pré-compilé

1. Récupérez le fichier `qupath-extension-orthanc-1.0.jar` depuis la page [Releases](../../releases) ou compilez-le vous-même (voir ci-dessous).

2. Localisez le dossier d'extensions de QuPath :
   - **Windows :** `C:\Users\<votre-nom>\AppData\Local\QuPath\v0.5\extensions\`
   - **macOS :** `~/Library/Application Support/QuPath/v0.5/extensions/`
   - **Linux :** `~/.local/share/QuPath/v0.5/extensions/`

   > Vous pouvez aussi trouver ce chemin depuis QuPath via **Edit > Preferences > Extensions directory**.

3. Copiez le fichier `.jar` dans ce dossier.

4. Redémarrez QuPath. Un menu **Extension Orthanc** apparaît dans la barre de menu.

#### Option 2 — Compiler depuis les sources

**Cloner le dépôt :**
```bash
git clone <url-du-depot>
cd qupath-extension-orthanc-v0.3.1
```

**Compiler avec Gradle :**

```bash
gradle clean build
```

Le JAR est généré dans :
```
build/libs/qupath-extension-orthanc-1.0.jar
```

Copiez ensuite ce fichier dans le dossier d'extensions de QuPath (voir Option 1, étape 2).

### Utilisation

1. Lancez QuPath.
2. Allez dans le menu **Extension Orthanc > Importer une image DICOM...**.
3. Renseignez l'adresse de votre serveur Orthanc (par défaut : `http://localhost:8042`) et vos identifiants si nécessaire.
4. Naviguez dans les études et séries disponibles.
5. Sélectionnez une image ou une série, puis cliquez sur **Importer**.

**Remarque :** L'import d'une série complète nécessite un projet QuPath. L'extension proposera d'en créer un automatiquement si aucun n'est ouvert.
