package com.architecture.admin.models.dao.noti;

import com.architecture.admin.models.dto.noti.NotiDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface NotiDao {

    /**
     * 활동 알람 인서트
     *
     * @param notiDto member_uuid sub_type sender_uuid type title body  reg_date
     * @return insertedIdx
     */
    int registNoti(NotiDto notiDto);

    int modiNotiRegDate(NotiDto notiDto);

    void insertNoticeNoti(NotiDto notiDto);

    void insertNotiShow(NotiDto notiDto);

    void updateNotiShow(NotiDto notiDto);

    /**
     * 알림 리스트 등록
     *
     * @param notiDto
     */
    int registNotiList(NotiDto notiDto);
}
