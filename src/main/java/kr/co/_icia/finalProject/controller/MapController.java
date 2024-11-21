package kr.co._icia.finalProject.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.co._icia.finalProject.entity.Node;
import kr.co._icia.finalProject.service.MapService;
import kr.co._icia.finalProject.service.PlanService;
import kr.co._icia.finalProject.util.JsonResult;
import kr.co._icia.finalProject.util.KakaoApiUtil.Point;

@Controller
@RequestMapping("/travelMap")
public class MapController {

	@Autowired
	private PlanService planService;

	@Autowired
	private MapService mapService;

// 키워드로 장소 검색하기 기능 >> 결과 : 마커, 경로 표시
	@GetMapping("/poi")
	@ResponseBody
	public JsonResult getPoi(@RequestParam(required = false, value = "x") double x,
			@RequestParam(required = false, value = "y") double y,
			@RequestParam(required = false, value = "keyword") String keyword) throws Exception {
		Point center = new Point(x, y);
		JsonResult result = mapService.searchPoi(keyword, center);

		return result;
	}

// 저장 기능
	@PostMapping("/save")
	@ResponseBody
	public String savePath(@RequestBody PlanSave planSave) {
		System.out.println("save 호출");
		System.out.println("저장할 날짜 : " + planSave.getDetailDate());

		System.out.println(planSave.getPlanList().size());
		System.out.println(planSave.getPlanList());

		System.out.println(planSave.getPathPoints().size());
		System.out.println(planSave.getPathPoints());

		planService.registPlanDetail(planSave);
		return "111";
	}

// 경로 최적화 기능
	@PostMapping("/vrp")
	@ResponseBody
	public JsonResult postVrp(@RequestBody List<Node> nodeList) throws Exception {
		JsonResult jsonResult = mapService.postVrp(nodeList);
		return jsonResult;
	}

}