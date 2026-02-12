package com.dockflow.backend.dto.document.comment;

import com.dockflow.backend.entity.document.DocumentComment;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentCommentDTO {

    private Long commentNo;
    private Long documentNo;
    private Long memberNo;
    private String memberName;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @JsonProperty("isAuthor")
    private boolean isAuthor;

    @JsonProperty("isAuthor")
    public boolean getIsAuthor() {
        return this.isAuthor;
    }

    public static DocumentCommentDTO fromEntity (DocumentComment comment,String memberId) {

        if (memberId == null) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }

        return DocumentCommentDTO.builder()
                .commentNo(comment.getCommentNo())
                .documentNo(comment.getDocument().getDocumentNo())
                .memberNo(comment.getMember().getMemberNo())
                .memberName(comment.getMember().getMemberName())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .isAuthor(memberId.equals(comment.getMember().getMemberId()))
                .build();

    }
}
