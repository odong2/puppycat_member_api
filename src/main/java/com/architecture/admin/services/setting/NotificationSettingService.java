package com.architecture.admin.services.setting;

import com.architecture.admin.libraries.exception.CustomError;
import com.architecture.admin.libraries.exception.CustomException;
import com.architecture.admin.models.dao.setting.NotificationSettingDao;
import com.architecture.admin.models.daosub.setting.NotificationSettingSubDao;
import com.architecture.admin.models.dto.member.MemberDto;
import com.architecture.admin.models.dto.setting.NotificationSettingDto;
import com.architecture.admin.services.BaseService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class NotificationSettingService extends BaseService {
    private final NotificationSettingDao notificationSettingDao;
    private final NotificationSettingSubDao notificationSettingSubDao;
    @Value("${notification.main.total.check.count}")
    private Integer notificationMainCheckTotalCount;
    @Value("${notification.sub.total.check.count}")
    private Integer notificationSubCheckTotalCount;

    /*****************************************************
     *  Modules
     ****************************************************/

    /*****************************************************
     *  Select
     ****************************************************/

    /**
     * NotificationSetting List
     *
     * @param memberDto
     */
    public List<NotificationSettingDto> getMainList(MemberDto memberDto) {
        return notificationSettingSubDao.getMainList(memberDto);
    }

    /**
     * NotificationSubSetting List
     *
     * @param memberDto
     */
    public List<NotificationSettingDto> getSubList(MemberDto memberDto) {
        return notificationSettingSubDao.getSubList(memberDto);
    }

    /*****************************************************
     *  Insert
     ****************************************************/
    /**
     * NotificationSetting init 등록
     *
     * @param list
     */
    public void insertNotificationInitSetting(List<NotificationSettingDto> list) {
        Integer iResult = notificationSettingDao.insertNotificationInitSetting(list);

        if (!iResult.equals(notificationMainCheckTotalCount)) {
            throw new CustomException(CustomError.JOIN_FAIL); // 회원 가입에 실패 하였습니다.
        }
    }

    /**
     * NotificationSubSetting type init 등록
     *
     * @param list
     */
    public void insertNotificationSubInitSetting(List<NotificationSettingDto> list) {
        Integer iResult = notificationSettingDao.insertNotificationSubInitSetting(list);

        if (!iResult.equals(notificationSubCheckTotalCount)) {
            throw new CustomException(CustomError.JOIN_FAIL); // 회원 가입에 실패 하였습니다.
        }
    }

    /*****************************************************
     *  UPDATE
     ****************************************************/
    /**
     * Notification Setting update
     *
     * @param params
     */
    public void updateSetting(Map<String, Object> params) {
        // JSON파싱 객체 생성
        JSONObject data = new JSONObject(params);

        // memberUuid 값이 넘어오지 않았다면
        if (!data.has("memberUuid")) {
            // 회원 UUID가 비었습니다.
            throw new CustomException(CustomError.NICK_MEMBERUUID_EMPTY);
        }
        // get member_uuid
        String memberUuid = data.getString("memberUuid");

        // set Data
        MemberDto memberDto = new MemberDto();
        memberDto.setMemberUuid(memberUuid);

        // ON / OFF 값[0:OFF / 1:ON]
        Integer mainState;
        Integer subState;

        // 이용약관 리스트 가져오기
        List<NotificationSettingDto> notificationMainList = notificationSettingSubDao.getMainList(memberDto);
        List<NotificationSettingDto> notificationSubList = notificationSettingSubDao.getSubList(memberDto);

        //Main 처리
        for (NotificationSettingDto mainStr : notificationMainList) {
            if (data.has("main_" + mainStr.getType())) {
                // data Set
                mainState = (Integer) data.get("main_" + mainStr.getType());

                if (mainState > 1) {
                    throw new CustomException(CustomError.SETTING_PUSH_MAIN_ERROR); // Main 값이 유효하지 않습니다.
                }

                NotificationSettingDto notificationSettingDto = new NotificationSettingDto();
                notificationSettingDto.setMemberUuid(memberDto.getMemberUuid());
                notificationSettingDto.setType(mainStr.getType());
                notificationSettingDto.setState(mainState);
                notificationSettingDto.setRegDate(dateLibrary.getDatetime());
                // action
                notificationSettingDao.updateMainState(notificationSettingDto);
            }
        }

        //Sub 처리
        for (NotificationSettingDto subStr : notificationSubList) {
            if (data.has("sub_" + subStr.getNotiType() + "_" + subStr.getSubType())) {
                //data Set
                subState = (Integer) data.get("sub_" + subStr.getNotiType() + "_" + subStr.getSubType());

                if (subState > 1) {
                    throw new CustomException(CustomError.SETTING_PUSH_SUB_ERROR); // Main 값이 유효하지 않습니다.
                }

                NotificationSettingDto notificationSettingDto = new NotificationSettingDto();
                notificationSettingDto.setMemberUuid(memberDto.getMemberUuid());
                notificationSettingDto.setNotiType(subStr.getNotiType());
                notificationSettingDto.setSubType(subStr.getSubType());
                notificationSettingDto.setSubState(subState);
                notificationSettingDto.setRegDate(dateLibrary.getDatetime());
                // action
                notificationSettingDao.updateSubState(notificationSettingDto);
            }
        }
    }
}
