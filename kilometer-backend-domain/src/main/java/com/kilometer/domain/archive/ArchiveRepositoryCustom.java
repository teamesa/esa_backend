package com.kilometer.domain.archive;

import com.kilometer.domain.archive.dto.ArchiveSummary;
import com.kilometer.domain.archive.dto.ItemArchiveDto;
import com.kilometer.domain.archive.dto.ArchiveSortType;
import com.kilometer.domain.archive.dto.MyArchiveDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface ArchiveRepositoryCustom {

    Page<ItemArchiveDto> findAllByItemId(Pageable pageable, ArchiveSortType sortType,
        long itemId);
    Page<MyArchiveDto> findAllByUserId(Pageable pageable, ArchiveSortType sortType,
        long userId);

    Double avgStarRatingByItemId(long itemId);

    Map<Long, ArchiveSummary> findAllArchiveInfosByItemIds(List<Long> itemIds);
}
