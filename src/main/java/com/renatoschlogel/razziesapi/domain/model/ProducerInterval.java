package com.renatoschlogel.razziesapi.domain.model;

public record ProducerInterval(
        String producer,
        Integer interval,
        Integer previousWin,
        Integer followingWin
) {}
