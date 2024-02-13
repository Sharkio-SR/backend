package com.sharkio.backend.service;

import com.sharkio.backend.enums.WorldState;
import com.sharkio.backend.model.Food;
import com.sharkio.backend.model.Mine;
import com.sharkio.backend.model.Player;
import com.sharkio.backend.model.World;
import com.sharkio.backend.repository.PlayerRepository;
import com.sharkio.backend.repository.WorldRepository;

import lombok.Data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Data
@Service
public class PlayerService {
    /******************************************************************************************************************/
    /*                                                 ATTRIBUTES                                                     */
    /******************************************************************************************************************/
    private final int PENALTY = 2;
    private final int SCORE_POINTS = 1;
    private final float MOVE_RANGE = 4;
    private final int IMG_MID_WITH = 30;
    private final int IMG_MID_HEIGHT = 17;
    private final double EATING_RANGE = 10;
    private final float PLAYER_HITBOX_RANGE = 27;
    private final List<String> MOVE_ERRORS = new ArrayList<>(
            List.of("New coordinates are out of bound",
                    "Invalid move, stop trying tp",
                    "Can't move here, there is a collision"));

    @Autowired
    private PlayerRepository repository;
    @Autowired
    private WorldRepository worldRepository;
    @Autowired
    private FoodService foodService;
    @Autowired
    private MineService mineService;

    /******************************************************************************************************************/
    /*                                                 MAIN FUNCTIONS                                                 */
    /******************************************************************************************************************/
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

    public Player move(int id, float newX, float newY) {
        // get player and world
        World world = this.worldRepository.findAll().iterator().next();

        // Validate movement
        if(world.getState() == WorldState.RUNNING) {
            Player player = this.getById(id);

            int validation = is_valid_move(player, world, newX, newY);

            // Collision
            if (validation == 3) {
                int possibilities = tryOtherMove(player, world, newX, newY);
                if (possibilities == 1) {
                    newY = player.getPos_y();
                } else if (possibilities == 0) {
                    newX = player.getPos_x();
                }
            }

            // Move
            validation = is_valid_move(player, world, newX, newY);
            if (validation == 0) {
                // Change value
                player.setPos_x(newX);
                player.setPos_y(newY);
                this.repository.save(player); // Save to prevent other player to move here while current player is eating

                // Check if player can eat something and perform action
                Set<Food> foods = world.getFoods();
                if (!foods.isEmpty()) {
                    this.eat(player, foods);
                }

                // Check if player eat a mine and perform explosion
                Set<Mine> mines = world.getMines();
                if(!mines.isEmpty()) {
                    this.explode(player, mines);
                }

                if (player.getScore() >= 50) {
                    world.setState(WorldState.FINISHED);
                    worldRepository.save(world);
                }

                return this.getById(id);
            }
            throw new RuntimeException("Move invalid : " + this.MOVE_ERRORS.get(validation - 1));
        }
        throw new RuntimeException("No current game running");
    }

    /******************************************************************************************************************/
    /*                                               PRIVATE FUNCTIONS                                                */
    /******************************************************************************************************************/
    private int is_valid_move(Player player, World world, float newX, float newY) {
        // Assert coordinates are valid
        if(0 > newX || newX > world.getX_dim() || 0 > newY || newY > world.getY_dim()) {
            return 1;
        }

        // Assert no teleport
        if((player.getPos_x()-this.MOVE_RANGE) > newX && newX > (player.getPos_x()+this.MOVE_RANGE) &&
                (player.getPos_y()-this.MOVE_RANGE) > newY && newY > (player.getPos_y()+this.MOVE_RANGE)) {
            return 2;
        }

        // Check for collisions
        for(Player p : world.getPlayers()) {
            if(! p.getId().equals(player.getId())) {
                if(checkIfCollision(p, newX, newY)) {
                    return 3;
                }
            }
        }
        return 0;
    }

    private boolean checkIfCollision(Player p, float newX, float newY) {
        // Use rectangle intersection method from java geometry lib
        Rectangle2D rect1 = new Rectangle2D.Float(newX- getIMG_MID_WITH(), newY-
                getIMG_MID_HEIGHT(), getIMG_MID_WITH()*2, getIMG_MID_HEIGHT()*2);
        Rectangle2D rect2 = new Rectangle2D.Float(p.getPos_x()- getIMG_MID_WITH(), p.getPos_y()-
                getIMG_MID_HEIGHT(), getIMG_MID_WITH()*2, getIMG_MID_HEIGHT()*2);

        return rect1.intersects(rect2);
    }

    private int tryOtherMove(Player player, World world, float newX, float newY) {
        // Try move only on X or on Y => return axis
        if(is_valid_move(player, world, newX, player.getPos_y())==0) {
            return 1;
        } else if (is_valid_move(player, world, player.getPos_x(), newY)==0) {
            return 0;
        } // if both are invalid => return -1s
        return -1;
    }

    private void eat(Player player, Set<Food> foods) {
        // Store food ids in range
        List<Integer> idsFoodsToRemove = new ArrayList<>();

        for (Food f : foods) {
            if (computeDistance(player.getPos_x(), player.getPos_y(), f.getPos_x(), f.getPos_y()) < EATING_RANGE) {
                idsFoodsToRemove.add(f.getId());
            }
        }

        // remove all food in eating range
        for (int idFood : idsFoodsToRemove) {
            this.foodService.delete(idFood);
            int newScore = player.getScore() + this.SCORE_POINTS;
            player.setScore(newScore);

            if (Math.random()<0.8) {
                this.respawnFish();
            } else {
                this.respawnMine();
            }
        }
        // save in repository
        this.repository.save(player);
    }

    private void explode(Player player, Set<Mine> mines) {
        // Store mines in range
        List<Integer> idsMinesToRemove = new ArrayList<>();

        for (Mine m : mines) {
            if (computeDistance(player.getPos_x(), player.getPos_y(), m.getPos_x(), m.getPos_y()) < EATING_RANGE) {
                idsMinesToRemove.add(m.getId());
            }
        }

        // remove all mines in eating range
        for (int idMine : idsMinesToRemove) {
            this.mineService.delete(idMine);
            int newScore = player.getScore() - this.PENALTY;
            player.setScore(newScore);

            if (Math.random()<0.2) {
                this.respawnFish();
            } else {
                this.respawnMine();
            }
        }
        // save in repository
        this.repository.save(player);
    }

    private void respawnMine() {
        Random random  = new Random();
        World world = worldRepository.findAll().iterator().next();
        // Create new mine
        Mine mine = new Mine();

        // Init in random position
        mine.setPos_x(random.nextFloat()* world.getX_dim());
        mine.setPos_y(random.nextFloat()* world.getY_dim());
        mineService.addMine(mine);

        // Associate and save
        Set<Mine> mines = world.getMines();
        mines.add(mine);
        world.setMines(mines);
    }

    private void respawnFish() {
        Random random  = new Random();
        World world = worldRepository.findAll().iterator().next();
        // Create new food
        Food food = new Food();

        // Init in random position
        food.setPos_x(random.nextFloat()* world.getX_dim());
        food.setPos_y(random.nextFloat()* world.getY_dim());
        foodService.addFood(food);

        // Associate and save
        Set<Food> foods = world.getFoods();
        foods.add(food);
        worldRepository.save(world);
    }

    private float computeDistance(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2));
    }
}
