package com.eyeguard.service;

import com.eyeguard.exception.EyeGuardException;
import com.eyeguard.view.DashboardController;
import com.eyeguard.viewmodel.DashboardViewModel;
import java.io.IOException;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the DashboardService managing the dashboard stats popup stage lifecycle.
 */
public class DashboardServiceImpl implements DashboardService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DashboardServiceImpl.class);
    private static final double POPUP_WIDTH = 340.0;
    private static final double POPUP_HEIGHT = 480.0;
    private static final double EDGE_MARGIN = 16.0;

    private final DashboardViewModel viewModel;
    private Stage dashboardStage;

    /**
     * Constructs the DashboardServiceImpl with the specified view model.
     *
     * @param viewModel the dashboard view model
     */
    public DashboardServiceImpl(final DashboardViewModel viewModel) {
        this.viewModel = viewModel;
    }

    /**
     * Shows the dashboard popup on the primary screen.
     * Must be called on the JavaFX Application Thread.
     */
    @Override
    public void showDashboard() {
        if (!Platform.isFxApplicationThread()) {
            LOGGER.error("showDashboard must be called on the JavaFX Application Thread");
            return;
        }
        if (dashboardStage != null && dashboardStage.isShowing()) {
            dashboardStage.hide();
            LOGGER.info("Dashboard hidden via toggle");
            return;
        }
        try {
            loadAndShowDashboard();
        } catch (final IOException exception) {
            LOGGER.error("Failed to load dashboard popup FXML", exception);
            throw new EyeGuardException("Failed to display Dashboard stats popup", exception);
        }
    }

    private void loadAndShowDashboard() throws IOException {
        final FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard-popup.fxml"));
        final Parent root = loader.load();
        final DashboardController controller = loader.getController();
        controller.setViewModel(viewModel);

        dashboardStage = new Stage(StageStyle.TRANSPARENT);
        final Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);

        dashboardStage.setScene(scene);
        dashboardStage.setAlwaysOnTop(true);
        dashboardStage.setResizable(false);
        positionStage(dashboardStage);

        controller.setStage(dashboardStage);
        dashboardStage.show();
        LOGGER.info("Dashboard shown");
    }

    private void positionStage(final Stage stage) {
        final Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX(screenBounds.getMaxX() - POPUP_WIDTH - EDGE_MARGIN);
        stage.setY(screenBounds.getMaxY() - POPUP_HEIGHT - EDGE_MARGIN);
    }

    /**
     * Hides the dashboard popup from the screen.
     * Must run on the JavaFX Application Thread.
     */
    @Override
    public void hideDashboard() {
        Platform.runLater(() -> {
            if (dashboardStage != null) {
                dashboardStage.hide();
                LOGGER.info("Dashboard hidden");
            }
        });
    }
}
