package com.architecture.admin.controllers.v1.comment;

import com.architecture.admin.controllers.v1.BaseController;
import com.architecture.admin.models.dto.member.MemberDto;
import com.architecture.admin.services.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/comment")
public class CommentV1Controller extends BaseController {

    private final CommentService commentService;

    /**
     * 좋아요 많은 댓글 회원 정보 조회
     *
     * @param uuid
     * @return
     */
    @GetMapping("/like/many/member/info")
    public MemberDto getLikeManyCommentMemberInfo(String uuid) {
        return commentService.getLikeManyCommentMemberInfo(uuid);
    }

}
