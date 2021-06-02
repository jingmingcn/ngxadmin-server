package com.iotqlu.ngxadmin.controller;

import com.iotqlu.ngxadmin.domain.dto.DetectFileView;
import com.iotqlu.ngxadmin.domain.model.DetectFile;
import com.iotqlu.ngxadmin.domain.model.Role;
import com.iotqlu.ngxadmin.payload.UploadFileResponse;
import com.iotqlu.ngxadmin.repository.DetectFileRepo;
import com.iotqlu.ngxadmin.service.DetectFileService;
import com.iotqlu.ngxadmin.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "api/thesis")
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private DetectFileService detectFileService;

    @RolesAllowed(Role.ROLE_STUDENT)
    @PostMapping("/uploadFile")
    public UploadFileResponse uploadFile(Authentication authentication,
                                         @RequestParam("file") MultipartFile file,
                                         @RequestParam("type") String type) {

        String username = authentication.getName();

        String originFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String saveFileName = username + originFileName.substring(originFileName.lastIndexOf("."));

        String fileName = fileStorageService.storeFile(file,type+"/"+saveFileName);

        DetectFile df = detectFileService.getDetectFileByUsername(username);
        if(df == null){
            df = new DetectFile();
            df.setUsername(username);
        }
        df.setFilename(fileName);
        df.setStatus("0");
        detectFileService.upsertDetectFile(df);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile/")
                .path(fileName)
                .toUriString();

        return new UploadFileResponse(fileName, fileDownloadUri,
                file.getContentType(), file.getSize());
    }

    //@RolesAllowed(Role.ROLE_STUDENT)
    //@PostMapping("/uploadFile")
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file) {

        String fileName = fileStorageService.storeFile(file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile/")
                .path(fileName)
                .toUriString();

        return new UploadFileResponse(fileName, fileDownloadUri,
                file.getContentType(), file.getSize());
    }

    @RolesAllowed(Role.ROLE_STUDENT)
    @PostMapping("/uploadMultipleFiles")
    public List<UploadFileResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        return Arrays.asList(files)
                .stream()
                .map(file -> uploadFile(file))
                .collect(Collectors.toList());
    }

    @RolesAllowed(Role.ROLE_STUDENT)
    @GetMapping("/downloadFile/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @RolesAllowed(Role.ROLE_STUDENT)
    @GetMapping("/downloadFile/{type:.+}/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String type,
                                                 @PathVariable String fileName,
                                                 HttpServletRequest request) {
        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(type+"/"+fileName);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @RolesAllowed(Role.ROLE_STUDENT)
    @GetMapping("/downloadDetectFile")
    public ResponseEntity<Resource> downloadDetectFile(Authentication authentication,
                                                 HttpServletRequest request) {
        String username = authentication.getName();

        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource("detect/"+username+".docx");

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @RolesAllowed(Role.ROLE_STUDENT)
    @GetMapping("/detectfile")
    public DetectFileView getDetectFile(Authentication authentication){
        String username = authentication.getName();
        DetectFileView df = detectFileService.getDetectFileViewByUsername(username);
        return df;
    }



}
