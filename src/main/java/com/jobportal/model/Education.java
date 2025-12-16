package com.jobportal.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Education {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String institution;
    private String course;
    private String branch;
    private String startYear;
    private String endYear;

    @ManyToOne
    private User user;
}
