package com.renatoschlogel.razziesapi.domain.usecases;

import com.renatoschlogel.razziesapi.domain.model.AwardIntervalsResult;
import com.renatoschlogel.razziesapi.domain.model.ProducerInterval;
import com.renatoschlogel.razziesapi.domain.model.WinningMovie;
import com.renatoschlogel.razziesapi.domain.port.WinningMovieRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class GetProducersWithAwardIntervalsUseCase implements GetProducersWithAwardIntervals {

    private final WinningMovieRepositoryPort winningMovieRepositoryPort;

    public AwardIntervalsResult execute() {
        List<WinningMovie> winningMovies = winningMovieRepositoryPort.findAll();
        Map<String, List<Integer>> yearsByProducer = groupYearsByProducer(winningMovies);
        List<ProducerInterval> intervalsBetweenProducerWins = calculateIntervalsBetweenProducerWins(yearsByProducer);

        if (intervalsBetweenProducerWins.isEmpty()) {
            return new AwardIntervalsResult(List.of(), List.of());
        }

        int minInterval = intervalsBetweenProducerWins.stream()
                .mapToInt(ProducerInterval::interval)
                .min()
                .getAsInt();
        int maxInterval = intervalsBetweenProducerWins.stream()
                .mapToInt(ProducerInterval::interval)
                .max()
                .getAsInt();

        List<ProducerInterval> minProducers = filterIntervalsByValue(intervalsBetweenProducerWins, minInterval);
        List<ProducerInterval> maxProducers = filterIntervalsByValue(intervalsBetweenProducerWins, maxInterval);

        return new AwardIntervalsResult(minProducers, maxProducers);
    }

    private Map<String, List<Integer>> groupYearsByProducer(List<WinningMovie> winningMovies) {
        Map<String, List<Integer>> winsByProducer = new HashMap<>();
        for (WinningMovie movie : winningMovies) {
            for (String producer : movie.producers()) {
                winsByProducer.computeIfAbsent(producer, k -> new ArrayList<>()).add(movie.year());
            }
        }

        winsByProducer.values().forEach(Collections::sort);
        return winsByProducer;
    }

    private List<ProducerInterval> calculateIntervalsBetweenProducerWins(Map<String, List<Integer>> winsByProducer) {
        List<ProducerInterval> intervals = new ArrayList<>();
        for (Map.Entry<String, List<Integer>> entry : winsByProducer.entrySet()) {
            String producer = entry.getKey();
            List<Integer> years = entry.getValue();
            if (years.size() > 1) {
                for (int i = 0; i < years.size() - 1; i++) {
                    int previousWin = years.get(i);
                    int followingWin = years.get(i + 1);
                    int interval = followingWin - previousWin;
                    intervals.add(new ProducerInterval(producer, interval, previousWin, followingWin));
                }
            }
        }
        return intervals;
    }

    private List<ProducerInterval> filterIntervalsByValue(List<ProducerInterval> allIntervals, int intervalValue) {
        return allIntervals.stream()
                .filter(p -> p.interval() == intervalValue)
                .toList();
    }
}
