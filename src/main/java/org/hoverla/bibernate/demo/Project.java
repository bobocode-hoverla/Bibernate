package org.hoverla.bibernate.demo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hoverla.bibernate.annotation.*;

@ToString
@NoArgsConstructor
@Entity
@Getter
@Table(name = "project")
public class Project {
    @Id
    private Integer id;

    @Setter
    @Column(name = "project_name")
    private String projectName;

    @Column(name = "customer_id")
    @ManyToOne
    private Customer customer;
}
