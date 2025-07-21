package com.renatoschlogel.razziesapi.api.dtos;

import lombok.Builder;

@Builder
public record WorstMovieProducerIntervalDto(
        String producer,
        Integer interval,
        Integer previousWin,
        Integer followingWin
) {}
