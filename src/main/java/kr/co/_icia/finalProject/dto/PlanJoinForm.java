package kr.co._icia.finalProject.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class PlanJoinForm {
	
	private Long id;
	private Long member_id;
	
	private LocalDateTime pbasedate;   //시작일
	private LocalDateTime penddate;    //종료일
}
