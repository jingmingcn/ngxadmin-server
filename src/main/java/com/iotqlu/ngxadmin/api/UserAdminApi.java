package com.iotqlu.ngxadmin.api;

import com.iotqlu.ngxadmin.domain.dto.CreateUserRequest;
import com.iotqlu.ngxadmin.domain.dto.ListResponse;
import com.iotqlu.ngxadmin.domain.dto.SearchRequest;
import com.iotqlu.ngxadmin.domain.dto.SearchUsersQuery;
import com.iotqlu.ngxadmin.domain.dto.UpdateUserRequest;
import com.iotqlu.ngxadmin.domain.dto.UserView;
import com.iotqlu.ngxadmin.domain.model.Role;
import com.iotqlu.ngxadmin.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

@Tag(name = "UserAdmin")
@RestController @RequestMapping(path = "api/admin/user")
@RolesAllowed(Role.USER_ADMIN)
public class UserAdminApi {

    private final UserService userService;

    public UserAdminApi(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserView create(@RequestBody @Valid CreateUserRequest request) {
        return userService.create(request);
    }

    @PutMapping("{id}")
    public UserView update(@PathVariable String id, @RequestBody @Valid UpdateUserRequest request) {
        return userService.update(new ObjectId(id), request);
    }

    @DeleteMapping("{id}")
    public UserView delete(@PathVariable String id) {
        return userService.delete(new ObjectId(id));
    }

    @GetMapping("{id}")
    public UserView get(@PathVariable String id) {
        return userService.getUser(new ObjectId(id));
    }

    @PostMapping("search")
    public ListResponse<UserView> search(@RequestBody SearchRequest<SearchUsersQuery> request) {
        return new ListResponse<>(userService.searchUsers(request.getPage(), request.getQuery()));
    }

}
