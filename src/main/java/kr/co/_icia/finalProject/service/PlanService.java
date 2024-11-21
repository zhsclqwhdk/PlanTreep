package kr.co._icia.finalProject.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;

import jakarta.servlet.http.HttpSession;
import kr.co._icia.finalProject.controller.PlanSave;
import kr.co._icia.finalProject.entity.Members;
import kr.co._icia.finalProject.entity.Node;
import kr.co._icia.finalProject.entity.PlanDayDetail;
import kr.co._icia.finalProject.entity.PlanNodeBridge;
import kr.co._icia.finalProject.entity.Plans;
import kr.co._icia.finalProject.repository.PlanDayDetailRepository;
import kr.co._icia.finalProject.repository.PlanNodeBridgeRepository;
import kr.co._icia.finalProject.repository.PlanRepository;

@Service
public class PlanService {

	@Autowired
	private PlanRepository pRepository;

	@Autowired
	private PlanDayDetailRepository pddRepository;

	@Autowired
	private PlanNodeBridgeRepository bridgeRepository;

	@Autowired
	private HttpSession httpSession;

	@Transactional
	public void registPlanDetail(PlanSave planSave) {
		// 1.detail save
		List<Node> planList = planSave.getPlanList();
		List<Node> pathList = planSave.getPathPoints();

		List<Map<String, String>> pointsList = new ArrayList<>();

		for (Node node : pathList) {
			Map<String, String> points = new HashMap<>();
			points.put("x", node.getX() + "");
			points.put("y", node.getY() + "");
			pointsList.add(points);
		}
		Long obj = (Long) httpSession.getAttribute("newPlanId");
		// Plans planId = findById(obj);
		String pathJson = new Gson().toJson(pointsList);
		Plans plan = pRepository.findById(obj).orElse(null);
		List<PlanDayDetail> planDetailList = plan.getPlanDayDetailList();

		PlanDayDetail planDayDetail = new PlanDayDetail();
		for (PlanDayDetail planDetail : planDetailList) {
			if (planDetail.getDetailDate().equals(planSave.getDetailDate())) {
				planDayDetail = planDetail;
			}
		}

		planDayDetail.setPathjson(pathJson);
		planDayDetail.setDetailDate(planSave.getDetailDate());
		planDayDetail.setPlan(plan);
		planDayDetail = pddRepository.save(planDayDetail);

		bridgeRepository.deleteByPlanDayDetailId_id(planDayDetail.getId());
		for (Node node : planList) {
			PlanNodeBridge pnb = new PlanNodeBridge();
			pnb.setNodeId(node);
			pnb.setPlanDayDetailId(planDayDetail);
			bridgeRepository.save(pnb);
		}

	}

	public List<PlanDayDetail> findDetailsByPlanId(Long planId) {
		List<PlanDayDetail> detailsByPlanId = pddRepository.findByPlanIdOrderByDetailDate(planId);

		return detailsByPlanId;
	}

	public List<PlanDayDetail> findDetails() {
		List<PlanDayDetail> planDetails = pddRepository.findAll();
		return planDetails;
	}

	public Plans registPlan(String[] AlldateList, String title, Members loginMember) {
		int lastIndexVal = (AlldateList.length - 1);
		String stratIndex = AlldateList[0];
		String lastIndex = AlldateList[lastIndexVal];

		Object newPlanId = httpSession.getAttribute("newPlanId");

		Plans plans = new Plans(); // Plan Entity 생성
		plans.setPname(title);
		plans.setPstartdate(stratIndex);
		plans.setPenddate(lastIndex);
		plans.setMember(loginMember);
		if (newPlanId == null) {
			plans = pRepository.save(plans);
		} else {
			plans = pRepository.findById((Long) newPlanId).orElse(null);
			if (!plans.getId().equals((Long) newPlanId)) {
				plans = pRepository.save(plans);
			}
		}

		return plans;
	}

	public List<Plans> findPlans() {
		List<Plans> pList = pRepository.findAll();
		return pList;
	}

	public void planCkeck(Plans plans) {
		pRepository.save(plans);/// id
		System.out.println("플랜 ID: " + plans.getId());
		httpSession.setAttribute("newPlanId", plans.getId());
	}

	public Plans findById(Long newPlanId) {
		return pRepository.findById(newPlanId).orElse(null);
	}

	public List<Plans> findPlanList(Long loginId) {
		return pRepository.findByMemberId(loginId);
	}

	public List<Plans> findByAllPlan(Long[] planId) {
		List<Plans> planList = new ArrayList<>();
		for (int i = 0; i < planId.length; i++) {
			Long planid = planId[i];
			Plans plans = pRepository.findById(planid).orElse(null);
			planList.add(plans);
		}
		return planList;
	}

	public List<Node> shareNode(Long planid) {
		Plans plan = pRepository.findById(planid).orElse(null);
		List<PlanDayDetail> sharePlanDayDetail = plan.getPlanDayDetailList();
		List<Node> nodeList = new ArrayList<>();
		for (PlanDayDetail planDayDetail : sharePlanDayDetail) {
			List<PlanNodeBridge> sharePlanNodeBridge = planDayDetail.getNodeId();
			for (PlanNodeBridge lanNodeBridge : sharePlanNodeBridge) {
				Node node = lanNodeBridge.getNodeId();
				nodeList.add(node);
			}
		}
		return nodeList;
	}

//	public List<PlanNodeBridge> findNodeByDetailId(Long id) {
//		List<PlanNodeBridge> nodeList = bridgeRepository.findByDetailIdOrderById(id);
//		return null;
//	}

}