package com.golfzonaca.officesharingplatform.service.comment;

import com.golfzonaca.officesharingplatform.domain.Comment;
import com.golfzonaca.officesharingplatform.domain.Place;
import com.golfzonaca.officesharingplatform.domain.User;
import com.golfzonaca.officesharingplatform.repository.comment.CommentRepository;
import com.golfzonaca.officesharingplatform.repository.place.PlaceRepository;
import com.golfzonaca.officesharingplatform.repository.user.UserRepository;
import com.golfzonaca.officesharingplatform.web.comment.dto.CommentData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class SpringJpaDslCommentService implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;

    @Override
    public void save(Long userId, Long placeId, CommentData data) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 사용자입니다."));
        Place place = placeRepository.findById(placeId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 공간입니다."));
        commentRepository.save(new Comment(place, data.getContext(), user, LocalDateTime.now()));
    }

    @Override
    public List<Comment> findAllByPlaceId(long placeId) {
        Place place = placeRepository.findById(placeId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 공간입니다."));
        return commentRepository.findAllByPlace(place);
    }

    @Override
    public void update(long commentId, CommentData data) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 댓글입니다."));
        commentRepository.update(comment, data);
    }

    @Override
    public void delete(long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 댓글입니다."));
        commentRepository.delete(comment);
    }
}
