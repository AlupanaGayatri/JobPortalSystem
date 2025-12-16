package com.jobportal.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String level;
    private String duration; // e.g., "6 months", "2 years"
    private String experienceType; // "Theoretical", "Working"

    @ManyToOne
    private User user;
}
