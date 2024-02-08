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

    @GetMapping("/{id}")
    public Player getPlayerById(@PathVariable Integer id) {
        return this.service.getById(id);
    }

    @DeleteMapping("/{id}")
    public Player deletePlayer(@PathVariable Integer id) {
     return this.service.delete(id);
    }

    @PutMapping("/{id}/move")
    public Player move(@PathVariable Integer id, @RequestParam float newX, @RequestParam float newY, @RequestParam float dt) {
        return this.service.move(id, newX, newY, dt);
    }
}
