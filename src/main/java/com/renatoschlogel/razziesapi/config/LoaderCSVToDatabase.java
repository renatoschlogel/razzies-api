package com.renatoschlogel.razziesapi.config;

import com.renatoschlogel.razziesapi.infra.db.entities.MovieEntity;
import com.renatoschlogel.razziesapi.infra.db.repositories.MovieRepository;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Configuration
public class LoaderCSVToDatabase {

    private static final String CSV_FILE_PATH = "/data/movielist.csv";
    private static final String CSV_DELIMITER = ";";

    @Bean
    @Transactional
    public CommandLineRunner loadData(MovieRepository movieRepository) {

        return args -> {
            if (movieRepository.count() > 0) {
                log.info("Database already populated. Skipping CSV import.");
                return;
            }

            log.info("Populating database from CSV file: {}", CSV_FILE_PATH);
            List<MovieEntity> moviesToSave = new ArrayList<>();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ClassPathResource(CSV_FILE_PATH).getInputStream()))) {

                reader.lines().skip(1).forEach(line -> {
                    String[] fields = line.split(CSV_DELIMITER, -1);
                    if (fields.length >= 5) {
                        try {
                            Integer year = Integer.parseInt(fields[0]);
                            String title = fields[1];
                            String studios = fields[2];
                            String producers = fields[3];
                            boolean winner = fields.length > 4 && "yes".equalsIgnoreCase(fields[4].trim());

                            moviesToSave.add(new MovieEntity(year, title, studios, producers, winner));
                        } catch (NumberFormatException e) {
                            log.warn("Skipping malformed line in CSV: {}", line);
                        }
                    }
                });

                movieRepository.saveAll(moviesToSave);
                log.info("Successfully loaded {} movies into the database.", moviesToSave.size());

            } catch (Exception e) {
                log.error("Failed to initialize database with CSV.", e);
                throw new RuntimeException("Failed to initialize database with CSV", e);
            }
        };
    }
}
