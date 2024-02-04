package com.sharkio.backend.service;

import com.sharkio.backend.model.Player;
import com.sharkio.backend.model.World;
import com.sharkio.backend.repository.PlayerRepository;
import com.sharkio.backend.repository.WorldRepository;
import lombok.Data;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
        // return user if exists or throw exception
        return this.repository.findById(id).orElseThrow(() ->
                new RuntimeException("Player with id " + id + " not found"));
    }

    public Player addPlayer(Player player) {
        // Assert no null player are added
        if(player != null) {
            // Assert player is not already in the game
            for (Player value : this.getPlayers()) {
                if (value.equals(player)) {
                    throw new RuntimeException("Not allowed to add existing player");
                }
            }
            // Save player
            return this.repository.save(player);
        }
        throw new RuntimeException("Not allowed to add null player");
    }

    public Player delete(Integer id) {
        Player player = this.getById(id);

        // Remove player from world first to avoid error
        World world = this.worldRepository.findAll().iterator().next();
        Set<Player> players = world.getPlayers();
        players.remove(player);

        // update in database
        this.worldRepository.save(world);
        this.repository.delete(player);

        return player;
    }

    public Player move(Integer id, float newX, float newY) {
        // get player and world
        Player player = this.getById(id);
        World world = this.worldRepository.findAll().iterator().next();

        // Assert coordinates are valid
        if(0 > newX || newX > world.getX_dim() || 0 > newY || newY > world.getY_dim()) {
         throw new RuntimeException("New coordinates are out of bound");
        }


        // Act and save
        player.setPos_x(newX);
        player.setPos_y(newY);
        this.repository.save(player);

        return this.getById(id);
    }
}
