package com.akuev.repository;

import com.akuev.model.MovieSessionRedis;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieSessionRedisRepository extends CrudRepository<MovieSessionRedis, Long> {
}
