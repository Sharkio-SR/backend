package com.sharkio.backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Data
@Entity
@Table(name="World")
public class World {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="x_dim")
    private float x_dim;

    @Column(name="y_dim")
    private float y_dim;

    @ManyToMany
    @JoinTable(name = "World_Player",
    joinColumns = @JoinColumn(name = "world_id"),
    inverseJoinColumns = @JoinColumn(name = "player_id"))
    private Set<Player> players;
}
