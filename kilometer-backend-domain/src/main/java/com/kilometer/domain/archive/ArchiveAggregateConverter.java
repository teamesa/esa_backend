package com.kilometer.domain.archive;

import com.kilometer.domain.archive.archiveImage.ArchiveImage;
import com.kilometer.domain.archive.dto.ItemArchiveDto;
import com.kilometer.domain.archive.dto.ArchiveInfo;
import com.kilometer.domain.archive.dto.MyArchiveDto;
import com.kilometer.domain.archive.dto.MyArchiveInfo;
import com.kilometer.domain.archive.heart.ArchiveHeart;
import com.kilometer.domain.archive.heart.ArchiveHeartGenerator;
import com.kilometer.domain.archive.userVisitPlace.UserVisitPlace;
import com.kilometer.domain.badge.ItemBadge;
import com.kilometer.domain.badge.ItemBadgeGenerator;
import com.kilometer.domain.item.enumType.ExhibitionType;
import com.kilometer.domain.util.ApiUrlUtils;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArchiveAggregateConverter {

    private final ArchiveHeartGenerator archiveHeartGenerator;
    private final ItemBadgeGenerator itemBadgeGenerator;

    public ArchiveInfo convertArchiveInfo(ItemArchiveDto itemArchiveDto,
        List<ArchiveImage> archiveImages, List<UserVisitPlace> userVisitPlaces) {

        Map<PlaceType, String> placeTypes = convertFoodAndCafe(userVisitPlaces);

        ArchiveHeart archiveHeart = archiveHeartGenerator.generateArchiveHeart(
            itemArchiveDto);

        return ArchiveInfo.builder()
            .id(itemArchiveDto.getId())
            .userName(itemArchiveDto.getName())
            .userProfileUrl(itemArchiveDto.getImageUrl())
            .updatedAt(itemArchiveDto.getUpdatedAt())
            .starRating(itemArchiveDto.getStarRating())
            .heartCount(itemArchiveDto.getHeartCount())
            .heart(archiveHeart)
            .comment(itemArchiveDto.getComment())
            .food(placeTypes.getOrDefault(PlaceType.FOOD, ""))
            .cafe(placeTypes.getOrDefault(PlaceType.CAFE, ""))
            .photoUrls(archiveImages.stream()
                .map(ArchiveImage::getImageUrl)
                .collect(Collectors.toList()))
            .build();
    }

    public ArchiveInfo convertArchiveInfo(Archive archive) {

        Map<PlaceType, String> placeTypes = convertFoodAndCafe(archive.getUserVisitPlaces());

        ArchiveHeart archiveHeart = archiveHeartGenerator.generateArchiveHeart(archive.getId());

        return ArchiveInfo.builder()
            .id(archive.getId())
            .userProfileUrl(archive.getUser().getImageUrl())
            .userName(archive.getUser().getName())
            .updatedAt(archive.getUpdatedAt())
            .starRating(archive.getStarRating())
            .heartCount(archive.getLikeCount())
            .heart(archiveHeart)
            .comment(archive.getComment())
            .food(placeTypes.getOrDefault(PlaceType.FOOD, ""))
            .cafe(placeTypes.getOrDefault(PlaceType.CAFE, ""))
            .photoUrls(archive.getArchiveImages().stream()
                .map(ArchiveImage::getImageUrl)
                .collect(Collectors.toList()))
            .build();
    }

    public MyArchiveInfo convertMyArchiveInfo(MyArchiveDto myArchiveDto,
        List<ArchiveImage> archiveImages, List<UserVisitPlace> userVisitPlaces) {

        ItemBadge itemBadge = itemBadgeGenerator.generateTypeItemBadge(ExhibitionType.EXHIBITION);

        return MyArchiveInfo.builder()
            .title(myArchiveDto.getTitle())
            .comment(myArchiveDto.getComment())
            .places(convertVisitPlaces(userVisitPlaces))
            .typeBadge(itemBadge)
            .updatedAt(myArchiveDto.getUpdatedAt())
            .existArchiveImages(!archiveImages.isEmpty())
            .api(ApiUrlUtils.getArchiveUrl(myArchiveDto.getId()))
            .build();
    }

    private Map<PlaceType, String> convertFoodAndCafe(List<UserVisitPlace> userVisitPlaces) {
        return userVisitPlaces.stream()
            .collect(Collectors.toMap(UserVisitPlace::getPlaceType, UserVisitPlace::getPlaceName));
    }

    private String convertVisitPlaces(List<UserVisitPlace> userVisitPlaces) {
        StringBuilder places = new StringBuilder();
        userVisitPlaces.forEach(userVisitPlace -> {
            if (places.length() != 0) {
                places.append(", ");
            }
            places.append(userVisitPlace.getPlaceName());
        });

        return (places.length() == 0) ? null : places.toString();
    }

}
