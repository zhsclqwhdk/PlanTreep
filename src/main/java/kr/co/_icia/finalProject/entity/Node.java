package kr.co._icia.finalProject.entity;
import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 노드  Class
 */
@Entity
@Getter @Setter
@Table(name="node")
@ToString
public class Node {
    @Id
    private Long id;//노드id
    private String name;
    private String address;
    private String phone;
    private Double x;//경도
    private Double y;//위도
    private Date regDt;//등록일시
    private Date modDt;//수정일시
    private String categoryGroupName;
    private String placeUrl;
	

	
}