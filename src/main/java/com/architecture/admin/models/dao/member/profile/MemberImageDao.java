package com.architecture.admin.models.dao.member.profile;

import com.architecture.admin.models.dto.member.MemberDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;

@Repository
@Mapper
public interface MemberImageDao {

    /*****************************************************
     *  Insert
     ****************************************************/
    /**
     * 프로필 이미지 등록
     *
     * @param fileMap memberUuid, uploadFile ,regDate
     * @return Integer
     */
    Integer insertImage(HashMap<String,Object> fileMap);

    /**
     * 회원 가입 시 프로필 이미지 등록하기
     *
     * @param memberDto imgUuid, memberUuid, regDate
     * @return Integer
     */
    int insertInitImage(MemberDto memberDto);

    /**
     * 프로필 이미지 로그 등록
     *
     * @param fileMap memberUuid, uploadFile ,regDate
     * @return Integer
     */
    Integer insertImageLog(HashMap<String,Object> fileMap);

    /*****************************************************
     *  Update
     ****************************************************/
    /**
     * 프로필 이미지 수정
     *
     * @param fileMap memberUuid, uploadFile ,regDate
     * @return Integer
     */
    Integer updateImage(HashMap<String,Object> fileMap);


}
