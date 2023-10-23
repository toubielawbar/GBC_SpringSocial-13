package ca.gbc.commentservice.service;

import ca.gbc.commentservice.dto.CommentRequest;
import ca.gbc.commentservice.dto.CommentResponse;
import ca.gbc.commentservice.model.Comment;
import ca.gbc.commentservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class CommentService implements ICommentService {
    private final CommentRepository commentRepository;

    @Override
    public Comment createComment(CommentRequest commentRequest) {
        log.info("Creating a new comment ");

        Comment comment = new Comment();
        comment.setComment(commentRequest.getAuthor());
        comment.setComment(commentRequest.getPost());
        comment.setComment(commentRequest.getComment());

        Comment savedComment = commentRepository.save(comment);

        log.info("Comment is saved with ID: {}", savedComment.getId());

        return savedComment;
    }

    @Override
    public List<CommentResponse> getAllComments() {
        log.info("Returning a list of comments");

        List<Comment> comments = commentRepository.findAll();

        return comments.stream()
                .map(this::mapToCommentResponse)
                .collect(Collectors.toList());
    }


    private CommentResponse mapToCommentResponse(Comment comment){

        return CommentResponse.builder()
                .id(comment.getId())
                .author(comment.getAuthor())
                .post(comment.getPost())
                .comment(comment.getComment())
                .build();
    }

    @Override
    public Long updateComment(Long commentId, CommentRequest commentRequest) {
        log.info("Updating a comment with id {}", commentId);

        Optional<Comment> commentID = commentRepository.findById(commentId);

        if (commentID.isPresent()) {
            Comment comment = commentID.get();
            comment.setAuthor(comment.getAuthor());
            comment.setPost(comment.getPost());
            comment.setComment(commentRequest.getComment());

            log.info("Comment {} is updated",comment.getId());

            commentRepository.save(comment);
            return comment.getId();
        } else {
            return commentId;
        }
    }

    @Override
    public void deleteComment(Long commentId) {
        log.info("comment {} is deleted",commentId);
        commentRepository.deleteById(commentId);
    }

}