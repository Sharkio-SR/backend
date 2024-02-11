package com.sharkio.backend.repository;

import com.sharkio.backend.model.Mine;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MineRepository  extends CrudRepository<Mine, Integer> {
}
