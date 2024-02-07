package com.sharkio.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="Food")
public class Food {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="pos_x")
    private float pos_x;

    @Column(name="pos_y")
    private float pos_y;
}
