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
@Entity @Table(name="PLANNODEBRIDGE")
@ToString
public class PlanNodeBridge {
	@Id
	@GeneratedValue
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="planDayDetail_id")
	private PlanDayDetail planDayDetailId;
	
	@ManyToOne
	@JoinColumn(name="node_id")
	private Node nodeId;
}
