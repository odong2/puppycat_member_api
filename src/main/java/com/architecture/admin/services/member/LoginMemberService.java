package com.architecture.admin.services.member;

import com.architecture.admin.libraries.ServerLibrary;
import com.architecture.admin.models.dao.member.LoginMemberDao;
import com.architecture.admin.models.daosub.member.LoginMemberDaoSub;
import com.architecture.admin.models.dto.member.MemberDto;
import com.architecture.admin.services.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@Service
public class LoginMemberService extends BaseService {
    private final LoginMemberDao loginMemberDao;
    private final LoginMemberDaoSub loginMemberDaoSub;

    /*****************************************************
     *  Modules
     ****************************************************/
    public void memberLoginInsert(String memberUuid) {
        HttpServletRequest request = ServerLibrary.getCurrReq();

        MemberDto memberDto = new MemberDto();
        memberDto.setLoginIp(super.getClientIP(request));
        memberDto.setRegDate(dateLibrary.getDatetime());
        memberDto.setMemberUuid(memberUuid);

        // puppycat_member_login 테이블 정보
        boolean checkMemberLogin = selectMemberLogin(memberDto);

        if (!checkMemberLogin) {
            // 인서트
            loginMemberDao.insertMemberLogin(memberDto);
        }
        // 업데이트
        loginMemberDao.updateMemberLogin(memberDto);

        // 로그 인서트
        loginMemberDao.insertMemberLoginLog(memberDto);
    }

    /*****************************************************
     *  SubFunction - Select
     ****************************************************/
    public Boolean selectMemberLogin(MemberDto memberDto) {
        int iCount = loginMemberDaoSub.getMemberCntCheck(memberDto);

        return iCount > 0;
    }

    /*****************************************************
     *  SubFunction - Insert
     ****************************************************/

    /*****************************************************
     *  SubFunction - Update
     ****************************************************/

    /*****************************************************
     *  SubFunction - Delete
     ****************************************************/

    /*****************************************************
     *  Validate
     ****************************************************/


}
