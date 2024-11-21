package kr.co._icia.finalProject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import kr.co._icia.finalProject.entity.Members;

public interface MemberRepository extends JpaRepository<Members, Long> {
	List<Members> findByMidLike(@Param("memid") String mid);

	Members findByMidAndMpw(@Param("mid") String mid, @Param("mpw") String mpw);

	Members findByMid(@Param("mid") String mid);

	Members findBymnickname(String mnickname);

	List<Members> findByMnicknameContaining(String mnickname);

}
