package org.hoverla.bibernate.fixtures;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hoverla.bibernate.annotation.Column;
import org.hoverla.bibernate.annotation.Entity;
import org.hoverla.bibernate.annotation.Id;
import org.hoverla.bibernate.annotation.Table;

@Entity
@Table(name = "person")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Person {
    @Id
    private int id;
    @Column(name = "name")
    private String name;
    @Column(name = "age")
    private int age;
}
