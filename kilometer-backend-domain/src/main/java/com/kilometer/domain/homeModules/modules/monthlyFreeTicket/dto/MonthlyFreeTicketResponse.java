package com.kilometer.domain.homeModules.modules.monthlyFreeTicket.dto;

import com.kilometer.domain.converter.listItem.dto.ListItem;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MonthlyFreeTicketResponse {

    private String topTitle;
    private String bottomTitle;
    private List<ListItem> contents;

    public static MonthlyFreeTicketResponse of(String topTitle, String bottomTitle,
        List<ListItem> contents) {
        return new MonthlyFreeTicketResponse(topTitle, bottomTitle, contents);
    }
}