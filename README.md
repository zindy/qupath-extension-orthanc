# qupath-extension-orthanc

> **[English](#english) | [Français](#français)**

![Version](https://img.shields.io/badge/version-0.3.1-blue)
![QuPath](https://img.shields.io/badge/QuPath-%3E%3D%200.5.0-green)
![Java](https://img.shields.io/badge/Java-%3E%3D%2017-orange)
![License](https://img.shields.io/badge/license-GPL--v3-blue)

---

## English

### Overview

**qupath-extension-orthanc** is a [QuPath](https://qupath.github.io/) extension that lets you browse and import DICOM images directly from an [Orthanc](https://www.orthanc-server.com/) server into QuPath.

It supports two import modes:
- **Single instance** — the instance is rendered and downloaded as a temporary PNG file, then opened in QuPath.
- **Full WSI series** — tiles are streamed on demand via the [Orthanc WSI plugin](https://orthanc.uclouvain.be/book/plugins/wsi.html); no full download is required.

The interface language is automatically set to match your system locale (English and French supported).

---

### Features

- Connect to any Orthanc server, with or without HTTP authentication
- Browse DICOM studies and filter series by SM (Slide Microscopy) modality
- Import a single DICOM instance (rendered as PNG)
- Import a complete series as a pyramidal WSI — tiles are fetched on demand, no full download needed
- Automatic QuPath project creation when none is open
- Display metadata of the currently open image (name, dimensions, channels, project)

---

### Requirements

| Dependency | Version |
|---|---|
| [QuPath](https://qupath.github.io/) | >= 0.5.0 |
| Java | >= 17 |
| Orthanc server | any recent version |
| [Orthanc WSI plugin](https://orthanc.uclouvain.be/book/plugins/wsi.html) | required for pyramidal series import |

> The JAR is self-contained: OkHttp, Gson, and dcm4che are bundled — no extra installation needed.

---

### Installation

#### Option 1 — Pre-built JAR (recommended)

1. Download `qupath-extension-orthanc-1.0.jar` from the [Releases](../../releases) page.

2. Locate your QuPath extensions folder:
   - **Windows:** `C:\Users\<you>\AppData\Local\QuPath\v0.5\extensions\`
   - **macOS:** `~/Library/Application Support/QuPath/v0.5/extensions/`
   - **Linux:** `~/.local/share/QuPath/v0.5/extensions/`

   > You can find this path in QuPath under **Edit > Preferences > Extensions directory**.

3. Copy the `.jar` file into that folder.

4. Restart QuPath — an **Orthanc Extension** menu will appear in the menu bar.

#### Option 2 — Build from source

```bash
git clone https://github.com/D4rkL4s3r44/qupath-extension-orthanc.git
cd qupath-extension-orthanc
./gradlew clean build
```

The JAR is generated at:
```
build/libs/qupath-extension-orthanc-1.0.jar
```

Copy it to your QuPath extensions folder (see Option 1, step 2).

> On Windows, use `gradlew.bat clean build` instead.

---

### Usage

1. Open QuPath.
2. Go to **Orthanc Extension > Import a DICOM image...**.
3. Enter your Orthanc server URL (default: `http://localhost:8042`).
4. If authentication is required, check the **Authentication required** box and enter your credentials.
5. Click **Connect**.
6. Select a study, then a series from the drop-down lists.
7. Choose an import mode:
   - **Import a single instance** — imports the selected instance as a PNG image.
   - **Import the whole series** — streams the full pyramidal WSI directly from Orthanc (requires a QuPath project).
8. Click **Import**.

> If no project is open when importing, the extension will offer to create one automatically.

To view metadata of the currently open image, go to **Orthanc Extension > Image information**.

---

### Architecture

```
src/main/java/com/example/qupath/
├── TestExtension.java              # QuPath extension entry point, menu registration
├── Messages.java                   # i18n utility (auto-detects system locale)
└── orthanc/
    ├── OrthancClient.java          # Orthanc REST API client (studies, series, instances)
    ├── OrthancImageServer.java     # QuPath ImageServer — streams tiles on demand
    ├── OrthancImageServerBuilder.java  # ImageServerBuilder registration
    └── EnhancedOrthancImportDialog.java  # Import dialog UI
```

---

### Bundled dependencies

| Library | Version | Purpose |
|---|---|---|
| [OkHttp](https://square.github.io/okhttp/) | 4.12.0 | HTTP communication with Orthanc |
| [Gson](https://github.com/google/gson) | 2.10.1 | JSON parsing of Orthanc API responses |
| [dcm4che](https://www.dcm4che.org/) | 5.29.2 | DICOM file parsing |

---

### License

This project is released under the [GNU General Public License v3.0](LICENSE).

Copyright (C) 2026 D4rkL4s3r

---

## Français

### Présentation

**qupath-extension-orthanc** est une extension [QuPath](https://qupath.github.io/) permettant de parcourir et d'importer des images DICOM directement depuis un serveur [Orthanc](https://www.orthanc-server.com/).

Elle supporte deux modes d'import :
- **Instance unique** — l'instance est rendue et téléchargée dans un fichier PNG temporaire, puis ouverte dans QuPath.
- **Série WSI complète** — les tuiles sont récupérées à la demande via le [plugin Orthanc WSI](https://orthanc.uclouvain.be/book/plugins/wsi.html) ; aucun téléchargement intégral n'est nécessaire.

La langue de l'interface est automatiquement adaptée à la locale du système (anglais et français supportés).

---

### Fonctionnalités

- Connexion à un serveur Orthanc, avec ou sans authentification HTTP
- Navigation dans les études DICOM et filtrage des séries par modalité SM (Slide Microscopy)
- Import d'une instance DICOM unique (rendue en PNG)
- Import d'une série complète en WSI pyramidale — les tuiles sont récupérées à la demande, sans téléchargement intégral
- Création automatique d'un projet QuPath si aucun n'est ouvert
- Affichage des métadonnées de l'image courante (nom, dimensions, canaux, projet)

---

### Prérequis

| Dépendance | Version |
|---|---|
| [QuPath](https://qupath.github.io/) | >= 0.5.0 |
| Java | >= 17 |
| Serveur Orthanc | toute version récente |
| [Plugin Orthanc WSI](https://orthanc.uclouvain.be/book/plugins/wsi.html) | requis pour l'import de séries pyramidales |

> Le JAR est autonome : OkHttp, Gson et dcm4che sont inclus — aucune installation supplémentaire requise.

---

### Installation

#### Option 1 — JAR pré-compilé (recommandé)

1. Téléchargez `qupath-extension-orthanc-1.0.jar` depuis la page [Releases](../../releases).

2. Localisez le dossier d'extensions de QuPath :
   - **Windows :** `C:\Users\<votre-nom>\AppData\Local\QuPath\v0.5\extensions\`
   - **macOS :** `~/Library/Application Support/QuPath/v0.5/extensions/`
   - **Linux :** `~/.local/share/QuPath/v0.5/extensions/`

   > Ce chemin est accessible depuis QuPath via **Edit > Preferences > Extensions directory**.

3. Copiez le fichier `.jar` dans ce dossier.

4. Redémarrez QuPath — un menu **Orthanc Extension** apparaît dans la barre de menus.

#### Option 2 — Compiler depuis les sources

```bash
git clone https://github.com/D4rkL4s3r44/qupath-extension-orthanc.git
cd qupath-extension-orthanc
./gradlew clean build
```

Le JAR est généré dans :
```
build/libs/qupath-extension-orthanc-1.0.jar
```

Copiez-le dans le dossier d'extensions de QuPath (voir Option 1, étape 2).

> Sous Windows, utilisez `gradlew.bat clean build`.

---

### Utilisation

1. Lancez QuPath.
2. Allez dans **Orthanc Extension > Importer une image DICOM...**.
3. Saisissez l'URL de votre serveur Orthanc (par défaut : `http://localhost:8042`).
4. Si une authentification est requise, cochez **Authentification requise** et entrez vos identifiants.
5. Cliquez sur **Connexion**.
6. Sélectionnez une étude, puis une série dans les listes déroulantes.
7. Choisissez le mode d'import :
   - **Importer une instance unique** — importe l'instance sélectionnée en image PNG.
   - **Importer la série complète** — diffuse la WSI pyramidale directement depuis Orthanc (nécessite un projet QuPath).
8. Cliquez sur **Importer**.

> Si aucun projet n'est ouvert lors de l'import, l'extension proposera d'en créer un automatiquement.

Pour consulter les métadonnées de l'image courante, allez dans **Orthanc Extension > Informations sur l'image**.

---

### Architecture

```
src/main/java/com/example/qupath/
├── TestExtension.java              # Point d'entrée de l'extension, enregistrement du menu
├── Messages.java                   # Utilitaire i18n (locale système détectée automatiquement)
└── orthanc/
    ├── OrthancClient.java          # Client REST Orthanc (études, séries, instances)
    ├── OrthancImageServer.java     # ImageServer QuPath — diffusion des tuiles à la demande
    ├── OrthancImageServerBuilder.java  # Enregistrement du ImageServerBuilder
    └── EnhancedOrthancImportDialog.java  # Interface du dialogue d'import
```

---

### Dépendances incluses

| Bibliothèque | Version | Rôle |
|---|---|---|
| [OkHttp](https://square.github.io/okhttp/) | 4.12.0 | Communication HTTP avec Orthanc |
| [Gson](https://github.com/google/gson) | 2.10.1 | Parsing JSON des réponses de l'API Orthanc |
| [dcm4che](https://www.dcm4che.org/) | 5.29.2 | Lecture des fichiers DICOM |

---

### Licence

Ce projet est distribué sous la licence [MIT](LICENSE).
