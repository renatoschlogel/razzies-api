package com.renatoschlogel.razziesapi.domain.model;

import java.util.List;

public record WinningMovie(int year, String title, List<String> producers) { }