package com.architecture.admin.controllers.v1.noti;

import com.architecture.admin.controllers.v1.BaseController;
import com.architecture.admin.libraries.exception.CustomError;
import com.architecture.admin.libraries.exception.CustomException;
import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.noti.NotiDto;
import com.architecture.admin.services.noti.NotiService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/noti")
public class NotiV1Controller extends BaseController {
    private final NotiService notiService;

    @GetMapping("")
    public ResponseEntity notiLists(@RequestHeader(value = "Authorization") String token,
                                    @ModelAttribute SearchDto searchDto) {
        // 토큰에서 회원 UUID 가져오기
        String memberUuid = super.getAccessMemberUuid(token);

        // 알림함 처음 보는지 ( 처음보면 true, 본적있으면 false )
        boolean isFirstShow = notiService.checkNotiShow(memberUuid);

        // 안 읽은 전체 공지 알림 인서트
        notiService.registNoticeNoti(memberUuid);

        // 회원 idx 세팅
        searchDto.setMemberUuid(memberUuid);

        // 리스트 가져오기
        List<NotiDto> list = notiService.getNotiList(searchDto);

        // 공지 시간 업데이트
        notiService.registNotiShow(memberUuid);

        // 혜택 알림 ON/OFF 여부
        boolean isAppPush = notiService.checkEventNoti(memberUuid);

        Map<String, Object> map = new HashMap<>();
        map.put("list", list);
        map.put("params", searchDto);
        map.put("isFirst", isFirstShow);
        map.put("isPush", isAppPush);

        String sMessage;

        if (!list.isEmpty()) {
            // 리스트 가져오기 성공
            sMessage = super.langMessage("lang.noti.success.list");
        } else {
            // 리스트가 없습니다
            sMessage = super.langMessage("lang.noti.exception.list.null");
        }

        JSONObject data = new JSONObject(map);

        return displayJson(true, "1000", sMessage, data);
    }

    /**
     * 신규 알림 유무
     *
     * @param token
     * @return
     */
    @GetMapping("new")
    public ResponseEntity checkNoti(@RequestHeader(value = "Authorization") String token) {

        // 토큰에서 회원 UUID 가져오기
        String memberUuid = super.getAccessMemberUuid(token);

        // 신규 알림 체크 있으면 true
        Boolean bIsCheckNoti = notiService.checkNoti(memberUuid);

        // 신규 알람이 없으면
        if (!Boolean.TRUE.equals(bIsCheckNoti)) {
            // 신규 알람이 없습니다
            String message = super.langMessage("lang.noti.exception.empty.newNoti");
            return displayJson(false, "1000", message);
        }

        // 신규 알람이 있습니다
        String message = super.langMessage("lang.noti.success.newNoti");
        return displayJson(true, "1000", message);
    }

    /**
     * 알림 N일 이내 내역 중복건 idx 가져오기
     *
     * @param notiDto memberUuid subType senderUuid checkNotiDate contentsIdx
     * @return
     */
    @PostMapping("/duple")
    public ResponseEntity getNotiDuple(@RequestBody NotiDto notiDto) {

        long notiIdx = notiService.getNotiDuple(notiDto);

        String sMessage = super.langMessage("lang.common.success.check");

        JSONObject data = new JSONObject();
        data.put("notiDupleIdx", notiIdx);

        return displayJson(true, "1000", sMessage, data);
    }

    /**
     * noti 등록
     *
     * @param notiDto member_uuid  sub_type sender_uuid type title body
     * @return
     */
    @PostMapping()
    public ResponseEntity registNoti(@RequestBody NotiDto notiDto) {

        int result = notiService.registNoti(notiDto);

        if (result < 1) {
            // 입력 실패
            throw new CustomException(CustomError.REGIST_ERROR);
        }

        String sMessage = super.langMessage("lang.common.success.regist");

        return displayJson(true, "1000", sMessage);
    }

    /**
     * noti regdate 수정
     *
     * @param notiIdx
     * @return
     */

    @PutMapping("{notiIdx}")
    public ResponseEntity modiNotiRegDate(@PathVariable("notiIdx") Long notiIdx) {

        Integer result = notiService.modiNotiRegDate(notiIdx);

        if (result < 1) {
            // 수정 실패
            throw new CustomException(CustomError.MODIFY_ERROR);
        }

        String sMessage = super.langMessage("lang.common.success.modify");

        return displayJson(true, "1000", sMessage);
    }

    /**
     * noti 리스트 등록
     *
     * @param notiDto : memberUuidList, sub_type, sender_uuid, type, title, body
     * @return
     */
    @PostMapping("/list")
    public ResponseEntity registNotiList(@RequestBody NotiDto notiDto) {

        int result = notiService.registNotiList(notiDto);

        if (result < 1) {
            // 입력 실패
            throw new CustomException(CustomError.REGIST_ERROR);
        }

        String sMessage = super.langMessage("lang.common.success.regist");

        return displayJson(true, "1000", sMessage);
    }

    /**
     * 팔로워 300명 이상인 회원 컨텐츠 알림 등록 [CRON 에서 사용]
     * 1순위 One :: 로그인 최근 1주일
     * 2순위 Two :: 로그인 최근 1주일~30일
     * 3순위 Thr :: 로그인 30일 이후
     *
     * @param notiDto : memberUuidList, sub_type, sender_uuid, type, title, body
     * @return
     */
    @PostMapping("/follow/list")
    public ResponseEntity notifyFollowerOfNewContents(@RequestBody NotiDto notiDto) {

        boolean result = notiService.notifyFollowerOfNewContents(notiDto);

        String sMessage = super.langMessage("lang.common.success.regist");
        JSONObject data = new JSONObject();
        data.put("result", result);

        return displayJson(true, "1000", sMessage, data);
    }

}
