package org.zerock.b01.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Swagger UI를 위한 별도의 DTO클래스 설정
 */
@Data
public class UploadFileDTO {

    private List<MultipartFile> files;
}
