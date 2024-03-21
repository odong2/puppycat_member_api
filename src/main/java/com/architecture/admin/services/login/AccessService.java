package com.architecture.admin.services.login;

import com.architecture.admin.libraries.ServerLibrary;
import com.architecture.admin.models.dao.access.AccessDao;
import com.architecture.admin.models.daosub.access.AccessDaoSub;
import com.architecture.admin.models.dto.member.MemberDto;
import com.architecture.admin.services.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@Service
@Transactional
public class AccessService extends BaseService {

    private final AccessDao accessDao;
    private final AccessDaoSub accessDaoSub;

    /**
     * 접속/앱 정보 등록
     *
     * @param memberDto uuid, appKey, appVer, domain, iso, fcmToken
     */
    public void accessAppLogInsert(MemberDto memberDto) {

        // 등록일/수정일 set
        memberDto.setModiDate(dateLibrary.getDatetime());
        memberDto.setRegDate(dateLibrary.getDatetime());

        // 접속 IP set
        HttpServletRequest request = ServerLibrary.getCurrReq();
        memberDto.setAccessIp(super.getClientIP(request));

        //해당 FCM 가지고 있는 계정 카운트
        Integer appSameFcmCount = accessDaoSub.getAppSameFcmCount(memberDto);

        // 해당 FCM 초기화
        if (appSameFcmCount > 0) {
            accessDao.fcmUpdate(memberDto);
        }

        // 앱키가 있는지 확인 [puppycat_member_app]
        MemberDto appInfo = accessDaoSub.getAppInfo(memberDto);

        // 있으면 update
        if (appInfo != null) {
            accessDao.appUpdate(memberDto);
        }
        // 없으면 insert
        else {
            accessDao.appInsert(memberDto);
        }

        // memberUuid 가 있는지 확인 [puppycat_member_access]
        MemberDto accessInfo = accessDaoSub.getAccessInfo(memberDto);

        // 있으면 update
        if (accessInfo != null) {
            accessDao.accessUpdate(memberDto);
        }
        // 없으면 insert
        else {
            accessDao.accessInsert(memberDto);
        }

        // access log insert [puppycat_member_access_log]
        accessDao.accessLogInsert(memberDto);

    }
}
