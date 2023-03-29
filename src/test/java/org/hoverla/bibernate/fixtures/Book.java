package org.hoverla.bibernate.fixtures;

import lombok.*;
import org.hoverla.bibernate.annotation.Column;
import org.hoverla.bibernate.annotation.Entity;
import org.hoverla.bibernate.annotation.Id;
import org.hoverla.bibernate.annotation.Table;

@Entity
@Table(name = "book")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Book {
    @Id
    private int id;
    @Column(name = "title")
    private String title;
}
