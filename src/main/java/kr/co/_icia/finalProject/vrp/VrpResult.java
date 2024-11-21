package kr.co._icia.finalProject.vrp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * 경로 최적화 최종 결과
 */
@Setter
@Getter
public class VrpResult {
  private Integer totalJobCount; // 전체 배송 건수
  private Long cost; // 비용
  private Integer totalVehicleCount; // 전체 차량 개수
  /**
   * 차량별 경로 맵
   */
  private Map<String/* vehicleId */, List<VrpVehicleRoute>> vrpVehicleRouteListMap = new HashMap<String, List<VrpVehicleRoute>>();
  /**
   * 경로 목록
   */
  private List<VrpVehicleRoute> vrpVehicleRouteList = new ArrayList<VrpVehicleRoute>();
  /**
   * 경로 수
   */
  private Integer routeCount;
  /**
   * 전체 이동 거리
   */
  private Long totalDistance = 0L;
  /**
   * 전체 이동 시간
   */
  private Long totalTime = 0L;
  /**
   * 전체 방문지 개수
   */
  private Long totalVisitCount = 0L;

  /**
   * 경로 추가
   * 
   * @param vrpVehicleRoute
   */
  public void addVehilceRoute(VrpVehicleRoute vrpVehicleRoute) {
    String vehicleId = vrpVehicleRoute.getVehicleId();
    if (!vrpVehicleRouteListMap.containsKey(vehicleId)) {
      vrpVehicleRouteListMap.put(vehicleId, new ArrayList<>());
    }

    vrpVehicleRouteListMap.get(vehicleId).add(vrpVehicleRoute);
    vrpVehicleRouteList.add(vrpVehicleRoute);
    if ("end".equals(vrpVehicleRoute.getActivityName())) {
      totalTime += vrpVehicleRoute.getTotalTime();
      totalDistance += vrpVehicleRoute.getTotalDistance();
      totalVisitCount += vrpVehicleRoute.getTotalVisitCount();
    }
  }

  /**
   * 차량 개수
   * 
   * @return
   */
  public Integer getVehicleCount() {
    return vrpVehicleRouteListMap.keySet().size();
  }

}


