package com.iotqlu.ngxadmin.domain.mapper;

import com.iotqlu.ngxadmin.domain.dto.BookView;
import com.iotqlu.ngxadmin.domain.model.Book;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring", uses = ObjectIdMapper.class)
public abstract class BookViewMapper {

    private UserViewerMapper userViewerMapper;

    @Autowired
    public void setUserViewMapper(UserViewerMapper userViewerMapper) {
        this.userViewerMapper = userViewerMapper;
    }

    public abstract BookView toBookView(Book book);

    public abstract List<BookView> toBookView(List<Book> books);

    @AfterMapping
    protected void after(Book book, @MappingTarget BookView bookView) {
        bookView.setCreator(userViewerMapper.toUserViewById(book.getCreatorId()));
    }

}
