package com.kilometer.domain.item;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExhibitionType {

    EXHIBITION("전시회"), MUSICAL("뮤지컬"), FESTIVAL("뮤직페스티벌"), CONCERT("콘서트");

    private final String description;
}
