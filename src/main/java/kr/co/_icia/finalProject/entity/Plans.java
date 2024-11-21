package kr.co._icia.finalProject.entity;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Table(name = "PLANS")
public class Plans {

	@Id
	@GeneratedValue
	private Long id;

	private String pname;

	private String pstartdate;
	private String penddate;

	@ManyToOne
	@JoinColumn(name = "member_id")
	private Members member;

	@OneToMany(mappedBy = "plan")
	private List<PlanDayDetail> planDayDetailList;

}