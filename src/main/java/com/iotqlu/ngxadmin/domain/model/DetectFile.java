package com.iotqlu.ngxadmin.domain.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;

@Document(collection = "detectfiles") @Data
public class DetectFile implements Serializable {

    @Id
    private ObjectId id;

    @CreatedBy
    private ObjectId creatorId;
    @LastModifiedBy
    private ObjectId modifierId;

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime modifiedAt;

    private String username;
    private String teaname;
    private String filename;
    private String status;
    private String comment;

}
