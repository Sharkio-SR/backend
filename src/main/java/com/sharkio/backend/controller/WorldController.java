package com.sharkio.backend.controller;

import com.sharkio.backend.model.Player;
import com.sharkio.backend.model.World;
import com.sharkio.backend.service.WorldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/world")
public class WorldController {
    @Autowired
    private WorldService service;

    @GetMapping("")
    public World getWorld() {
        return this.service.getWorld();
    }

    @GetMapping("/join")
    public Player join() {
        return this.service.join();
    }
}
