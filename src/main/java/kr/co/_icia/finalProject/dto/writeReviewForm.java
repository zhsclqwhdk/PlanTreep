package kr.co._icia.finalProject.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class writeReviewForm {
  
  private Long id;
  
  private String btitle;

  private String bprofilename;
  private MultipartFile[] bprofile;
}
