package kr.co._icia.finalProject.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@Entity @Table(name="SHARECONTENT")
@ToString
public class ShareContent {
	@Id
	@GeneratedValue
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="send_member_id")
	private Members sendMemberId;
	
	@ManyToOne
	@JoinColumn(name="receive_member_id")
	private Members receiveMemberId;
	
	@ManyToOne
	@JoinColumn(name="share_plan_id")
	private Plans planId;
	
	// 아직 : 0 , 확인 : 1
	private Integer checkState = 0;
}