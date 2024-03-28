package softuni.exam.domain.entities;

import javax.persistence.*;

@Entity
@Table(name = "pictures")
public class Picture extends BaseEntity{
@Column(name = "url",nullable = false)
    private String url;

    public Picture() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
