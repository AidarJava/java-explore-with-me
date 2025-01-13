package ru.practicum.explore.participation.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Table(name = "participation")
@Entity
public class Participation {
    LocalDateTime created = LocalDateTime.now();
    Integer event;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    Integer requester;
    String status;
}
