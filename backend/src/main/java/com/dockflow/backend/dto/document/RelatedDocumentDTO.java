package com.dockflow.backend.dto.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelatedDocumentDTO {

    private Long documentNo;
    private String title;
    private String categoryDescription;
    private Integer matchingTagCount;

}
