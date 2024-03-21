package com.architecture.admin.models.daosub.wordcheck;

import com.architecture.admin.models.dto.wordcheck.PetWordCheckDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface PetWordCheckDaoSub {

    /*****************************************************
     * Select
     ****************************************************/
    /**
     * 금칙어 목록
     *
     * @return list
     */
    List<PetWordCheckDto> getList(int type);

}
