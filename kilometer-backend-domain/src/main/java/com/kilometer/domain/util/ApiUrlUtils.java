package com.kilometer.domain.util;

public class ApiUrlUtils {
    private static final String ROOT = "/api";
    public static final String SEARCH_ROOT = ROOT + "/search";
    public static final String PICK_ROOT = ROOT + "/pick";
    public static final String PICK_ITEM = PICK_ROOT + "/{itemId}";
    public static final String PICK_ITEM_PATTERN = PICK_ROOT + "/%s";

    public static String getPickItemUrl(long itemId, boolean pickStatus) {
        return String.format(PICK_ITEM_PATTERN + "?status=%s", itemId, pickStatus);
    }
}
