package com.iotqlu.ngxadmin.repository;

import com.iotqlu.ngxadmin.domain.dto.Page;
import com.iotqlu.ngxadmin.domain.dto.SearchDetectFilesQuery;
import com.iotqlu.ngxadmin.domain.exception.NotFoundException;
import com.iotqlu.ngxadmin.domain.model.DetectFile;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Repository
public interface DetectFileRepo extends MongoRepository<DetectFile, ObjectId>, DetectFileRepoCustom {

    default DetectFile getById(ObjectId id) {
        return findById(id).orElseThrow(() -> new NotFoundException(DetectFile.class, id));
    }

    List<DetectFile> findAllById(Iterable<ObjectId> ids);

    List<DetectFile> findByUsername(String username);

}

interface DetectFileRepoCustom {

    List<DetectFile> searchDetectFiles(Page page, SearchDetectFilesQuery query);

}

class DetectFileRepoCustomImpl implements DetectFileRepoCustom {

    private final MongoTemplate mongoTemplate;

    DetectFileRepoCustomImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<DetectFile> searchDetectFiles(Page page, SearchDetectFilesQuery query) {
        List<AggregationOperation> operations = new ArrayList<>();

        List<Criteria> criteriaList = new ArrayList<>();
        if (!StringUtils.isEmpty(query.getId())) {
            criteriaList.add(Criteria.where("id").is(new ObjectId(query.getId())));
        }
        if (!StringUtils.isEmpty(query.getCreatorId())) {
            criteriaList.add(Criteria.where("creatorId").is(new ObjectId(query.getCreatorId())));
        }
        if (query.getCreatedAtStart() != null) {
            criteriaList.add(Criteria.where("createdAt").gte(query.getCreatedAtStart()));
        }
        if (query.getCreatedAtEnd() != null) {
            criteriaList.add(Criteria.where("createdAt").lt(query.getCreatedAtEnd()));
        }


        if (!criteriaList.isEmpty()) {
            Criteria authorCriteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
            operations.add(match(authorCriteria));
        }

        criteriaList = new ArrayList<>();

        if (!criteriaList.isEmpty()) {
            Criteria bookCriteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
            operations.add(lookup("books", "bookIds", "_id", "book"));
            operations.add(unwind("book", false));
            operations.add(match(bookCriteria));
        }

        operations.add(sort(Sort.Direction.DESC, "createdAt"));
        operations.add(skip((page.getNumber() - 1) * page.getLimit()));
        operations.add(limit(page.getLimit()));

        TypedAggregation<DetectFile> aggregation = newAggregation(DetectFile.class, operations);
        AggregationResults<DetectFile> results = mongoTemplate.aggregate(aggregation, DetectFile.class);
        return results.getMappedResults();
    }
}