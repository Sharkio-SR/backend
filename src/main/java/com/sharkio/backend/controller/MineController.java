package com.sharkio.backend.controller;

import com.sharkio.backend.model.Food;
import com.sharkio.backend.model.Mine;
import com.sharkio.backend.service.FoodService;
import com.sharkio.backend.service.MineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mine")
public class MineController {
    @Autowired
    private MineService service;

    @GetMapping("")
    public Iterable<Mine> getFoods() {
        return this.service.getMines();
    }

    @GetMapping("/{id}")
    public Mine getFoodById(@PathVariable Integer id) {
        return this.service.getById(id);
    }
}
