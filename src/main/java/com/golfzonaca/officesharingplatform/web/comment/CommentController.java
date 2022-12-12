package com.golfzonaca.officesharingplatform.web.comment;

import com.golfzonaca.officesharingplatform.annotation.TokenUserId;
import com.golfzonaca.officesharingplatform.service.comment.CommentService;
import com.golfzonaca.officesharingplatform.web.comment.dto.CommentData;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{ratingId}/comment/add")
    public String saveComment(@TokenUserId Long userId, @PathVariable Long ratingId, @Validated @RequestBody CommentData data, BindingResult bindingResult) {
        commentService.save(userId, ratingId, data);
        return "ok";
    }

    @GetMapping("/{ratingId}/comment")
    public String findComment(@PathVariable long ratingId, @RequestParam Integer commentpage) {
        commentService.findAllByRatingId(ratingId, commentpage);
        return "ok";
    }

    @PostMapping("/{placeId}/comment/{commentId}/edit")
    public String editComment(@TokenUserId Long userId, @PathVariable long placeId, @PathVariable long commentId, @Validated @RequestBody CommentData data, BindingResult bindingResult) {
        commentService.update(commentId, data);
        return "ok";
    }

    @GetMapping("/{placeId}/comment/{commentId}/delete")
    public String deleteComment(@TokenUserId Long userId, @PathVariable long placeId, @PathVariable long commentId) {
        commentService.delete(commentId);
        return "ok";
    }
}
