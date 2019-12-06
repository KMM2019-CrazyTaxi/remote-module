package gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import remote.listeners.DataListener;

/**
 * Camera Image-feature Controller class. This is the main controller class for the Camera Image-feature.
 *
 * @author Henrik Nilsson
 */
public class CameraImageFeatureController implements DataListener<Image> {

    private static final Image DEFAULT_IMAGE = new Image("no_image.png");

    @FXML private ImageView cameraImageFeature;

    public void initialize() {
        cameraImageFeature.setImage(DEFAULT_IMAGE);
    }

    @Override
    public void update(Image data) {
        cameraImageFeature.setImage(data);
    }
}
