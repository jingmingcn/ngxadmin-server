package com.iotqlu.ngxadmin.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class SearchDetectFilesQuery {

    private String id;

    private String creatorId;
    private LocalDateTime createdAtStart;
    private LocalDateTime createdAtEnd;

    private String username;
    private String teaname;

    private String status;
    private String comment;

}
