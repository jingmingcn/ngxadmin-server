package com.iotqlu.ngxadmin.domain.mapper;

import com.iotqlu.ngxadmin.domain.dto.DetectFileView;
import com.iotqlu.ngxadmin.domain.model.DetectFile;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = ObjectIdMapper.class)
public abstract class DetectFileViewMapper {

    public abstract DetectFileView toDetectFileView(DetectFile df);

}
