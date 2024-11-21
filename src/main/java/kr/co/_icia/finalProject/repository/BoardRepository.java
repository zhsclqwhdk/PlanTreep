package kr.co._icia.finalProject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co._icia.finalProject.entity.Board;

public interface BoardRepository extends JpaRepository<Board, Long>{


Board findByPlanId(Long planId);

}