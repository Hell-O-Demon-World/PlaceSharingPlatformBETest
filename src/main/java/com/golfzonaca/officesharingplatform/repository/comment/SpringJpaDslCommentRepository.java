package com.golfzonaca.officesharingplatform.repository.comment;

import com.golfzonaca.officesharingplatform.domain.Comment;
import com.golfzonaca.officesharingplatform.domain.Place;
import com.golfzonaca.officesharingplatform.web.comment.dto.CommentData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Repository
@Transactional
@RequiredArgsConstructor
public class SpringJpaDslCommentRepository implements CommentRepository {

    private final SpringJpaCommentRepository jpaRepository;
    private final QueryCommentRepository query;


    @Override
    public void save(Comment comment) {
        jpaRepository.save(comment);
    }

    @Override
    public Comment findById(long commentId) {
        return jpaRepository.findById(commentId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 댓글입니다."));
    }

    @Override
    public List<Comment> findAllByPlace(Place place) {
        return query.findAllByPlace(place);
    }

    @Override
    public void update(Comment comment, CommentData data) {
        comment.updateComment(data.getContext());
    }

    @Override
    public void delete(Comment comment) {
        jpaRepository.delete(comment);
    }
}
