package org.hoverla.bibernate.demo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hoverla.bibernate.annotation.*;
import org.hoverla.bibernate.collection.LazyList;

import java.time.LocalDateTime;
import java.util.List;

@ToString
@Setter
@Getter
@Entity
@Table(name = "customer")
public class Customer {
    @Id
    private Integer id;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "email")
    private String email;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @OneToMany
    @Column
    private List<Project> projects;
}
