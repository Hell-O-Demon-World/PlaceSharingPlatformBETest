package com.golfzonaca.officesharingplatform.service.comment;

import com.golfzonaca.officesharingplatform.domain.Comment;
import com.golfzonaca.officesharingplatform.web.comment.dto.CommentData;

import java.util.List;

public interface CommentService {

    void save(Long userId, Long placeId, CommentData data);

    List<Comment> findAllByPlaceId(long placeId);

    void update(long commentId, CommentData data);

    void delete(long commentId);
}
