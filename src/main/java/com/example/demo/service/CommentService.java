package com.example.demo.service;

import com.example.demo.entity.Comment;
import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.events.CommentEvent;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;

    public List<Comment> getCommentsByProductId(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product Id not found with by id" + productId));
        return commentRepository.findRootCommentsByProductId(productId);

    }

    public Comment getCommentById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found with id" + id));
        return comment;
    }

    public Comment createComment(Long productId, Long userId, Comment newcComment) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("product not found with id " + productId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id" + userId));
        Comment comment = Comment.builder()
                .content(newcComment.getContent())
                .product(product)
                .user(user)
                .parentComment(null)
                .createdAt(LocalDateTime.now())
                .isStaffReply("STAFF".equals(user.getRole().getName()) || "ADMIN".equals(user.getRole().getName()))
                .build();
        Comment savedComment = commentRepository.save(comment);
        return savedComment;
    }

    public Comment replyComment(Long commentId, Long userId, Comment newComment) {
        Comment parentComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with id " + commentId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("user not found with id " + userId));

        Comment reply = Comment.builder()
                .content(newComment.getContent())
                .product(parentComment.getProduct())
                .user(user)
                .parentComment(parentComment)
                .createdAt(LocalDateTime.now())
                .isStaffReply("STAFF".equals(user.getRole().getName()) || "ADMIN".equals(user.getRole().getName()))
                .build();

        Comment savedComment = commentRepository.save(reply);
        return savedComment;
    }

    public Comment updateComment(Long id, Comment comment) {
        Comment currentComment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found with id " + id));
        currentComment.setContent(comment.getContent());
        return commentRepository.save(currentComment);
    }

    public String deleteComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("comment not found with id " + id));
        List<Comment> replies = commentRepository.findRepliesByParentId(id);
        if (!replies.isEmpty()) {
            commentRepository.deleteAll(replies);
        }
        commentRepository.delete(comment);
        return "xoa comment thanh cong";
    }
}
