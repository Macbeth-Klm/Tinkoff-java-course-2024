package edu.java.api.domain.repository.jpa;

import edu.java.model.domain.jpa.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaChatRepository extends JpaRepository<Chat, Long> {
}
