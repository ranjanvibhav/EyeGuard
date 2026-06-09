package com.eyeguard.view;

import com.eyeguard.viewmodel.ToastViewModel;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

/**
 * Controller class for the Toast FXML layout.
 */
public class ToastController implements Initializable {

    @FXML
    private StackPane toastRoot;

    @FXML
    private Label toastMessageLabel;

    private ToastViewModel viewModel;

    @Override
    public void initialize(final URL url, final ResourceBundle resources) {
        // Initialization behavior if required
    }

    /**
     * Binds visual FXML elements to the view model properties.
     *
     * @param viewModel presentation view model
     */
    public void setViewModel(final ToastViewModel viewModel) {
        this.viewModel = viewModel;
        toastMessageLabel.textProperty().bind(viewModel.messageTextProperty());
    }
}
