package com.kilometer.backend.controller;

import com.kilometer.backend.security.exception.ResourceNotFoundException;
import com.kilometer.domain.item.ItemService;
import com.kilometer.domain.item.dto.DetailResponse;
import com.kilometer.domain.item.dto.SummaryResponse;
import com.kilometer.domain.util.ApiUrlUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiUrlUtils.ITEM_ROOT)
public class ItemController {

  private final ItemService itemService;

  @GetMapping(ApiUrlUtils.ITEM_INFO)
  @ApiOperation(value = "전시글 기본정보 API")
  public SummaryResponse getSummary(@ApiParam(value = "조회할 전시글 ID", required = true) @PathVariable Long itemId) {
    return itemService.findToSummaryResponseById(itemId)
        .orElseThrow(() -> new ResourceNotFoundException("Item", "id", itemId));
  }

  @GetMapping(ApiUrlUtils.ITEM_DETAIL)
  @ApiOperation(value = "전시글 소개 API")
  public DetailResponse getDetail(@ApiParam(value = "조회할 전시글 ID", required = true) @PathVariable Long itemId) {
    return itemService.findToDetailResponseById(itemId);
  }

}
