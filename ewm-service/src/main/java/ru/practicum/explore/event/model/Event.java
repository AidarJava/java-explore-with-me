package ru.practicum.explore.event.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@Table(name = "events")
@Entity
@ToString
public class Event {
    @NotBlank
    String annotation;
    @NotNull
    Integer category;
    @Column(name = "created_on")
    LocalDateTime createdOn = LocalDateTime.now();
    String description;
    @NotNull
    @Column(name = "event_date")
    LocalDateTime eventDate;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    @NotNull
    Integer initiator;
    @NotNull
    @Column(name = "location_lat")
    Float locationLat;
    @NotNull
    @Column(name = "location_lon")
    Float locationLon;
    @NotNull
    Boolean paid;
    @Column(name = "participant_limit")
    Integer participantLimit;
    @Column(name = "published_on")
    LocalDateTime publishedOn;
    @Column(name = "request_moderation")
    Boolean requestModeration;
    String state;
    @NotBlank
    String title;
}
