package kr.co._icia.finalProject.util.kakaoResponse;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoAddress {	// 공통적으로 써야하니까 private에서 public으로 변경
	private List<Document> documents;

	public List<Document> getDocuments() {
		return documents;
	}
}