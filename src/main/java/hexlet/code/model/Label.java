package hexlet.code.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "labels")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Label implements BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Length(min = 3, max = 1000, message = "Length of label name must be between 3 and 1000")
    @Column(name = "name", unique = true)
    private String name;

    @ManyToMany(mappedBy = "labels")
    @Column(name = "tasks")
    private Set<Task> tasks = new HashSet<>();

    @CreatedDate
    @Column(name = "createdAt")
    private LocalDate createdAt;

    public Label() {
    }

    public Label(String name) {
        this.name = name;
    }
}
