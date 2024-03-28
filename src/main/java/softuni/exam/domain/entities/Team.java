package softuni.exam.domain.entities;

import javax.persistence.*;

@Table
@Entity(name = "teams")
public class Team extends BaseEntity{
    @Column(name = "name")
    private  String name;
    @ManyToOne(cascade = CascadeType.ALL)
    private Picture picture;


    public Team() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Picture getPicture() {
        return picture;
    }

    public void setPicture(Picture picture) {
        this.picture = picture;
    }
}
