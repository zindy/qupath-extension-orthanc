package com.example.qupath;

import javafx.application.Platform;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.DirectoryChooser;
import qupath.lib.gui.QuPathGUI;
import qupath.lib.gui.extensions.QuPathExtension;
import qupath.lib.images.ImageData;
import qupath.lib.images.servers.ImageServerBuilder;
import qupath.lib.images.servers.ImageServerProvider;
import qupath.lib.projects.Project;
import qupath.lib.projects.ProjectImageEntry;
import qupath.lib.projects.Projects;
import com.example.qupath.orthanc.EnhancedOrthancImportDialog;
import com.example.qupath.orthanc.OrthancImageServer;
import com.example.qupath.orthanc.OrthancImageServerBuilder;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Optional;
import java.util.ServiceLoader;

/**
 * QuPath extension for importing DICOM images from Orthanc.
 */
public class TestExtension implements QuPathExtension {

    @Override
    public void installExtension(QuPathGUI qupath) {
        // Register OrthancImageServerBuilder in QuPath's ServiceLoader
        ImageServerProvider.setServiceLoader(
                ServiceLoader.load(ImageServerBuilder.class, OrthancImageServerBuilder.class.getClassLoader()));

        Menu menu = new Menu(Messages.get("menu.name"));

        // Import item
        MenuItem importFromOrthanc = new MenuItem(Messages.get("menu.import"));
        importFromOrthanc.setOnAction(e -> importDicomFromOrthanc(qupath));

        // Image info item
        MenuItem imageInfo = new MenuItem(Messages.get("menu.imageInfo"));
        imageInfo.setOnAction(e -> {
            ImageData<?> imageData = qupath.getImageData();
            if (imageData == null) {
                showAlert(Messages.get("info.noImage"), Messages.get("info.noImageContent"));
                return;
            }

            StringBuilder info = new StringBuilder();
            info.append(Messages.get("info.name")).append(" ").append(imageData.getServer().getMetadata().getName()).append("\n");
            info.append(Messages.get("info.width")).append(" ").append(imageData.getServer().getWidth()).append(Messages.get("info.widthUnit"));
            info.append(Messages.get("info.height")).append(" ").append(imageData.getServer().getHeight()).append(Messages.get("info.heightUnit"));
            info.append(Messages.get("info.channels")).append(" ").append(imageData.getServer().nChannels());

            Project<?> project = qupath.getProject();
            if (project != null) {
                info.append(Messages.get("info.project")).append(" ").append(project.getName());
                info.append(Messages.get("info.projectCount")).append(" ").append(project.getImageList().size());
            } else {
                info.append(Messages.get("info.noProject"));
            }

            showAlert(Messages.get("info.title"), info.toString());
        });

        // About item
        MenuItem about = new MenuItem(Messages.get("menu.about"));
        about.setOnAction(e -> showAlert(Messages.get("about.title"), Messages.get("about.content")));

        menu.getItems().addAll(importFromOrthanc, imageInfo, about);
        qupath.getMenuBar().getMenus().add(menu);
    }

    private void importDicomFromOrthanc(QuPathGUI qupath) {
        EnhancedOrthancImportDialog dialog = new EnhancedOrthancImportDialog();
        Optional<EnhancedOrthancImportDialog.OrthancImportResult> result = dialog.showAndWait();

        result.ifPresent(importResult -> {
            try {
                Project<BufferedImage> project = qupath.getProject();

                if (project == null) {
                    boolean createProject = askToCreateProject(importResult.isWholeSeries());

                    if (createProject) {
                        TextInputDialog nameDialog = new TextInputDialog("Orthanc_Project");
                        nameDialog.setTitle(Messages.get("project.nameTitle"));
                        nameDialog.setHeaderText(Messages.get("project.nameHeader"));
                        nameDialog.setContentText(Messages.get("project.nameLabel"));
                        Optional<String> nameResult = nameDialog.showAndWait();
                        if (nameResult.isEmpty()) return;
                        String projectName = nameResult.get().trim().isEmpty() ? "Orthanc_Project" : nameResult.get().trim();
                        project = createNewProject(qupath, projectName);
                        if (project == null) {
                            showAlert(Messages.get("error.title"), Messages.get("error.createProject"));
                            return;
                        }
                    } else if (importResult.isWholeSeries()) {
                        showAlert(Messages.get("error.title"), Messages.get("project.required"));
                        return;
                    } else {
                        openImageSimple(qupath, importResult.getDicomFiles().get(0));
                        showAlert(Messages.get("import.success"), Messages.get("import.openSuccess"));
                        return;
                    }
                }

                if (importResult.isWholeSeries()) {
                    addSeriesToProject(qupath, project, importResult);
                } else {
                    addImageToProject(qupath, project, importResult.getDicomFiles().get(0), importResult.getSeriesName());
                }

            } catch (Exception e) {
                showAlert(Messages.get("error.title"), Messages.get("error.import") + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private void openImageSimple(QuPathGUI qupath, File imageFile) {
        Platform.runLater(() -> {
            try {
                var viewer = qupath.getViewer();
                qupath.openImage(viewer, imageFile.getAbsolutePath(), true, true);
                autoAdjustDisplay(qupath);
            } catch (Exception e) {
                showAlert(Messages.get("error.title"), Messages.get("error.openImage") + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private void autoAdjustDisplay(QuPathGUI qupath) {
        var viewer = qupath.getViewer();
        if (viewer == null) return;
        var imageDisplay = viewer.getImageDisplay();
        if (imageDisplay == null) return;
        for (var channel : imageDisplay.availableChannels()) {
            imageDisplay.autoSetDisplayRange(channel);
        }
        viewer.repaint();
    }

    private boolean askToCreateProject(boolean isWholeSeries) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(Messages.get("project.askTitle"));

        if (isWholeSeries) {
            alert.setHeaderText(Messages.get("project.askHeaderSeries"));
            alert.setContentText(Messages.get("project.askContentSeries"));
        } else {
            alert.setHeaderText(Messages.get("project.askHeader"));
            alert.setContentText(Messages.get("project.askContent"));
        }

        ButtonType buttonYes = new ButtonType(Messages.get("project.yes"));
        ButtonType buttonNo  = new ButtonType(Messages.get("project.no"));
        alert.getButtonTypes().setAll(buttonYes, buttonNo);

        Optional<ButtonType> response = alert.showAndWait();
        return response.isPresent() && response.get() == buttonYes;
    }

    private Project<BufferedImage> createNewProject(QuPathGUI qupath, String projectName) {
        try {
            DirectoryChooser dirChooser = new DirectoryChooser();
            dirChooser.setTitle(Messages.get("project.nameTitle"));
            File projectDir = dirChooser.showDialog(qupath.getStage());

            if (projectDir == null) return null;

            File projectFile = new File(projectDir, projectName);
            if (!projectFile.exists()) projectFile.mkdirs();

            File qpprojFile = new File(projectFile, "project.qpproj");
            Project<BufferedImage> project = Projects.createProject(qpprojFile, BufferedImage.class);
            qupath.setProject(project);

            showAlert(Messages.get("project.created"), Messages.get("project.createdContent") + " " + projectName);
            return project;

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Messages.get("error.title"), Messages.get("error.createProject") + ":\n" + e.getMessage());
            return null;
        }
    }

    private void addImageToProject(QuPathGUI qupath, Project<BufferedImage> project, File dicomFile, String imageName) {
        try {
            var uris = ImageServerProvider.getPreferredUriImageSupport(BufferedImage.class, dicomFile.getAbsolutePath());

            if (uris == null || uris.getBuilders().isEmpty()) {
                showAlert(Messages.get("error.title"), Messages.get("error.noServer"));
                return;
            }

            var builder = uris.getBuilders().get(0);
            ProjectImageEntry<BufferedImage> entry = project.addImage(builder);

            if (imageName != null && !imageName.isEmpty()) {
                entry.setImageName(imageName);
            }

            project.syncChanges();

            Platform.runLater(() -> {
                try {
                    qupath.refreshProject();
                    qupath.openImageEntry(entry);
                    autoAdjustDisplay(qupath);
                    showAlert(Messages.get("import.success"),
                        Messages.get("import.successContent") + " " + imageName +
                        Messages.get("import.successProject") + " " + project.getName());
                } catch (Exception e) {
                    showAlert(Messages.get("error.title"), Messages.get("error.addToProject") + e.getMessage());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Messages.get("error.title"), Messages.get("error.addToProject") + e.getMessage());
        }
    }

    private void addSeriesToProject(QuPathGUI qupath, Project<BufferedImage> project,
                                     EnhancedOrthancImportDialog.OrthancImportResult importResult) {
        new Thread(() -> {
            try {
                OrthancImageServer server = new OrthancImageServer(
                        importResult.getClient(), importResult.getSeriesId());
                var builder = server.getServerBuilder();
                server.close();

                ProjectImageEntry<BufferedImage> entry = project.addImage(builder);
                entry.setImageName(importResult.getSeriesName());
                project.syncChanges();

                Platform.runLater(() -> {
                    qupath.refreshProject();
                    try {
                        qupath.openImageEntry(entry);
                    } catch (Exception e) {
                        System.err.println("Error opening image: " + e.getMessage());
                    }
                    showAlert(Messages.get("import.successSeries"),
                        Messages.get("import.successSeriesContent") + " " + importResult.getSeriesName() +
                        Messages.get("import.successSeriesProject") + " " + project.getName());
                });

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() ->
                    showAlert(Messages.get("error.title"), Messages.get("error.import") + e.getMessage()));
            }
        }).start();
    }

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
        return "Full DICOM image import from Orthanc with WSI pyramidal series support";
    }
}
