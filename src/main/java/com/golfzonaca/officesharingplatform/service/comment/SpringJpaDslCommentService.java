package com.golfzonaca.officesharingplatform.service.comment;

import com.golfzonaca.officesharingplatform.domain.Comment;
import com.golfzonaca.officesharingplatform.domain.Rating;
import com.golfzonaca.officesharingplatform.domain.User;
import com.golfzonaca.officesharingplatform.repository.comment.CommentRepository;
import com.golfzonaca.officesharingplatform.repository.place.PlaceRepository;
import com.golfzonaca.officesharingplatform.repository.rating.RatingRepository;
import com.golfzonaca.officesharingplatform.repository.user.UserRepository;
import com.golfzonaca.officesharingplatform.web.comment.dto.CommentData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class SpringJpaDslCommentService implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;
    private final RatingRepository ratingRepository;

    @Override
    public void save(Long userId, Long ratingId, CommentData data) {
        User user = userRepository.findById(userId);
        Rating rating = ratingRepository.findById(ratingId);
        commentRepository.save(new Comment(rating, data.getContext(), user, LocalDateTime.now()));
    }

    @Override
    public List<Comment> findAllByRatingId(long ratingId, Integer commentpage) {
        return commentRepository.findAllByRating(ratingRepository.findById(ratingId), commentpage);
    }

    @Override
    public void update(long commentId, CommentData data) {
        Comment comment = commentRepository.findById(commentId);
        commentRepository.update(comment, data);
    }

    @Override
    public void delete(long commentId) {
        Comment comment = commentRepository.findById(commentId);
        commentRepository.delete(comment);
    }
}
