package com.dockflow.backend.entity.document;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "DOCUMENT_TAG", uniqueConstraints = @UniqueConstraint(columnNames = {"document_no", "tag_name"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class DocumentTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_no")
    private Long tagNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_no", nullable = false)
    private Document document;

    @Column(name = "tag_name", nullable = false, length = 50)
    private String tagName;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime localDateTime;
}
