package com.dockflow.backend.dto.document;

import com.dockflow.backend.entity.document.Document;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentCreateRequest {

    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 200, message = "제목은 200자를 초과할 수 없습니다.")
    private String title;

    @NotNull(message = "팀을 선택해주세요.")
    private Long teamNo;

    @NotNull(message = "카테고리를 선택해주세요.")
    private Document.DocumentCategory category;
}
