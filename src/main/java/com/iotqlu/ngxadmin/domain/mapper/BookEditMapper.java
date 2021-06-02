package com.iotqlu.ngxadmin.domain.mapper;

import com.iotqlu.ngxadmin.domain.dto.EditBookRequest;
import com.iotqlu.ngxadmin.domain.model.Book;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValueCheckStrategy.ALWAYS;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring", uses = ObjectIdMapper.class)
public interface BookEditMapper {

    Book create(EditBookRequest request);

    @BeanMapping(nullValueCheckStrategy = ALWAYS, nullValuePropertyMappingStrategy = IGNORE)
    void update(EditBookRequest request, @MappingTarget Book book);

}
