package com.architecture.admin.models.daosub.setting;

import com.architecture.admin.models.dto.member.MemberDto;
import com.architecture.admin.models.dto.setting.NotificationSettingDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface NotificationSettingSubDao {

    /*****************************************************
     * Select
     ****************************************************/
    /**
     * puppycat_member_notification_setting get list
     *
     * @param memberDto
     * @return 쿼리 실행결과
     */
    List<NotificationSettingDto> getMainList(MemberDto memberDto);

    List<NotificationSettingDto> getSubList(MemberDto memberDto);

}
