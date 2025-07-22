package com.renatoschlogel.razziesapi.api.controllers;

import com.renatoschlogel.razziesapi.RazziesApiApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest(classes = RazziesApiApplication.class)
@AutoConfigureMockMvc
class WorstMoviesControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String PRODUCERS_INTERVALS_ENDPOINT =  "/api/v1/worst-movies/producers-intervals";

    // The expected response is configured in the file below, as it depends on the source CSV file.
    // If the CSV file changes, this expected response must be updated accordingly.
    @Value("${test.data.expected-json-path}")
    private Resource expectedJsonResponse;

    @Test
    @DisplayName("Should return producers with min and max award intervals for worst movies in the specified format")
    void producersAwardsIntervals_shouldReturnExpectedJson() throws Exception {

        String expectedJson = loadJson(expectedJsonResponse);

        mockMvc.perform(get(PRODUCERS_INTERVALS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    private String loadJson(Resource resource) throws IOException {
        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        }
    }

}