package org.hoverla.bibernate.demo;

import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hoverla.bibernate.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@ToString
@NoArgsConstructor
@Entity
@Table(name = "customer")
public class Customer {
    @Id
    private Integer id;
    @Setter
    @Column(name = "first_name")
    private String firstName;
    @Setter
    @Column(name = "last_name")
    private String lastName;
    @Setter
    @Column(name = "email")
    private String email;
    @Setter
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany
    private List<Project> projects;
}
