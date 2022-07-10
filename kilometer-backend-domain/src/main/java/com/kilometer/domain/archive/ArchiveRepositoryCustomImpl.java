package com.kilometer.domain.archive;

import com.kilometer.domain.archive.dto.ArchiveSortType;
import com.kilometer.domain.archive.dto.ArchiveSummary;
import com.kilometer.domain.archive.dto.ItemArchiveDto;
import com.kilometer.domain.archive.dto.MyArchiveDto;
import com.kilometer.domain.item.QItemEntity;
import com.kilometer.domain.user.QUser;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class ArchiveRepositoryCustomImpl implements ArchiveRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final static QArchive archive = QArchive.archive;
    private final static QUser user = QUser.user;
    private final static QItemEntity itemEntity = QItemEntity.itemEntity;

    public ArchiveRepositoryCustomImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Page<ItemArchiveDto> findAllByItemId(Pageable pageable, ArchiveSortType sortType,
        long itemId) {
        List<ItemArchiveDto> archives = queryFactory
            .select(Projections.fields(ItemArchiveDto.class,
                    archive.id,
                    user.name,
                    user.imageUrl,
                    archive.updatedAt,
                    archive.starRating,
                    archive.likeCount,
                    archive.comment
                )
            )
            .from(archive)
            .leftJoin(user)
            .on(user.id.eq(archive.user.id))
            .where(
                archive.item.id.eq(itemId),
                archive.isVisibleAtItem.eq(true)
            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(getOrderSpecifier(sortType))
            .fetch();

        int count = queryFactory
            .select(archive.id)
            .from(archive)
            .where(
                archive.item.id.eq(itemId)
            )
            .fetch().size();

        return new PageImpl<>(archives, pageable, count);
    }

    @Override
    public Page<MyArchiveDto> findAllByUserId(Pageable pageable, ArchiveSortType sortType,
        long userId) {

        List<MyArchiveDto> archives = queryFactory
            .select(Projections.fields(MyArchiveDto.class,
                archive.id,
                itemEntity.title,
                itemEntity.exhibitionType,
                itemEntity.thumbnailImageUrl,
                archive.comment,
                archive.updatedAt
            ))
            .from(archive)
            .leftJoin(itemEntity)
            .on(itemEntity.id.eq(archive.item.id))
            .where(
                archive.user.id.eq(userId)
            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(getOrderSpecifier(sortType))
            .fetch();

        int count = queryFactory
            .select(archive.id)
            .from(archive)
            .where(
                archive.user.id.eq(userId)
            )
            .fetch().size();

        return new PageImpl<>(archives, pageable, count);
    }

    @Override
    public Double avgStarRatingByItemId(long itemId) {
        return queryFactory
            .select(
                archive.starRating.avg()
            )
            .from(archive)
            .where(
                archive.item.id.eq(itemId)
            )
            .fetchOne();
    }

    @Override
    public Map<Long, ArchiveSummary> findAllArchiveInfosByItemIds(List<Long> itemIds) {
        if (itemIds.isEmpty()) {
            return Map.of();
        }
        return queryFactory
            .select(Projections.fields(ArchiveSummary.class,
                archive.item.id.as("itemId"),
                archive.starRating.avg().as("avgStarRating"),
                archive.item.id.count().as("archiveCount")
            ))
            .from(archive)
            .join(archive.item, QItemEntity.itemEntity)
            .where(archive.item.id.in(itemIds))
            .groupBy(archive.item.id)
            .fetch()
            .stream()
            .collect(
                Collectors.toMap(
                    ArchiveSummary::getItemId, Function.identity(), (k1, k2) -> k2
                )
            );
    }

    @SuppressWarnings("rawtypes")
    private OrderSpecifier getOrderSpecifier(ArchiveSortType sortType) {
        if (sortType == ArchiveSortType.LIKE_DESC) {
            return archive.likeCount.desc();
        }
        return archive.updatedAt.desc();
    }
}
