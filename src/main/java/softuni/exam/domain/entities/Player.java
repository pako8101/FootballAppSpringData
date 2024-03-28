package softuni.exam.domain.entities;

import javax.persistence.*;
import softuni.exam.domain.enums.Position;

import java.math.BigDecimal;

@Table(name = "players")
@Entity
public class Player extends BaseEntity{

    @Column(name = "first_name",nullable = false)
    private String firstName;
    @Column(name = "last_name",nullable = false)
    private String lastName;

    @Column(name = "number",nullable = false)
    private Integer number;
    @Column(nullable = false)
    private BigDecimal salary;
    @Enumerated(EnumType.STRING)
    private Position position;
    @ManyToOne(cascade = CascadeType.ALL)
    private Picture picture;
    @ManyToOne(cascade = CascadeType.ALL)
    private Team team;

    public Player() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Picture getPicture() {
        return picture;
    }

    public void setPicture(Picture picture) {
        this.picture = picture;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}
