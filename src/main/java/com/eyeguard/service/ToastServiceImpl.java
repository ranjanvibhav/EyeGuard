package com.eyeguard.service;

import com.eyeguard.view.ToastController;
import com.eyeguard.viewmodel.ToastViewModel;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of ToastService controlling the stage window lifecycle and slide/fade transitions.
 */
public class ToastServiceImpl implements ToastService {

    private static final Logger log = LoggerFactory.getLogger(ToastServiceImpl.class);

    private Stage toastStage;
    private final ToastViewModel viewModel;
    private final ScheduledExecutorService scheduler;
    private ScheduledFuture<?> dismissFuture;
    private final BooleanProperty toastVisible;

    /**
     * Constructs ToastServiceImpl.
     */
    public ToastServiceImpl() {
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            final Thread t = new Thread(r, "eyeguard-toast");
            t.setDaemon(true);
            return t;
        });
        this.toastVisible = new SimpleBooleanProperty(false);
        this.viewModel = new ToastViewModel();
    }

    @Override
    public void showToast(final String message, final int durationSeconds) {
        if (toastStage != null) {
            hideToast();
        }
        viewModel.messageTextProperty().set(message);
        try {
            final FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/toast-notification.fxml"));
            final Parent root = loader.load();
            ((ToastController) loader.getController()).setViewModel(viewModel);
            setupStageAndAnimations(root);
            scheduleDismiss(durationSeconds);
            log.info("Toast shown: {}", message);
        } catch (final Exception e) {
            log.error("Failed to load toast", e);
        }
    }

    private void setupStageAndAnimations(final Parent root) {
        toastStage = new Stage(javafx.stage.StageStyle.TRANSPARENT);
        toastStage.setAlwaysOnTop(true);
        toastStage.setResizable(false);
        final Scene scene = new Scene(root);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        toastStage.setScene(scene);
        root.setOpacity(0.0);
        root.setTranslateY(20.0);
        toastStage.show();
        final javafx.geometry.Rectangle2D screen = javafx.stage.Screen.getPrimary().getVisualBounds();
        toastStage.setX(screen.getMaxX() - toastStage.getWidth() - 16);
        toastStage.setY(screen.getMaxY() - toastStage.getHeight() - 16);
        playFadeAndSlideIn(root);
        toastVisible.set(true);
    }

    private void playFadeAndSlideIn(final Parent root) {
        final javafx.animation.FadeTransition fadeIn =
                new javafx.animation.FadeTransition(javafx.util.Duration.millis(300), root);
        fadeIn.setToValue(1.0);
        final javafx.animation.TranslateTransition slideIn =
                new javafx.animation.TranslateTransition(javafx.util.Duration.millis(300), root);
        slideIn.setToY(0.0);
        fadeIn.play();
        slideIn.play();
    }

    private void scheduleDismiss(final int durationSeconds) {
        dismissFuture = scheduler.schedule(() ->
                Platform.runLater(this::hideToast),
                durationSeconds, TimeUnit.SECONDS
        );
    }

    @Override
    public void hideToast() {
        if (toastStage == null) {
            return;
        }
        if (dismissFuture != null) {
            dismissFuture.cancel(false);
            dismissFuture = null;
        }
        playFadeOutAndHide();
    }

    private void playFadeOutAndHide() {
        final Stage stageToHide = toastStage;
        toastStage = null;
        final javafx.animation.FadeTransition fadeOut =
                new javafx.animation.FadeTransition(javafx.util.Duration.millis(400), stageToHide.getScene().getRoot());
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> {
            stageToHide.hide();
            toastVisible.set(false);
        });
        fadeOut.play();
    }

    @Override
    public ReadOnlyBooleanProperty toastVisibleProperty() {
        return toastVisible;
    }
}
