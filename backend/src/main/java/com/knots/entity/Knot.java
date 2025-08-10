package com.knots.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "knots")
public class Knot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(columnDefinition = "TEXT")
    private String steps; // 打结步骤，JSON格式存储
    
    @Column(name = "cover_image")
    private String coverImage; // 封面图片
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private KnotCategory category;
    
    @Column(name = "difficulty_level")
    private Integer difficultyLevel = 1; // 难度等级 1-5
    
    @Column(name = "view_count")
    private Integer viewCount = 0; // 浏览次数
    
    @Column(name = "is_published")
    private Boolean isPublished = false; // 是否发布
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "knot", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<KnotImage> images;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
