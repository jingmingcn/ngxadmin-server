package com.iotqlu.ngxadmin.domain.mapper;

import com.iotqlu.ngxadmin.domain.dto.UserView;
import com.iotqlu.ngxadmin.domain.model.User;
import com.iotqlu.ngxadmin.repository.UserRepo;
import org.bson.types.ObjectId;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", uses = {ObjectIdMapper.class})
public class UserViewerMapper {

    @Autowired
    private UserRepo userRepo;

    public UserView toUserView(User user){
        UserView uv = new UserView();
        uv.setId(user.getId().toString());
        uv.setUsername(user.getUsername());
        uv.setFullName(user.getFullName());
        uv.setName(user.getFullName());
        return uv;
    }

    public List<UserView> toUserView(List<User> users){
        List<UserView> list = new ArrayList<>();
        for(User u:users){
            list.add(toUserView(u));
        }
        return list;
    }

    public UserView toUserViewById(ObjectId id) {
        if (id == null) {
            return null;
        }
        return toUserView(userRepo.getById(id));
    }

}
