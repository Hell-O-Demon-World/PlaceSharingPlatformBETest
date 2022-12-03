package com.golfzonaca.officesharingplatform.repository.comment;

import com.golfzonaca.officesharingplatform.domain.Comment;
import com.golfzonaca.officesharingplatform.domain.Place;
import com.golfzonaca.officesharingplatform.web.comment.dto.CommentData;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {
    void save(Comment comment);

    Comment findById(long commentId);

    List<Comment> findAllByPlace(Place place);

    void update(Comment comment, CommentData data);

    void delete(Comment comment);

}
