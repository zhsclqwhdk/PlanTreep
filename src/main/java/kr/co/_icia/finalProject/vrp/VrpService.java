package kr.co._icia.finalProject.vrp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.job.Shipment;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleType;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.util.Solutions;
import com.graphhopper.jsprit.core.util.VehicleRoutingTransportCostsMatrix;

public class VrpService {
  private Map<String/* vehicleId */, VehicleImpl.Builder> vehicleBuilderMap = new HashMap<String, VehicleImpl.Builder>();
  private Map<String/* shipmentId */, Shipment.Builder> shipmentBuilderMap = new HashMap<String, Shipment.Builder>();
  private VehicleRoutingTransportCostsMatrix.Builder costMatrixBuilder = VehicleRoutingTransportCostsMatrix.Builder
      .newInstance(true);

  /**
   * 차량 등록
   * 
   * @param vehicleId       차량ID
   * @param startLocationId 차량시작위치ID
   * @return
   */
  public VehicleImpl.Builder addVehicle(String vehicleId, String startLocationId, String endLocationId) {
    VehicleImpl.Builder builder = VehicleImpl.Builder.newInstance(vehicleId);
    builder.setType(getVehicleType());

    Location startLoc = Location.Builder.newInstance().setId(startLocationId).build();
    Location endLoc = Location.Builder.newInstance().setId(endLocationId).build();
    builder.setStartLocation(startLoc);
    builder.setEndLocation(endLoc);
    vehicleBuilderMap.put(vehicleId, builder);
    return builder;
  }

  /**
   * 업무등록
   * 
   * @param shipmentId         업무ID
   * @param pickupLocationId   픽업위치ID
   * @param deliveryLocationId 배송위치ID
   * @return
   */
  public Shipment.Builder addShipement(String shipmentId, String pickupLocationId, String deliveryLocationId) {
    Shipment.Builder shipmentBuilder = Shipment.Builder.newInstance(shipmentId);
    shipmentBuilder.setPickupLocation(Location.Builder.newInstance().setId(pickupLocationId).build());//
    shipmentBuilder.setDeliveryLocation(Location.Builder.newInstance().setId(deliveryLocationId).build());//
    shipmentBuilderMap.put(shipmentId, shipmentBuilder);
    return shipmentBuilder;
  }

  /**
   * 비용등록
   * 
   * @param fromLocationId 출발지위치ID
   * @param toLocationId   도착지위치ID
   * @param cost           비용
   */
  public void addCost(String fromLocationId, String toLocationId, long cost) {
    costMatrixBuilder.addTransportTime(fromLocationId, toLocationId, cost);
  }

  /**
   * 비용등록
   * 
   * @param fromLocationId 출발지위치ID
   * @param toLocationId   도착지위치ID
   * @param cost           비용(이동시간)
   * @param distnace       거리
   */
  public void addCost(String fromLocationId, String toLocationId, long cost, long distnace) {
    costMatrixBuilder.addTransportTime(fromLocationId, toLocationId, cost);
    costMatrixBuilder.addTransportDistance(fromLocationId, toLocationId, distnace);
  }

  /**
   * vrp 결과 조회
   * 
   * @return VrpResult
   */
  public VrpResult getVrpResult() {
    VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
    addVehicles(vrpBuilder);
    addShipments(vrpBuilder);
    VehicleRoutingTransportCostsMatrix vehicleRoutingTransportCostsMatrix = costMatrixBuilder.build();
    VehicleRoutingProblem vrp = vrpBuilder.setRoutingCost(vehicleRoutingTransportCostsMatrix).build();

    VehicleRoutingProblemSolution bestSolution = getBestSolution(vrp, vehicleRoutingTransportCostsMatrix);

    VrpResult vrpResult = new VrpResult();
    vrpResult.setTotalJobCount(shipmentBuilderMap.size());
    vrpResult.setTotalVehicleCount(vehicleBuilderMap.size());
    vrpResult.setCost(Double.valueOf(bestSolution.getCost()).longValue());

    List<VehicleRoute> list = new ArrayList<VehicleRoute>(bestSolution.getRoutes());
    Collections.sort(list, new com.graphhopper.jsprit.core.util.VehicleIndexComparator());

    int routeNo = 1;
    long costs = 0l;
    long totalDistance = 0l;
    long totalTime = 0l;
    long totalVisitCount = 0l;
    for (VehicleRoute route : list) {

      totalDistance = 0l;
      totalTime = 0l;
      totalVisitCount = 0l;
      VrpVehicleRoute startVrpVehicleRoute = new VrpVehicleRoute();
      startVrpVehicleRoute.setRouteNo(routeNo);
      String vehicleId = route.getVehicle().getId();
      startVrpVehicleRoute.setVehicleId(vehicleId);
      startVrpVehicleRoute.setActivityName(route.getStart().getName());
      startVrpVehicleRoute.setLocationId(route.getStart().getLocation().getId());
      startVrpVehicleRoute.setEndTime(Double.valueOf(route.getStart().getEndTime()).longValue());
      startVrpVehicleRoute.setCosts(costs);
      startVrpVehicleRoute.setJobId("-");
      startVrpVehicleRoute.setTotalDistance(totalDistance);
      startVrpVehicleRoute.setTotalTime(totalTime);
      startVrpVehicleRoute.setTotalVisitCount(totalVisitCount);
      vrpResult.addVehilceRoute(startVrpVehicleRoute);

      TourActivity prevAct = route.getStart();
      for (TourActivity act : route.getActivities()) {
        String jobId;
        if (act instanceof TourActivity.JobActivity) {
          jobId = ((TourActivity.JobActivity) act).getJob().getId();
        } else {
          jobId = "-";
        }

        double c = vrp.getTransportCosts().getTransportCost(prevAct.getLocation(), act.getLocation(),
            prevAct.getEndTime(), route.getDriver(), route.getVehicle());
        c += vrp.getActivityCosts().getActivityCost(act, act.getArrTime(), route.getDriver(), route.getVehicle());
        costs += c;

        double distance = vrp.getTransportCosts().getDistance(prevAct.getLocation(), act.getLocation(), 0l, null);
        totalDistance += distance;
        totalTime += vrp.getTransportCosts().getTransportTime(prevAct.getLocation(), act.getLocation(), 0l, null, null);
        totalTime += vrp.getActivityCosts().getActivityDuration(act, 0d, null, null);
        if (distance > 0) {
          totalVisitCount++;
        }

        VrpVehicleRoute vrpVehicleRoute = new VrpVehicleRoute();
        vrpVehicleRoute.setRouteNo(routeNo);
        vrpVehicleRoute.setVehicleId(vehicleId);
        vrpVehicleRoute.setActivityName(act.getName());
        vrpVehicleRoute.setLocationId(act.getLocation().getId());
        vrpVehicleRoute.setArrivalTime(Double.valueOf(act.getArrTime()).longValue());
        vrpVehicleRoute.setEndTime(Double.valueOf(act.getEndTime()).longValue());
        vrpVehicleRoute.setCosts(costs);
        vrpVehicleRoute.setJobId(jobId);
        vrpVehicleRoute.setTotalDistance(totalDistance);
        vrpVehicleRoute.setTotalTime(totalTime);
        vrpVehicleRoute.setTotalVisitCount(totalVisitCount);
        vrpResult.addVehilceRoute(vrpVehicleRoute);
        prevAct = act;
      }

      double c = vrp.getTransportCosts().getTransportCost(prevAct.getLocation(), route.getEnd().getLocation(),
          prevAct.getEndTime(), route.getDriver(), route.getVehicle());
      c += vrp.getActivityCosts().getActivityCost(route.getEnd(), route.getEnd().getArrTime(), route.getDriver(),
          route.getVehicle());
      costs += c;

      double distance = vrp.getTransportCosts().getDistance(prevAct.getLocation(), route.getEnd().getLocation(), 0l,
          null);
      totalDistance += distance;
      totalTime += vrp.getTransportCosts().getTransportTime(prevAct.getLocation(), route.getEnd().getLocation(), 0l,
          null, null);
      totalTime += vrp.getActivityCosts().getActivityDuration(prevAct, 0d, null, null);
      if (distance > 0) {
        totalVisitCount++;
      }
      VrpVehicleRoute endVrpVehicleRoute = new VrpVehicleRoute();
      endVrpVehicleRoute.setRouteNo(routeNo);
      endVrpVehicleRoute.setVehicleId(vehicleId);
      endVrpVehicleRoute.setActivityName(route.getEnd().getName());
      endVrpVehicleRoute.setLocationId(route.getEnd().getLocation().getId());
      endVrpVehicleRoute.setArrivalTime(Double.valueOf(route.getEnd().getArrTime()).longValue());
      endVrpVehicleRoute.setCosts(costs);
      endVrpVehicleRoute.setTotalDistance(totalDistance);
      endVrpVehicleRoute.setTotalTime(totalTime);
      endVrpVehicleRoute.setTotalVisitCount(totalVisitCount);
      endVrpVehicleRoute.setJobId("-");
      vrpResult.addVehilceRoute(endVrpVehicleRoute);
      routeNo++;
    }
    vrpResult.setRouteCount(routeNo - 1);
    return vrpResult;
  }

  /**
   * 화물 등록
   * 
   * @param vrpBuilder
   */
  private void addShipments(VehicleRoutingProblem.Builder vrpBuilder) {
    for (Shipment.Builder shipmentBuilder : shipmentBuilderMap.values()) {
      Shipment shipment = shipmentBuilder.build();
      vrpBuilder.addJob(shipment);
    }
  }

  /**
   * 차량 유형 생성
   * 
   * @return
   */
  private static VehicleType getVehicleType() {
    VehicleTypeImpl.Builder vehicleTypeBuilder = VehicleTypeImpl.Builder.newInstance("DEFAULT_VEHICLE_TYPE")//
        .setCostPerDistance(1)// 이동거리
        .setCostPerWaitingTime(1)// 대기시간
        .setCostPerTransportTime(1)// 이동시간
        .setCostPerServiceTime(1)// 서비스시간
        .addCapacityDimension(0, 15);//
    return vehicleTypeBuilder.build();
  }

  /**
   * 경로최적화 모듈에 차량 등록
   * 
   * @param vrpBuilder
   */
  private void addVehicles(VehicleRoutingProblem.Builder vrpBuilder) {
    for (VehicleImpl.Builder vehicleBuilder : vehicleBuilderMap.values()) {
      VehicleImpl vehicle = vehicleBuilder.build();
      vrpBuilder.addVehicle(vehicle);
    }
  }

  /**
   * 가장 좋은 결과 조회
   * 
   * @param vrp
   * @param vehicleRoutingTransportCostsMatrix
   * @return
   */
  private VehicleRoutingProblemSolution getBestSolution(VehicleRoutingProblem vrp,
      VehicleRoutingTransportCostsMatrix vehicleRoutingTransportCostsMatrix) {

    // build the problem
    com.graphhopper.jsprit.core.algorithm.box.Jsprit.Builder jspritBuilder = Jsprit.Builder.newInstance(vrp);//
    VehicleRoutingAlgorithm algorithm = jspritBuilder.buildAlgorithm();

    Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();
    VehicleRoutingProblemSolution bestSolution = Solutions.bestOf(solutions);

    return bestSolution;
  }

}


