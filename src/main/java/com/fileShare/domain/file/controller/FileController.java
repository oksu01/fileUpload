package com.fileShare.domain.file.controller;

import com.fileShare.domain.file.dto.FileApi;
import com.fileShare.domain.file.service.FileService;
import com.fileShare.global.annotation.LoginId;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<List<FileApi>> uploadFiles(@RequestPart MultipartFile[] uploadFiles,
                                                     @LoginId Long loginId) {

        List<FileApi> uploadedFiles = fileService.uploadFiles(uploadFiles, loginId);

        return ResponseEntity.ok(uploadedFiles);
    }

}
