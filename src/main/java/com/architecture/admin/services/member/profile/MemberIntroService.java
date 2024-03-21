package com.architecture.admin.services.member.profile;

import com.architecture.admin.libraries.exception.CustomError;
import com.architecture.admin.libraries.exception.CustomException;
import com.architecture.admin.models.dao.member.profile.MemberIntroDao;
import com.architecture.admin.models.daosub.member.profile.MemberIntroDaoSub;
import com.architecture.admin.models.dto.member.profile.MemberIntroDto;
import com.architecture.admin.services.BaseService;
import com.architecture.admin.services.restrain.RestrainService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.BreakIterator;

@RequiredArgsConstructor
@Service
@Transactional
public class MemberIntroService extends BaseService {

    private final MemberIntroDao memberIntroDao;
    private final MemberIntroDaoSub memberIntroDaoSub;
    private final RestrainService restrainService;
    @Value("${profile.intro.text.max}")
    private Integer textLimit;

    /*****************************************************
     *  Modules
     ****************************************************/
    /**
     * 프로필 소개글 등록/수정
     *
     * @param memberIntroDto memberUuid, intro
     */
    public void setProfileIntro(MemberIntroDto memberIntroDto) {

        // intro
        String newIntro = memberIntroDto.getIntro();

        // 댓글 최대 길이 초과시
        BreakIterator it = BreakIterator.getCharacterInstance();
        it.setText(newIntro);
        int introLength = 0;

        while (it.next() != BreakIterator.DONE) {
            introLength++;
        }

        // validate
        if (introLength > textLimit) {
            throw new CustomException(CustomError.PROFILE_INTRO_LIMIT_ERROR); // 최대 입력 가능 글자수를 초과하였습니다.
        }

        String oldIntro = memberIntroDaoSub.getIntroByMemberUuid(memberIntroDto.getMemberUuid());

        // 인트로 수정 사항 있으면
        if (oldIntro != null && !newIntro.equals(oldIntro)) {

            // 회원 로그인 제재 체크 ( 글작성 제재 : 2 )
            restrainService.getRestrainCheck(memberIntroDto.getMemberUuid(), 2);

            // 소개글 DB 수정
            updateIntro(memberIntroDto);
            // 로그 insert
            insertIntroLog(memberIntroDto);
        }  // 등록된 인트로 없으면
        else if (oldIntro == null && newIntro != null) {
            // 소개글 DB 등록
            insertIntro(memberIntroDto);
            // 로그 insert
            insertIntroLog(memberIntroDto);
        }

    }

    /*****************************************************
     *  Select
     ****************************************************/


    /*****************************************************
     *  Insert
     ****************************************************/
    /**
     * 소개글 등록
     *
     * @param memberIntroDto memberUuid , intro , regDate
     */
    public void insertIntro(MemberIntroDto memberIntroDto) {
        memberIntroDto.setRegDate(dateLibrary.getDatetime());
        int iResult = memberIntroDao.insertIntro(memberIntroDto);

        if (iResult < 1) {
            throw new CustomException(CustomError.PROFILE_INTRO_ERROR); // 프로필 소개글 등록에 실패하였습니다.
        }
    }

    /**
     * 소개글 로그 등록
     *
     * @param memberIntroDto memberUuid , intro , regDate
     */
    private void insertIntroLog(MemberIntroDto memberIntroDto) {
        memberIntroDto.setRegDate(dateLibrary.getDatetime());
        int iResult = memberIntroDao.insertIntroLog(memberIntroDto);

        if (iResult < 1) {
            throw new CustomException(CustomError.PROFILE_INTRO_LOG_ERROR); // 소개글 로그 등록에 실패하였습니다.
        }
    }

    /*****************************************************
     *  Update
     ****************************************************/
    /**
     * 소개글 수정
     *
     * @param memberIntroDto memberUuid , intro , regDate
     */
    public void updateIntro(MemberIntroDto memberIntroDto) {
        memberIntroDto.setRegDate(dateLibrary.getDatetime());
        Integer iResult = memberIntroDao.updateIntro(memberIntroDto);

        if (iResult < 1) {
            throw new CustomException(CustomError.PROFILE_INTRO_UPDATE_ERROR); // 프로필 소개글 수정에 실패하였습니다.
        }
    }

}
