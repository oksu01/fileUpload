package com.fileShare.domain.file.service;


import com.fileShare.domain.file.dto.FileApi;
import com.fileShare.domain.file.repository.FileRepository;
import com.fileShare.domain.member.repository.MemberRepository;
import com.fileShare.global.exception.FileNotFoundException;
import com.fileShare.global.exception.MemberAccessDeniedException;
import lombok.RequiredArgsConstructor;

import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;
    private final MemberRepository memberRepository;


    @Value("${solo_project.uploadFile.path}") // application.properties 의 변수
    private String uploadPath;

    public List<FileApi> uploadFiles(@RequestPart MultipartFile[] uploadFiles, Long loginId) {

        validateUser(loginId);

        List<FileApi> files = new ArrayList<>();

        for (MultipartFile uploadFile : uploadFiles) {
            FileApi resultDTO = uploadFile(uploadFile);
            files.add(resultDTO);
        }

        return files;
    }

    private FileApi uploadFile(MultipartFile uploadFile) {

        if (uploadFile.isEmpty()) {

            // 파일이 비어있을 경우 예외 처리
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }

        // 이미지 파일만 업로드 가능
        if (!uploadFile.getContentType().startsWith("image")) {
            throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다.");
        }

        String originalName = uploadFile.getOriginalFilename();
        String fileName = originalName.substring(originalName.lastIndexOf("\\") + 1);

        // 파일 저장 경로 생성( 날짜 폴더 생성 )
        String folderPath = makeFolder();

        // 고유한 이름 생성해서 파일 이름으로 사용 -> 파일 중복 방지
        String uuid = UUID.randomUUID().toString();

        String saveName = uploadPath + File.separator + folderPath + File.separator + uuid + "_" + fileName;

        Path savePath = Paths.get(saveName);

        try {
            // 원본 파일 저장
            uploadFile.transferTo(savePath.toFile());

            // 썸네일 파일 이름은 중간에 s_로 시작 하게끔
            String thumbnailSaveName = uploadPath + File.separator + folderPath + File.separator + "s_" + uuid + "_" + fileName;

            File thumbnailFile = new File(thumbnailSaveName);

            Thumbnailator.createThumbnail(savePath.toFile(), thumbnailFile, 100, 100);

            return new FileApi(fileName, uuid, folderPath);
        } catch (IOException e) {

            // 파일 저장 중 예외 처리
            throw new RuntimeException("파일 저장 중 오류가 발생했습니다.", e);
        }
    }

    public void deleteFileById(Long fileId) {

        validateFile(fileId);

        fileRepository.deleteById(fileId);
    }


    private String makeFolder() {
        String str = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyy/MM/dd"));
        String folderPath = str.replace("/", File.separator);

        File uploadPathFolder = new File(uploadPath, folderPath);
        if (!uploadPathFolder.exists()) {
            uploadPathFolder.mkdirs();
        }

        return folderPath;
    }

    private void validateFile(Long fileId) {

        fileRepository.findById(fileId).orElseThrow(FileNotFoundException::new);
    }

    private void validateUser(Long loginId) {
        memberRepository.findById(loginId).orElseThrow(MemberAccessDeniedException::new);
    }

}