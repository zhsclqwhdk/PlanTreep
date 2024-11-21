package kr.co._icia.finalProject.controller;

import java.util.List;

import kr.co._icia.finalProject.entity.Node;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlanSave {

  private String detailDate;

  private List<Node> planList;

  private List<Node> pathPoints;
}
