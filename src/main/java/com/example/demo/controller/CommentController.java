package com.example.demo.controller;

import com.example.demo.entity.Comment;
import com.example.demo.service.CommentService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.yaml.snakeyaml.events.CommentEvent;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comments")
public class CommentController {
    @Autowired
    private CommentService commentService;

    // lay binh luan cua san pham
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Comment>> getCommentByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(commentService.getCommentsByProductId(productId));
    }

    // lay chi tiet binh luan theo id
    @GetMapping("/{id}")
    public ResponseEntity<Comment> getCommentById(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.getCommentById(id));
    }

    // tao binh luan moi
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/product/{productId}/user/{userId}")
    public ResponseEntity<Comment> createComment(@PathVariable Long productId, @PathVariable Long userId,
            @Valid @RequestBody Comment comment) {
        return ResponseEntity.ok(commentService.createComment(productId, userId, comment));
    }

    // reply binh luan (chi danh chp staff or admin)
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @PostMapping("/{commentId}/reply/user/{userId}")
    public ResponseEntity<Comment> replyComment(@PathVariable Long commentId, @PathVariable Long userId,
            @Valid @RequestBody Comment comment) {
        return ResponseEntity.ok(commentService.replyComment(commentId, userId, comment));
    }

    // cap nhat binh luan
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Comment> updateComment(@PathVariable Long id, @Valid @RequestBody Comment comment) {
        return ResponseEntity.ok(commentService.updateComment(id, comment));
    }

    // xoa binh luan
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteComment(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.deleteComment(id));
    }
}
