package com.architecture.admin.services.contents;

import com.architecture.admin.libraries.exception.CustomError;
import com.architecture.admin.libraries.exception.CustomException;
import com.architecture.admin.models.daosub.contents.ContentsDaoSub;
import com.architecture.admin.models.dto.member.MemberDto;
import com.architecture.admin.models.dto.member.MemberInfoDto;
import com.architecture.admin.models.dto.tag.MentionTagDto;
import com.architecture.admin.models.dto.wordcheck.MemberWordCheckDto;
import com.architecture.admin.services.BaseService;
import com.architecture.admin.services.wordcheck.MemberWordCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContentsService extends BaseService {

    private final ContentsDaoSub contentsDaoSub;
    @Value("${cloud.aws.s3.img.url}")
    private String imgDomain;
    private final MemberWordCheckService wordCheckService;

    /*****************************************************
     *  Modules
     ****************************************************/
    /**
     * uuid 조회 by nick
     *
     * @param nickList nick List
     * @return List<MemberDto>
     */
    public List<MemberDto> getUuidByNick(List<String> nickList) {

        // 회원 uuid 조회
        return contentsDaoSub.getUuidByNick(nickList);
    }

    /**
     * 멘션된 회원 정보 리스트
     *
     * @param memberUuidList
     * @return
     */
    public List<MentionTagDto> getMentionInfoList(List<String> memberUuidList) {

        if (memberUuidList.isEmpty()) {
            throw new CustomException(CustomError.MEMBER_UUID_EMPTY); // 회원 UUID가 비었습니다.
        }

        // 정상 회원 정보 리스트(memberUuid, nick)
        List<MentionTagDto> memberInfoList = contentsDaoSub.getMentionMemberList(memberUuidList);

        // 정상 회원 리스트 사이즈와 memberUuid 리스트 사이즈 같지 않으면 탈퇴한 회원 존재
        if (!ObjectUtils.isEmpty(memberInfoList) && memberInfoList.size() != memberUuidList.size()) {

            for (MentionTagDto mentionTagDto : memberInfoList) {
                // 이미 정상 회원으로 조회된 uuid 제거
                memberUuidList.removeIf(n -> n.equals(mentionTagDto.getUuid()));
            }

            // 탈퇴한 회원 정보 set
            for (String uuid : memberUuidList) {
                MentionTagDto outMentionMemberInfo = MentionTagDto.builder()
                        .uuid(uuid)
                        .nick("")
                        .state(0).build();

                memberInfoList.add(outMentionMemberInfo);
            }

        }

        return memberInfoList;
    }

    /**
     * 이미지 내 태그된 회원 정보 리스트
     *
     * @param memberUuidList
     * @return : uuid, nick, profileImgUrl, intro
     */
    public List<MemberDto>  getImgMemberTagInfoList(List<String> memberUuidList) {

        if (memberUuidList.isEmpty()) {
            throw new CustomException(CustomError.MEMBER_UUID_EMPTY); // 회원 uuid가 비었습니다.
        }

        MemberDto memberDto = new MemberDto();
        memberDto.setImgDomain(imgDomain);
        memberDto.setUuidList(memberUuidList);

        List<MemberDto> memberInfoList = contentsDaoSub.getImgMemberTagInfoList(memberDto); // 회원 정보 리스트 조회

        if (!ObjectUtils.isEmpty(memberInfoList)) {

            // 정상 회원 리스트 사이즈와 memberUuid 리스트 사이즈 같지 않으면 탈퇴한 회원 존재
            if (memberInfoList.size() != memberInfoList.size()) {
                for (MemberDto member : memberInfoList) {
                    // 이미 정상 회원으로 조회된 uuid 제거
                    memberUuidList.removeIf(n -> n.equals(member.getUuid()));
                }

                // 탈퇴한 회원 정보 set
                for (String uuid : memberUuidList) {
                    MemberDto outImgMemberInfo = MemberDto.builder()
                            .uuid(uuid)
                            .nick("")
                            .state(0).build();

                    memberInfoList.add(outImgMemberInfo);
                }
            }

            List<MemberWordCheckDto> introBadWordList = wordCheckService.getIntroBadWordList(); // 금칙어 리스트 조회

            memberInfoList.forEach(memberInfo -> {
                String intro = wordCheckService.memberWordCheck(memberInfo.getIntro(), introBadWordList); // 금칙어 치환
                memberInfo.setIntro(intro); // intro set
            });
        }

        return memberInfoList;
    }

    /**
     * 컨텐츠 작성자 정보
     *
     * @param memberUuid
     * @return
     */
    public MemberInfoDto getWriterInfoByUuid(String memberUuid) {

        if (ObjectUtils.isEmpty(memberUuid)) {
            throw new CustomException(CustomError.MEMBER_UUID_EMPTY); // 회원 uuid가 비었습니다.
        }

        MemberDto memberDto = new MemberDto();
        memberDto.setImgDomain(imgDomain);
        memberDto.setMemberUuid(memberUuid);

        // 작성자 정보 조회
        MemberInfoDto writerInfo = contentsDaoSub.getWriterInfoByUuid(memberDto);

        // intro 금칙어 치환
        List<MemberWordCheckDto> introBadWordList = wordCheckService.getIntroBadWordList();
        String intro = wordCheckService.memberWordCheck(writerInfo.getIntro(), introBadWordList);
        writerInfo.setIntro(intro);

        return writerInfo;
    }

    /**
     * 컨텐츠 작성자 리스트 정보
     *
     * @param memberUuidList
     * @return
     */
    public List<MemberInfoDto> getWriterInfoList(List<String> memberUuidList) {

        if (ObjectUtils.isEmpty(memberUuidList)) {
            throw new CustomException(CustomError.MEMBER_UUID_EMPTY); // 회원 uuid가 비었습니다.
        }

        MemberDto memberDto = new MemberDto();
        memberDto.setImgDomain(imgDomain);
        memberDto.setUuidList(memberUuidList);

        // 작성자 정보 리스트 조회
        List<MemberInfoDto> writerInfoList = contentsDaoSub.getWriterInfoList(memberDto);
        List<MemberWordCheckDto> introBadWordList = wordCheckService.getIntroBadWordList(); // 금칙어 리스트 조회

        writerInfoList.forEach(memberInfo -> {
            String intro = wordCheckService.memberWordCheck(memberInfo.getIntro(), introBadWordList); // 금칙어 치환
            memberInfo.setIntro(intro); // intro set
        });

        return writerInfoList;
    }

    /*****************************************************
     *  SubFunction - select
     ****************************************************/

    /*****************************************************
     *  SubFunction - update
     ****************************************************/

    /*****************************************************
     *  SubFunction - insert
     ****************************************************/

    /*****************************************************
     *  SubFunction - delete
     ****************************************************/

    /*****************************************************
     *  ETC
     ****************************************************/
}
