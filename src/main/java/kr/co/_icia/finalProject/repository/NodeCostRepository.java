package kr.co._icia.finalProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co._icia.finalProject.entity.NodeCost;

public interface NodeCostRepository extends JpaRepository<NodeCost, Long> {

  NodeCost findByStartNodeIdAndEndNodeId(Long startNodeId, Long endNodeId);

}
