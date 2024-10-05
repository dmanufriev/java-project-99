package hexlet.code.app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
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

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "tasks")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Task implements BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private Integer index;

    @NotNull
    @Length(min = 1, message = "Length of task name must be greater than 1")
    private String name;

    private String description;

    @NotNull
    @ManyToOne(cascade = { PERSIST, MERGE }, fetch = FetchType.LAZY)
    private TaskStatus taskStatus;

    @ManyToOne(cascade = { PERSIST, MERGE }, fetch = FetchType.LAZY)
    private User assignee;

    @ManyToMany(cascade = { PERSIST, MERGE })
    private Set<Label> labels = new HashSet<>();

    @CreatedDate
    private LocalDate createdAt;

    public Label addLabel(Label newLabel) {
        if (!labels.contains(newLabel)) {
            labels.add(newLabel);
            newLabel.getTasks().add(this);
        }
        return newLabel;
    }

    public Label removeLabel(Label label) {
        if (labels.contains(label)) {
            labels.remove(label);
            label.getTasks().remove(this);
        }
        return label;
    }
}
