package kr.co._icia.finalProject.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@Entity @Table(name="BESTWORSTCHECK")
@ToString
public class BestCheck {
    @Id
     @GeneratedValue
     private Long id;
    
    private Long best;
    
    @ManyToOne
    private Members member;
    
    @ManyToOne
    private Board board;
}
