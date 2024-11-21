package kr.co._icia.finalProject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co._icia.finalProject.entity.BestCheck;

public interface BestCheckRepository extends JpaRepository<BestCheck, Long> {


	BestCheck findByMemberIdAndBoardId(Long memberId, Long boardId);

	List<BestCheck> findByBoardId(Long memberId);

}