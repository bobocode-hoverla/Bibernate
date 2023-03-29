package org.hoverla.bibernate.demo;

import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hoverla.bibernate.annotation.Column;
import org.hoverla.bibernate.annotation.Entity;
import org.hoverla.bibernate.annotation.Id;
import org.hoverla.bibernate.annotation.Table;

import java.time.LocalDateTime;

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
}
