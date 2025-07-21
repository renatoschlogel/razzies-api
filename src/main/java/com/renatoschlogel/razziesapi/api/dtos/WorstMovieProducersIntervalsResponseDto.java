package com.renatoschlogel.razziesapi.api.dtos;

import lombok.Builder;

import java.util.List;
@Builder
public record WorstMovieProducersIntervalsResponseDto(
        List<WorstMovieProducerIntervalDto> min,
        List<WorstMovieProducerIntervalDto> max
) {}
