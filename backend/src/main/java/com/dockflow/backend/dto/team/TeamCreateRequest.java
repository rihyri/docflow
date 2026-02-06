package com.dockflow.backend.dto.team;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeamCreateRequest {

    @NotBlank(message = "팀 이름은 필수입니다.")
    @Size(max = 100, message = "팀 이름은 100자 이하여야 합니다.")
    private String teamName;

    @Size(max = 500, message = "설명은 500자 이하여야 합니다.")
    private String description;
}
