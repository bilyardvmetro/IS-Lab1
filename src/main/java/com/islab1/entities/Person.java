package com.islab1.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "person")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически

    @NotBlank
    @Column(name = "name", nullable = false)
    private String name; //Поле не может быть null, Строка не может быть пустой

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coordinates", nullable = false)
    private Coordinates coordinates; //Поле не может быть null

    @Column(name = "creation_date", nullable = false)
    private java.time.LocalDateTime creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически

    @Enumerated(EnumType.STRING)
    @Column(name = "eye_color", nullable = false)
    private Color eyeColor; //Поле не может быть null

    @Enumerated(EnumType.STRING)
    @Column(name = "hair_color", nullable = false)
    private Color hairColor; //Поле не может быть null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location; //Поле может быть null

    @Min(1)
    @Column(name = "weight")
    private Integer weight; //Поле может быть null, Значение поля должно быть больше 0

    @Min(1)
    @Column(name = "height")
    private long height; //Значение поля должно быть больше 0

    @Column(name = "passport_id")
    private String passportID; //Поле может быть null

    @Enumerated(EnumType.STRING)
    @Column(name = "nationality", nullable = false)
    private Country nationality; //Поле не может быть null

    @PrePersist
    protected void onCreate() {
        this.creationDate = LocalDateTime.now();
    }
}