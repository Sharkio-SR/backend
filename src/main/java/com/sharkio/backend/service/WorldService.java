package com.sharkio.backend.service;

import com.sharkio.backend.model.Food;
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
    private final float X_DIM = 600;
    private final float Y_DIM = 600;
    private final Integer NB_FOODS = 10;

    @Autowired
    private WorldRepository repository;

    @Autowired
    private PlayerService playerService;
    @Autowired
    private FoodService foodService;

    private World initWorld() {
        Random random  = new Random();
        World world = new World();
        world.setX_dim(this.X_DIM);
        world.setY_dim(this.Y_DIM);
        world.setPlayers(new HashSet<>());

        Set<Food> foods = new HashSet<>();
        for(int i=0; i<this.NB_FOODS; i++) {
            Food f = new Food();
            f.setPos_x(random.nextFloat()* world.getX_dim());
            f.setPos_y(random.nextFloat()* world.getY_dim());
            this.foodService.addFood(f);
            foods.add(f);
        }
        world.setFoods(foods);

        this.repository.save(world);

        return world;
    }

    public World getWorld() {
        // Look if a World already exists, if yes => return it, otherwise create and save it
        // Singleton in database mocking strategy
        Iterable<World> worlds = this.repository.findAll();
        if(!worlds.iterator().hasNext()) {
            return initWorld();
        }
        return this.repository.findAll().iterator().next();
    }

    public Player join(String name) {
        World world = this.getWorld();
        Random random  = new Random();

        // Create new player with random coordinates
        Player new_player = new Player();
        new_player.setName(name);
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
