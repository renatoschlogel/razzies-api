package com.renatoschlogel.razziesapi.api.controllers;

import com.renatoschlogel.razziesapi.api.dtos.WorstMovieProducerIntervalDto;
import com.renatoschlogel.razziesapi.api.dtos.WorstMovieProducersIntervalsResponseDto;
import com.renatoschlogel.razziesapi.domain.model.AwardIntervalsResult;
import com.renatoschlogel.razziesapi.domain.model.ProducerInterval;
import com.renatoschlogel.razziesapi.domain.usecases.GetProducersWithAwardIntervals;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/worst-movies")
public class WorstMoviesController {

    private final GetProducersWithAwardIntervals getProducersWithAwardIntervals;

    @GetMapping("/producers-intervals")
    public ResponseEntity<WorstMovieProducersIntervalsResponseDto> producersAwardsIntervals() {
        AwardIntervalsResult awardIntervalsResult = getProducersWithAwardIntervals.execute();

        List<WorstMovieProducerIntervalDto> minProducersDto = awardIntervalsResult.min().stream()
                .map(this::convertToDto)
                .toList();
        List<WorstMovieProducerIntervalDto> maxProducersDto = awardIntervalsResult.max().stream()
                .map(this::convertToDto)
                .toList();
        WorstMovieProducersIntervalsResponseDto response = WorstMovieProducersIntervalsResponseDto.builder()
                .min(minProducersDto)
                .max(maxProducersDto)
                .build();

        return ResponseEntity.ok(response);
    }

    private WorstMovieProducerIntervalDto convertToDto(ProducerInterval domain) {
        return WorstMovieProducerIntervalDto.builder()
                .producer(domain.producer())
                .interval(domain.interval())
                .previousWin(domain.previousWin())
                .followingWin(domain.followingWin())
                .build();
    }
}
