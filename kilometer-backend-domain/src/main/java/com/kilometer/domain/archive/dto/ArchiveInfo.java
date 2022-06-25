package com.kilometer.domain.archive.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kilometer.domain.archive.PlaceType;
import com.kilometer.domain.archive.archiveImage.ArchiveImage;
import com.kilometer.domain.archive.heart.ArchiveHeart;
import com.kilometer.domain.archive.heart.ArchiveHeartGenerator;
import com.kilometer.domain.archive.userVisitPlace.UserVisitPlace;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArchiveInfo {

    private Long id;
    private String userProfileUrl;
    private String userName;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime updatedAt;
    private Integer starRating;
    private Integer heartCount;
    private ArchiveHeart heart;
    private String comment;
    private String food;
    private String cafe;
    @Builder.Default
    private List<String> photoUrls = List.of();
}
