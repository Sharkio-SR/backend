package com.sharkio.backend.service;

import com.sharkio.backend.model.Food;
import com.sharkio.backend.model.Mine;
import com.sharkio.backend.model.Player;
import com.sharkio.backend.model.World;
import com.sharkio.backend.enums.WorldState;
import com.sharkio.backend.repository.WorldRepository;

import lombok.Data;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@Data
@Service
public class WorldService {
    /******************************************************************************************************************/
    /*                                                 ATTRIBUTES                                                     */
    /******************************************************************************************************************/
    // Global variable of the world
    private final float X_DIM = 600;
    private final float Y_DIM = 600;
    private final Integer NB_FOODS = 20;
    private final Integer NB_MINES = 10;
    private final float REQUIRED_SPAWN_AREA = 60.00f;
    private final int MAX_JOIN_TRIES = 200;

    // Autowired components
    @Autowired
    private WorldRepository repository;
    @Autowired
    private PlayerService playerService;
    @Autowired
    private FoodService foodService;
    @Autowired
    private MineService mineService;

    /******************************************************************************************************************/
    /*                                                 MAIN FUNCTIONS                                                 */
    /******************************************************************************************************************/
    public World getWorld() {
        // Look if a World already exists, if yes => return it, otherwise create and save it
        // Singleton in database mocking strategy
        Iterable<World> worlds = this.repository.findAll();
        if(!worlds.iterator().hasNext()) {
            return initWorld();
        }
        return this.repository.findAll().iterator().next();
    }

    public Boolean getState() {
        return this.getWorld().getState() == WorldState.RUNNING;
    }

    public Player join(String name) {
        if(this.getWorld().getState()==WorldState.FINISHED) {
            this.reset(this.getWorld());
        }

        World world = this.getWorld();
        Random random  = new Random();

        // Create new random coordinates
        float newX = random.nextFloat()* world.getX_dim();
        float newY = random.nextFloat()* world.getY_dim();
        int iter = 0;
        if(checkIfSpawnIsIncorrect(newX, newY)) {
            throw new RuntimeException("Can't join, there are too many sharks in the sea");
        } else {
            // while the coordinates are invalid => get new values
            while (checkIfSpawnIsIncorrect(newX, newY) && iter<this.MAX_JOIN_TRIES) {
                iter ++;
                newX = random.nextFloat()* world.getX_dim();
                newY = random.nextFloat()* world.getY_dim();
            }
            if(iter==this.MAX_JOIN_TRIES-1) {
                throw new RuntimeException("Can't join, there are too many sharks in the sea");
            }
            Player new_player = new Player();
            new_player.setName(name);

            new_player.setPos_x(newX);
            new_player.setPos_y(newY);

            // Save player and add it in the player set of the world
            Player saved_player = this.playerService.addPlayer(new_player);
            Set<Player> players  = world.getPlayers();
            players.add(saved_player);
            world.setPlayers(players);
            this.repository.save(world);

            return saved_player;
        }
    }

    /******************************************************************************************************************/
    /*                                               PRIVATE FUNCTIONS                                                */
    /******************************************************************************************************************/
    private World initWorld() {
        // Create new world
        Random random  = new Random();
        World world = new World();
        world.setX_dim(this.X_DIM);
        world.setY_dim(this.Y_DIM);
        world.setPlayers(new HashSet<>());
        world.setState(WorldState.RUNNING);

        // Fill code with fishes
        Set<Food> foods = new HashSet<>();
        for(int i=0; i<this.NB_FOODS; i++) {
            Food f = new Food();
            f.setPos_x(random.nextFloat()* world.getX_dim());
            f.setPos_y(random.nextFloat()* world.getY_dim());
            this.foodService.addFood(f);
            foods.add(f);
        }

        // Fill world with mines
        Set<Mine> mines = new HashSet<>();
        for(int i=0; i<this.NB_MINES; i++) {
            Mine m = new Mine();
            m.setPos_x(random.nextFloat()* world.getX_dim());
            m.setPos_y(random.nextFloat()* world.getY_dim());
            this.mineService.addMine(m);
            mines.add(m);
        }

        // Save in repository
        world.setFoods(foods);
        world.setMines(mines);

        this.repository.save(world);

        return world;
    }

    private void reset(World world) {
        // Create lists to get living entities ids
        List<Integer> foodIds = new ArrayList<>();
        List<Integer> playerIds = new ArrayList<>();
        List<Integer> minesIds = new ArrayList<>();

        // Collect ids
        for (Food food : world.getFoods()) { foodIds.add(food.getId()); }
        for (Player player : world.getPlayers()) { playerIds.add(player.getId()); }
        for (Mine mine : world.getMines()) { minesIds.add(mine.getId()); }

        // Remove associations
        for(Integer id: foodIds) {this.foodService.delete(id);}
        for(Integer id: playerIds) {this.playerService.delete(id);}
        for(Integer id: minesIds) {this.mineService.delete(id);}

        this.repository.delete(world);
    }

    private boolean checkIfSpawnIsIncorrect(float newX, float newY) {
        // Compute distance for each player, compute distance
        for(Player p : getWorld().getPlayers()) {
            float distance = (float) Math.sqrt(Math.pow(p.getPos_x()-newX,2)+Math.pow(p.getPos_y()-newY,2));
            if(distance < this.REQUIRED_SPAWN_AREA) {
                return true;
            }
        }
        return false;
    }
}
