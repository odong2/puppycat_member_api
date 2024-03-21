package com.architecture.admin.models.dto.pet;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PersonalityDto {

    private String petUuid;    // pet.uuid
    private Long idx;          // pet_personality_code.idx
    private String name;       // 성격 이름
    private Integer state;     // 상태
    private String regDate;    // 등록일

}
