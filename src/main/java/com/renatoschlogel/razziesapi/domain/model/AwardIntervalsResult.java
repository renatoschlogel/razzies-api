package com.renatoschlogel.razziesapi.domain.model;

import java.util.List;

public record AwardIntervalsResult(
        List<ProducerInterval> min,
        List<ProducerInterval> max
) {}
