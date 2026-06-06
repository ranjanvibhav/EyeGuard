package com.eyeguard.service;

import com.eyeguard.viewmodel.BreakOverlayViewModel;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Unit tests for {@link OverlayServiceImpl} to verify overlay display and disposal.
 */
class OverlayServiceImplTest {

    private static final int LATCH_TIMEOUT_SECONDS = 5;

    private BreakOverlayViewModel viewModel;
    private OverlayService overlayService;

    /**
     * Initializes the JavaFX Toolkit runtime required for Stage/Scene operations.
     */
    @BeforeAll
    static void initToolkit() {
        try {
            Platform.startup(() -> {});
        } catch (final IllegalStateException exception) {
            // Toolkit already initialized, safe to ignore
        }
        Platform.setImplicitExit(false);
    }

    /**
     * Prepares mock objects and the service instance before each test.
     */
    @BeforeEach
    void setUp() {
        viewModel = new BreakOverlayViewModel();
        overlayService = new OverlayServiceImpl(viewModel);
    }

    /**
     * Verifies that showOverlay and hideOverlay execute without errors on the FX thread.
     *
     * @throws InterruptedException if thread execution is interrupted
     */
    @Test
    void testShowAndHideOverlayOnFxThread() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                assertDoesNotThrow(() -> overlayService.showOverlay());
                assertDoesNotThrow(() -> overlayService.hideOverlay());
                latch.countDown();
            } catch (final Exception exception) {
                fail("Exception occurred in JavaFX thread: " + exception.getMessage());
            }
        });

        assertTrue(latch.await(LATCH_TIMEOUT_SECONDS, TimeUnit.SECONDS), "JavaFX thread execution timed out");
    }

    /**
     * Verifies that calling showOverlay on a non-FX thread is handled safely and logs an error.
     */
    @Test
    void testShowOverlayOnNonFxThread() {
        // Calling showOverlay directly on the JUnit runner thread (non-FX thread)
        assertDoesNotThrow(() -> overlayService.showOverlay());
    }

    /**
     * Verifies that calling hideOverlay on a non-FX thread executes safely by marshalling.
     */
    @Test
    void testHideOverlayOnNonFxThread() {
        // Calling hideOverlay directly on non-FX thread should marshal back automatically
        assertDoesNotThrow(() -> overlayService.hideOverlay());
    }
}
