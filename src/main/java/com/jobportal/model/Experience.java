package com.jobportal.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Experience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String company;
    private String role;
    private String duration;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    private User user;
}
