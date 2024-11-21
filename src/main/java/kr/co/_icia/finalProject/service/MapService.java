package kr.co._icia.finalProject.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co._icia.finalProject.entity.Node;
import kr.co._icia.finalProject.entity.NodeCost;
import kr.co._icia.finalProject.parameter.NodeCostParam;
import kr.co._icia.finalProject.util.JsonResult;
import kr.co._icia.finalProject.util.KakaoApiUtil;
import kr.co._icia.finalProject.util.KakaoApiUtil.Point;
import kr.co._icia.finalProject.util.kakaoResponse.KakaoDirections;
import kr.co._icia.finalProject.util.kakaoResponse.KakaoDirections.Route;
import kr.co._icia.finalProject.util.kakaoResponse.KakaoDirections.Route.Section;
import kr.co._icia.finalProject.util.kakaoResponse.KakaoDirections.Route.Summary;
import kr.co._icia.finalProject.util.kakaoResponse.KakaoDirections.Route.Section.Road;
import kr.co._icia.finalProject.util.kakaoResponse.KakaoDirections.Route.Summary.Fare;
import kr.co._icia.finalProject.vrp.VrpResult;
import kr.co._icia.finalProject.vrp.VrpService;
import kr.co._icia.finalProject.vrp.VrpVehicleRoute;

@Service
public class MapService {
	@Autowired
	private KakaoApiService kakaoApiService;

	public JsonResult searchPoi(String keyword, Point center) throws Exception {
		List<Point> pointByKeyword = KakaoApiUtil.getPointByKeyword(keyword, center);
		List<Node> nodeList = new ArrayList<>();
		for (Point point : pointByKeyword) {
			Long pointId = point.getId();
			Node node = kakaoApiService.findNode(pointId);
			if (node == null) {
				node = new Node();
				node.setId(pointId);
				node.setName(point.getName());
				node.setAddress(point.getAddress());
				node.setPhone(point.getPhone());
				node.setX(point.getX());
				node.setY(point.getY());
				node.setRegDt(new Date());
				node.setModDt(new Date());
				node.setCategoryGroupName(point.getCategoryGroupName());
				node.setPlaceUrl(point.getPlaceUrl());
				kakaoApiService.registNode(node);
				// System.out.println(point.toString());
			}
			nodeList.add(node);
		}

		int totalDistance = 0;
		int totalDuration = 0;
		List<Point> totalPathPointList = new ArrayList<>();

		for (int i = 1; i < nodeList.size(); i++) {
			Node prev = nodeList.get(i - 1);
			Node next = nodeList.get(i);
			NodeCost nodeCost = getNodeCost(prev, next);

			if (nodeCost == null) {
				continue;
			}

			totalDistance += nodeCost.getDistanceMeter();
			totalDuration += nodeCost.getDurationSecond();
			totalPathPointList
					.addAll(new ObjectMapper().readValue(nodeCost.getPathJson(), new TypeReference<List<Point>>() {
					}));
		}
		JsonResult jsonResult = new JsonResult();
		jsonResult.addData("totalDistance", totalDistance);// 전체이동거리
		jsonResult.addData("totalDuration", totalDuration);// 전체이동시간
		jsonResult.addData("totalPathPointList", totalPathPointList);// 전체이동경로
		jsonResult.addData("nodeList", nodeList);// 방문지목록
		return jsonResult;
	}
	private NodeCost getNodeCost(Node prev, Node next) throws IOException, InterruptedException {
		NodeCostParam nodeCostParam = new NodeCostParam();
		nodeCostParam.setStartNodeId(prev.getId());
		nodeCostParam.setEndNodeId(next.getId());
		NodeCost nodeCost = kakaoApiService.findNodeCost(nodeCostParam.getStartNodeId(), nodeCostParam.getEndNodeId());

		if (nodeCost == null) {
			KakaoDirections kakaoDirections = KakaoApiUtil.getKakaoDirections(new Point(prev.getX(), prev.getY()),
					new Point(next.getX(), next.getY()));
			List<Route> routes = kakaoDirections.getRoutes();
			Route route = routes.get(0);
			List<Point> pathPointList = new ArrayList<Point>();
			List<Section> sections = route.getSections();

			if (sections == null) {
				// {"trans_id":"018e3d7f7526771d9332cb717909be8f","routes":[{"result_code":104,"result_msg":"출발지와
				// 도착지가 5 m 이내로 설정된 경우 경로를 탐색할 수 없음"}]}
				pathPointList.add(new Point(prev.getX(), prev.getY()));
				pathPointList.add(new Point(next.getX(), next.getY()));
				nodeCost = new NodeCost();
				nodeCost.setStartNodeId(prev.getId());// 시작노드id
				nodeCost.setEndNodeId(next.getId());// 종료노드id
				nodeCost.setDistanceMeter(0l);// 이동거리(미터)
				nodeCost.setDurationSecond(0l);// 이동시간(초)
				nodeCost.setTollFare(0);// 통행 요금(톨게이트)
				nodeCost.setTaxiFare(0);// 택시 요금(지자체별, 심야, 시경계, 복합, 콜비 감안)
				nodeCost.setPathJson(new ObjectMapper().writeValueAsString(pathPointList));// 이동경로json [[x,y],[x,y]]
				nodeCost.setRegDt(new Date());// 등록일시
				nodeCost.setModDt(new Date());// 수정일시
				kakaoApiService.registNodeCost(nodeCost);
				return null;
			}
			List<Road> roads = sections.get(0).getRoads();
			for (Road road : roads) {
				List<Double> vertexes = road.getVertexes();
				for (int q = 0; q < vertexes.size(); q++) {
					pathPointList.add(new Point(vertexes.get(q), vertexes.get(++q)));
				}
			}
			Summary summary = route.getSummary();
			Integer distance = summary.getDistance();
			Integer duration = summary.getDuration();
			Fare fare = summary.getFare();
			Integer taxi = fare.getTaxi();
			Integer toll = fare.getToll();

			nodeCost = new NodeCost();
			nodeCost.setStartNodeId(prev.getId());// 시작노드id
			nodeCost.setEndNodeId(next.getId());// 종료노드id
			nodeCost.setDistanceMeter(distance.longValue());// 이동거리(미터)
			nodeCost.setDurationSecond(duration.longValue());// 이동시간(초)
			nodeCost.setTollFare(toll);// 통행 요금(톨게이트)
			nodeCost.setTaxiFare(taxi);// 택시 요금(지자체별, 심야, 시경계, 복합, 콜비 감안)
			nodeCost.setPathJson(new ObjectMapper().writeValueAsString(pathPointList));// 이동경로json [[x,y],[x,y]]
			nodeCost.setRegDt(new Date());// 등록일시
			nodeCost.setModDt(new Date());// 수정일시
			kakaoApiService.registNodeCost(nodeCost);
		}
		return nodeCost;
	}
	public JsonResult postVrp(List<Node> nodeList) throws Exception {
		VrpService vrpService = new VrpService();
		Node firstNode = nodeList.get(0);
		Node lastNode = nodeList.get(nodeList.size() - 1);
		String firstNodeId = String.valueOf(firstNode.getId());
		String lastNodeId = String.valueOf(lastNode.getId());
		// 차량 등록
		vrpService.addVehicle("차량01", firstNodeId, lastNodeId);

		Map<String, Node> nodeMap = new HashMap<>();
		Map<String, Map<String, NodeCost>> nodeCostMap = new HashMap<>();

		for (Node node : nodeList) {
			String nodeId = String.valueOf(node.getId());
			// 화물 등록
			vrpService.addShipement(node.getName(), firstNodeId, nodeId);
			nodeMap.put(nodeId, node);
		}

		for (int i = 0; i < nodeList.size(); i++) {
			Node startNode = nodeList.get(i);
			for (int j = 0; j < nodeList.size(); j++) {
				Node endNode = nodeList.get(j);
				NodeCost nodeCost = getNodeCost(startNode, endNode);
				if (i == j) {
					continue;
				}
				if (nodeCost == null) {
					nodeCost = new NodeCost();
					nodeCost.setDistanceMeter(0l);
					nodeCost.setDurationSecond(0l);
				}
				Long distanceMeter = nodeCost.getDistanceMeter();
				Long durationSecond = nodeCost.getDurationSecond();
				String startNodeId = String.valueOf(startNode.getId());
				String endNodeId = String.valueOf(endNode.getId());

				// 비용 등록
				vrpService.addCost(startNodeId, endNodeId, durationSecond, distanceMeter);
				if (!nodeCostMap.containsKey(startNodeId)) {
					nodeCostMap.put(startNodeId, new HashMap<>());
				}
				nodeCostMap.get(startNodeId).put(endNodeId, nodeCost);
			}
		}

		List<Node> vrpNodeList = new ArrayList<>();

		VrpResult vrpResult = vrpService.getVrpResult();

		String prevLocationId = null;
		for (VrpVehicleRoute vrpVehicleRoute : vrpResult.getVrpVehicleRouteList()) {
			// 시작 약국에서 픽업 몇개하고 배송 후 다시 픽업해도 되는 코드
			String locationId = vrpVehicleRoute.getLocationId();
			if (prevLocationId == null) {
				prevLocationId = locationId;
			} else if (locationId.equals(prevLocationId)) {
				continue;
			}

			prevLocationId = locationId;
			vrpNodeList.add(nodeMap.get(locationId));

		}

		int totalDistance = 0;
		int totalDuration = 0;
		List<Point> totalPathPointList = new ArrayList<>();
		for (int i = 1; i < vrpNodeList.size(); i++) {
			Node prev = vrpNodeList.get(i - 1);
			Node next = vrpNodeList.get(i);

			NodeCost nodeCost = nodeCostMap.get(String.valueOf(prev.getId())).get(String.valueOf(next.getId()));
			if (nodeCost == null) {
				continue;
			}

			totalDistance += nodeCost.getDistanceMeter();
			totalDuration += nodeCost.getDurationSecond();
			String pathJson = nodeCost.getPathJson();
			if (pathJson != null) {
				totalPathPointList.addAll(new ObjectMapper().readValue(pathJson, new TypeReference<List<Point>>() {
				}));
			}
		}

		JsonResult jsonResult = new JsonResult();
		jsonResult.addData("totalDistance", totalDistance);// 전체이동거리
		jsonResult.addData("totalDuration", totalDuration);// 전체이동시간
		jsonResult.addData("totalPathPointList", totalPathPointList);// 전체이동경로
		jsonResult.addData("nodeList", vrpNodeList);// 방문지목록
		return jsonResult;
	}
	
}