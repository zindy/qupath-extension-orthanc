package com.example.qupath;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import qupath.lib.gui.QuPathGUI;
import qupath.lib.gui.extensions.QuPathExtension;
import qupath.lib.images.ImageData;
import qupath.lib.images.servers.ImageServerProvider;
import qupath.lib.projects.Project;
import qupath.lib.projects.ProjectImageEntry;
import qupath.lib.projects.Projects;
import com.example.qupath.orthanc.EnhancedOrthancImportDialog;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Optional;

/**
 * Extension QuPath avec import DICOM simplifié depuis Orthanc
 */
public class TestExtension implements QuPathExtension {
    
    @Override
    public void installExtension(QuPathGUI qupath) {
        // Créer un menu principal
        Menu menu = new Menu("Extension Orthanc");
        
        // Menu item principal : Importer depuis Orthanc
        MenuItem importFromOrthanc = new MenuItem("Importer une image DICOM...");
        importFromOrthanc.setOnAction(e -> {
            importDicomFromOrthanc(qupath);
        });
        
        // Menu item : Informations de l'image courante
        MenuItem imageInfo = new MenuItem("Informations de l'image");
        imageInfo.setOnAction(e -> {
            ImageData<?> imageData = qupath.getImageData();
            if (imageData == null) {
                showAlert("Aucune image ouverte", "Veuillez ouvrir une image d'abord.");
                return;
            }
            
            StringBuilder info = new StringBuilder();
            info.append("Nom: ").append(imageData.getServer().getMetadata().getName()).append("\n");
            info.append("Largeur: ").append(imageData.getServer().getWidth()).append(" px\n");
            info.append("Hauteur: ").append(imageData.getServer().getHeight()).append(" px\n");
            info.append("Canaux: ").append(imageData.getServer().nChannels()).append("\n");
            
            // Info sur le projet
            Project<?> project = qupath.getProject();
            if (project != null) {
                info.append("\nProjet: ").append(project.getName()).append("\n");
                info.append("Nombre d'images: ").append(project.getImageList().size());
            } else {
                info.append("\nAucun projet ouvert");
            }
            
            showAlert("Informations", info.toString());
        });
        
        // Menu item : À propos
        MenuItem about = new MenuItem("À propos");
        about.setOnAction(e -> {
            showAlert("À propos", 
                "Extension QuPath - Import DICOM Orthanc v0.3.1\n\n" +
                "Fonctionnalités:\n" +
                "• Navigation complète dans Orthanc\n" +
                "• Import d'une instance unique\n" +
                "• Import de séries complètes\n" +
                "• Barre de progression avec annulation\n" +
                "• Création automatique de projet\n" +
                "• Gestion intelligente des images multiples\n\n" +
                "Utilisation:\n" +
                "1. Cliquez sur 'Importer depuis Orthanc'\n" +
                "2. Connectez-vous à votre serveur Orthanc\n" +
                "3. Naviguez et sélectionnez votre image/série\n" +
                "4. Importez !"
            );
        });
        
        // Ajouter tous les items au menu
        menu.getItems().addAll(importFromOrthanc, imageInfo, about);
        
        // Ajouter le menu à la barre de menu de QuPath
        qupath.getMenuBar().getMenus().add(menu);
        
        System.out.println("Extension Orthanc v0.3.1 installée avec succès !");
    }
    
    /**
     * Ouvre le dialog d'import DICOM depuis Orthanc
     */
    private void importDicomFromOrthanc(QuPathGUI qupath) {
        EnhancedOrthancImportDialog dialog = new EnhancedOrthancImportDialog();
        Optional<EnhancedOrthancImportDialog.OrthancImportResult> result = dialog.showAndWait();
        
        result.ifPresent(importResult -> {
            try {
                // Vérifier s'il y a un projet ouvert
                Project<BufferedImage> project = qupath.getProject();
                
                if (project == null) {
                    // Pas de projet ouvert - en créer un
                    boolean createProject = askToCreateProject(importResult.isWholeSeries());
                    
                    if (createProject) {
                        project = createNewProject(qupath, "Orthanc_Project");
                        if (project == null) {
                            showAlert("Erreur", "Impossible de créer un projet");
                            return;
                        }
                    } else if (importResult.isWholeSeries()) {
                        showAlert("Erreur", "Un projet est requis pour importer une série complète");
                        return;
                    } else {
                        // Ouvrir juste la première image sans projet
                        openImageSimple(qupath, importResult.getDicomFiles().get(0));
                        showAlert("Import réussi", "Image DICOM ouverte avec succès !");
                        return;
                    }
                }
                
                // Ajouter les images au projet
                if (importResult.isWholeSeries()) {
                    addSeriesToProject(qupath, project, importResult);
                } else {
                    addImageToProject(qupath, project, importResult.getDicomFiles().get(0), importResult.getSeriesName());
                }
                
            } catch (Exception e) {
                showAlert("Erreur", "Erreur lors de l'import:\n" + e.getMessage());
                e.printStackTrace();
            }
        });
    }
    
    /**
     * Ouvre une image sans projet
     */
    private void openImageSimple(QuPathGUI qupath, File imageFile) {
        Platform.runLater(() -> {
            try {
                var viewer = qupath.getViewer();
                qupath.openImage(viewer, imageFile.getAbsolutePath(), true, true);
            } catch (Exception e) {
                showAlert("Erreur", "Erreur lors de l'ouverture de l'image:\n" + e.getMessage());
                e.printStackTrace();
            }
        });
    }
    
    /**
     * Demande à l'utilisateur s'il veut créer un projet
     */
    private boolean askToCreateProject(boolean isWholeSeries) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Aucun projet ouvert");
        
        if (isWholeSeries) {
            alert.setHeaderText("Un projet est requis pour importer une série");
            alert.setContentText(
                "L'import de série complète nécessite un projet QuPath.\n\n" +
                "Voulez-vous créer un nouveau projet ?"
            );
        } else {
            alert.setHeaderText("Voulez-vous créer un nouveau projet ?");
            alert.setContentText(
                "Aucun projet n'est actuellement ouvert.\n\n" +
                "• OUI : Créer un nouveau projet et y ajouter l'image\n" +
                "• NON : Ouvrir l'image sans projet"
            );
        }
        
        ButtonType buttonYes = new ButtonType("Oui");
        ButtonType buttonNo = new ButtonType("Non");
        
        alert.getButtonTypes().setAll(buttonYes, buttonNo);
        
        Optional<ButtonType> response = alert.showAndWait();
        return response.isPresent() && response.get() == buttonYes;
    }
    
    /**
     * Crée un nouveau projet QuPath
     */
    private Project<BufferedImage> createNewProject(QuPathGUI qupath, String projectName) {
        try {
            // Utiliser DirectoryChooser pour sélectionner un dossier
            DirectoryChooser dirChooser = new DirectoryChooser();
            dirChooser.setTitle("Choisir le dossier du projet");
            File projectDir = dirChooser.showDialog(qupath.getStage());
            
            if (projectDir == null) {
                return null;
            }
            
            // Créer le dossier du projet
            File projectFile = new File(projectDir, projectName);
            if (!projectFile.exists()) {
                projectFile.mkdirs();
            }
            
            // Créer le fichier project.qpproj
            File qpprojFile = new File(projectFile, "project.qpproj");
            
            // Créer et ouvrir le projet
            Project<BufferedImage> project = Projects.createProject(qpprojFile, BufferedImage.class);
            qupath.setProject(project);
            
            showAlert("Projet créé", "Nouveau projet créé : " + projectName);
            
            return project;
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la création du projet:\n" + e.getMessage());
            return null;
        }
    }
    
    /**
     * Ajoute une image au projet et l'ouvre
     */
    private void addImageToProject(QuPathGUI qupath, Project<BufferedImage> project, File dicomFile, String imageName) {
        try {
            // Créer un ServerBuilder pour l'image
            var uris = ImageServerProvider.getPreferredUriImageSupport(BufferedImage.class, dicomFile.getAbsolutePath());
            
            if (uris == null || uris.getBuilders().isEmpty()) {
                showAlert("Erreur", "Aucun serveur d'image disponible pour ce fichier DICOM");
                return;
            }
            
            var builder = uris.getBuilders().get(0);
            
            // Ajouter l'image au projet
            ProjectImageEntry<BufferedImage> entry = project.addImage(builder);
            
            // Définir le nom de l'image
            if (imageName != null && !imageName.isEmpty()) {
                entry.setImageName(imageName);
            }
            
            // Sauvegarder le projet
            project.syncChanges();
            
            // Ouvrir l'image
            Platform.runLater(() -> {
                try {
                    qupath.openImageEntry(entry);
                    showAlert("Import réussi", 
                        "Image DICOM importée et ajoutée au projet !\n\n" +
                        "Nom: " + imageName + "\n" +
                        "Projet: " + project.getName()
                    );
                } catch (Exception e) {
                    showAlert("Erreur", "Image ajoutée au projet mais erreur à l'ouverture:\n" + e.getMessage());
                }
            });
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ajout de l'image au projet:\n" + e.getMessage());
        }
    }
    
    /**
     * Ajoute toutes les images d'une série au projet avec barre de progression
     */
    private void addSeriesToProject(QuPathGUI qupath, Project<BufferedImage> project, 
                                     EnhancedOrthancImportDialog.OrthancImportResult importResult) {
        
        int totalImages = importResult.getDicomFiles().size();
        
        // Créer un dialog de progression
        Alert progressDialog = new Alert(AlertType.INFORMATION);
        progressDialog.setTitle("Import en cours");
        progressDialog.setHeaderText("Import de la série : " + importResult.getSeriesName());
        
        // Créer une barre de progression
        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(400);
        
        Label statusLabel = new Label("Préparation de l'import...");
        
        VBox progressBox = new VBox(10);
        progressBox.getChildren().addAll(statusLabel, progressBar);
        progressBox.setPadding(new Insets(20));
        
        progressDialog.getDialogPane().setContent(progressBox);
        
        // Bouton annuler
        ButtonType cancelButton = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        progressDialog.getButtonTypes().setAll(cancelButton);
        
        // Flag pour l'annulation
        final boolean[] cancelled = {false};
        
        progressDialog.setOnCloseRequest(e -> {
            cancelled[0] = true;
        });
        
        // Afficher le dialog sans bloquer
        progressDialog.show();
        
        // Import en arrière-plan
        new Thread(() -> {
            int successCount = 0;
            
            try {
                for (int i = 0; i < totalImages && !cancelled[0]; i++) {
                    File dicomFile = importResult.getDicomFiles().get(i);
                    String imageName = importResult.getSeriesName() + "_" + (i + 1);
                    
                    final int currentIndex = i + 1;
                    Platform.runLater(() -> {
                        statusLabel.setText(String.format("Import de l'image %d sur %d...", currentIndex, totalImages));
                        progressBar.setProgress((double) currentIndex / totalImages);
                    });
                    
                    try {
                        var uris = ImageServerProvider.getPreferredUriImageSupport(BufferedImage.class, dicomFile.getAbsolutePath());
                        
                        if (uris != null && !uris.getBuilders().isEmpty()) {
                            var builder = uris.getBuilders().get(0);
                            ProjectImageEntry<BufferedImage> entry = project.addImage(builder);
                            entry.setImageName(imageName);
                            successCount++;
                        }
                    } catch (Exception e) {
                        System.err.println("Erreur lors de l'ajout de l'image " + imageName + ": " + e.getMessage());
                    }
                }
                
                project.syncChanges();
                
                final int finalSuccessCount = successCount;
                final boolean wasCancelled = cancelled[0];
                
                Platform.runLater(() -> {
                    progressDialog.close();
                    
                    if (wasCancelled) {
                        showAlert("Import annulé", 
                            "Import annulé par l'utilisateur.\n\n" +
                            "Images importées : " + finalSuccessCount + "/" + totalImages + "\n" +
                            "Série : " + importResult.getSeriesName()
                        );
                    } else {
                        showAlert("Import réussi", 
                            "Série importée avec succès !\n\n" +
                            "Images ajoutées : " + finalSuccessCount + "/" + totalImages + "\n" +
                            "Série : " + importResult.getSeriesName() + "\n" +
                            "Projet : " + project.getName()
                        );
                    }
                    
                    // Ouvrir la première image
                    if (finalSuccessCount > 0) {
                        try {
                            qupath.openImageEntry(project.getImageList().get(project.getImageList().size() - finalSuccessCount));
                        } catch (Exception e) {
                            System.err.println("Erreur lors de l'ouverture de la première image : " + e.getMessage());
                        }
                    }
                });
                
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    progressDialog.close();
                    showAlert("Erreur", "Erreur lors de l'ajout de la série au projet :\n" + e.getMessage());
                });
            }
        }).start();
    }
    
    /**
     * Méthode utilitaire pour afficher une boîte de dialogue
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    @Override
    public String getName() {
        return "Extension Orthanc Import";
    }
    
    @Override
    public String getDescription() {
        return "Import complet d'images DICOM depuis Orthanc avec support des séries complètes";
    }
}
