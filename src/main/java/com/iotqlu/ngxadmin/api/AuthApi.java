package com.iotqlu.ngxadmin.api;

import com.iotqlu.ngxadmin.configuration.security.JwtTokenUtil;
import com.iotqlu.ngxadmin.domain.dto.AuthRequest;
import com.iotqlu.ngxadmin.domain.dto.CreateUserRequest;
import com.iotqlu.ngxadmin.domain.dto.UserView;
import com.iotqlu.ngxadmin.domain.mapper.UserViewerMapper;
import com.iotqlu.ngxadmin.domain.model.User;
import com.iotqlu.ngxadmin.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.bson.types.ObjectId;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

@Tag(name = "Authentication")
@RestController @RequestMapping(path = "api/auth")
public class AuthApi {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserViewerMapper userViewerMapper;
    private final UserService userService;

    public AuthApi(AuthenticationManager authenticationManager,
                   JwtTokenUtil jwtTokenUtil,
                   UserViewerMapper userViewerMapper,
                   UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userViewerMapper = userViewerMapper;
        this.userService = userService;
    }

    @PostMapping("login")
    public ResponseEntity<UserView> login(@RequestBody @Valid AuthRequest request) {
        try {
            Authentication authenticate = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

            User user = (User) authenticate.getPrincipal();

            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION, jwtTokenUtil.generateAccessToken(user))
                    .body(userViewerMapper.toUserView(user));
        } catch (BadCredentialsException ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("register")
    public UserView register(@RequestBody @Valid CreateUserRequest request) {
        return userService.create(request);
    }

    @GetMapping("user")
    public UserView user(Authentication authentication) {
        String username = authentication.getName();
        return userService.getUserByUsername(username);
    }

}
