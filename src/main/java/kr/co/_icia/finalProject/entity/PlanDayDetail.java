package kr.co._icia.finalProject.entity;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import kr.co._icia.finalProject.controller.PlanSave;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "PLANDAYDETAIL")
public class PlanDayDetail {

  @Id
  @GeneratedValue
  private Long id;

  private String detailDate;
  
  @ManyToOne
  @JoinColumn(name = "plan_id")
  private Plans plan;

  @OneToMany(mappedBy = "planDayDetailId")
  private List<PlanNodeBridge> nodeId;

  @Lob
  private String pathjson;

  public static PlanDayDetail createPlan(PlanSave planSave, Plans plans) {
    PlanDayDetail planDayDetail = new PlanDayDetail();
    planDayDetail.setDetailDate(planSave.getDetailDate());
    planDayDetail.setPlan(plans);
    return planDayDetail;
  }

}
