package com.kilometer.domain.archive.service;

import static com.kilometer.common.statics.Statics.금칙어가_포함된_아카이브_코멘트;
import static com.kilometer.common.statics.Statics.아카이브_공개_설정;
import static com.kilometer.common.statics.Statics.아카이브_별점;
import static com.kilometer.common.statics.Statics.아카이브_코멘트;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.kilometer.common.annotation.SpringTestWithData;
import com.kilometer.domain.archive.archiveImage.ArchiveImageEntity;
import com.kilometer.domain.archive.archiveImage.ArchiveImageRepository;
import com.kilometer.domain.archive.dto.ArchiveInfo;
import com.kilometer.domain.archive.dto.PlaceInfo;
import com.kilometer.domain.archive.exception.ArchiveNotFoundException;
import com.kilometer.domain.archive.exception.ArchiveValidationException;
import com.kilometer.domain.archive.request.ArchiveCreateRequest;
import com.kilometer.domain.archive.request.ArchiveUpdateRequest;
import com.kilometer.domain.archive.userVisitPlace.UserVisitPlaceEntity;
import com.kilometer.domain.archive.userVisitPlace.UserVisitPlaceRepository;
import com.kilometer.domain.item.ItemEntity;
import com.kilometer.domain.item.ItemRepository;
import com.kilometer.domain.item.enumType.ExhibitionType;
import com.kilometer.domain.item.enumType.ExposureType;
import com.kilometer.domain.item.enumType.FeeType;
import com.kilometer.domain.item.enumType.RegionType;
import com.kilometer.domain.item.exception.ItemExposureOffException;
import com.kilometer.domain.item.exception.ItemNotFoundException;
import com.kilometer.domain.user.User;
import com.kilometer.domain.user.UserRepository;
import com.kilometer.domain.user.exception.UserNotFoundException;
import com.kilometer.exception.KilometerErrorCode;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SpringTestWithData
public class ArchiveServiceTest {

    private final List<String> 방문_사진 = List.of("photoUrls");
    private final List<PlaceInfo> 근처_맛집 = List.of(new PlaceInfo("FOOD", "맛집", "address", "roadAddress"));

    @Autowired
    private ArchiveService archiveService;

    @Autowired
    private ArchiveImageRepository archiveImageRepository;

    @Autowired
    private UserVisitPlaceRepository userVisitPlaceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    @DisplayName("아카이브와 관련한 정보를 등록한다.")
    void saveArchive() {
        // given
        User 회원 = 회원가입을_한다();
        ItemEntity 아이템 = 아이템을_등록한다();
        ArchiveCreateRequest request = new ArchiveCreateRequest(아이템.getId(), 아카이브_코멘트, 아카이브_별점, 아카이브_공개_설정, 방문_사진,
            근처_맛집);

        // when
        ArchiveInfo actual = archiveService.save(회원.getId(), request);

        // then
        assertAll(
            () -> assertThat(actual.getId()).isNotNull(),
            () -> assertThat(actual.getComment()).isEqualTo("comment"),
            () -> assertThat(actual.getPhotoUrls()).contains("photoUrls"),
            () -> assertThat(actual.getFood()).contains("맛집")
        );
    }

    @Test
    @DisplayName("아카이브와 관련한 정보를 등록 할 때, 아카이브 이미지와 방문장소도 같이 저장된다.")
    void saveArchive_archiveImageAndUserVisitPlace() {
        // given
        User 회원 = 회원가입을_한다();
        ItemEntity 아이템 = 아이템을_등록한다();
        ArchiveCreateRequest request = new ArchiveCreateRequest(아이템.getId(), 아카이브_코멘트, 아카이브_별점, 아카이브_공개_설정, 방문_사진,
            근처_맛집);

        // when
        ArchiveInfo archiveInfo = archiveService.save(회원.getId(), request);

        // then
        ArchiveImageEntity archiveImageEntity = archiveImageRepository.findById(1L).get();
        UserVisitPlaceEntity userVisitPlaceEntity = userVisitPlaceRepository.findById(1L).get();

        assertThat(archiveImageEntity.getId()).isEqualTo(1L);
        assertThat(userVisitPlaceEntity.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("아카이브 정보를 등록할때, 이미 등록한 Archive를 다시 등록하려 하면 예외가 발생한다.")
    void saveArchive_duplicate() {
        // given
        User 회원 = 회원가입을_한다();
        ItemEntity 아이템 = 아이템을_등록한다();
        ArchiveCreateRequest request = new ArchiveCreateRequest(아이템.getId(), 아카이브_코멘트, 아카이브_별점, 아카이브_공개_설정, 방문_사진,
            근처_맛집);
        ArchiveInfo 아카이브_생성_응답 = archiveService.save(회원.getId(), request);

        // when & then
        assertThatThrownBy(() -> archiveService.save(회원.getId(), request))
            .isInstanceOf(ArchiveValidationException.class)
            .hasMessage(
                String.format("이미 등록한 Archive가 있습니다. sItemId : %d / UserId : %d", 아카이브_생성_응답.getId(),
                    회원.getId()));
    }

    @Test
    @DisplayName("아카이브 정보를 등록할 때, photoUrls가 null이면 예외가 발생한다.")
    void saveArchive_nullPhotoUrls() {
        // given
        User 회원 = 회원가입을_한다();
        ItemEntity 아이템 = 아이템을_등록한다();

        List<String> invalidPhotoUrls = null;
        ArchiveCreateRequest request = new ArchiveCreateRequest(아이템.getId(), 아카이브_코멘트, 아카이브_별점, 아카이브_공개_설정,
            invalidPhotoUrls,
            근처_맛집);

        // when & then
        assertThatThrownBy(() -> archiveService.save(회원.getId(), request))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("아카이브 정보를 등록할 때, photoInfos가 null이면 예외가 발생한다.")
    void saveArchive_nullPhotoInfos() {
        // given
        User 회원 = 회원가입을_한다();
        ItemEntity 아이템 = 아이템을_등록한다();

        List<PlaceInfo> invalidPhotoInfos = null;
        ArchiveCreateRequest request = new ArchiveCreateRequest(아이템.getId(), 아카이브_코멘트, 아카이브_별점, 아카이브_공개_설정, 방문_사진,
            invalidPhotoInfos);

        // when & then
        assertThatThrownBy(() -> archiveService.save(회원.getId(), request))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("아카이브 정보를 등록할 때, 사용자가 존재하지 않으면 예외가 발생한다.")
    void saveArchive_notExistUser() {
        // given
        Long invalidUserId = 1L;

        ItemEntity 아이템 = 아이템을_등록한다();
        ArchiveCreateRequest request = new ArchiveCreateRequest(아이템.getId(), 아카이브_코멘트, 아카이브_별점, 아카이브_공개_설정, 방문_사진,
            근처_맛집);

        // when & then
        assertThatThrownBy(() -> archiveService.save(invalidUserId, request))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessage("저장되지 않은 사용자 입니다.");
    }

    @Test
    @DisplayName("아카이브 정보를 등록할 때, 코멘트에 금칙어가 포함되면 예외가 발생한다.")
    void saveArchive_forbiddenCommentWord() {
        // given
        User 회원 = 회원가입을_한다();
        ItemEntity 아이템 = 아이템을_등록한다();
        ArchiveCreateRequest request = new ArchiveCreateRequest(아이템.getId(), 금칙어가_포함된_아카이브_코멘트, 아카이브_별점, 아카이브_공개_설정,
            방문_사진,
            근처_맛집);

        // when & then
        assertThatThrownBy(() -> archiveService.save(회원.getId(), request))
            .isInstanceOf(ArchiveValidationException.class)
            .hasMessage("입력된 comment에 금칙어가 포함되어 있습니다.");
    }

    @Test
    @DisplayName("존재하지 않는 전시글에 아카이브를 등록할 수 없다.")
    void saveArchive_notExistsItem() {
        // given
        Long invalidItemId = -1L;
        User user = 회원가입을_한다();
        ArchiveCreateRequest request = new ArchiveCreateRequest(invalidItemId, 아카이브_코멘트, 아카이브_별점, 아카이브_공개_설정, 방문_사진,
            근처_맛집);

        // when
        ItemNotFoundException actual = assertThrows(ItemNotFoundException.class,
            () -> archiveService.save(user.getId(), request));

        // then
        assertEquals(actual.getErrorCode(), KilometerErrorCode.ITEM_NOT_FOUND);
    }

    @Test
    @DisplayName("미전시 전시글에 아카이브를 등록할 수 없다.")
    void saveArchive_exposureOffItem() {
        // given
        User user = 회원가입을_한다();
        ItemEntity item = ItemEntity.builder().exposureType(ExposureType.OFF).build();
        itemRepository.save(item);
        ArchiveCreateRequest request = new ArchiveCreateRequest(item.getId(), 아카이브_코멘트, 아카이브_별점, 아카이브_공개_설정, 방문_사진,
            근처_맛집);

        // when
        ItemExposureOffException actual = assertThrows(ItemExposureOffException.class,
            () -> archiveService.save(user.getId(), request));

        // then
        assertEquals(actual.getErrorCode(), KilometerErrorCode.ITEM_EXPOSURE_OFF);
    }

    @Test
    @DisplayName("아카이브를 수정한다.")
    void updateArchive() {
        // given
        User user = 회원가입을_한다();
        ItemEntity item = 아이템을_등록한다();
        ArchiveCreateRequest request = new ArchiveCreateRequest(item.getId(), 아카이브_코멘트, 아카이브_별점, 아카이브_공개_설정, 방문_사진,
            근처_맛집);
        ArchiveInfo savedArchive = archiveService.save(user.getId(), request);

        // when
        ArchiveUpdateRequest updateRequest = new ArchiveUpdateRequest(null, "수정된 아카이브 코멘트", 3, false, 방문_사진,
            근처_맛집);
        ArchiveInfo actual = archiveService.update(user.getId(), savedArchive.getId(), updateRequest);

        // then
        assertAll(
            () -> assertThat(actual.getComment()).isEqualTo("수정된 아카이브 코멘트"),
            () -> assertThat(actual.getStarRating()).isEqualTo(3),
            () -> assertThat(actual.getPhotoUrls()).hasSize(1),
            () -> assertThat(actual.getFood()).isEqualTo("맛집"),
            () -> assertThat(actual.getCafe()).isBlank()
        );
    }

    @Test
    @DisplayName("아카이브를 수정 할 때, 아카이브 이미지와 방문 사진도 함께 수정된다.")
    void updateArchive_withArchiveImageAndUserVisitPlace() {
        // given
        User user = 회원가입을_한다();
        ItemEntity item = 아이템을_등록한다();
        ArchiveCreateRequest request = new ArchiveCreateRequest(item.getId(), 아카이브_코멘트, 아카이브_별점, 아카이브_공개_설정, List.of(),
            List.of());
        ArchiveInfo savedArchive = archiveService.save(user.getId(), request);

        ArchiveUpdateRequest updateRequest = new ArchiveUpdateRequest(null, "수정된 아카이브 코멘트", 3, false, 방문_사진,
            근처_맛집);

        // when
        archiveService.update(user.getId(), savedArchive.getId(), updateRequest);

        // then
        ArchiveImageEntity archiveImageEntity = archiveImageRepository.findById(1L).get();
        UserVisitPlaceEntity userVisitPlaceEntity = userVisitPlaceRepository.findById(1L).get();

        assertThat(archiveImageEntity.getId()).isEqualTo(1L);
        assertThat(userVisitPlaceEntity.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("아카이브를 수정할 때, 수정하려는 코멘트에 금칙어가 포함되면 예외가 발생한다.")
    void updateArchive_forbiddenComment() {
        // given
        User user = 회원가입을_한다();
        ItemEntity item = 아이템을_등록한다();
        ArchiveCreateRequest request = new ArchiveCreateRequest(item.getId(), 아카이브_코멘트, 아카이브_별점, 아카이브_공개_설정, 방문_사진,
            근처_맛집);
        ArchiveInfo savedArchive = archiveService.save(user.getId(), request);

        ArchiveUpdateRequest updateRequest = new ArchiveUpdateRequest(null, 금칙어가_포함된_아카이브_코멘트, 3, false, List.of(),
            List.of());

        // then
        assertThatThrownBy(() -> archiveService.update(user.getId(), savedArchive.getId(), updateRequest))
            .isInstanceOf(ArchiveValidationException.class)
            .hasMessage("입력된 comment에 금칙어가 포함되어 있습니다.");
    }

    @Test
    @DisplayName("자신의 아카이브가 아니면 수정 요청을 보냈을 때 예외가 발생한다.")
    void updateArchive_unauthorized() {
        // given
        User user = 회원가입을_한다();
        ItemEntity item = 아이템을_등록한다();
        ArchiveCreateRequest request = new ArchiveCreateRequest(item.getId(), 아카이브_코멘트, 아카이브_별점, 아카이브_공개_설정, 방문_사진,
            근처_맛집);
        ArchiveInfo savedArchive = archiveService.save(user.getId(), request);

        ArchiveUpdateRequest updateRequest = new ArchiveUpdateRequest(null, "수정된 아카이브 코멘트", 3, false, List.of(),
            List.of());
        Long invalidUserId = -1L;

        // when
        ArchiveNotFoundException actualException = assertThrows(ArchiveNotFoundException.class,
            () -> archiveService.update(invalidUserId, savedArchive.getId(), updateRequest));

        // then
        assertEquals(actualException.getErrorCode(), KilometerErrorCode.ARCHIVE_NOT_FOUND);
    }

    @Test
    @DisplayName("존재하지 않는 아카이브를 수정 시도한다.")
    void updateArchive_archiveNotExists() {
        // given
        User user = 회원가입을_한다();
        ItemEntity item = 아이템을_등록한다();
        ArchiveCreateRequest request = new ArchiveCreateRequest(item.getId(), 아카이브_코멘트, 아카이브_별점, 아카이브_공개_설정, 방문_사진,
            근처_맛집);
        ArchiveInfo savedArchive = archiveService.save(user.getId(), request);

        // when
        ArchiveUpdateRequest updateRequest = new ArchiveUpdateRequest(null, "수정된 아카이브 코멘트", 3, false, List.of(),
            List.of());
        Long invalidArchiveId = -1L;
        ArchiveNotFoundException actualException = assertThrows(ArchiveNotFoundException.class,
            () -> archiveService.update(user.getId(), invalidArchiveId, updateRequest));

        // then
        assertEquals(actualException.getErrorCode(), KilometerErrorCode.ARCHIVE_NOT_FOUND);
    }

    private User 회원가입을_한다() {
        User user = User.builder()
            .name("user")
            .email("user@email.com")
            .build();
        return userRepository.save(user);
    }

    private ItemEntity 아이템을_등록한다() {
        ItemEntity item = ItemEntity.builder()
            .exposureType(ExposureType.ON)
            .exhibitionType(ExhibitionType.EXHIBITION)
            .regionType(RegionType.CHUNGCHEONG)
            .feeType(FeeType.FREE)
            .listImageUrl("listImageUrl")
            .thumbnailImageUrl("thumbnailImageUrl")
            .title("title")
            .placeName("placeName")
            .startDate(LocalDate.now())
            .endDate(LocalDate.now())
            .build();
        return itemRepository.save(item);
    }
}