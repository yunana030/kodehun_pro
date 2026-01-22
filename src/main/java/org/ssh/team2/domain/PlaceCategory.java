package org.ssh.team2.domain;

public enum PlaceCategory {
    TOUR, HOTEL, CAFE, RESTR;

    public static PlaceCategory from(String input) {
        if (input == null || input.isBlank()) return null;
        for (PlaceCategory c : values()) {
            if (c.name().equalsIgnoreCase(input)) {
                return c;
            }
        }
        return null; // 없을 땐 null 반환 → 에러 대신 전체 검색용
    }
}
