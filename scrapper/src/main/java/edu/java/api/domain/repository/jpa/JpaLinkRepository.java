package edu.java.api.domain.repository.jpa;

import edu.java.model.jpa.Link;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaLinkRepository extends JpaRepository<Link, Long> {
    Optional<Link> findLinkByUrl(String uri);

    @Query(
        value = "SELECT l FROM Link l WHERE CURRENT_TIMESTAMP - l.checkedAt > :min"
    )
    List<Link> findLinkByCheckedAt(@Param("min") Duration minutes);
}
