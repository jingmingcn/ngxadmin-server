package com.iotqlu.ngxadmin.service;

import com.iotqlu.ngxadmin.domain.dto.DetectFileView;
import com.iotqlu.ngxadmin.domain.mapper.DetectFileViewMapper;
import com.iotqlu.ngxadmin.domain.model.DetectFile;
import com.iotqlu.ngxadmin.repository.DetectFileRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DetectFileService {

    private final DetectFileRepo detectFileRepo;
    private final DetectFileViewMapper detectFileViewMapper;

    public DetectFileService(DetectFileRepo detectFileRepo,
                             DetectFileViewMapper detectFileViewMapper){
        this.detectFileRepo = detectFileRepo;
        this.detectFileViewMapper = detectFileViewMapper;
    }

    public DetectFileView getDetectFileViewByUsername(String username){
        List<DetectFile> list = detectFileRepo.findByUsername(username);
        return list.size()>0 ? detectFileViewMapper.toDetectFileView(list.get(0)):null;
    }

    public DetectFile getDetectFileByUsername(String username){
        List<DetectFile> list = detectFileRepo.findByUsername(username);
        return list.size()>0 ? list.get(0):null;
    }

    public void upsertDetectFile(DetectFile df){
        if(df.getId()==null){
            detectFileRepo.insert(df);
        }else{
            detectFileRepo.save(df);
        }
    }

}
