package kr.co._icia.finalProject.dto;

import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MemberJoinForm {

  // @NotNull(message = "아이디를 입력해주세요")
  @Size(min = 3, max = 12, message = "아이디는 3 ~ 12자 이여야 합니다!")

  private String mid;
  
  @NotEmpty(message = "비밀번호를 입력해주세요")
  private String mpw;
  @NotEmpty(message = "이름을 입력해주세요")
  private String mname;
  private LocalDateTime mdate;
  private String maddr;
  @NotEmpty(message = "전화번호를 입력해주세요")
  private String mphone;
  @NotEmpty(message = "이메일을 입력해주세요")
  private String memail;
  private String mdomain;
  private String mnickname;

  private String mstate="C0";

  @NotEmpty
  private String postcode;// 우편번호
  @NotEmpty
  private String roadAddress;// 도로명주소
  @NotEmpty
  private String jibunAddress;// 지번주소
  @NotEmpty
  private String detailAddress;// 상세주소
  private String extraAddress;// 참고사항

  private String mprofilename;
  private MultipartFile mprofile;
}
