package com.architecture.admin.models.daosub.restrain;

import com.architecture.admin.models.dto.restrain.RestrainDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface RestrainDaoSub {
    /*****************************************************
     * Select
     ****************************************************/
    // 회원 제재 리스트 가져오기
    List<Integer> getRestrainList(RestrainDto restrainDto);

    /**
     * 회원 제재 체크
     * 
     * @param restrainDto
     * @return
     */
    Integer getRestrainCheck(RestrainDto restrainDto);
    
    /**
     * 회원 제재 상세 정보
     *
     * @param restrainDto memberUuid
     * @return
     */
   RestrainDto getInfoRestrain(RestrainDto restrainDto);
}
