package com.example.demo.controller;

import com.example.demo.entity.Comment;
import com.example.demo.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comments")
public class CommentController {
    @Autowired
    private CommentService commentService;

    // Create comment
    @PostMapping
    public ResponseEntity<Comment> addComment(@RequestBody Comment comment) {
        return ResponseEntity.ok(commentService.addComment(comment));
    }

    // get all comment
    @GetMapping
    public ResponseEntity<List<Comment>> getAllComments() {
        return ResponseEntity.ok(commentService.getAllComments());
    }

    // get comment by Id
    @GetMapping("/{id}")
    public ResponseEntity<Comment> getCommentById(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.getCommentById(id));
    }

    // Get comments by productId
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Comment>> getCommentsByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(commentService.getCommentsByProductId(productId));
    }

    // Get comments by userId
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Comment>> getCommentsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(commentService.getCommentsByUserId(userId));
    }

    // Update
    @PutMapping("/{id}")
    public ResponseEntity<Comment> updateComment(
            @PathVariable Long id,
            @RequestBody Comment comment) {
        return ResponseEntity.ok(commentService.updateCommentById(id, comment));
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteComment(@PathVariable Long id) {
        commentService.deleteCommentById(id);
        return ResponseEntity.ok("Comment deleted successfully");
    }

}
