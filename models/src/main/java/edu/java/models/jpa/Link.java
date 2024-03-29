package edu.java.models.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "link")
public class Link {
    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "link_id_generator", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "link_id_generator", sequenceName = "link_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "checked_at")
    private OffsetDateTime checkedAt;

    @ManyToMany(mappedBy = "links", fetch = FetchType.LAZY)
    Set<Chat> chats;
}
