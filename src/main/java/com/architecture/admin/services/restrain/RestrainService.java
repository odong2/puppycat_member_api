package com.architecture.admin.services.restrain;

import com.architecture.admin.libraries.exception.CustomError;
import com.architecture.admin.libraries.exception.CustomException;
import com.architecture.admin.models.daosub.restrain.RestrainDaoSub;
import com.architecture.admin.models.dto.restrain.RestrainDto;
import com.architecture.admin.services.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*****************************************************
 * 회원 제재 모델러
 ****************************************************/
@Service
@RequiredArgsConstructor
@Transactional
public class RestrainService extends BaseService {

    private final RestrainDaoSub restrainDaoSub;

    /*****************************************************
     *  Modules
     ****************************************************/
    /**
     * 회원 제재 리스트 가져오기
     *
     * @param memberUUid
     * @return
     */
    public List<Integer> getRestrainList(String memberUUid) {
        if (memberUUid == null || memberUUid.equals("")) {
            // 회원 UUID가 비었습니다.
            throw new CustomException(CustomError.RESTRAIN_MEMBERUUID_EMPTY);
        }

        RestrainDto restrainDto = new RestrainDto();
        restrainDto.setMemberUuid(memberUUid);
        restrainDto.setNowDate(dateLibrary.getDatetime());

        return restrainDaoSub.getRestrainList(restrainDto);
    }

    /**
     * 제재 항목별 제재 체크
     *
     * @param memberUUid   회원UUID
     * @param restrainType ( 1: 로그인, 2:글작성(모든 글 작성), 3:게시글작성 , 4:댓글작성, 5:산책제한)
     */
    public void getRestrainCheck(String memberUUid, Integer restrainType) {
        if (memberUUid == null || memberUUid.equals("")) {
            // 회원 UUID가 비었습니다.
            throw new CustomException(CustomError.RESTRAIN_MEMBERUUID_EMPTY);
        }

        if (restrainType == null || restrainType < 0) {
            // 제재 타입이 비어있습니다.
            throw new CustomException(CustomError.RESTRAIN_TYPE_EMPTY);
        }

        RestrainDto restrainDto = new RestrainDto();
        restrainDto.setMemberUuid(memberUUid);
        restrainDto.setNowDate(dateLibrary.getDatetime());
        restrainDto.setType(restrainType);

        int iCount = restrainDaoSub.getRestrainCheck(restrainDto);

        if (iCount > 0) {
            switch (restrainType) {
                case 1 -> throw new CustomException(CustomError.RESTRAIN_LOGIN);    // 로그인 제재 회원입니다.
                case 2 -> throw new CustomException(CustomError.RESTRAIN_WRITE);    // 글작성 제재 회원입니다.
                case 3 -> throw new CustomException(CustomError.RESTRAIN_CONTENTS); // 컨텐츠 작성 제재 회원입니다.
                case 4 -> throw new CustomException(CustomError.RESTRAIN_COMMENT);  // 댓글 작성 제재 회원입니다.
                case 5 -> throw new CustomException(CustomError.RESTRAIN_WALK);     // 산책 제재 회원입니다.
                default -> throw new CustomException(CustomError.RESTRAIN_MEMBER);  // 제재 회원입니다.
            }
        }
    }

    /*****************************************************
     *  SubFunction - select
     ****************************************************/
    /**
     * 제재 상세 정보 가져오기
     *
     * @param restrainDto
     * @return
     */
    public RestrainDto getInfoRestrain(RestrainDto restrainDto) {
        restrainDto.setNowDate(dateLibrary.getDatetime());
        return restrainDaoSub.getInfoRestrain(restrainDto);
    }

}
