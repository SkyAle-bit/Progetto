package com.project.tesi.config;

import com.project.tesi.service.DatabaseInitializerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.CommandLineRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

/**
 * Test unitari per {@link DataInitializer}.
 */
@ExtendWith(MockitoExtension.class)
class DataInitializerTest {

    @Mock private DatabaseInitializerService databaseInitializerService;

    @InjectMocks private DataInitializer dataInitializer;

    @Test @DisplayName("initData — crea un CommandLineRunner che chiama initialize()")
    void initData() throws Exception {
        CommandLineRunner runner = dataInitializer.initData();
        assertThat(runner).isNotNull();

        runner.run();

        verify(databaseInitializerService).initialize();
    }
}

