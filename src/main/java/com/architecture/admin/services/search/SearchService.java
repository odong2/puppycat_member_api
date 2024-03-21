package com.architecture.admin.services.search;

import com.architecture.admin.libraries.exception.CustomError;
import com.architecture.admin.libraries.exception.CustomException;
import com.architecture.admin.models.daosub.search.SearchDaoSub;
import com.architecture.admin.services.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;


@RequiredArgsConstructor
@Service
@Transactional
public class SearchService extends BaseService {
    private final SearchDaoSub searchDaoSub;

    /*****************************************************
     *  Modules
     ****************************************************/


    /*****************************************************
     *  SubFunction - Select
     ****************************************************/
     public List<String> getSearchNickUuid(String searchNick) {

        if (ObjectUtils.isEmpty(searchNick)) {
            throw new CustomException(CustomError.SEARCH_WORD_EMPTY);
        }

        // 회원 조회
        return searchDaoSub.getSearchNickUuid(searchNick);
    }

    /**
     * 닉네임이 온전히 같은 회원 UUID 조회
     * 
     * @param searchNick
     * @return
     */
    public String getSameNickUuid(String searchNick) {

        if (ObjectUtils.isEmpty(searchNick)) {
            throw new CustomException(CustomError.SEARCH_WORD_EMPTY);
        }

        // 회원 조회
        return searchDaoSub.getSameNickUuid(searchNick);
    }

    /*****************************************************
     *  SubFunction - Update
     ****************************************************/

    /*****************************************************
     *  SubFunction - ETC
     ****************************************************/

}
