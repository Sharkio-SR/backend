package com.sharkio.backend.repository;

import com.sharkio.backend.model.World;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorldRepository extends CrudRepository<World, Long> {
}
