package com.sharkio.backend.model;

import com.sharkio.backend.enums.WorldState;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Data
@Entity
@Table(name="World")
public class World {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="x_dim")
    private float x_dim;

    @Column(name="y_dim")
    private float y_dim;

    @Enumerated(EnumType.STRING)
    @Column(name="state")
    private WorldState state;

    @ManyToMany
    @JoinTable(name = "World_Player",
    joinColumns = @JoinColumn(name = "world_id"),
    inverseJoinColumns = @JoinColumn(name = "player_id"))
    private Set<Player> players;

    @ManyToMany
    @JoinTable(name = "World_Food",
            joinColumns = @JoinColumn(name = "world_id"),
            inverseJoinColumns = @JoinColumn(name = "food_id"))
    private Set<Food> foods;
}
