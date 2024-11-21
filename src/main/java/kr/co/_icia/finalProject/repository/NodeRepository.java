package kr.co._icia.finalProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co._icia.finalProject.entity.Node;

public interface NodeRepository extends JpaRepository<Node, Long> {
  
  Node findByid(Long id);

}
