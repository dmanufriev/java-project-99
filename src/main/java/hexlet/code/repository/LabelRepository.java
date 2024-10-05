package hexlet.code.repository;

import hexlet.code.model.Label;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LabelRepository extends JpaRepository<Label, Long> {

    Optional<Label> findByName(String name);

    @Query("SELECT l FROM Label l LEFT JOIN FETCH l.tasks WHERE l.name = :name")
    Optional<Label> findByNameWithRelatedEntities(@Param("name") String name);
}
