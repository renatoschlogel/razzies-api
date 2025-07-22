package com.renatoschlogel.razziesapi.domain.port;

import com.renatoschlogel.razziesapi.domain.model.WinningMovie;

import java.util.List;

public interface WinningMovieRepositoryPort {
    List<WinningMovie> findAll();
}
