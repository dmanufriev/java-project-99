package hexlet.code.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "task_statuses")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class TaskStatus implements BaseEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @NotNull
    @Length(min = 1, message = "Length of task status name must be greater than 1")
    private String name;

    @NotNull
    @Column(unique = true)
    @Length(min = 1, message = "Length of task status slug must be greater than 1")
    private String slug;

    @CreatedDate
    private LocalDate createdAt;

    public TaskStatus() {
    }

    public TaskStatus(String name, String slug) {
        this.name = name;
        this.slug = slug;
    }
}
