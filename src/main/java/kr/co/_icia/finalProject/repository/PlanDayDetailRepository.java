package kr.co._icia.finalProject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co._icia.finalProject.entity.PlanDayDetail;
import kr.co._icia.finalProject.entity.Plans;

public interface PlanDayDetailRepository extends JpaRepository<PlanDayDetail, Long> {
	List<PlanDayDetail> findByPlanId(Long planId);

	List<PlanDayDetail> findByPlanIdOrderByDetailDate(Long planId);
}