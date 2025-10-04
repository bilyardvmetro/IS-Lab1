package com.islab1.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "coordinates")
public class Coordinates {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "x", nullable = false)
    private Long x; //Поле не может быть null

    @Min(-803)
    @Column(name = "y", nullable = false)
    private Double y; //Значение поля должно быть больше -804, Поле не может быть null
}
