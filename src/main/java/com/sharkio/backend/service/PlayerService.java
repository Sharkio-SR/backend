package com.sharkio.backend.service;

import com.sharkio.backend.model.Player;
import com.sharkio.backend.repository.PlayerRepository;
import lombok.Data;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Data
@Service
public class PlayerService {
    @Autowired
    private PlayerRepository repository;


    public Iterable<Player> getPlayers() {
        return this.repository.findAll();
    }

    public Player getById(Integer id) {
        return this.repository.findById(id.longValue()).orElseThrow(() ->
                new RuntimeException("Player with id " + id + " not found"));
    }

    public Player addPlayer(Player player) {
        return this.repository.save(player);
    }
}
