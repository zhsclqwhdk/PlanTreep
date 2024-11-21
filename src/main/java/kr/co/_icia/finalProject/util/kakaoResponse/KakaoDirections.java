package kr.co._icia.finalProject.util.kakaoResponse;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true) // 파싱시 없는 필드 무시
public class KakaoDirections { // class에 static을 붙이는 경우는 class 안에 class를 정의하는 경우

  private List<Route> routes;

  public List<Route> getRoutes() {
    return routes;
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  @Getter
  public static class Route {
    private List<Section> sections;
    private Summary summary;

    public List<Section> getSections() {
      return sections;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    public static class Summary {
      private Fare fare;
      private Integer distance;
      private Integer duration;

      @JsonIgnoreProperties(ignoreUnknown = true)
      @Getter
      public static class Fare {
        private Integer taxi;
        private Integer toll;
       
      }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Section {
      private List<Road> roads;

      public List<Road> getRoads() {
        return roads;
      }

      @JsonIgnoreProperties(ignoreUnknown = true)
      public static class Road {
        private List<Double> vertexes;

        public List<Double> getVertexes() {
          return vertexes;
        }

      }

    }
  }
}