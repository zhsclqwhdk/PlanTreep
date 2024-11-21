package kr.co._icia.finalProject.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class WheaterPoint {
  
  private String lat;
  private String lon;
  
  public WheaterPoint() {
    
  }
  
  public WheaterPoint(String lat, String lon) {
    this.lat = lat;
    this.lon = lon;
  }
  

}