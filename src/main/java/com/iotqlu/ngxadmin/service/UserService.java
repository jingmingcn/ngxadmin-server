package com.iotqlu.ngxadmin.service;

import com.iotqlu.ngxadmin.domain.dto.CreateUserRequest;
import com.iotqlu.ngxadmin.domain.dto.Page;
import com.iotqlu.ngxadmin.domain.dto.SearchUsersQuery;
import com.iotqlu.ngxadmin.domain.dto.UpdateUserRequest;
import com.iotqlu.ngxadmin.domain.dto.UserView;
import com.iotqlu.ngxadmin.domain.mapper.UserEditorMapper;
import com.iotqlu.ngxadmin.domain.mapper.UserViewerMapper;
import com.iotqlu.ngxadmin.domain.model.User;
import com.iotqlu.ngxadmin.repository.UserRepo;
import org.bson.types.ObjectId;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ValidationException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

@Service
public class UserService implements UserDetailsService {

    private final UserRepo userRepo;
    private final UserEditorMapper userEditorMapper;
    private final UserViewerMapper userViewerMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepo userRepo,
                       UserEditorMapper userEditorMapper,
                       UserViewerMapper userViewerMapper,
                       PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.userEditorMapper = userEditorMapper;
        this.userViewerMapper = userViewerMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserView create(CreateUserRequest request) {
        if (userRepo.findByUsername(request.getUsername()).isPresent()) {
            throw new ValidationException("Username exists!");
        }
        if (!request.getPassword().equals(request.getRePassword())) {
            throw new ValidationException("Passwords don't match!");
        }
        if (request.getAuthorities() == null) {
            request.setAuthorities(new HashSet<>());
        }

        User user = userEditorMapper.create(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user = userRepo.save(user);

        return userViewerMapper.toUserView(user);
    }

    @Transactional
    public UserView update(ObjectId id, UpdateUserRequest request) {
        User user = userRepo.getById(id);
        userEditorMapper.update(request, user);

        user = userRepo.save(user);

        return userViewerMapper.toUserView(user);
    }

    @Transactional
    public UserView upsert(CreateUserRequest request) {
        Optional<User> optionalUser = userRepo.findByUsername(request.getUsername());

        if (optionalUser.isEmpty()) {
            return create(request);
        } else {
            UpdateUserRequest updateUserRequest = new UpdateUserRequest();
            updateUserRequest.setFullName(request.getFullName());
            return update(optionalUser.get().getId(), updateUserRequest);
        }
    }

    @Transactional
    public UserView delete(ObjectId id) {
        User user = userRepo.getById(id);

        user.setUsername(user.getUsername().replace("@", String.format("_%s@", user.getId().toString())));
        user.setEnabled(false);
        user = userRepo.save(user);

        return userViewerMapper.toUserView(user);
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo
                .findByUsername(username)
                .orElseThrow(
                        () -> new UsernameNotFoundException(format("User with username - %s, not found", username))
                );
    }

    public boolean usernameExists(String username) {
        return userRepo.findByUsername(username).isPresent();
    }

    public UserView getUser(ObjectId id) {
        return userViewerMapper.toUserView(userRepo.getById(id));
    }

    public UserView getUserByUsername(String username) {
        return userViewerMapper.toUserView(userRepo.getByUsername(username));
    }

    public List<UserView> searchUsers(Page page, SearchUsersQuery query) {
        List<User> users = userRepo.searchUsers(page, query);
        return userViewerMapper.toUserView(users);
    }

}
