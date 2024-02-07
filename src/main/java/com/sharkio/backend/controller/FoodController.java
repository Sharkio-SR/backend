package com.sharkio.backend.controller;

import com.sharkio.backend.model.Food;
import com.sharkio.backend.service.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/food")
public class FoodController {
    @Autowired
    private FoodService service;

    @GetMapping("")
    public Iterable<Food> getFoods() {
        return this.service.getFoods();
    }

    @GetMapping("/{id}")
    public Food getFoodById(@PathVariable Integer id) {
        return this.service.getById(id);
    }
}
