package com.sharkio.backend.service;

import com.sharkio.backend.model.Player;
import com.sharkio.backend.model.World;
import com.sharkio.backend.repository.WorldRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Data
@Service
public class WorldService {
    @Autowired
    private WorldRepository repository;

    @Autowired
    private PlayerService playerService;


    public World getWorld() {
        // Look if a World already exists, if yes => return it, otherwise create and save it
        // Singleton in database mocking strategy
        Iterable<World> worlds = this.repository.findAll();
        if(!worlds.iterator().hasNext()) {
            World world = new World();
            world.setX_dim(600);
            world.setY_dim(800);
            world.setPlayers(new HashSet<>());
            this.repository.save(world);
            return world;
        }
        return this.repository.findAll().iterator().next();
    }

    public Player join() {
        World world = this.getWorld();
        Random random  = new Random();

        // Create new player with random coordinates
        Player new_player = new Player();
        new_player.setPos_x(random.nextFloat()* world.getX_dim());
        new_player.setPos_y(random.nextFloat()* world.getY_dim());

        // Save player and add it in the player set of the world
        Player saved_player = this.playerService.addPlayer(new_player);
        Set<Player> players  = world.getPlayers();
        players.add(saved_player);
        world.setPlayers(players);

        this.repository.save(world);

        return saved_player;
    }
}
