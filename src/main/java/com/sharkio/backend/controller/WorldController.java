package com.sharkio.backend.controller;

import com.sharkio.backend.model.Player;
import com.sharkio.backend.model.World;
import com.sharkio.backend.service.WorldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/join")
    public Player join(@RequestParam String name) {
        return this.service.join(name);
    }
}
