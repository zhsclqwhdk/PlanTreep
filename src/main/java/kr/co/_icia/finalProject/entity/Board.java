package kr.co._icia.finalProject.entity;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="BOARD")
public class Board {
  
  @Id
  @GeneratedValue
  private Long id;
  
  private String boarddate;
  
  // 개추
  private Long bestCnt;
  
  // 비추
  private Long worstCnt;

  // 업로드 파일명
  private String filename;
  
  // 조회수
  private int hits = 0;
  
//작성자 정보 조인
  @ManyToOne
  @JoinColumn(name="member_id")
  private Members member;

// 플랜 정보 조인
  @OneToOne
  @JoinColumn(name="plan_id")
  private Plans plan;

  
  
}
