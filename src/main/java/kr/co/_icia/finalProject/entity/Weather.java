package kr.co._icia.finalProject.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "WEATHER")
// 삭제? (3)
public class Weather {
	@Id
	private Long id;
	
	private String temp;
	private String feels_like;
	private String temp_min;
	private String temp_max;
	private String main;
	private String description;
	private String icon;
	
	@Column(name="clouds_all")
	private String all;
	
	private String speed;
	private String deg;
	private String gust;
	private String visibility;
	private String pop;
	private String pod;
	private String dt_txt;
}