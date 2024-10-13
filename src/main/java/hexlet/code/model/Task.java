package hexlet.code.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
    @Column(name = "id")
    private Long id;

    @Column(name = "index")
    private Integer index;

    @NotNull
    @Length(min = 1, message = "Length of task name must be greater than 1")
    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @NotNull
    @ManyToOne(cascade = { PERSIST, MERGE }, fetch = FetchType.LAZY)
    @JoinColumn(name = "task_status_id")
    private TaskStatus taskStatus;

    @ManyToOne(cascade = { PERSIST, MERGE }, fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    private User assignee;

    @ManyToMany(cascade = { PERSIST, MERGE })
    @Column(name = "labels")
    private Set<Label> labels = new HashSet<>();

    @CreatedDate
    @Column(name = "createdAt")
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
