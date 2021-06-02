package com.iotqlu.ngxadmin.domain.mapper;

import com.iotqlu.ngxadmin.domain.dto.AuthorView;
import com.iotqlu.ngxadmin.domain.model.Author;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring", uses = ObjectIdMapper.class)
public abstract class AuthorViewMapper {

    private UserViewerMapper userViewerMapper;

    @Autowired
    public void setUserViewMapper(UserViewerMapper userViewerMapper) {
        this.userViewerMapper = userViewerMapper;
    }

    public abstract AuthorView toAuthorView(Author author);

    public abstract List<AuthorView> toAuthorView(List<Author> authors);

    @AfterMapping
    protected void after(Author author, @MappingTarget AuthorView authorView) {
        authorView.setCreator(userViewerMapper.toUserViewById(author.getCreatorId()));
    }

}
