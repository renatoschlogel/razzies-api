package com.renatoschlogel.razziesapi.infra.db;

import com.renatoschlogel.razziesapi.domain.model.WinningMovie;
import com.renatoschlogel.razziesapi.domain.port.WinningMovieRepositoryPort;
import com.renatoschlogel.razziesapi.infra.db.entities.MovieEntity;
import com.renatoschlogel.razziesapi.infra.db.repositories.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class WinningMovieRepositoryAdapter implements WinningMovieRepositoryPort {

    private final MovieRepository movieRepository;

    @Override
    public List<WinningMovie> findAll() {
        return movieRepository.findByWinnerTrueOrderByYearAsc()
                .stream()
                .map(this::convertToDomain)
                .toList();
    }

    private WinningMovie convertToDomain(MovieEntity movieEntity) {
        return new WinningMovie(
                movieEntity.getYear(),
                movieEntity.getTitle(),
                parseProducers(movieEntity.getProducers())
        );
    }

    private static List<String> parseProducers(String producers) {
        String cleanedProducers = producers.trim();
        if (cleanedProducers.startsWith("\"") && cleanedProducers.endsWith("\"")) {
            cleanedProducers = cleanedProducers.substring(1, cleanedProducers.length() - 1);
        }

        if (cleanedProducers.isEmpty()) {
            return List.of();
        }

        String[] producerNames = cleanedProducers.split(",|\\s+and\\s+");
        return Arrays.stream(producerNames)
                .map(String::trim)
                .filter(name -> !name.isEmpty())
                .toList();
    }
}
