package com.architecture.admin.libraries.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.EnumSet;

/**
 * ****** 오류코드 작성 규칙 ******
 * - 영문4자 와  숫자4자리로 구성 ex) ELGI-9999
 * - 앞4자리 영문은 기능이나 페이지를 알 수 있도록 작성
 * - 뒤4자리 숫자는 아래 규칙에 따라 분류
 * 오류번호   /   설명
 * 1000    =   정상
 * 2xxx    =   필수값 없음
 * 3xxx    =   유효성오류
 * 4xxx    =   sql구문오류
 * 5xxx    =   DB데이터오류
 * 6xxx    =   파일오류
 * 7xxx    =   권한오류
 * 9xxx    =   기타오류
 */
public enum CustomError {
    // EBAD : 유저의 잘못된 요청
    BAD_REQUEST("MEBAD-3999", "lang.common.exception.bad.request")                            // 잘못된 요청입니다.(bad request 공통)
    , BAD_REQUEST_PARAMETER_TYPE_MISMATCH("EBAD-3998", "lang.common.exception.bad.request")  // 잘못된 요청입니다.(메소드로 넘어오는 파라미터의 타입 미스매치등)
    , BAD_REQUEST_REQUIRED_VALUE("EBAD-3997", "lang.common.exception.bad.required.value")    // 필수값을 입력해주세요.
    , EMPTY_LIST("1000", "lang.common.exception.empty.list")

    // ESER : 서버 오류(SQL,DB)
    , SERVER_DATABASE_ERROR("ESER-5999", "lang.common.exception.server.database")   // 죄송합니다.서버에 문제가 발생했습니다.잠시후 다시 이용해주세요.
    , SERVER_SQL_ERROR("ESER-5998", "lang.common.exception.server.database")        // 죄송합니다.서버에 문제가 발생했습니다.잠시후 다시 이용해주세요.

    // EDAT
    , DATE_START_DATE_EMPTY("EDAT-2999", "lang.common.exception.start.date.empty") // 시작 날짜가 비었습니다.
    , DATE_END_DATE_EMPTY("EDAT-2998", "lang.common.exception.end.date.empty")     // 종료 날짜가 비었습니다.
    , DATE_PERIOD_EMPTY("EDAT-2997", "lang.common.exception.period.empty")         // 기간이 비었습니다.

    // ECOM : 공통 오류
    , TOKEN_ERROR("ECOM-9999", "lang.member.exception.token")                           // 토큰 값 에러 다시 로그인 해주세요.
    , REGIST_ERROR("ECOM-9998", "lang.common.exception.regist")                         // 입력 실패
    , MODIFY_ERROR("ECOM-9997", "lang.common.exception.modify")                         // 입력 실패
    , SWITCH_FALSE_ERROR("ECOM-9998", "lang.common.exception.switch.false")             // 이용 불가한 기능입니다.
    , NOT_PATTERN_PHONE("ECOM-8686", "lang.member.exception.not.pattern.phone")         // 정상적 핸드폰 번호가 아닙니다.

    // ENTI : 알림 관련 오류
    , NOTI_IDX_ERROR("ENTI-3999", "lang.noti.exception.memberIdx")                   // 존재하지 않는 회원입니다
    , NOTI_SENDERUUID_ERROR("ENTI-2999", "lang.noti.exception.senderUuid.empty")     // 알림 보내는 회원 IDX가 비어있습니다.
    , NOTI_MEMBERUUID_ERROR("ENTI-2998", "lang.noti.exception.memberUuid.empty")     // 알림 받는 회원 IDX가 비어있습니다.
    , NOTI_CONTENTSIDX_ERROR("ENTI-2997", "lang.noti.exception.contentsIdx.null")    // 알림 보낼 컨텐츠 IDX가 비어있습니다.
    , NOTI_SUBTYPE_ERROR("ENTI-2996", "lang.noti.exception.subType.null")            // 서브타입을 입력해주세요.
    , NOTI_COMMENTIDX_ERROR("ENTI-2995", "lang.noti.exception.commentIdx.null")      // 알림 보낼 댓글 IDX가 비어있습니다.

    // ELGI : 로그인 관련 오류
    , LOGIN_FAIL("ELGI-9999", "lang.login.exception.login.fail")                  // 로그인이 실패하였습니다.
    , MEMBER_STATE_ERROR("ELGI-3999", "lang.login.exception.state")               // 계정상태를 확인해주세요.
    , RESTORE_ERROR("ELGI-3998", "lang.login.exception.restore")                  // 복구 불가능한 계정입니다.
    , ALREADY_RESTORE_ERROR("ELGI-3997", "lang.login.exception.already.restore")  // 이미 복구된 계정입니다.
    , LOGIN_ID_ERROR("ELGI-2999", "lang.login.exception.id.null")                 // 아이디를 입력해주세요
    , LOGIN_PW_ERROR("ELGI-2998", "lang.login.exception.password")                // 패스워드를 입력해주세요
    , LOGIN_SIMPLE_TYPE_ERROR("ELGI-2997", "lang.login.exception.simple.type")    // 심플 타입 값이 없습니다.
    , LOGIN_IS_SIMPLE_ERROR("ELGI-2996", "lang.login.exception.is.simple.type")   // 가입 종류 값이 없습니다.
    , LOGIN_APP_KEY_ERROR("ELGI-2995", "lang.login.exception.app.key")            // App Key 값이 없습니다.
    , LOGIN_APP_VER_ERROR("ELGI-2994", "lang.login.exception.app.ver")            // App Ver 값이 없습니다.
    , LOGIN_DOMAIN_ERROR("ELGI-2993", "lang.login.exception.domain")              // Domain 값이 없습니다.
    , LOGIN_ISO_ERROR("ELGI-2992", "lang.login.exception.iso")                    // Iso 값이 없습니다.
    , LOGIN_FCM_TOKEN_ERROR("ELGI-2991", "lang.login.exception.fcm.token")        // FCM 토큰 값 이 없습니다.
    , LOGIN_SIMPLE_ID_ERROR("ELGI-2990", "lang.login.exception.simpleId")         // 소셜 고유아이디 값이 없습니다.

    // ELGO : 로그아웃 관련 오류
    , LOGOUT_FAIL("ELGO-9999", "lang.login.exception.logout.fail")          // 로그아웃에 실패하였습니다.

    // SPME : 푸시 세팅 관련 오류
    , SETTING_PUSH_MAIN_ERROR("SPME-8888", "lang.setting.exception.main.error")           // Main 값이 유효하지 않습니다.
    , SETTING_PUSH_SUB_ERROR("SPME-8887", "lang.setting.exception.sub.error")           // Sub 값이 유효하지 않습니다.

    // JCER : 본인인증 관련 오류
    , JOIN_CERTIFICATION_ERROR("JCER-8888", "lang.login.exception.certification.error")     // 본인 인증 중 오류가 발생하였습니다.
    , JOIN_CERTIFICATION_CANCEL("JCER-9999", "lang.login.exception.certification.cancel")     // 본인 인증 취소하였습니다.

    // EJOI : 회원가입 관련 오류
    , JOIN_FAIL("EJOI-9999", "lang.login.exception.join.fail")               // 회원가입에 실패하였습니다.
    , JOIN_NICK_FAIL("EJOI-9998", "lang.login.exception.nick.fail")          // 닉네임 등록 실패
    , PASSWORD_CONFIRM("EJOI-3999", "lang.login.exception.password.confirm") // 패스워드를 동일하게 입력해주세요.
    , ID_DUPLE("EJOI-3998", "lang.login.exception.id.duple")                 // 이미 존재하는 아이디입니다.
    , JOIN_ID_EMAIL_ERROR("EJOI-3997", "lang.login.exception.id.email")      // 아이디는 이메일로 입력해주세요.
    , SIMPLE_JOIN_ERROR("EJOI-3996", "lang.login.exception.simple.join")     // 간편 가입으로 진입 해 주세요
    , UUID_DUPLE("EJOI-3995", "lang.login.exception.uuid.duple")             // 이미 존재하는 아이디입니다.
    , JOIN_POLICY_MUST("EJOI-3994", "lang.login.exception.policy.must")      // 약관 필수값에 동의해주세요
    , JOIN_REDIRECT("EJOI-7777", "lang.login.exception.join.redirect")       // 회원 가입 페이지로 이동합니다.
    , SELECT_POLICY_ERROR("EJOI-3993", "lang.login.exception.select.policy.null")     // 비정상적인 선택정책 값입니다.
    , JOIN_ID_ERROR("EJOI-2999", "lang.login.exception.id.null")             // 아이디를 입력해주세요
    , JOIN_PW_ERROR("EJOI-2998", "lang.login.exception.password")            // 패스워드를 입력해주세요
    , JOIN_SOCIAL_ERROR("EJOI-2997", "lang.login.exception.social.fail")     // 소셜 코드 조회 실패
    , GOOGLE_SOCIAL_ERROR("EJOI-2996", "lang.login.exception.google.fail")   // 구글 조회 실패
    , NAVER_SOCIAL_ERROR("EJOI-2995", "lang.login.exception.naver.fail")     // 네이버 조회 실패
    , JOIN_POLICY_EMPTY("EJOI-2994", "lang.login.exception.policy.empty")    // 약관 필수값이 넘어오지 않았습니다
    , KAKAO_SOCIAL_ERROR("EJOI-2993", "lang.login.exception.kakao.fail")     // 카카오 조회 실패
    , JOIN_NICK_ERROR("EJOI-2992", "lang.login.exception.nick.null")           // 닉네임 입력 해주세요
    , JOIN_NAME_ERROR("EJOI-2991", "lang.login.exception.name.null")           // 이름 입력 해주세요
    , JOIN_GENDER_ERROR("EJOI-2990", "lang.login.exception.gender.null")       // 성별 입력 해주세요
    , JOIN_BIRTH_ERROR("EJOI-2989", "lang.login.exception.birth.null")         // 생년월일 입력 해주세요
    , JOIN_PHONE_ERROR("EJOI-2988", "lang.login.exception.phone.null")         // 핸드폰 번호 입력 해주세요
    , JOIN_ACCESS_TOKEN_ERROR("EJOI-2987", "lang.login.exception.access.token.null")      // Access Token 입력 해주세요
    , JOIN_REFRESH_TOKEN_ERROR("EJOI-2986", "lang.login.exception.refresh.token.null")    // Refresh Token 입력 해주세요
    , JOIN_EVENT_PUSH_EMPTY("EJOI-2985", "lang.login.exception.event.push.null")          // EventPush 값에 오류가 있습니다.
    , JOIN_CI_ERROR("EJOI-2984", "lang.login.exception.ci.null")                    // CI 값에 오류가 있습니다.
    , JOIN_DI_ERROR("EJOI-2983", "lang.login.exception.di.null")                    // DI 값에 오류가 있습니다.
    , JOIN_SAME_CI_ERROR("EJOI-2982", "lang.login.exception.same.ci")               // 이미 본인인증을 사용한 계정이 있습니다.
    , JOIN_SIMPLE_ID_ERROR("EJOI-2981", "lang.login.exception.simple.id.error")     // simple ID가 오류가 있습니다..
    , JOIN_AGE_UNDER("EJOI-2980", "lang.login.exception.age.under.error")           // 만 14세 이상 가입 가능합니다.
    , SIMPLE_JOIN_DUPLE("SIJD-3999", "lang.login.exception.simple.join.duple")      // 이미 가입된 계정이 있습니다.

    // EMEM : 회원 관련 오류
    , MEMBER_UUID_ERROR("EMEM-3998", "lang.member.exception.uuid.error")       // 회원 UUID가 유효하지 않습니다.
    , MEMBER_UUID_EMPTY("EMEM-2998", "lang.member.exception.uuid.empty")       // 회원 UUID가 비었습니다.

    // EOUT : 회원 탈퇴 관련 오류
    , MEMBER_OUT("EOUT-1000", "lang.member.out")                                      // 탈퇴한 유저 입니다.
    , MEMBER_OUT_FAIL("EOUT-9999", "lang.member.out.exception")                       // 회원탈퇴에 실패하였습니다.
    , MEMBER_OUT_CODE_EMPTY("EOUT-2999", "lang.member.out.exception.code.empty")      // 탈퇴사유를 선택해주세요.
    , MEMBER_OUT_REASON_EMPTY("EOUT-2998", "lang.member.out.exception.reason.empty")  // 탈퇴 상세사유를 입력해주세요.
    , MEMBER_OUT_CODE_INVALID("EOUT-3999", "lang.member.out.exception.code.invalid")  // 유효하지 않은 탈퇴사유입니다.
    , MEMBER_OUT_STATE("EOUT-7777", "lang.member.out.exception.code.out.state")       // 탈퇴 대기 상태입니다.
    , MEMBER_OUT_COMPLETE_STATE("EOUT-6666", "lang.member.out.exception.code.out.complete.state")       // 탈퇴 상태입니다.

    // ENIC : 닉네임 관련 오류퇴
    , NICK_CHANGE_FAIL("ENIC-9999", "lang.member.exception.nick.change.fail")   // 닉네임 변경 실패
    , NICK_CHECK_FAIL("ENIC-9998", "lang.member.exception.nick.check.fail")     // 닉네임 검사 실패
    , NICK_EMPTY("ENIC-2999", "lang.member.exception.nick.empty")               // 닉네임값이 비어있습니다.
    , NICK_MEMBER_EMPTY("ENIC-2998", "lang.member.exception.idx.empty")         // 회원 IDX가 존재하지 않습니다.
    , NICK_MEMBERUUID_EMPTY("ENIC-2997", "lang.member.exception.uuid.empty")    // 회원 UUID가 비었습니다.
    , NICK_LENGTH_ERROR("ENIC-3999", "lang.member.exception.nick.length")       // 최소 2자 이상 최대 20자 이하만 입력할 수 있습니다
    , NICK_STRING_ERROR("ENIC-3998", "lang.member.exception.nick.string")       // 사용할 수 없는 문자가 포함되어 있습니다.
    , NICK_DUPLE("ENIC-3997", "lang.member.exception.nick.duple")               // 이미 존재하는 닉네임입니다.
    , NICK_HAVE("ENIC-3996", "lang.member.exception.nick.have")                 // 이미 닉네임을 등록한 회원입니다

    // EPIM : 프로필 이미지 관련 오류
    , PROFILE_IMAGE_ERROR("EPIM-9999", "lang.member.exception.profile.image.regist.fail")           // 프로필 이미지 등록에 실패하였습니다.
    , PROFILE_IMAGE_UPDATE_ERROR("EPIM-9998", "lang.member.exception.profile.image.update.fail")    // 프로필 이미지 수정에 실패하였습니다.
    , PROFILE_IMAGE_LOG_ERROR("EPIM-9997", "lang.member.exception.profile.image.log.reigst.fail")   // 이미지 로그 등록에 실패하였습니다.
    , PROFILE_IMAGE_EMPTY("EPIM-2999", "lang.member.exception.profile.image.empty")             // 이미지를 등록해주세요.
    , PROFILE_IMAGE_UID_DUPLE("EPIM-3999", "lang.member.exception.profile.image.uuid.duple")    // 이미 존재하는 이미지 고유아이디입니다.

    // EPIN : 프로필 소개글 관련 오류
    , PROFILE_INTRO_ERROR("EPIN-9999", "lang.member.exception.profile.intro.regist.fail")           // 프로필 소개글 등록에 실패하였습니다.
    , PROFILE_INTRO_UPDATE_ERROR("EPIN-9998", "lang.member.exception.profile.intro.update.fail")    // 프로필 소개글 수정에 실패하였습니다.
    , PROFILE_INTRO_LOG_ERROR("EPIN-9997", "lang.member.exception.profile.intro.log.reigst.fail")   // 소개글 로그 등록에 실패하였습니다.
    , PROFILE_INTRO_EMPTY("EPIN-2999", "lang.member.exception.profile.intro.empty")                 // 소개글을 등록해주세요.
    , PROFILE_INTRO_LIMIT_ERROR("EPIN-3999", "lang.member.exception.profile.intro.limit.over")      // 최대 입력 가능 글자수를 초과하였습니다.

    // ENSA : 인증 관련 오류
    , NOT_SAME_APP_KEY("ENSA-2999", "lang.login.exception.not.same.app.key")    // App Key 값이 없습니다.

    // ERES : 제재 관련 오류
    , RESTRAIN_MEMBER("ERES-9999", "lang.restrain.exception")                       // 제재 회원입니다.
    , RESTRAIN_LOGIN("ERES-9998", "lang.restrain.exception.login")                  // 로그인 제재 회원입니다.
    , RESTRAIN_WRITE("ERES-9997", "lang.restrain.exception.write")                  // 글작성 제재 회원입니다.
    , RESTRAIN_CONTENTS("ERES-9996", "lang.restrain.exception.contents")            // 컨텐츠 작성 제재 회원입니다.
    , RESTRAIN_COMMENT("ERES-9995", "lang.restrain.exception.comment")              // 댓글 작성 제재 회원입니다.
    , RESTRAIN_WALK("ERES-9994", "lang.restrain.exception.walk")                    // 산책 제재 회원입니다.
    , RESTRAIN_MEMBERUUID_EMPTY("ERES-2999", "lang.member.exception.uuid.empty")    // 회원 UUID가 비었습니다.
    , RESTRAIN_TYPE_EMPTY("ERES-2998", "lang.restrain.exception.type")              // 제재 타입이 비어있습니다.

    // EIMG : 이미지 관련 오류
    , IMAGE_NOT_EXIST_ERROR("EIMG-9999", "lang.common.exception.image.not.exist")           // 이미지가 존재하지 않습니다.
    , IMAGE_EXTENSION_ERROR("EIMG-9998", "lang.common.exception.image.extension.error")     // 허용하지않는 확장자를 가진 파일입니다.
    , IMAGE_SIZE_LIMIT_OVER("EIMG-9997", "lang.common.exception.image.size.error")          // 등록할 이미지 용량이 너무 큽니다.

    // EBWO : 금칙어 관련 오류
    , WORD_EMPTY("EBWO-99999", "lang.common.exception.word.empty")                    // 검사할 단어가 존재하지 않습니다.
    , WORD_TYPE_EMPTY("EBWO-9998", "lang.common.exception.word.type.empty")           // 금칙어 타입이 존재하지 않습니다.

    // ERTE : 토큰 관련 오류
    , TOKEN_EXPIRE_TIME_ERROR("ERTE-9999", "lang.token.expire.time.error.publish.fail")           // 유효 기간이 만료 되어 토큰 검증에 실패 하였습니다.
    , REFRESH_TOKEN_CREATE_ERROR("ERTE-9998", "lang.token.refresh.token.create.fail")             // 리프레시 토큰 생성에 실패하였습니다.
    , AUTHORIZATION_CODE_EXPIRE_REVOKE("ERTE-9997","lang.token.authorization.code.expire.revoke") // Authorization 코드가 만료되었습니다.
    , AUTHORIZATION_CODE_ALREADY_USED("ERTE-9996", "lang.token.authorization.code.already.used")  // 이미 사용된 Authorization 코드입니다.
    , AUTHORIZATION_CODE_EMPTY("ERTE-2999", "lang.token.authorization.code.empty")                // Authorization 코드가 비었습니다
    , CLIENTID_CODE_EMPTY("ERTE-2998", "lang.token.client.id.empty")                              // client Id 가비었습니다

    // ESEA : 검색 관련 오류
    , SEARCH_WORD_EMPTY("ESEA-9999", "lang.common.exception.search.word.empty") // 검색할 단어가 없습니다.

    // EMCS : 내 정보 수정
    , MY_INFO_CERTIFICATION_SAVE_ERROR("EMCS-9999", "lang.member.info.phone.update.fail") // 본인인증 정보 업데이트에 실패 하였습니다.

    // EPET : 반려동물 관련 오류
    , PET_UUID_ERROR("EPET-9999", "lang.pet.exception.uuid.error")         // 존재하지 반려동물 입니다.
    , PET_REGISTER_FAIL("EPET-9998", "lang.pet.register.exception")        // 반려동물 등록에 실패하였습니다.
    , PET_MEMBER_REGISTER_FAIL("EPET-9997", "lang.pet.member.register.exception")      // 반려동물 매핑 등록에 실패하였습니다.
    , PET_INFO_REGISTER_FAIL("EPET-9996", "lang.pet.info.register.exception")          // 반려동물 정보 등록에 실패하였습니다.
    , PET_PROFILE_IMG_REGISTER_FAIL("EPET-9995", "lang.pet.profile.img.register.exception")              // 프로필 이미지 등록 실패하였습니다.
    , PET_PROFILE_IMG_LOG_REGISTER_FAIL("EPET-9994", "lang.pet.profile.img.log.register.exception")      // 프로필 이미지 로그 등록 실패하였습니다.
    , PET_PROFILE_INTRO_REGISTER_FAIL("EPET-9993", "lang.pet.profile.intro.register.exception")          // 프로필 소개글 등록 실패하였습니다.
    , PET_PROFILE_INTRO_LOG_REGISTER_FAIL("EPET-9992", "lang.pet.profile.intro.log.register.exception")  // 프로필 소개글 로그 등록 실패하였습니다.
    , PET_PERSONALITY_REGISTER_FAIL("EPET-9991", "lang.pet.personality.register.exception") // 성격 등록 실패하였습니다.
    , PET_HEALTH_REGISTER_FAIL("EPET-9990", "lang.pet.health.register.exception")           // 건강질환 등록 실패하였습니다.
    , PET_ALLERGY_REGISTER_FAIL("EPET-9989", "lang.pet.allergy.register.exception")         // 알러지 등록 실패하였습니다.
    , PET_PERSONALITY_ETC_REGISTER_FAIL("EPET-9988", "lang.pet.personality.etc.register.exception")      // 성격 기타 등록 실패하였습니다.
    , PET_HEALTH_ETC_REGISTER_FAIL("EPET-9987", "lang.pet.health.etc.register.exception")                // 건강질환 기타 등록 실패하였습니다.
    , PET_ALLERGY_ETC_REGISTER_FAIL("EPET-9986", "lang.pet.allergy.etc.register.exception")              // 알러지 기타 등록 실패하였습니다.
    , PET_UPDATE_FAIL("EPET-9985", "lang.pet.exception.update")                         // 반려동물 수정에 실패하였습니다.
    , PET_PERSONALITY_UPDATE_FAIL("EPET-9984", "lang.pet.exception.personality.modify") // 성격 기타 수정에 실패하였습니다.
    , PET_HEALTH_UPDATE_FAIL("EPET-9983", "lang.pet.exception.health.modify")           // 건강질환 기타 수정에 실패하였습니다.
    , PET_ALLERGY_UPDATE_FAIL("EPET-9982", "lang.pet.exception.allergy.modify")         // 알러지 기타 수정에 실패하였습니다.
    , PET_PERSONALITY_DELETE_FAIL("EPET-9981", "lang.pet.exception.personality.delete") // 성격 삭제에 실패하였습니다.
    , PET_HEALTH_DELETE_FAIL("EPET-9980", "lang.pet.exception.health.delete")           // 건강질환 삭제에 실패하였습니다.
    , PET_ALLERGY_DELETE_FAIL("EPET-9979", "lang.pet.exception.allergy.delete")         // 알러지 삭제에 실패하였습니다.
    , PET_DELETE_FAIL("EPET-9978", "lang.pet.exception.pet.delete")                     // 반려동물 삭제에 실패하였습니다.
    , PET_PROFILE_IMG_UPDATE_FAIL("EPET-9977", "lang.pet.exception.image.update")       // 반려동물 프로필 이미지 수정에 실패하였습니다.
    , PET_PROFILE_INTRO_UPDATE_FAIL("EPET-9976", "lang.pet.exception.intro.update")     // 반려동물 프로필 소개글 수정에 실패하였습니다.
    , PET_UID_DUPLE("EPET-3999", "lang.pet.exception.uuid.duple")                       // 이미 존재하는 고유아이디입니다.
    , PET_NAME_TEXT_LIMIT_OVER("EPET-3998", "lang.pet.exception.name.text.limit.over")  // 이름은 최대 20자까지 입력 가능합니다.
    , PET_NUMBER_LIMIT_OVER("EPET-3997", "lang.pet.exception.number.limit.over")        // 등록번호는 15자 이상 입력 불가합니다.
    , PET_BREED_IDX_ERROR("EPET-3996", "lang.pet.exception.breed.idx.error")            // 존재하지 않는 품종 idx 입니다.
    , PET_HEALTH_IDX_ERROR("EPET-3995", "lang.pet.exception.health.idx.error")          // 존재하지 않는 건강질환 idx 입니다.
    , PET_ALLERGY_IDX_ERROR("EPET-3994", "lang.pet.exception.allergy.idx.error")        // 존재하지 않는 알러지 idx 입니다.
    , PET_GENDER_ERROR("EPET-3993", "lang.pet.exception.gender.error")                  // 존재하지 않는 성별입니다.
    , PET_SIZE_ERROR("EPET-3992", "lang.pet.exception.size.error")                      // 사이즈 값 오류
    , PET_WEIGHT_ERROR("EPET-3991", "lang.pet.exception.weight.error")                  // 몸무게 값 오류
    , PET_BIRTH_STRING_ERROR("EPET-3990", "lang.pet.exception.birth.string.error")      // 생년월일 형식 에러
    , PET_AGE_ERROR("EPET-3989", "lang.pet.exception.age.error")                        // 연령 값 오류
    , PET_INTRO_TEXT_LIMIT_OVER("EPET-3988", "lang.pet.exception.intro.text.limit.over")      // 소개는 50자 이상 입력 불가합니다.
    , PET_PERSONALITY_IDX_ERROR("EPET-3987", "lang.pet.exception.personality.idx.error")      // 존재하지 않는 성격코드 입니다.
    , PET_PERSONALITY_ETC_NULL("EPET-3986", "lang.pet.exception.personality.etc.null")        // 성격을 입력해주세요.
    , PET_HEALTH_ETC_NULL("EPET-3985", "lang.pet.exception.health.etc.null")                  // 건강질환을 입력해주세요.
    , PET_ALLERGY_ETC_NULL("EPET-3984", "lang.pet.exception.allergy.etc.null")                // 알러지를 입력해주세요.
    , PET_PERSONALITY_TEXT_LIMIT_OVER("EPET-3983", "lang.pet.exception.personality.text.limit.over")      // 성격은 200자 이상 입력 불가합니다.
    , PET_HEALTH_TEXT_LIMIT_OVER("EPET-3982", "lang.pet.exception.health.text.limit.over")                // 건강질환은 200자 이상 입력 불가합니다.
    , PET_ALLERGY_TEXT_LIMIT_OVER("EPET-3981", "lang.pet.exception.allergy.text.limit.over")              // 알러지는 200자 이상 입력 불가합니다.
    , PET_NAME_STRING_ERROR("EPET-3980", "lang.pet.exception.name.string")              // 사용할 수 없는 문자가 포함되어 있습니다.
    , PET_NAME_WORD_CHK_ERROR("EPET-3979", "lang.pet.exception.name.string")            // 사용할 수 없는 문자가 포함되어 있습니다.
    , PET_NOT_MY_PET("EPET-3978", "lang.pet.exception.not.my.pet.error")                // 나의 반려동물만 열람 가능합니다.
    , PET_NOT_MEMBER_OWN_PET("EPET-3977", "lang.pet.exception.not.member.own")          // 회원 소유의 반려동물이 아닙니다.
    , PET_NAME_NULL("EPET-2999", "lang.pet.exception.name.null")                        // 이름을 입력해주세요.
    , PET_BREED_IDX_NULL("EPET-2998", "lang.pet.exception.breed.idx.null")              // 품종을 선택해주세요.
    , PET_GENDER_NULL("EPET-2997", "lang.pet.exception.gender.null")    // 성별을 선택해주세요.
    , PET_SIZE_NULL("EPET-2996", "lang.pet.exception.size.null")        // 사이즈를 선택해주세요.
    , PET_WEIGHT_NULL("EPET-2995", "lang.pet.exception.weight.null")    // 몸무게를 입력해주세요.
    , PET_BIRTH_NULL("EPET-2994", "lang.pet.exception.birth.null")      // 생년월일을 입력해주세요.
    , PET_AGE_NULL("EPET-2993", "lang.pet.exception.age.null")          // 연령을 선택해주세요.
    , PET_PERSONALITY_IDX_NULL("EPET-2992", "lang.pet.exception.personality.idx.null") // 성격을 선택해주세요.
    , PET_HEALTH_IDX_NULL("EPET-2991", "lang.pet.exception.health.idx.null")   // 건강질환을 선택해주세요.
    , PET_ALLERGY_IDX_NULL("EPET-2990", "lang.pet.exception.allergy.idx.null") // 알러지를 선택해주세요.
    , PET_UUID_NULL("EPET-2989", "lang.pet.exception.uuid.empty")              // 반려동물 고유아이디가 비었습니다.


    // EPOI : 포인트 관련 오류
    , POINT_SAVE_ERROR("EPOI-9999", "lang.point.exception.save")                     // 포인트 적립에 실패하였습니다.
    , POINT_USE_ERROR("EPOI-9998", "lang.point.exception.use")                       // 포인트 사용에 실패하였습니다.
    , POINT_EXPIRE_DATE_ERROR("EPOI-9997", "lang.point.exception.expire.date")       // 포인트 만료일 형식이 올바르지 않습니다.
    , POINT_USE_OVER("EPOI-3999", "lang.point.exception.use.over")                   // 보유 포인트가 부족합니다.
    , POINT_EMPTY("EPOI-2999", "lang.point.exception.empty")                         // 포인트가 비었습니다.
    , POINT_EXPIRE_DATE_EMPTY("EPOI-2998", "lang.point.exception.expire.date.empty") // 포인트 만료일이 비었습니다.
    , POINT_TITLE_EMPTY("EPOI-2997", "lang.point.exception.title.empty")             // title이 비었습니다.
    , POINT_POSITION_EMPTY("EPOI-2996", "lang.point.exception.position.empty")       // position이 비었습니다.
    ,POINT_PRODUCT_ORDER_ID_EMPTY("EPOI-2995", "lang.point.exception.product.order.id.empty") // 상품 주문번호가 비었습니다.
    ;

    @Autowired
    MessageSource messageSource;
    private String code;
    private String message;

    CustomError(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return messageSource.getMessage(message, null, LocaleContextHolder.getLocale());
    }

    public CustomError setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
        return this;
    }

    @Component
    public static class EnumValuesInjectionService {

        @Autowired
        private MessageSource messageSource;

        // bean
        @PostConstruct
        public void postConstruct() {
            for (CustomError customError : EnumSet.allOf(CustomError.class)) {
                customError.setMessageSource(messageSource);
            }
        }
    }
}
