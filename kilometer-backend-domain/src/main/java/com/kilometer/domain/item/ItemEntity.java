package com.kilometer.domain.item;

import com.kilometer.domain.item.dto.ItemUpdateRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemEntity {

    @Id @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    private ExhibitionType exhibitionType;

    @Enumerated(EnumType.STRING)
    private ProgressType progressType;

    private String image;

    private String title;

    private String term;

    private String place;
    private Double latitude;
    private Double longitude;
    @Enumerated(EnumType.STRING)
    private RegionType regionType;

    @Enumerated(EnumType.STRING)
    private FeeType fee;
    private Integer price;
    private String url;

    public void update(ItemUpdateRequest item) {
        this.exhibitionType = item.getExhibitionType();
        this.progressType = item.getProgressType();
        this.image = item.getImage();
        this.title = item.getTitle();
        this.term = item.getTerm();
        this.place = item.getPlace();
        this.latitude = item.getLatitude();
        this.longitude = item.getLongitude();
        this.regionType = item.getRegionType();
        this.fee = item.getFee();
        this.price = item.getPrice();
        this.url = item.getUrl();
    }
}
