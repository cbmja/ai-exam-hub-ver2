package com.aiexamhub.exam.util;


import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class FileToMultipartFile implements MultipartFile {

    private File file;

    // 생성자에서 파일을 받음
    public FileToMultipartFile(File file) {
        this.file = file;
    }

    @Override
    public String getName() {
        return file.getName();
    }

    @Override
    public String getOriginalFilename() {
        return file.getName();
    }

    @Override
    public String getContentType() {
        try {
            return Files.probeContentType(file.toPath()); // 파일의 MIME 타입을 구함
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean isEmpty() {
        return file.length() == 0; // 파일 크기가 0이면 비어있는 파일로 처리
    }

    @Override
    public long getSize() {
        return file.length(); // 파일 크기 반환
    }

    @Override
    public byte[] getBytes() throws IOException {
        return Files.readAllBytes(file.toPath()); // 파일 내용을 바이트 배열로 반환
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return Files.newInputStream(file.toPath()); // 파일 내용을 InputStream으로 반환
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        Files.copy(file.toPath(), dest.toPath()); // 파일을 지정된 경로로 복사
    }
}