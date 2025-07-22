package com.renatoschlogel.razziesapi.infra.db.repositories;

import com.renatoschlogel.razziesapi.infra.db.entities.MovieEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<MovieEntity, Long> {
    List<MovieEntity> findByWinnerTrueOrderByYearAsc();
}
