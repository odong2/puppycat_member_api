package com.architecture.admin.models.dao.access;

import com.architecture.admin.models.dto.member.MemberDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface AccessDao {

    /*****************************************************
     * Insert
     ****************************************************/
    /**
     * 접속 정보 등록
     * @param memberDto
     */
    void accessInsert(MemberDto memberDto);

    /**
     * 접속 정보 로그 등록
     * @param memberDto
     */
    void accessLogInsert(MemberDto memberDto);

    /**
     * 앱 정보 등록
     * @param memberDto
     */
    void appInsert(MemberDto memberDto);

    /*****************************************************
     * Update
     ****************************************************/
    /**
     * 접속 정보 업데이트
     * @param memberDto
     */
    void accessUpdate(MemberDto memberDto);

    /**
     * 앱 정보 업데이트
     * @param memberDto
     */
    void appUpdate(MemberDto memberDto);

    /**
     * 앱 FCM 초기화 업데이트
     * @param memberDto
     */
    void fcmUpdate(MemberDto memberDto);

    /**
     * 로그아웃 앱 정보 상태값 수정 (state :0)
     * @param memberDto
     */
    void appStateUpdate(MemberDto memberDto);


}