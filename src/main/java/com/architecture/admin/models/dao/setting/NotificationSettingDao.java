package com.architecture.admin.models.dao.setting;

import com.architecture.admin.models.dto.setting.NotificationSettingDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface NotificationSettingDao {

    /*****************************************************
     * Insert
     ****************************************************/
    /**
     * puppycat_member_notification_setting Insert
     *
     * @param list
     * @return 쿼리 실행결과
     */
    Integer insertNotificationInitSetting(List<NotificationSettingDto> list);

    /**
     * puppycat_member_notification_sub_setting Insert
     *
     * @param list
     */
    Integer insertNotificationSubInitSetting(List<NotificationSettingDto> list);

    /*****************************************************
     * Update
     ****************************************************/
    /**
     * puppycat_member_notification_setting update
     *
     * @param notificationSettingDto
     */
    void updateMainState(NotificationSettingDto notificationSettingDto);

    /**
     * puppycat_member_notification_sub_setting update
     *
     * @param notificationSettingDto
     */
    void updateSubState(NotificationSettingDto notificationSettingDto);
}
