package com.eyeguard.service;

import javafx.application.Platform;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for ToastServiceImpl.
 */
class ToastServiceImplTest {

    private ToastServiceImpl toastService;

    @BeforeAll
    static void initToolkit() {
        try {
            Platform.startup(() -> {});
        } catch (final IllegalStateException exception) {
            // Already initialized
        }
    }

    @BeforeEach
    void setUp() {
        toastService = new ToastServiceImpl();
    }

    @AfterEach
    void tearDown() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            toastService.hideToast();
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
    }

    @Test
    void testShowAndHideToast() throws Throwable {
        final CountDownLatch hideLatch = new CountDownLatch(1);
        Platform.runLater(() -> {
            assertFalse(toastService.toastVisibleProperty().get());
            toastService.toastVisibleProperty().addListener((o, oldV, newV) -> {
                if (!newV) hideLatch.countDown();
            });
            toastService.showToast("Test Msg", 1);
            assertTrue(toastService.toastVisibleProperty().get());
            toastService.hideToast();
        });
        assertTrue(hideLatch.await(3, TimeUnit.SECONDS));
        assertFalse(toastService.toastVisibleProperty().get());
    }
}
