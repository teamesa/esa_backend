package com.kilometer.domain.archive.domain;

import com.kilometer.domain.archive.ArchiveEntity;
import com.kilometer.domain.archive.archiveImage.ArchiveImageEntity;
import com.kilometer.domain.archive.domain.archiveImages.ArchiveImages;
import com.kilometer.domain.archive.domain.userVisitPlaces.UserVisitPlace;
import com.kilometer.domain.archive.domain.userVisitPlaces.UserVisitPlaces;
import com.kilometer.domain.archive.exception.ArchiveValidationException;
import com.kilometer.domain.archive.userVisitPlace.UserVisitPlaceEntity;
import com.kilometer.domain.item.ItemEntity;
import com.kilometer.domain.user.User;
import java.util.List;

public class Archive {

    private static final int MAX_STAR_RATING = 5;
    private static final int MIN_STAR_RATING = 1;

    private final Long id;
    private final String comment;
    private final int starRating;
    private final boolean isVisibleAtItem;
    private final ArchiveImages archiveImages;
    private final UserVisitPlaces userVisitPlaces;

    public Archive(final Long id, final String comment, final int starRating, final boolean isVisibleAtItem,
                   final List<String> photoUrls, final List<UserVisitPlace> userVisitPlaces) {
        validate(comment, starRating);
        this.id = id;
        this.comment = comment;
        this.starRating = starRating;
        this.isVisibleAtItem = isVisibleAtItem;
        this.archiveImages = new ArchiveImages(photoUrls);
        this.userVisitPlaces = new UserVisitPlaces(userVisitPlaces);
    }

    private void validate(final String comment, final int starRating) {
        validateCommentField(comment);
        validateStarRatingField(starRating);
    }

    private void validateCommentField(final String comment) {
        if (comment == null) {
            throw new ArchiveValidationException();
        }
    }

    private void validateStarRatingField(final int starRating) {
        if (starRating < MIN_STAR_RATING || starRating > MAX_STAR_RATING) {
            throw new ArchiveValidationException();
        }
    }

    public ArchiveEntity toEntity(final User user, final ItemEntity item) {
        return ArchiveEntity.builder()
                .comment(this.comment)
                .starRating(this.starRating)
                .isVisibleAtItem(this.isVisibleAtItem)
                .user(user)
                .item(item)
                .build();
    }

    public List<ArchiveImageEntity> createImageEntities() {
        return archiveImages.toEntity();
    }

    public List<UserVisitPlaceEntity> createVisitPlaceEntities() {
        return userVisitPlaces.toEntity();
    }

    public Long getId() {
        return id;
    }

    public String getComment() {
        return comment;
    }

    public int getStarRating() {
        return starRating;
    }

    public boolean getIsVisibleAtItem() {
        return isVisibleAtItem;
    }
}
