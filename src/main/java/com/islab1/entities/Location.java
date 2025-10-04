package com.islab1.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "location")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "x", nullable = false)
    private Double x; //Поле не может быть null

    @Column(name = "y")
    private float y;

    @Column(name = "z")
    private double z;

    @Column(name = "name", nullable = false)
    private String name; //Поле не может быть null
}
