package hexlet.code.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
public class TaskStatus implements BaseEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Length(min = 1, message = "Length of task status name must be greater than 1")
    @Column(name = "name")
    private String name;

    @NotNull
    @Length(min = 1, message = "Length of task status slug must be greater than 1")
    @Column(name = "slug", unique = true)
    private String slug;

    @CreatedDate
    @Column(name = "createdAt")
    private LocalDate createdAt;

    public TaskStatus(String name, String slug) {
        this.name = name;
        this.slug = slug;
    }
}
