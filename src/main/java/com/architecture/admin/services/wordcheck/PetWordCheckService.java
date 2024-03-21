package com.architecture.admin.services.wordcheck;

import com.architecture.admin.libraries.exception.CustomError;
import com.architecture.admin.libraries.exception.CustomException;
import com.architecture.admin.models.daosub.wordcheck.PetWordCheckDaoSub;
import com.architecture.admin.models.dto.wordcheck.PetWordCheckDto;
import com.architecture.admin.services.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;

/*****************************************************
 * 금칙어
 ****************************************************/
@Service
@RequiredArgsConstructor
@Transactional
public class PetWordCheckService extends BaseService {

    private final PetWordCheckDaoSub petWordCheckDaoSub;
    @Value("${word.check.pet.name.type}")
    private int nameWordChk;  // 펫 이름 금칙어 타입

    /*****************************************************
     *  Modules
     ****************************************************/
    /**
     * 반려동물 금칙어 체크
     *
     * @param word 검사 할 단어
     * @param type 금칙어 타입
     */
    public void petWordCheck(String word, int type){

        // validate
        if (ObjectUtils.isEmpty(word)) {
            throw new CustomException(CustomError.WORD_EMPTY);
        }
        if (ObjectUtils.isEmpty(type)) {
            throw new CustomException(CustomError.WORD_TYPE_EMPTY);
        }

        // 금칙어 리스트
        List<PetWordCheckDto> list = getList(type);
        for(PetWordCheckDto str : list){
            String noWord = str.getWord();
            // 금칙어가 포함되어있으면
            if(word.contains(noWord)){
                if (type == nameWordChk) { // type : 이름
                    // 사용할 수 없는 문자가 포함되어 있습니다.
                    throw new CustomException(CustomError.PET_NAME_WORD_CHK_ERROR);
                }
            }
        }
    }

    /*****************************************************
     *  SubFunction - select
     ****************************************************/
    /**
     * 금칙어 목록
     *
     * @return 금칙어 리스트
     */
    public List<PetWordCheckDto> getList(int type){

        return petWordCheckDaoSub.getList(type);
    }

}
