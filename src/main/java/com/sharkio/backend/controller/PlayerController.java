package com.sharkio.backend.controller;

import com.sharkio.backend.model.Player;
import com.sharkio.backend.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/player")
public class PlayerController {
    @Autowired
    private PlayerService service;

    @GetMapping("")
    public Iterable<Player> getPlayers() {
        return this.service.getPlayers();
    }
}
