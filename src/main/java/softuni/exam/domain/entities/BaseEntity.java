package softuni.exam.domain.entities;

import javax.persistence.*;
import org.springframework.boot.autoconfigure.web.ResourceProperties;

@MappedSuperclass
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
