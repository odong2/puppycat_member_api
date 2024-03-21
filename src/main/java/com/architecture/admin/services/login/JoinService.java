package com.architecture.admin.services.login;

import com.architecture.admin.libraries.ChannelTalkLibrary;
import com.architecture.admin.libraries.ServerLibrary;
import com.architecture.admin.libraries.exception.CurlException;
import com.architecture.admin.libraries.exception.CustomError;
import com.architecture.admin.libraries.exception.CustomException;
import com.architecture.admin.models.dao.member.MemberDao;
import com.architecture.admin.models.dao.member.profile.MemberImageDao;
import com.architecture.admin.models.dao.member.profile.MemberIntroDao;
import com.architecture.admin.models.dto.member.MemberDto;
import com.architecture.admin.models.dto.member.MemberNickDto;
import com.architecture.admin.models.dto.member.MemberPointDto;
import com.architecture.admin.models.dto.member.profile.MemberIntroDto;
import com.architecture.admin.models.dto.setting.NotificationSettingDto;
import com.architecture.admin.services.BaseService;
import com.architecture.admin.services.member.MemberNickService;
import com.architecture.admin.services.setting.NotificationSettingService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/*****************************************************
 * 회원 가입 모델러
 ****************************************************/
@RequiredArgsConstructor
@Service
@Transactional
public class JoinService extends BaseService {

    private final MemberDao memberDao;
    private final MemberImageDao memberImageDao;
    private final MemberNickService memberNickService;
    private final MemberIntroDao memberIntroDao;
    private final NotificationSettingService notificationSettingService;
    private final JoinCurlService joinCurlService;
    @Value("${notification.main.total.count}")
    private Integer notificationTotalCount;
    @Value("${notification.sub.total.count}")
    private Integer notificationSubTotalCount;
    @Value("${walk.notification.sub.total.count}")
    private Integer walkNotificationSubTotalCount;
    @Value("${use.channel.hash}")
    private boolean useChannelHash;     // 채널톡 해시값 인서트


    /*****************************************************
     *  Modules
     ****************************************************/
    /**
     * 회원 아이디 중복 검색
     *
     * @param memberDto id,isSimple,simpleId,simpleType
     * @return
     */
    public Boolean checkDupleId(MemberDto memberDto) {
        Integer iCount = memberDaoSub.getCountById(memberDto);

        return iCount > 0;
    }

    /**
     * 회원 CI && Simple Type 중복 검색
     *
     * @param memberDto CI,simpleType
     * @return
     */
    public Boolean checkDupleCi(MemberDto memberDto) {
        Integer iCount = memberDaoSub.getCountByCi(memberDto);

        return iCount > 0;
    }

    /**
     * 회원 고유 아이디 중복 검색
     *
     * @param uuid 고유아이디
     * @return
     */
    public Boolean checkDupleUuid(String uuid) {
        Integer iCount = memberDaoSub.getCountByUuid(uuid);

        return iCount > 0;
    }

    /**
     * 회원 가입
     *
     * @param memberDto (id,pw,partner,isSimple,pwConfirm)
     * @return
     * @throws Exception
     */
    @Transactional
    public Map<String, Object> regist(MemberDto memberDto) throws Exception {

        // 아이디/패스워드 검증
        String id = memberDto.getId();
        String password = memberDto.getPassword();
        String passwordConfirm = memberDto.getPasswordConfirm();
        String simpleId = memberDto.getSimpleId();
        Integer isSimple = memberDto.getIsSimple();
        String simpleType = memberDto.getSimpleType();
        Integer selectPolicy3 = memberDto.getSelectPolicy_3();
        // 리턴할 데이터
        Map<String, Object> data = new HashMap();

        // validate
        if (id == null || id.equals("")) { // 아이디 확인
            throw new CustomException(CustomError.JOIN_ID_ERROR);
        }

        if (password == null || password.equals("")) { // 패스워드 확인
            throw new CustomException(CustomError.JOIN_PW_ERROR);
        }

        if (passwordConfirm == null || passwordConfirm.equals("")) { // 패스워드 확인
            throw new CustomException(CustomError.JOIN_PW_ERROR);
        }

        if (simpleId == null || simpleId.equals("")) { // 심플 아이디 체크
            throw new CustomException(CustomError.JOIN_SIMPLE_ID_ERROR);
        }

        if (isSimple == 1 && (simpleType == null || simpleType.equals(""))) { // 간편가입 타입 확인
            throw new CustomException(CustomError.SIMPLE_JOIN_ERROR);
        }

        MemberNickDto memberNickDto = new MemberNickDto();
        memberNickDto.setNick(memberDto.getNick());

        if (memberDto.getNick() == null || memberDto.getNick().equals("") || !memberNickService.checkNick(memberNickDto)) { // 닉네임 확인
            throw new CustomException(CustomError.JOIN_NICK_ERROR);
        }

        if (memberDto.getName() == null || memberDto.getName().equals("")) { // 이름 확인
            throw new CustomException(CustomError.JOIN_NAME_ERROR);
        }

        if (memberDto.getCi() == null || memberDto.getCi().equals("")) { // CI 확인
            throw new CustomException(CustomError.JOIN_CI_ERROR);
        }

        if (memberDto.getDi() == null || memberDto.getDi().equals("")) { // DI 확인
            throw new CustomException(CustomError.JOIN_DI_ERROR);
        }

        if (memberDto.getGender() == null || memberDto.getGender().equals("")) { // 성별 확인
            throw new CustomException(CustomError.JOIN_GENDER_ERROR);
        }

        if (memberDto.getBirth() == null || memberDto.getBirth().equals("")) { // 생년월일 확인
            throw new CustomException(CustomError.JOIN_BIRTH_ERROR);
        }

        if (memberDto.getPhone() == null || memberDto.getPhone().equals("")) { // 핸드폰 확인
            throw new CustomException(CustomError.JOIN_PHONE_ERROR);
        }

        if (selectPolicy3 == null || selectPolicy3.equals("") || selectPolicy3 > 1) { // 선택 사항 값 체크
            throw new CustomException(CustomError.SELECT_POLICY_ERROR);
        }

        if (memberDto.getAccessToken() == null || memberDto.getAccessToken().equals("")) { // Access Token 확인
            throw new CustomException(CustomError.JOIN_ACCESS_TOKEN_ERROR);
        }

        if (memberDto.getRefreshToken() == null || memberDto.getRefreshToken().equals("")) { // Refresh Token 확인
            throw new CustomException(CustomError.JOIN_REFRESH_TOKEN_ERROR);
        }

        // 본인인증 값 복호화
        // 이름 복호화
        String name = securityLibrary.aesDecrypt(memberDto.getName());
        // 휴대폰번호 복호화
        String phone = securityLibrary.aesDecrypt(memberDto.getPhone());
        // 성별 복호화
        String gender = securityLibrary.aesDecrypt(memberDto.getGender());
        // 생일 복호화
        String birth = securityLibrary.aesDecrypt(memberDto.getBirth());
        // ci 복호화
        String ci = securityLibrary.aesDecrypt(memberDto.getCi());
        // di 복호화
        String di = securityLibrary.aesDecrypt(memberDto.getDi());

        // 복호화 값 세팅
        memberDto.setName(name);
        memberDto.setPhone(phone);
        memberDto.setGender(gender);
        memberDto.setBirth(birth);
        memberDto.setCi(ci);
        memberDto.setDi(di);

        // 만 나이 가져오기
        int age = dateLibrary.calculateAge(memberDto.getBirth());

        if (age < 14) {
            throw new CustomException(CustomError.JOIN_AGE_UNDER);
        }

        // 일반 회원가입인 경우
        if (isSimple == 0) {

            // 아이디 중복체크
            memberDto.setState(1); // 가입된 상태만 검색
            Boolean bDupleId = checkDupleId(memberDto);

            if (Boolean.TRUE.equals(bDupleId)) {
                throw new CustomException(CustomError.ID_DUPLE); // 이미 존재하는 아이디입니다.
            }

            // 일반 회원가입 아이디는 이메일로
            if (!isEmail(id)) {
                throw new CustomException(CustomError.JOIN_ID_EMAIL_ERROR); // 아이디는 이메일로 입력해주세요.
            }

            // 패스워드 일치 확인
            if (!password.equals(passwordConfirm)) {
                throw new CustomException(CustomError.PASSWORD_CONFIRM); // 패스워드를 동일하게 입력해주세요.
            }

        }

        // 패스워드 암호화
        memberDto.setPassword(super.encrypt(password));

        // 회원 uuid 세팅 ( locale Language + 랜덤 UUID + Timestamp )
        String localeLang = super.getLocaleLang();
        String setUuid = dateLibrary.getTimestamp();
        String uuid = localeLang + UUID.randomUUID().toString().concat(setUuid);
        uuid = uuid.replace("-", "");

        // 48자 이상이면 48자로 자르기
        if (uuid.length() > 48) {
            uuid = uuid.substring(0, 49);
        }

        // 고유아이디 중복체크
        Boolean bDupleUuid = checkDupleUuid(uuid);

        // 고유아이디가 중복이면 5번 재시도
        int retry = 0;
        while (Boolean.TRUE.equals(bDupleUuid) && retry < 5) {
            retry++;
            pushAlarm("회원 고유아이디 중복 시도::" + retry + "번째");

            // 회원 uuid 세팅 ( locale Language + 랜덤 UUID + Timestamp )
            localeLang = super.getLocaleLang();
            setUuid = dateLibrary.getTimestamp();
            uuid = "mem_" + localeLang + UUID.randomUUID().toString().concat(setUuid);
            uuid = uuid.replace("-", "");

            bDupleUuid = checkDupleUuid(uuid);

            if (retry == 5) {
                throw new CustomException(CustomError.UUID_DUPLE);
            }
        }

        //같은 CI / SIMPLE_TYPE 회원 가입 불가
        if (Boolean.TRUE.equals(checkDupleCi(memberDto))) {
            // 이미 존재하는 아이디 조회
            String socialId = memberDaoSub.getMemberIdByCi(memberDto);
            data.put("isAvailableJoin", false); // 중복
            data.put("simpleType", simpleType); // 간편 회원가입 유형
            data.put("id", socialId);           // 이미 존재하는 아이디

            return data;
        }

        // 고유아이디 세팅
        memberDto.setUuid(uuid);
        memberDto.setMemberUuid(uuid);

        // [puppycat_member]
        insert(memberDto);
        // [puppycat_member_info]
        insertInfo(memberDto);
        // [puppycat_member_pw]
        insertPassword(memberDto);
        // [puppycat_member_profile_img]
        insertProfileImg(memberDto);
        // [puppycat_member_profile_intro]
        insertProfileIntro(memberDto);
        // [sns_member_follow_cnt] curl 통신
        String jsonString = joinCurlService.socialMemberJoin(memberDto.getUuid());

        JSONObject joinJsonObject = new JSONObject(jsonString);

        if (!(boolean) joinJsonObject.get("result")) {
            throw new CurlException(joinJsonObject);
        }

        JSONObject joinJsonResult = (JSONObject) joinJsonObject.get("data");
        boolean snsJoinResult = (boolean) joinJsonResult.get("result");

        if (!snsJoinResult) {
            throw new CustomException(CustomError.JOIN_FAIL); // 회원가입에 실패하였습니다.
        }

        // 간편가입일 경우
        if (isSimple == 1) {
            // [puppycat_member_simple]
            insertSimple(memberDto);
        }

        // 채널톡 해시값 인서트
        if (useChannelHash) {
            String hashId = ChannelTalkLibrary.encode(memberDto.getUuid());
            memberDto.setHashId(hashId);
            insertChannelTalk(memberDto);
        }

        // 회원 가입 시 알림 설정 값 [마더 값 푸쉬[1] : on, 이벤트[2] 선택, 야간[3] : on, 산책[4] : on]
        insertNotificationInitSetting(memberDto);
        insertNotificationSubInitSetting(memberDto);

        data.put("isAvailableJoin", true); // 가입 가능
        data.put("id", "");         // 이미 존재하는 아이디
        data.put("simpleType", ""); // 간편 회원가입 유형

        return data;
    }

    /**
     * 회원 가입 v2
     *
     * @param memberDto (id,pw,partner,isSimple,pwConfirm)
     * @return
     * @throws Exception
     */
    @Transactional
    public Map<String, Object> registV2(MemberDto memberDto) throws Exception {

        // 아이디/패스워드 검증
        String id = memberDto.getId();
        String password = memberDto.getPassword();
        String passwordConfirm = memberDto.getPasswordConfirm();
        String simpleId = memberDto.getSimpleId();
        Integer isSimple = memberDto.getIsSimple();
        String simpleType = memberDto.getSimpleType();
        Integer selectPolicy3 = memberDto.getSelectPolicy_3();
        // 리턴할 데이터
        Map<String, Object> data = new HashMap();

        // validate
        if (id == null || id.equals("")) { // 아이디 확인
            throw new CustomException(CustomError.JOIN_ID_ERROR);
        }

        if (password == null || password.equals("")) { // 패스워드 확인
            throw new CustomException(CustomError.JOIN_PW_ERROR);
        }

        if (passwordConfirm == null || passwordConfirm.equals("")) { // 패스워드 확인
            throw new CustomException(CustomError.JOIN_PW_ERROR);
        }

        if (simpleId == null || simpleId.equals("")) { // 심플 아이디 체크
            throw new CustomException(CustomError.JOIN_SIMPLE_ID_ERROR);
        }

        if (isSimple == 1 && (simpleType == null || simpleType.equals(""))) { // 간편가입 타입 확인
            throw new CustomException(CustomError.SIMPLE_JOIN_ERROR);
        }

        MemberNickDto memberNickDto = new MemberNickDto();
        memberNickDto.setNick(memberDto.getNick());

        if (memberDto.getNick() == null || memberDto.getNick().equals("") || !memberNickService.checkNick(memberNickDto)) { // 닉네임 확인
            throw new CustomException(CustomError.JOIN_NICK_ERROR);
        }

        if (memberDto.getName() == null || memberDto.getName().equals("")) { // 이름 확인
            throw new CustomException(CustomError.JOIN_NAME_ERROR);
        }

        if (memberDto.getCi() == null || memberDto.getCi().equals("")) { // CI 확인
            throw new CustomException(CustomError.JOIN_CI_ERROR);
        }

        if (memberDto.getDi() == null || memberDto.getDi().equals("")) { // DI 확인
            throw new CustomException(CustomError.JOIN_DI_ERROR);
        }

        if (memberDto.getGender() == null || memberDto.getGender().equals("")) { // 성별 확인
            throw new CustomException(CustomError.JOIN_GENDER_ERROR);
        }

        if (memberDto.getBirth() == null || memberDto.getBirth().equals("")) { // 생년월일 확인
            throw new CustomException(CustomError.JOIN_BIRTH_ERROR);
        }

        if (memberDto.getPhone() == null || memberDto.getPhone().equals("")) { // 핸드폰 확인
            throw new CustomException(CustomError.JOIN_PHONE_ERROR);
        }

        if (selectPolicy3 == null || selectPolicy3.equals("") || selectPolicy3 > 1) { // 선택 사항 값 체크
            throw new CustomException(CustomError.SELECT_POLICY_ERROR);
        }

        if (memberDto.getAccessToken() == null || memberDto.getAccessToken().equals("")) { // Access Token 확인
            throw new CustomException(CustomError.JOIN_ACCESS_TOKEN_ERROR);
        }

        if (memberDto.getRefreshToken() == null || memberDto.getRefreshToken().equals("")) { // Refresh Token 확인
            throw new CustomException(CustomError.JOIN_REFRESH_TOKEN_ERROR);
        }

        // 본인인증 값 복호화
        // 이름 복호화
        String name = securityLibrary.aesDecrypt(memberDto.getName());
        // 휴대폰번호 복호화
        String phone = securityLibrary.aesDecrypt(memberDto.getPhone());
        // 성별 복호화
        String gender = securityLibrary.aesDecrypt(memberDto.getGender());
        // 생일 복호화
        String birth = securityLibrary.aesDecrypt(memberDto.getBirth());
        // ci 복호화
        String ci = securityLibrary.aesDecrypt(memberDto.getCi());
        // di 복호화
        String di = securityLibrary.aesDecrypt(memberDto.getDi());

        // 복호화 값 세팅
        memberDto.setName(name);
        memberDto.setPhone(phone);
        memberDto.setGender(gender);
        memberDto.setBirth(birth);
        memberDto.setCi(ci);
        memberDto.setDi(di);

        // 만 나이 가져오기
        int age = dateLibrary.calculateAge(memberDto.getBirth());

        if (age < 14) {
            throw new CustomException(CustomError.JOIN_AGE_UNDER);
        }

        // 일반 회원가입인 경우
        if (isSimple == 0) {

            // 아이디 중복체크
            memberDto.setState(1); // 가입된 상태만 검색
            Boolean bDupleId = checkDupleId(memberDto);

            if (Boolean.TRUE.equals(bDupleId)) {
                throw new CustomException(CustomError.ID_DUPLE); // 이미 존재하는 아이디입니다.
            }

            // 일반 회원가입 아이디는 이메일로
            if (!isEmail(id)) {
                throw new CustomException(CustomError.JOIN_ID_EMAIL_ERROR); // 아이디는 이메일로 입력해주세요.
            }

            // 패스워드 일치 확인
            if (!password.equals(passwordConfirm)) {
                throw new CustomException(CustomError.PASSWORD_CONFIRM); // 패스워드를 동일하게 입력해주세요.
            }

        }

        // 패스워드 암호화
        memberDto.setPassword(super.encrypt(password));

        // 회원 uuid 세팅 ( locale Language + 랜덤 UUID + Timestamp )
        String localeLang = super.getLocaleLang();
        String setUuid = dateLibrary.getTimestamp();
        String uuid = localeLang + UUID.randomUUID().toString().concat(setUuid);
        uuid = uuid.replace("-", "");

        // 48자 이상이면 48자로 자르기
        if (uuid.length() > 48) {
            uuid = uuid.substring(0, 49);
        }

        // 고유아이디 중복체크
        Boolean bDupleUuid = checkDupleUuid(uuid);

        // 고유아이디가 중복이면 5번 재시도
        int retry = 0;
        while (Boolean.TRUE.equals(bDupleUuid) && retry < 5) {
            retry++;
            pushAlarm("회원 고유아이디 중복 시도::" + retry + "번째");

            // 회원 uuid 세팅 ( locale Language + 랜덤 UUID + Timestamp )
            localeLang = super.getLocaleLang();
            setUuid = dateLibrary.getTimestamp();
            uuid = "mem_" + localeLang + UUID.randomUUID().toString().concat(setUuid);
            uuid = uuid.replace("-", "");

            bDupleUuid = checkDupleUuid(uuid);

            if (retry == 5) {
                throw new CustomException(CustomError.UUID_DUPLE);
            }
        }

        //같은 CI / SIMPLE_TYPE 회원 가입 불가
        if (Boolean.TRUE.equals(checkDupleCi(memberDto))) {
            // 이미 존재하는 아이디 조회
            String socialId = memberDaoSub.getMemberIdByCi(memberDto);
            data.put("isAvailableJoin", false); // 중복
            data.put("simpleType", simpleType); // 간편 회원가입 유형
            data.put("id", socialId);           // 이미 존재하는 아이디

            return data;
        }

        // 고유아이디 세팅
        memberDto.setUuid(uuid);
        memberDto.setMemberUuid(uuid);

        // [puppycat_member]
        insert(memberDto);
        // [puppycat_member_info]
        insertInfo(memberDto);
        // [puppycat_member_pw]
        insertPassword(memberDto);
        // [puppycat_member_profile_img]
        insertProfileImg(memberDto);
        // [puppycat_member_profile_intro]
        insertProfileIntro(memberDto);
        // [puppycat_member_point}
        insertPoint(memberDto);

        // [sns_member_follow_cnt] curl 통신
        String jsonString = joinCurlService.socialMemberJoin(memberDto.getUuid());

        JSONObject joinJsonObject = new JSONObject(jsonString);

        if (!(boolean) joinJsonObject.get("result")) {
            throw new CurlException(joinJsonObject);
        }

        JSONObject joinJsonResult = (JSONObject) joinJsonObject.get("data");
        boolean snsJoinResult = (boolean) joinJsonResult.get("result");

        if (!snsJoinResult) {
            throw new CustomException(CustomError.JOIN_FAIL); // 회원가입에 실패하였습니다.
        }

        // 간편가입일 경우
        if (isSimple == 1) {
            // [puppycat_member_simple]
            insertSimple(memberDto);
        }

        // 채널톡 해시값 인서트
        if (useChannelHash) {
            String hashId = ChannelTalkLibrary.encode(memberDto.getUuid());
            memberDto.setHashId(hashId);
            insertChannelTalk(memberDto);
        }

        // 회원 가입 시 알림 설정 값 [마더 값 푸쉬[1] : on, 이벤트[2] 선택, 야간[3] : on, 산책[4] : on]
        insertNotificationInitSetting(memberDto);
        insertNotificationSubInitSetting(memberDto);

        data.put("isAvailableJoin", true); // 가입 가능
        data.put("id", "");         // 이미 존재하는 아이디
        data.put("simpleType", ""); // 간편 회원가입 유형

        return data;
    }

    /**
     * 회원 포인트 테이블 등록(v2)
     *
     * @param memberDto : memberUuid
     */
    private void insertPoint(MemberDto memberDto) {
        MemberPointDto pointDto = MemberPointDto.builder()
                .memberUuid(memberDto.getMemberUuid())
                .regdate(dateLibrary.getDatetime()).build();

        int result = memberDao.insertPoint(pointDto);

        if (result < 1) {
            throw new CustomException(CustomError.JOIN_FAIL); // 회원가입에 실패하였습니다.
        }
    }
    /*****************************************************
     *  SubFunction - Select
     ****************************************************/

    /*****************************************************
     *  SubFunction - Insert
     ****************************************************/

    /**
     * 회원 등록
     *
     * @param memberDto id, uuid, is_simple
     * @return insertedIdx
     */
    public void insert(MemberDto memberDto) {

        HttpServletRequest request = ServerLibrary.getCurrReq();
        memberDto.setJoinIp(super.getClientIP(request));
        memberDto.setLoginIp(super.getClientIP(request));
        memberDto.setLastLogin(dateLibrary.getDatetime());
        memberDto.setRegDate(dateLibrary.getDatetime());

        int result = memberDao.insert(memberDto);

        if (result < 1) {
            throw new CustomException(CustomError.JOIN_FAIL); // 회원가입에 실패하였습니다.
        }
    }

    /**
     * 회원 정보 등록
     *
     * @param memberDto email, name, phone, gender, birth
     */
    public void insertInfo(MemberDto memberDto) {
        int result = memberDao.insertInfo(memberDto);

        if (result < 1) {
            throw new CustomException(CustomError.JOIN_FAIL);
        }
    }

    /**
     * 회원 비밀번호 등록
     *
     * @param memberDto pw
     */
    public void insertPassword(MemberDto memberDto) {
        memberDto.setRegDate(dateLibrary.getDatetime());
        memberDto.setModiDate(dateLibrary.getDatetime());
        int result = memberDao.insertPassword(memberDto);

        if (result < 1) {
            throw new CustomException(CustomError.JOIN_FAIL);
        }
    }

    /**
     * 회원 프로필 이미지 등록
     *
     * @param memberDto uuid
     */
    public void insertProfileImg(MemberDto memberDto) {
        memberDto.setRegDate(dateLibrary.getDatetime());
        int result = memberImageDao.insertInitImage(memberDto);

        if (result < 1) {
            throw new CustomException(CustomError.JOIN_FAIL);
        }

    }

    /**
     * 회원 소개글 등록
     *
     * @param memberDto
     */
    public void insertProfileIntro(MemberDto memberDto) {
        MemberIntroDto memberIntroDto = new MemberIntroDto();
        memberIntroDto.setMemberUuid(memberDto.getUuid());
        memberIntroDto.setIntro("");
        memberIntroDto.setRegDate(dateLibrary.getDatetime());
        int result = memberIntroDao.insertIntro(memberIntroDto);

        if (result < 1) {
            throw new CustomException(CustomError.JOIN_FAIL);
        }
    }

    /**
     * 회원 가입시 푸쉬, 알림 초기 값 셋팅
     *
     * @param memberDto
     */
    public void insertNotificationInitSetting(MemberDto memberDto) {
        //회원 가입 시 알림 설정 값 [푸쉬[1] : on, 이벤트[2] 선택, 야간[3] : on, 산책[4] : on]
        List<NotificationSettingDto> lInitDataList = new ArrayList<NotificationSettingDto>();

        for (int count = 1; count < notificationTotalCount; count++) {
            //푸쉬, 알림 설정
            NotificationSettingDto notificationSettingDto = new NotificationSettingDto();
            notificationSettingDto.setMemberUuid(memberDto.getMemberUuid());
            notificationSettingDto.setType(count);

            // 이벤트 푸쉬 인지 체크
            if (count == 2) {
                notificationSettingDto.setState(memberDto.getSelectPolicy_3());
            } else {
                notificationSettingDto.setState(1);
            }

            notificationSettingDto.setRegDate(dateLibrary.getDatetime());
            lInitDataList.add(notificationSettingDto);
        }

        notificationSettingService.insertNotificationInitSetting(lInitDataList);
    }

    /**
     * 회원 가입시 푸쉬, 알림 Sub 초기 값 셋팅
     *
     * @param memberDto
     */
    public void insertNotificationSubInitSetting(MemberDto memberDto) {
        //회원 가입 시 알림 설정 값 [푸쉬[1] : on, 이벤트[2] 선택, 야간[3] : on]
        List<NotificationSettingDto> lInitDataList = new ArrayList<>();

        //type1 setting 기본 푸쉬
        for (int notiSubCount = 1; notiSubCount < notificationSubTotalCount; notiSubCount++) {
            setNotificationSettingInitData(lInitDataList, 1, notiSubCount, 1, memberDto);
        }

        //type2 setting 이벤트 푸쉬
        setNotificationSettingInitData(lInitDataList, 2, 1, memberDto.getSelectPolicy_3(), memberDto);

        //type3 setting 이벤트 푸쉬
        setNotificationSettingInitData(lInitDataList, 3, 1, 1, memberDto);

        //type4 setting 이벤트 푸쉬
        for (int walkNotiSubCount = 1; walkNotiSubCount < walkNotificationSubTotalCount; walkNotiSubCount++) {
            setNotificationSettingInitData(lInitDataList, 4, walkNotiSubCount, 1, memberDto);
        }

        notificationSettingService.insertNotificationSubInitSetting(lInitDataList);
    }

    /**
     * 회원 가입시 푸쉬, 알림 Sub 초기 값 셋팅
     */
    public void setNotificationSettingInitData(List<NotificationSettingDto> lInitDataList, int notiType, int subType, int state, MemberDto memberDto) {
        //푸쉬, 알림 설정
        NotificationSettingDto notificationSettingDto = new NotificationSettingDto();
        notificationSettingDto.setMemberUuid(memberDto.getMemberUuid());
        notificationSettingDto.setNotiType(notiType);
        notificationSettingDto.setSubType(subType);
        notificationSettingDto.setState(state);
        notificationSettingDto.setRegDate(dateLibrary.getDatetime());
        lInitDataList.add(notificationSettingDto);
    }

    /**
     * 소셜 로그인 등록
     *
     * @param memberDto simpleId, simpleType, authToken(refresh token)
     */
    public void insertSimple(MemberDto memberDto) {
        memberDao.insertSimple(memberDto);
    }

    /**
     * 채널톡 해시값 인서트
     *
     * @param memberDto hashId
     */
    public void insertChannelTalk(MemberDto memberDto) {
        memberDao.insertChannelTalk(memberDto);
    }

    /*****************************************************
     *  SubFunction - Update
     ****************************************************/

    /*****************************************************
     *  SubFunction - Delete
     ****************************************************/

}
