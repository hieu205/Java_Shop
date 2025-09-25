package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment;

    @OneToMany(mappedBy = "parentComment", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> replies;

    @NotBlank(message = "Nội dung bình luận không được để trống")
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    // Thêm trường createdAt
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "is_staff_reply")
    @Builder.Default
    private Boolean isStaffReply = false;

    public Comment(User user, Product product, String content) {
        this.user = user;
        this.product = product;
        this.content = content;
        this.isStaffReply = false;
    }

    public Comment(User user, Product product, Comment parentComment, String content, Boolean isStaffReply) {
        this.user = user;
        this.product = product;
        this.parentComment = parentComment;
        this.content = content;
        this.isStaffReply = isStaffReply;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Comment comment = (Comment) o;
        return Objects.equals(id, comment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}