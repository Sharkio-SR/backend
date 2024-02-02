package com.sharkio.backend.service;

import com.sharkio.backend.model.Player;
import com.sharkio.backend.model.World;
import com.sharkio.backend.repository.PlayerRepository;
import com.sharkio.backend.repository.WorldRepository;
import lombok.Data;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Data
@Service
public class PlayerService {
    @Autowired
    private PlayerRepository repository;
    @Autowired
    private WorldRepository worldRepository;


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

    public Player move(Integer id, float newX, float newY) {
        Player player = this.getById(id);
        World world = this.worldRepository.findAll().iterator().next();

        if(0 > newX || newX > world.getX_dim() || 0 > newY || newY > world.getY_dim()) {
         throw new RuntimeException("New coordinates are out of bound");
        }
        player.setPos_x(newX);
        player.setPos_y(newY);
        this.repository.save(player);

        return this.getById(id);
    }
}
