package com.fileShare.global;

import java.util.Collection;

public enum Authority implements BaseEnum {
    ROLE_USER("사용자"),
    ROLE_ADMIN("관리자");

    private final String description;

    Authority(String description) {
        this.description = description;
    }

    public static Collection<Object> ROLE_USER() {
        return null;
    }

    @Override
    public String getName() {
        return name();
    }

    @Override
    public String getDescription() {
        return this.description;
    }
}
