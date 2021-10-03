package xyz.pwmw.mynlife.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@NoArgsConstructor
@Entity
@Getter
@Setter
@AllArgsConstructor
public class Users {
    @Id
    int id;

    @Column(columnDefinition = "TEXT", nullable = false, unique = true)
    String email;

    @Column(columnDefinition = "TEXT", nullable = false)
    String password;

    @Column(columnDefinition = "VARCHAR(20)", nullable = false, unique = true)
    String name;
}
