package com.example.demo.service;

import com.example.demo.dto.request.CommentRequest;
import com.example.demo.dto.response.CommentResponse;
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
import java.util.ArrayList;
import java.util.List;

@Service
public class CommentService {
        @Autowired
        private CommentRepository commentRepository;
        @Autowired
        private ProductRepository productRepository;
        @Autowired
        private UserRepository userRepository;

        public List<CommentResponse> getCommentsByProductId(Long productId) {
                Product product = productRepository.findById(productId)
                                .orElseThrow(() -> new RuntimeException("Product Id not found with by id" + productId));
                List<Comment> comments = commentRepository.findRootCommentsByProductId(productId);
                List<CommentResponse> commentResponses = new ArrayList<>();
                for (Comment comment : comments) {
                        commentResponses.add(CommentResponse.fromEntity(comment));
                }
                return commentResponses;

        }

        public CommentResponse getCommentById(Long id) {
                Comment comment = commentRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Comment not found with id" + id));
                return CommentResponse.fromEntity(comment);
        }

        public CommentResponse createComment(Long productId, Long userId, CommentRequest newcCommentrRequest) {
                Product product = productRepository.findById(productId)
                                .orElseThrow(() -> new RuntimeException("product not found with id " + productId));
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("User not found with id" + userId));
                Comment comment = Comment.builder()
                                .content(newcCommentrRequest.getContent())
                                .product(product)
                                .user(user)
                                .parentComment(null)
                                .createdAt(LocalDateTime.now())
                                .isStaffReply("STAFF".equals(user.getRole().getName())
                                                || "ADMIN".equals(user.getRole().getName()))
                                .build();
                Comment savedComment = commentRepository.save(comment);
                return CommentResponse.fromEntity(savedComment);
        }

        public CommentResponse replyComment(Long commentId, Long userId, CommentRequest newCommentrRequest) {
                Comment parentComment = commentRepository.findById(commentId)
                                .orElseThrow(() -> new RuntimeException("Comment not found with id " + commentId));

                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("user not found with id " + userId));

                Comment reply = Comment.builder()
                                .content(newCommentrRequest.getContent())
                                .product(parentComment.getProduct())
                                .user(user)
                                .parentComment(parentComment)
                                .createdAt(LocalDateTime.now())
                                .isStaffReply("STAFF".equals(user.getRole().getName())
                                                || "ADMIN".equals(user.getRole().getName()))
                                .build();

                Comment savedComment = commentRepository.save(reply);
                return CommentResponse.fromEntity(savedComment);
        }

        public CommentResponse updateComment(Long id, CommentRequest comment) {
                Comment currentComment = commentRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Comment not found with id " + id));
                currentComment.setContent(comment.getContent());
                Comment saveComment = commentRepository.save(currentComment);
                return CommentResponse.fromEntity(saveComment);
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
