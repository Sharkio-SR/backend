package com.sharkio.backend.service;

import com.sharkio.backend.model.Food;
import com.sharkio.backend.model.World;
import com.sharkio.backend.repository.FoodRepository;
import com.sharkio.backend.repository.WorldRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Data
@Service
public class FoodService {
    @Autowired
    private FoodRepository repository;
    @Autowired
    private WorldRepository worldRepository;


    public Iterable<Food> getFoods() {
        return this.repository.findAll();
    }

    public Food getById(Integer id) {
        // return food if exists or throw exception
        return this.repository.findById(id).orElseThrow(() ->
                new RuntimeException("Food with id " + id + " not found"));
    }

    public Food addFood(Food food) {
        // Assert no null food are added
        if(food != null) {
            // Assert food is not already in the game
            for (Food value : this.getFoods()) {
                if (value.equals(food)) {
                    throw new RuntimeException("Not allowed to add existing food");
                }
            }
            // Save food
            return this.repository.save(food);
        }
        throw new RuntimeException("Not allowed to add null food");
    }

    public Food delete(Integer id) {
        Food food = this.getById(id);

        // Remove food from world first to avoid error
        World world = this.worldRepository.findAll().iterator().next();
        Set<Food> foods = world.getFoods();
        foods.remove(food);

        // update in database
        this.worldRepository.save(world);
        this.repository.delete(food);

        return food;
    }
}
