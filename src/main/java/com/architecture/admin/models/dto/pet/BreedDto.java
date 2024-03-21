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
public class BreedDto {

    private Long idx;          // idx
    private Integer typeIdx;   // 유형(1: 강아지, 2: 고양이)
    private String name;       // 이름
    private Integer state;     // 상태
    private Long sort;         // 정렬

}
