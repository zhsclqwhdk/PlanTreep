package kr.co._icia.finalProject.dto;

import kr.co._icia.finalProject.entity.Plans;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class PlanDetailForm {
	private Plans plan;
	private String jsonpath;
}
