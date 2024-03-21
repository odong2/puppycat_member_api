package com.architecture.admin.models.daosub.contents;

import com.architecture.admin.models.dto.member.MemberDto;
import com.architecture.admin.models.dto.member.MemberInfoDto;
import com.architecture.admin.models.dto.tag.MentionTagDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface ContentsDaoSub {

    /*****************************************************
     * Select
     ****************************************************/

    /**
     * uuid 조회 by nick
     *
     * @param nickList nick List
     * @return memberUuid list
     */
    List<MemberDto> getUuidByNick(List<String> nickList);

    /**
     * 멘션된 회원 정보 리스트
     *
     * @param memberUuidList memberUuidList
     * @return
     */
    List<MentionTagDto> getMentionMemberList(List<String> memberUuidList);

    /**
     * 이미지 내 태그된 회원 정보 리스트
     *
     * @param memberDto uuidList, imgDomain
     * @return
     */
    List<MemberDto> getImgMemberTagInfoList(MemberDto memberDto);

    /**
     * 컨텐츠 작성자 정보
     *
     * @param memberDto memberUuid, imgDomain
     * @return
     */
    MemberInfoDto getWriterInfoByUuid(MemberDto memberDto);

    /**
     * 컨텐츠 작성자 리스트 정보
     *
     * @param memberDto : uuidList, imgDomain
     * @return
     */
    List<MemberInfoDto> getWriterInfoList(MemberDto memberDto);
}
