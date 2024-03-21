package com.architecture.admin.models.dto.setting;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationSettingDto {
    /**
     * puppycat_member_notification_setting
     */
    private Long idx;           // 일련번호
    private String memberUuid;  // 회원 uuid
    private Integer type;       // 타입
    private Integer state;      // 상태값
    private Long mentionCnt;    // 멘션 사용 cnt

    /**
     * puppycat_member_notification_sub_setting
     */
    private Long subIdx;       // puppycat_member_notification_sub_setting.idx
    private Integer notiType;     // puppycat_member_notification_sub_setting.noti_type
    private Integer subType;      // puppycat_member_notification_sub_setting.sub_type
    private Integer subState;     // puppycat_member_notification_sub_setting.state
    private String regDate;      // puppycat_member_notification_sub_setting.regDate
    private String regDateTz;    // puppycat_member_notification_sub_setting.regDateTz

    // sql
    private Long insertedIdx;
    private Integer affectedRow;
}
