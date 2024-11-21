package kr.co._icia.finalProject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kr.co._icia.finalProject.entity.Plans;

public interface PlanRepository extends JpaRepository<Plans, Long> {

  List<Plans> findByMemberId(@Param("id") Long loginId);
}