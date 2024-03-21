package com.architecture.admin.services.noti;

import com.architecture.admin.libraries.DateLibrary;
import com.architecture.admin.libraries.PaginationLibray;
import com.architecture.admin.libraries.exception.CurlException;
import com.architecture.admin.libraries.exception.CustomError;
import com.architecture.admin.libraries.exception.CustomException;
import com.architecture.admin.models.dao.noti.NotiDao;
import com.architecture.admin.models.daosub.member.OutMemberDaoSub;
import com.architecture.admin.models.daosub.noti.NotiDaoSub;
import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.member.MemberDto;
import com.architecture.admin.models.dto.noti.NotiDto;
import com.architecture.admin.services.BaseService;
import com.architecture.admin.services.contents.SnsContentsCurlService;
import com.architecture.admin.services.follow.FollowCurlService;
import com.architecture.admin.services.member.MemberService;
import com.architecture.admin.services.member.SnsMemberCurlService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*****************************************************
 * 알림 공통 모델러
 ****************************************************/
@Service
@RequiredArgsConstructor
public class NotiService extends BaseService {

    private final NotiDao notiDao;
    private final NotiDaoSub notiDaoSub;
    private final OutMemberDaoSub outMemberDaoSub;
    private final MemberService memberService;
    private final SnsMemberCurlService snsMemberCurlService;
    private final FollowCurlService followCurlService;
    private final SnsContentsCurlService snsContentsCurlService;
    @Value("${cloud.aws.s3.img.url}")
    private String defaultImgDomain;

    /*****************************************************
     *  Modules
     ****************************************************/
    public List<NotiDto> getNotiList(SearchDto searchDto) {
        List<NotiDto> notiList = new ArrayList<>();

        // 목록 전체 count
        Long totalCount = notiDaoSub.getTotalCount(searchDto);

        if (totalCount > 0) {
            // paging
            PaginationLibray pagination = new PaginationLibray(Math.toIntExact(totalCount), searchDto);
            searchDto.setPagination(pagination);

            // list
            notiList = notiDaoSub.getNotiList(searchDto);

            if (!notiList.isEmpty()) {
                // 문자 변환
                remakeInfo(notiList);
            }
        }

        // 보내는 회원의 정보가 비어있으면 제외
        List<NotiDto> filteredList = new ArrayList<>();
        for (NotiDto NotiDto : notiList) {
            // 알림 보낸 회원의 uuid 가 비어있지 않다면 보낸사람 정보 조회( 공지,이벤트는 senderUuid = "" )
            if (NotiDto.getSenderUuid() != null && !NotiDto.getSenderUuid().equals("")) {
                if (NotiDto.getSenderInfo() != null && !NotiDto.getSenderInfo().isEmpty()) {
                    filteredList.add(NotiDto);
                }
            } else {
                filteredList.add(NotiDto);
            }
        }

        return filteredList;
    }


    /**
     * 새로운 알림이 있는지 체크
     *
     * @param memberUuid 회원 uuid
     * @return true/false
     */
    public Boolean checkNoti(String memberUuid) {

        // 회원idx 정상적인 회원인지 체크
        MemberDto memberDto = new MemberDto();
        memberDto.setMemberUuid(memberUuid);
        boolean bMemberCheck = memberService.getCountByUuid(memberDto);
        // 비정상적인 회원이면 오류 값
        if (!bMemberCheck) {
            // 존재하지 않는 회원입니다.
            throw new CustomException(CustomError.NOTI_IDX_ERROR);
        }

        // notiDto 세팅
        NotiDto notiDto = new NotiDto();
        notiDto.setMemberUuid(memberUuid);

        // 회원의 마지막 noti 본 날 가져오기
        String lastNotiShowDate = getNotiShowDate(notiDto);
        if (lastNotiShowDate != null) {
            // 마지막으로 확인 한 날짜 세팅
            notiDto.setShowDate(lastNotiShowDate);

        }

        boolean newNoti;
        // lastNotiShowDate 날짜 이후에 등록 된 공지 알림 있는지 체크
        newNoti = checkNewNoticeNoti(notiDto);

        // 새로운 공지 알림이 없다면 일반 알림도 체크
        if (!newNoti) {
            // 회원별 새로운 노티 있는지 체크
            newNoti = checkNewNoti(notiDto);
        }

        return newNoti;
    }

    public void registNoticeNoti(String memberUuid) {
        if (memberUuid == null || memberUuid.equals("")) {
            // 회원 UUID가 존재하지 않습니다.
            throw new CustomException(CustomError.MEMBER_UUID_EMPTY);
        }

        // notiDto 세팅
        NotiDto notiDto = new NotiDto();
        notiDto.setMemberUuid(memberUuid);

        // 회원 가입일 가져오기
        String joinDate = memberService.getMemberJoinDate(memberUuid);
        if (joinDate != null && !joinDate.equals("")) {
            notiDto.setJoinDate(joinDate);
        }


        // 회원의 마지막 noti 본 날 가져오기
        String lastNotiShowDate = getNotiShowDate(notiDto);
        if (lastNotiShowDate != null) {
            // 마지막으로 확인 한 날짜 세팅
            notiDto.setShowDate(lastNotiShowDate);
        }

        // 마지막으로 공지 알림 본 날짜 이후의 공지 리스트
        List<NotiDto> noticeNotiList = getNoticeNotiList(notiDto);

        // 있으면 회원 알림함에 넣어주기
        if (noticeNotiList != null) {
            for (NotiDto dto : noticeNotiList) {
                dto.setMemberUuid(memberUuid);
                insertNoticeNoti(dto);
            }
        }
    }


    public void registNotiShow(String memberUuid) {
        if (memberUuid == null || memberUuid.equals("")) {
            // 회원 UUID가 존재하지 않습니다.
            throw new CustomException(CustomError.MEMBER_UUID_EMPTY);
        }

        // notiDto 세팅
        NotiDto notiDto = new NotiDto();
        notiDto.setMemberUuid(memberUuid);
        // 회원의 마지막 noti 본 날 가져오기
        String lastNotiShowDate = selectNotiShowDate(notiDto);

        if (lastNotiShowDate == null) {
            // 지금 날짜로 업데이트
            insertNotiShow(notiDto);
        }
        // 지금 날짜로 인서트
        else {
            updateNotiShow(notiDto);
        }
    }

    /**
     * 팔로워 300명 이상인 회원 컨텐츠 알림 등록 [CRON]
     *
     * @param notiDto : memberUuidList, startDate, endDate
     * @return
     */
    public boolean notifyFollowerOfNewContents(NotiDto notiDto) {
        String lastMonth = notiDto.getStartDate();                 // 시작일 (로그인 조건) , lastMonth
        String lastWeek = notiDto.getEndDate();                     // 종료일 (로그인 조건) , lastWeek
        List<String> memberUuidList = notiDto.getMemberUuidList(); // 팔로우 회원 리스트
        boolean result = false; // 하나라도 insert 되면 true

        // 날짜 조건 없는 경우
        if (ObjectUtils.isEmpty(lastMonth)) {
            throw new CustomException(CustomError.DATE_START_DATE_EMPTY); // 시작 날짜가 비었습니다.
        }
        // 날짜 조건 없는 경우
        if (ObjectUtils.isEmpty(lastWeek)) {
            throw new CustomException(CustomError.DATE_END_DATE_EMPTY); // 종료 날짜가 비었습니다.
        }
        // 회원 UUID 없는 경우
        if (ObjectUtils.isEmpty(memberUuidList)) {
            throw new CustomException(CustomError.MEMBER_UUID_EMPTY); // 회원 UUID가 비었습니다.
        }

        /*
            - 알림 아래 조건으로 나누어서 등록
            - 1순위 One :: 로그인 최근 1주일
            - 2순위 Two :: 로그인 최근 1주일~30일
            - 3순위 Thr :: 로그인 30일 이후
         */

        // 조회용 dto
        NotiDto searchNotiDto = NotiDto.builder()
                .endDate(lastWeek)
                .memberUuidList(memberUuidList).build();

        // 1 순위 조건 팔로우 리스트 조회
        List<String> followUuidList = notiDaoSub.getFollowerMember(searchNotiDto);
        notiDto.setMemberUuidList(followUuidList);
        // 해당 조건 insert 안되도 다음 조건 조회하고 insert 될 수 있도록 if 문 필수
        if (!ObjectUtils.isEmpty(followUuidList)) {
            registNotiList(notiDto); // 알림 등록
            result = true;
        }

        // 2 순위 조건 팔로우 리스트 조회
        searchNotiDto.setStartDate(lastWeek);    // 일주일 전
        searchNotiDto.setEndDate(lastMonth);    // 한달 전
        searchNotiDto.setMemberUuidList(memberUuidList);
        followUuidList = notiDaoSub.getFollowerMember(searchNotiDto);
        notiDto.setMemberUuidList(followUuidList);
        // 해당 조건 insert 안되도 다음 조건 조회하고 insert 될 수 있도록 if 문 필수
        if (!ObjectUtils.isEmpty(followUuidList)) {
            registNotiList(notiDto);// 알림 등록
            result = true;
        }

        // 3 순위 조건 팔로우 리스트 조회
        searchNotiDto.setStartDate(lastMonth); // 한달 전
        searchNotiDto.setEndDate(null);
        searchNotiDto.setMemberUuidList(memberUuidList);
        followUuidList = notiDaoSub.getFollowerMember(searchNotiDto);
        notiDto.setMemberUuidList(followUuidList);

        if (!ObjectUtils.isEmpty(followUuidList)) {
            registNotiList(notiDto); // 알림 등록
            result = true;
        }
        return result;
    }


    /*****************************************************
     *  SubFunction - select
     ****************************************************/
    /**
     * 마지막으로 알림 함 본 날짜 가져오기
     *
     * @param notiDto 회원 idx
     * @return 마지막으로 알림 확인한 date
     */
    public String selectNotiShowDate(NotiDto notiDto) {
        return notiDaoSub.getNotiShowDate(notiDto);
    }


    public List<NotiDto> getNoticeNotiList(NotiDto notiDto) {
        return notiDaoSub.getNoticeNotiList(notiDto);
    }

    /**
     * N일 내 중복 알림 IDX
     *
     * @param notiDto memberUuid subType senderUuid checkNotiDate contentsIdx
     * @return
     */
    public Long getNotiDuple(NotiDto notiDto) {
        if (notiDto.getSenderUuid() == null || notiDto.getSenderUuid().equals("")) {
            // 알림 보내는 회원 UUID가 비어있습니다.
            throw new CustomException(CustomError.NOTI_SENDERUUID_ERROR);
        }
        if (notiDto.getMemberUuid() == null || notiDto.getMemberUuid().equals("")) {
            // 알림 받는 회원 UUID가 비어있습니다.
            throw new CustomException(CustomError.NOTI_MEMBERUUID_ERROR);
        }
        if (notiDto.getSubType() == null || notiDto.getSubType().equals("")) {
            // 서브타입을 입력해주세요.
            throw new CustomException(CustomError.NOTI_SUBTYPE_ERROR);
        }

        Long notiDuple = notiDaoSub.getNotiDuple(notiDto);
        if (notiDuple == null) {
            notiDuple = 0L;
        }

        return notiDuple;
    }

    /**
     * 알림함 처음 보는지
     *
     * @param memberUuid 회원uuid
     * @return 처음보면 true / 본적있으면 false
     */
    public boolean checkNotiShow(String memberUuid) {
        boolean reuslt = true;

        // notiDto 세팅
        NotiDto notiDto = new NotiDto();
        notiDto.setMemberUuid(memberUuid);

        // 회원의 마지막 noti 본 날 가져오기
        String lastNotiShowDate = getNotiShowDate(notiDto);
        if (lastNotiShowDate != null && !lastNotiShowDate.isEmpty()) {
            reuslt = false;
        }
        return reuslt;
    }

    /**
     * 혜택 및 이벤트 알림 켜져있는지
     *
     * @param memberUuid 회원uuid
     * @return 켜져있으면 true / 꺼져있으면 false
     */
    public boolean checkEventNoti(String memberUuid) {
        // notiDto 세팅
        NotiDto notiDto = new NotiDto();
        notiDto.setMemberUuid(memberUuid);

        // 회원의 마지막 이벤트 푸시 알림 설정값
        return getEventNotiSetting(notiDto);
    }

    /**
     * 마지막으로 알림 함 본 날짜 가져오기
     *
     * @param notiDto 회원 uuid
     * @return 마지막으로 알림 확인한 date
     */
    public String getNotiShowDate(NotiDto notiDto) {
        return notiDaoSub.getNotiShowDate(notiDto);
    }

    /**
     * 회원 이벤트 푸시 알림 설정 값
     *
     * @param notiDto 회원uuid
     * @return 설정했으면 1 안했으면 0
     */
    public Boolean getEventNotiSetting(NotiDto notiDto) {
        Integer check = notiDaoSub.getEventNotiSetting(notiDto);

        return check > 0;
    }

    /**
     * 새로운 알림 있는지 확인
     *
     * @param notiDto show_date 마지막으로 확인한 날짜
     * @return 신규알림 cnt
     */
    public Boolean checkNewNoti(NotiDto notiDto) {
        Integer iCount = notiDaoSub.getCountNewNoti(notiDto);

        return iCount > 0;
    }

    /**
     * 새로운 공지 알림 있는지 확인
     *
     * @param notiDto showDate
     * @return 신규 공지알림 cnt
     */
    public Boolean checkNewNoticeNoti(NotiDto notiDto) {
        Integer iCount = notiDaoSub.getCountNewNoticeNoti(notiDto);

        return iCount > 0;
    }

    /*****************************************************
     *  SubFunction - insert
     ****************************************************/
    /**
     * 새로군 공지 알림 넣기
     *
     * @param notiDto
     */
    public void insertNoticeNoti(NotiDto notiDto) {
        notiDao.insertNoticeNoti(notiDto);
    }

    /**
     * 알림 등록
     *
     * @param notiDto member_uuid  sub_type sender_uuid type title body
     * @return
     */
    public Integer registNoti(NotiDto notiDto) {
        if (notiDto.getSenderUuid() == null || notiDto.getSenderUuid().equals("")) {
            // 알림 보내는 회원 UUID가 비어있습니다.
            throw new CustomException(CustomError.NOTI_SENDERUUID_ERROR);
        }
        if (notiDto.getMemberUuid() == null || notiDto.getMemberUuid().equals("")) {
            // 알림 받는 회원 UUID가 비어있습니다.
            throw new CustomException(CustomError.NOTI_MEMBERUUID_ERROR);
        }
        if (notiDto.getSubType() == null || notiDto.getSubType().equals("")) {
            // 서브타입을 입력해주세요.
            throw new CustomException(CustomError.NOTI_SUBTYPE_ERROR);
        }

        // 등록일
        notiDto.setRegDate(dateLibrary.getDatetime());
        return notiDao.registNoti(notiDto);
    }

    /**
     * 알림 리스트 등록
     *
     * @param notiDto
     * @return
     */
    public int registNotiList(NotiDto notiDto) {

        if (ObjectUtils.isEmpty(notiDto.getSenderUuid())) {
            // 알림 보내는 회원 UUID가 비어있습니다.
            throw new CustomException(CustomError.NOTI_SENDERUUID_ERROR);
        }
        if (ObjectUtils.isEmpty(notiDto.getMemberUuidList())) {
            // 알림 보내는 회원 UUID가 비어있습니다.
            throw new CustomException(CustomError.NOTI_SENDERUUID_ERROR);
        }
        if (ObjectUtils.isEmpty(notiDto.getSubType())) {
            // 서브타입을 입력해주세요.
            throw new CustomException(CustomError.NOTI_SUBTYPE_ERROR);
        }

        // 등록일 set
        notiDto.setRegDate(dateLibrary.getDatetime());
        // 알림 리스트 등록
        return notiDao.registNotiList(notiDto);
    }

    /**
     * 공지 확인 한 날짜 인서트
     *
     * @param notiDto
     */
    public void insertNotiShow(NotiDto notiDto) {
        // 등록일
        notiDto.setRegDate(dateLibrary.getDatetime());

        notiDao.insertNotiShow(notiDto);
    }

    /*****************************************************
     *  SubFunction - Update
     ****************************************************/

    /**
     * 공지 본 날짜 업데이트
     *
     * @param notiDto
     */
    public void updateNotiShow(NotiDto notiDto) {
        // 등록일
        notiDto.setRegDate(dateLibrary.getDatetime());

        notiDao.updateNotiShow(notiDto);

    }

    public Integer modiNotiRegDate(Long idx) {

        NotiDto notiDto = new NotiDto();
        notiDto.setIdx(idx);
        notiDto.setRegDate(dateLibrary.getDatetime());

        return notiDao.modiNotiRegDate(notiDto);
    }
    /*****************************************************
     *  SubFunction - Etc
     ****************************************************/
    /**
     * 정보 변경 list
     *
     * @param list
     */
    public void remakeInfo(List<NotiDto> list) {
        for (NotiDto l : list) {
            remakeInfo(l);
        }
    }

    /**
     * 정보 변경 dto
     *
     * @param dto
     */
    public void remakeInfo(NotiDto dto) {
        // regDate  yyyy-MM-dd a hh시 mm분 형식으로 변환
        if (dto.getRegDate() != null && !dto.getRegDate().equals("")) {
            String date = DateLibrary.getConvertAmPmRegdate(dto.getRegDate());
            dto.setRegDate(date);
        }

        // 컨텐츠IDX가 존재하고 컨텐츠 내용이 있다면 멘션 정보 조회
        if (dto.getContentsIdx() > 0 && (dto.getContents() != null && !dto.getContents().equals(""))) {

            List<HashMap<String, List<MemberDto>>> mentionMemberInfo = mentionMember(dto.getContents());
            dto.setMentionMemberInfo(mentionMemberInfo);
        }

        // 알림 보낸 회원의 uuid 가 비어있지 않다면 보낸사람 정보 조회( 공지,이벤트는 senderUuid = "" )
        if (dto.getSenderUuid() != null && !dto.getSenderUuid().equals("")) {

            MemberDto getSenderMemberDto = MemberDto.builder().imgDomain(defaultImgDomain).memberUuid(dto.getSenderUuid()).build();
            // list
            List<MemberDto> senderInfo = memberDaoSub.getMemberInfoByUuid(getSenderMemberDto);

            // 뱃지 정보 가져오기
            String badgeInfo = snsMemberCurlService.getMemberBadge(dto.getSenderUuid());
            JSONObject badgeInfoObject = new JSONObject(badgeInfo);
            if (!((boolean) badgeInfoObject.get("result"))) {
                throw new CurlException(badgeInfoObject);
            }
            JSONObject followMemberInfoResult = (JSONObject) badgeInfoObject.get("data");
            boolean isBadge = (boolean) followMemberInfoResult.get("isBadge");
            // 뱃지 정보 세팅
            for (MemberDto memberDto : senderInfo) {
                if (isBadge) {
                    memberDto.setIsBadge(1);
                } else {
                    memberDto.setIsBadge(0);
                }
            }
            dto.setSenderInfo(senderInfo);
        }

        // 팔로우 여부 체크
        if (dto.getMemberUuid() != null && dto.getSenderUuid() != null) {

            String followInfo = followCurlService.getFollowCheck(dto.getMemberUuid(), dto.getSenderUuid());
            JSONObject followInfoObject = new JSONObject(followInfo);
            if (!((boolean) followInfoObject.get("result"))) {
                throw new CurlException(followInfoObject);
            }
            JSONObject followInfoResult = (JSONObject) followInfoObject.get("data");
            boolean isFollow = (boolean) followInfoResult.get("isFollow");

            if (isFollow) {
                dto.setFollowState(1);
            } else {
                dto.setFollowState(0);
            }
        }

        dto.setContentsLikeState(0);

        // 컨텐츠 좋아요 여부 체크
        if (dto.getMemberUuid() != null && dto.getContentsIdx() > 0) {
            String contentsLikeInfo = snsContentsCurlService.getContentsLike(dto.getMemberUuid(), dto.getContentsIdx());

            JSONObject contentsLikeInfoObject = new JSONObject(contentsLikeInfo);
            if (!((boolean) contentsLikeInfoObject.get("result"))) {
                throw new CurlException(contentsLikeInfoObject);
            }
            JSONObject contentsLikeInfoResult = (JSONObject) contentsLikeInfoObject.get("data");
            boolean isContentsLike = (boolean) contentsLikeInfoResult.get("isContentsLike");

            if (isContentsLike) {
                dto.setContentsLikeState(1);
            }
        }
    }

    /*****************************************************
     * 컨텐츠 멘션 회원 정보
     ****************************************************/
    public List<HashMap<String, List<MemberDto>>> mentionMember(String text) {
        List<HashMap<String, List<MemberDto>>> mentionList = new ArrayList<>();

        Pattern mentionPattern = Pattern.compile("\\[@\\[[0-9|a-z|A-Z|ㄱ-ㅎ|ㅏ-ㅣ|가-힝|_|.]*\\]]");
        Matcher mentionMatcher = mentionPattern.matcher(text);
        String extractMention;

        HashMap<String, List<MemberDto>> mentionInfoList = new HashMap<>();

        while (mentionMatcher.find()) {
            // 회원UUID 추출
            extractMention = mentionMatcher.group(); // 패턴에 일치하는 문자열 반환 ex) [@[ko07a1b7553]]
            String memberUuid = extractMention.substring(3, extractMention.length() - 2);
            MemberDto getSenderMemberDto = MemberDto.builder().imgDomain(defaultImgDomain).memberUuid(memberUuid).build();
            // 멘션된 회원 정보 가져오기
            List<MemberDto> mentionMemberInfo = memberDaoSub.getMemberInfoByUuid(getSenderMemberDto);
            for (MemberDto memberDto : mentionMemberInfo) {
                memberDto.setMemberState(1);
            }
            mentionInfoList.put(memberUuid, mentionMemberInfo);

            // 정상회원에 없으면 탈퇴회원 조회
            if (mentionMemberInfo.isEmpty()) {
                //  탈퇴회원목록에서 회원 정보 가져오기
                List<MemberDto> mentionMemberOutInfo = outMemberDaoSub.getMemberOutInfoByUuid(memberUuid);
                for (MemberDto memberDto : mentionMemberOutInfo) {
                    memberDto.setMemberState(0);
                }
                mentionInfoList.put(memberUuid, mentionMemberOutInfo);
            }
        }

        if (!mentionInfoList.isEmpty()) {
            mentionList.add(mentionInfoList);
        }
        return mentionList;
    }

}
