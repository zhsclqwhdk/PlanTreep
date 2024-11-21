package kr.co._icia.finalProject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kr.co._icia.finalProject.entity.Members;
import kr.co._icia.finalProject.entity.ShareContent;

public interface ShareContentRepository extends JpaRepository<ShareContent, Long> {

  List<ShareContent> findByReceiveMemberId_IdOrderByIdDesc(Long receiveMemberId);

  @Query("SELECT c FROM ShareContent c WHERE c.receiveMemberId.id = :id AND c.checkState = 0")
  List<ShareContent> findByMembersId(@Param("id") Long id);
  
  @Query("SELECT c FROM ShareContent c WHERE c.receiveMemberId.mid = :mid AND c.checkState = 0")
  List<ShareContent> findByMembersMid(@Param("mid") String mid);

  ShareContent findByPlanId_Id(Long planId);

  List<ShareContent> findByReceiveMemberIdAndSendMemberId(Members receiveMembers, Members sendMembers);

}