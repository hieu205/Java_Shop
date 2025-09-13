package com.example.demo.service;

import com.example.demo.entity.Comment;
import com.example.demo.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    // Create
    public Comment addComment(Comment comment) {
        return commentRepository.save(comment);
    }

    // Read all
    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    // Read by Id
    public Comment getCommentById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + id));
    }

    // Read comments by ProductId
    public List<Comment> getCommentsByProductId(Long productId) {
        return commentRepository.findByProductId(productId);
    }

    // Read comments by UserId
    public List<Comment> getCommentsByUserId(Long userId) {
        return commentRepository.findByUserId(userId);
    }

    // Update
    public Comment updateCommentById(Long id, Comment newComment) {
        Comment comment = getCommentById(id);

        if (newComment.getContent() != null && !newComment.getContent().isEmpty()) {
            comment.setContent(newComment.getContent());
        }

        if (newComment.getUserId() != null) {
            comment.setUserId(newComment.getUserId());
        }

        if (newComment.getProductId() != null) {
            comment.setProductId(newComment.getProductId());
        }

        return commentRepository.save(comment);
    }

    // Delete
    public void deleteCommentById(Long id) {
        commentRepository.deleteById(id);
    }
}
