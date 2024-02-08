package com.sharkio.backend.service;

import com.sharkio.backend.model.Food;
import com.sharkio.backend.model.Player;
import com.sharkio.backend.model.World;
import com.sharkio.backend.repository.PlayerRepository;
import com.sharkio.backend.repository.WorldRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@Service
public class PlayerService {
    @Autowired
    private PlayerRepository repository;
    @Autowired
    private WorldRepository worldRepository;
    @Autowired
    private FoodService foodService;

    private final double EATING_RANGE = 10;
    private final int SCORE_POINTS = 1;
    private final float MOVE_CONSTRAINT = 300;


    public Iterable<Player> getPlayers() {
        return this.repository.findAll();
    }

    public Player getById(int id) {
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

    public Player delete(int id) {
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

    public Player move(int id, float newX, float newY, float dt) {
        // get player and world
        Player player = this.getById(id);
        World world = this.worldRepository.findAll().iterator().next();

        // Assert coordinates are valid
        if(0 > newX || newX > world.getX_dim() || 0 > newY || newY > world.getY_dim()) {
         throw new RuntimeException("New coordinates are out of bound");
        }

        // Assert move is valid
        float move_range = this.MOVE_CONSTRAINT * dt;
        if(!(player.getPos_x()-move_range <= newX && newX <= player.getPos_x()+move_range
                && player.getPos_y()-move_range <= newY && newY <= player.getPos_y()+move_range)) {
            throw new RuntimeException("Invalid move, stop trying tp");
        }

        // Change value
        player.setPos_x(newX);
        player.setPos_y(newY);
        this.repository.save(player); // Save to prevent other player to move here while current player is eating

        // Check if player can eat something and perform action
        Set<Food> foods = world.getFoods();
        if(!foods.isEmpty()) {
            this.eat(player, foods);
        }

        return this.getById(id);
    }

    private void eat(Player player, Set<Food> foods) {
        List<Integer> idsFoodsToRemove = new ArrayList<>();

        for (Food f : foods) {
            if (computeDistanceToFood(player, f) < EATING_RANGE) {
                idsFoodsToRemove.add(f.getId());
            }
        }

        // remove all food in eating range
        for (int idFood : idsFoodsToRemove) {
            this.foodService.delete(idFood);
            int newScore = player.getScore() + this.SCORE_POINTS;
            player.setScore(newScore);
        }
        // save in repository
        this.repository.save(player);
    }

    private double computeDistanceToFood(Player player, Food f) {
        return Math.sqrt(Math.pow((player.getPos_x()-f.getPos_x()),2)+Math.pow((player.getPos_y()-f.getPos_y()),2));
    }
}
