package com.example.demo.dto.response;

import java.time.LocalDateTime;

import com.example.demo.entity.Category;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data

public class CategoryResponse {
    private Long id;
    private String name;
    private String description;

    @JsonProperty("parent_category_id")
    private Long parentCategoryId;

    @JsonProperty("parent_category_name")
    private String parentCategoryName;

    @JsonProperty("is_active")
    private Boolean isActive;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    // Static method để convert từ Entity sang DTO
    public static CategoryResponse fromEntity(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .parentCategoryId(category.getParentCategory() != null ? category.getParentCategory().getId() : null)
                .parentCategoryName(
                        category.getParentCategory() != null ? category.getParentCategory().getName() : null)
                .isActive(category.getIsActive())
                // .createdAt(category.getCreatedAt())
                // .updatedAt(category.getUpdatedAt())
                .build();
    }
}
