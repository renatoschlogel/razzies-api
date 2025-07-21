package com.renatoschlogel.razziesapi.api.controllers;

import com.renatoschlogel.razziesapi.api.dtos.WorstMovieProducerIntervalDto;
import com.renatoschlogel.razziesapi.api.dtos.WorstMovieProducersIntervalsResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/worst-movies")
public class WorstMoviesController {

    @GetMapping("/producers-intervals")
    public ResponseEntity<WorstMovieProducersIntervalsResponseDto> producersAwardsIntervals() {

        WorstMovieProducerIntervalDto minProducer = WorstMovieProducerIntervalDto.builder()
                .producer("Renato 1")
                .interval(1)
                .previousWin(2000)
                .followingWin(2001)
                .build();

        WorstMovieProducerIntervalDto maxProducer = WorstMovieProducerIntervalDto.builder()
                .producer("Renato 2")
                .interval(10)
                .previousWin(1990)
                .followingWin(2000)
                .build();

        WorstMovieProducersIntervalsResponseDto response = WorstMovieProducersIntervalsResponseDto.builder()
                .min(List.of(minProducer))
                .max(List.of(maxProducer))
                .build();

        return ResponseEntity.ok(response);
    }
}
