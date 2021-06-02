package com.iotqlu.ngxadmin.domain.dto;

import lombok.Data;
import org.springframework.boot.context.properties.bind.Name;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
public class AuthRequest {

    @NotNull @Email
    private String email;
    @NotNull
    private String password;

}
