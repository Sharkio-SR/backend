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
        Iterable<World> worlds = this.repository.findAll();
        if(!worlds.iterator().hasNext()) {
            World world = new World();
            world.setX_dim(600);
            world.setY_dim(800);
            world.setPlayers(new HashSet<Player>());
            this.repository.save(world);
        }
        return this.repository.findAll().iterator().next();
    }

    public Player join() {
        World world = this.getWorld();
        Random random  = new Random();

        Player new_player = new Player();
        new_player.setPos_x(random.nextFloat()* world.getX_dim());
        new_player.setPos_y(random.nextFloat()* world.getY_dim());

        Player saved_player = this.playerService.addPlayer(new_player);
        Set<Player> players  = world.getPlayers();
        players.add(saved_player);
        world.setPlayers(players);
        this.repository.save(world);

        return saved_player;
    }
}
