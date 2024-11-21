package kr.co._icia.finalProject.entity;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import kr.co._icia.finalProject.dto.MemberJoinForm;
import kr.co._icia.finalProject.util.KakaoApiUtil;
import kr.co._icia.finalProject.util.KakaoApiUtil.Point;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Table(name = "MEMBERS")
@ToString
public class Members {

	@Id
	@GeneratedValue
	private Long id;

	@Column(nullable = false, unique = true)
	private String mid;
	private String mpw;
	private String mname;
	private LocalDateTime mdate;
	private String postcode;// 우편번호
	private String roadAddress;// 도로명주소
	private String jibunAddress;// 지번주소
	private String detailAddress;// 상세주소
	private String extraAddress;// 참고사항
	private Double memberX;
	private Double memberY;
	private String mphone;
	private String memail;
	private String mdomain;
	private String mnickname;
	private String mstate;

	private String mprofilename;

	@OneToMany(mappedBy = "member")
	private List<Plans> planlist;

	public static Members createMembers(MemberJoinForm joinForm) throws IOException, InterruptedException {
		Members member = new Members();
		member.setMid(joinForm.getMid());
		member.setMpw(joinForm.getMpw());
		member.setMname(joinForm.getMname());
		member.setMphone(joinForm.getMphone());
		member.setMemail(joinForm.getMemail());
		member.setMdomain(joinForm.getMdomain());
		member.setPostcode(joinForm.getPostcode());
		member.setRoadAddress(joinForm.getRoadAddress());
		member.setJibunAddress(joinForm.getJibunAddress());
		member.setDetailAddress(joinForm.getDetailAddress());
		member.setExtraAddress(joinForm.getExtraAddress());
		member.setMstate(joinForm.getMstate());
		member.setMnickname(joinForm.getMnickname());
		member.setMdate(joinForm.getMdate());
		member.setMnickname(joinForm.getMnickname());
		member.setMprofilename(joinForm.getMprofilename());
		List<Point> memberPoint = KakaoApiUtil.getPointByAddress(member.getJibunAddress());
		member.setMemberX(memberPoint.get(0).getX());
		member.setMemberY(memberPoint.get(0).getY());

		return member;
	}
}