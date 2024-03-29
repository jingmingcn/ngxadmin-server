package com.iotqlu.ngxadmin.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@Data @AllArgsConstructor @NoArgsConstructor
public class Role implements GrantedAuthority {

    public static final String USER_ADMIN = "USER_ADMIN";
    public static final String AUTHOR_ADMIN = "AUTHOR_ADMIN";
    public static final String BOOK_ADMIN = "BOOK_ADMIN";

    public static final String ROLE_STUDENT = "student";
    public static final String ROLE_TEACHER = "teacher";
    public static final String ROLE_ADMIN = "admin";

    private String authority;

}
