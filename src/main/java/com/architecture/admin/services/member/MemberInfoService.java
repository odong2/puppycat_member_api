package com.architecture.admin.services.member;

import com.architecture.admin.libraries.exception.CustomError;
import com.architecture.admin.libraries.exception.CustomException;
import com.architecture.admin.models.dao.member.MemberInfoDao;
import com.architecture.admin.models.daosub.member.MemberInfoDaoSub;
import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.member.MemberDto;
import com.architecture.admin.models.dto.member.MemberInfoDto;
import com.architecture.admin.models.dto.member.MemberNickDto;
import com.architecture.admin.models.dto.member.profile.MemberImageDto;
import com.architecture.admin.models.dto.member.profile.MemberIntroDto;
import com.architecture.admin.services.BaseService;
import com.architecture.admin.services.member.profile.MemberImageService;
import com.architecture.admin.services.member.profile.MemberIntroService;
import com.architecture.admin.services.wordcheck.MemberWordCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;


@RequiredArgsConstructor
@Service
@Transactional
public class MemberInfoService extends BaseService {
    private final MemberInfoDaoSub memberInfoDaoSub;
    private final MemberInfoDao memberInfoDao;
    private final MemberWordCheckService memberWordCheckService;
    private final MemberImageService memberImageService;
    private final MemberIntroService memberIntroService;
    private final MemberNickService memberNickService;
    @Value("${word.check.member.intro.type}")
    private int introWordChk;  // 회원 인트로 금칙어 타입
    @Value("${cloud.aws.s3.img.url}")
    private String imgDomain;

    /*****************************************************
     *  Modules
     ****************************************************/

    /*****************************************************
     *  SubFunction - Select
     ****************************************************/
    /**
     * 회원 정보 가져오기 ORDER BY NICK
     *
     * @param searchDto
     * @return
     */
    public List<MemberInfoDto> getMemberInfoOrderByNick(SearchDto searchDto) {

        if (ObjectUtils.isEmpty(searchDto.getMemberUuidList())) {
            throw new CustomException(CustomError.MEMBER_UUID_EMPTY);
        }

        // 이미지 도메인 set
        searchDto.setImgDomain(imgDomain);
        List<MemberInfoDto> memberInfoList = memberInfoDaoSub.getMemberInfoOrderByNick(searchDto);

        /** 금칙어 치환 **/
        for (MemberInfoDto dto : memberInfoList) {
            // 금칙어가 포함되어있으면 치환
            if (!ObjectUtils.isEmpty(dto.getIntro())) {
                String intro = memberWordCheckService.memberWordCheck(dto.getIntro(), introWordChk);
                dto.setIntro(intro);
            }
        }

        return memberInfoList;
    }

    /**
     * 회원 정보 가져오기
     *
     * @param memberInfoDto memberUuid : 로그인 회원 idx
     * @return MemberInfoDto
     */
    public MemberInfoDto getMyInfo(MemberInfoDto memberInfoDto) {
        // 이미지 도메인 set
        memberInfoDto.setImgDomain(imgDomain);

        // 회원 정보 가져오기
        MemberInfoDto memberInfo = memberInfoDaoSub.getMyInfo(memberInfoDto);

        /** 금칙어 치환 **/
        if (!memberInfo.getIntro().isEmpty()) {
            String intro = memberWordCheckService.memberWordCheck(memberInfo.getIntro(), introWordChk);
            memberInfo.setIntro(intro);
        }

        return memberInfo;
    }

    /**
     * 유저 페이지 - 회원 info 가져오기
     *
     * @param memberDto : uuid
     * @return MemberInfoDto
     */
    public MemberInfoDto getMemberInfoByUuid(MemberDto memberDto) {
        // 이미지 도메인 set
        memberDto.setImgDomain(imgDomain);

        // 회원 정보 가져오기
        MemberInfoDto memberInfo = memberInfoDaoSub.getMemberInfoByUuid(memberDto);

        /** 금칙어 치환 **/
        if (!memberInfo.getIntro().isEmpty()) {
            String intro = memberWordCheckService.memberWordCheck(memberInfo.getIntro(), introWordChk);
            memberInfo.setIntro(intro);
        }

        return memberInfo;
    }

    /*****************************************************
     *  SubFunction - Update
     ****************************************************/
    /**
     * 회원 정보 수정
     *
     * @param memberInfoDto memberUuid, uploadFile, intro, nick, name, phone, gender, birth, ci, di
     */
    @Transactional
    public void updateMyInfo(MemberInfoDto memberInfoDto) throws Exception {

        // 이미지 등록/수정
        if (memberInfoDto.getUploadFile() != null && memberInfoDto.getResetState() == 0) {
            MemberImageDto memberImageDto = new MemberImageDto();
            memberImageDto.setMemberUuid(memberInfoDto.getMemberUuid());
            memberImageDto.setUploadFile(memberInfoDto.getUploadFile());

            memberImageService.setProfileImage(memberImageDto);
        } // 이미지 초기화
        else if (memberInfoDto.getResetState() == 1 && memberInfoDto.getUploadFile() == null) {
            MemberImageDto memberImageDto = new MemberImageDto();
            memberImageDto.setMemberUuid(memberInfoDto.getMemberUuid());

            memberImageService.resetProfileImage(memberImageDto);
        }

        // 소개글 변경
        MemberIntroDto memberIntroDto = new MemberIntroDto();
        memberIntroDto.setMemberUuid(memberInfoDto.getMemberUuid());
        memberIntroDto.setIntro(memberInfoDto.getIntro());
        memberIntroService.setProfileIntro(memberIntroDto);

        // 닉네임 변경
        if (!ObjectUtils.isEmpty(memberInfoDto.getNick())) {
            MemberNickDto memberNickDto = new MemberNickDto();
            memberNickDto.setMemberUuid(memberInfoDto.getMemberUuid());
            memberNickDto.setNick(memberInfoDto.getNick());
            memberNickService.modifyNick(memberNickDto);
        }

        // 본인인증 관련 정보 변경
        if (!ObjectUtils.isEmpty(memberInfoDto.getPhone()) && !ObjectUtils.isEmpty(memberInfoDto.getCi()) && !ObjectUtils.isEmpty(memberInfoDto.getDi())) {
            updateMemberCertificationInfo(memberInfoDto);
        }

    }

    /**
     * 회원 본인 인증 정보 수정
     *
     * @param memberInfoDto
     * @return
     */
    public void updateMemberCertificationInfo(MemberInfoDto memberInfoDto) throws Exception {

        // 암호화값 복호화
        // ci 암호화
        String ci = securityLibrary.aesDecrypt(memberInfoDto.getCi());
        // di 암호화
        String di = securityLibrary.aesDecrypt(memberInfoDto.getDi());
        //휴대폰번호 암호화
        String phone = memberInfoDto.getPhone();

        // 복호화 값 세팅
        memberInfoDto.setPhone(phone);
        memberInfoDto.setCi(ci);
        memberInfoDto.setDi(di);
        memberInfoDto.setModiDate(dateLibrary.getDatetime());
        // 유저 본인 인증 정보 변경 처리
        Integer iResult = memberInfoDao.updateMemberCertificationInfo(memberInfoDto);

        if (iResult != 1) {
            throw new CustomException(CustomError.MY_INFO_CERTIFICATION_SAVE_ERROR); // 저장 취소 실패하였습니다.
        }
    }
    /*****************************************************
     *  SubFunction - ETC
     ****************************************************/

}
