package com.eyeguard.service;

import com.eyeguard.viewmodel.DashboardViewModel;
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
 * Unit tests for {@link DashboardServiceImpl} verifying stats dashboard popup showing/hiding.
 */
class DashboardServiceImplTest {

    private static final int LATCH_TIMEOUT_SECONDS = 5;

    private DashboardViewModel viewModel;
    private DashboardService dashboardService;

    /**
     * Initializes JavaFX runtime context for FXML loading and stage rendering tests.
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
     * Sets up view model and service before each test.
     */
    @BeforeEach
    void setUp() {
        viewModel = new DashboardViewModel();
        dashboardService = new DashboardServiceImpl(viewModel);
    }

    /**
     * Verifies showing and hiding the dashboard on the JavaFX application thread.
     *
     * @throws InterruptedException if latch await is interrupted
     */
    @Test
    void testShowAndHideDashboardOnFxThread() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                assertDoesNotThrow(() -> dashboardService.showDashboard());
                assertDoesNotThrow(() -> dashboardService.showDashboard());
                assertDoesNotThrow(() -> dashboardService.hideDashboard());
                latch.countDown();
            } catch (final Exception exception) {
                fail("Exception occurred in FX thread: " + exception.getMessage());
            }
        });

        assertTrue(latch.await(LATCH_TIMEOUT_SECONDS, TimeUnit.SECONDS));
    }

    /**
     * Verifies calling showDashboard on a non-FX thread executes safely by logging and returning.
     */
    @Test
    void testShowDashboardOnNonFxThread() {
        assertDoesNotThrow(() -> dashboardService.showDashboard());
    }

    /**
     * Verifies calling hideDashboard on a non-FX thread executes safely by marshalling.
     */
    @Test
    void testHideDashboardOnNonFxThread() {
        assertDoesNotThrow(() -> dashboardService.hideDashboard());
    }
}
