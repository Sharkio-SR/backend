package com.sharkio.backend.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.sharkio.backend.model.Player;

@Repository
public interface PlayerRepository extends CrudRepository<Player, Long>{
}
