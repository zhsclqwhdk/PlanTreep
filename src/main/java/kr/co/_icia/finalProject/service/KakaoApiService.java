package kr.co._icia.finalProject.service;

import java.util.Arrays;

import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import kr.co._icia.finalProject.entity.Members;
import kr.co._icia.finalProject.entity.Node;
import kr.co._icia.finalProject.entity.NodeCost;
import kr.co._icia.finalProject.repository.NodeCostRepository;
import kr.co._icia.finalProject.repository.NodeRepository;
import kr.co._icia.finalProject.util.KakaoApiUtil;
import kr.co._icia.finalProject.util.KakaoApiUtil.Point;

@Service
public class KakaoApiService {

	@Autowired
	private NodeRepository nodeRepository;

	@Autowired
	private NodeCostRepository nodeCostRepository;

	private static final String client_id = "8946e7f3229301c903d5853d92588a50";
	private static final String redirect_uri = "http://121.65.47.74:5710/member/Kakao";
	private static final String response_type = "code";

	public Node registNode(Node node) {
		return nodeRepository.save(node);
	}

	public Node findNode(Long pointId) {
		return nodeRepository.findByid(pointId);
	}

	public NodeCost registNodeCost(NodeCost nodeCost) {
		return nodeCostRepository.save(nodeCost);
	}

	public NodeCost findNodeCost(Long prevId, Long nextId) {
		return nodeCostRepository.findByStartNodeIdAndEndNodeId(prevId, nextId);
	}

	public String getKakaoAccessToken(String code) {
		String access_token = null;
		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
			ClassicHttpRequest httpPost = ClassicRequestBuilder.post("https://kauth.kakao.com/oauth/token")
					.setEntity(new UrlEncodedFormEntity(Arrays.asList(
							new BasicNameValuePair("grant_type", "authorization_code"),
							new BasicNameValuePair("client_id", client_id),
							new BasicNameValuePair("redirect_uri", redirect_uri), new BasicNameValuePair("code", code))))
					.build();
			httpPost.setHeader("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
			String responseBody = httpClient.execute(httpPost, response -> {
				System.out.println(response.getCode() + " " + response.getReasonPhrase());
				final HttpEntity entity2 = response.getEntity();

				String responseData = EntityUtils.toString(entity2, "UTF-8");

				EntityUtils.consume(entity2);
				return responseData;
			});

			JsonObject response_Json = JsonParser.parseString(responseBody).getAsJsonObject();
			access_token = response_Json.get("access_token").getAsString();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return access_token;

	}

	public Members getKakaoUserInfo(String kakaoToken) {
		Members userInfo = null;
		try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
			ClassicHttpRequest httpGet = ClassicRequestBuilder.get("https://kapi.kakao.com/v2/user/me").build();
			httpGet.setHeader("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
			httpGet.setHeader("Authorization", "Bearer " + kakaoToken);

			String responseBody = httpclient.execute(httpGet, response -> {
				System.out.println(response.getCode() + " " + response.getReasonPhrase());
				final HttpEntity entity1 = response.getEntity();

				String responseData = EntityUtils.toString(entity1, "UTF-8");

				EntityUtils.consume(entity1);
				return responseData;
			});

			JsonObject response_Json = JsonParser.parseString(responseBody).getAsJsonObject();
			JsonObject userInfoJson = response_Json.get("properties").getAsJsonObject();// properties: 사용자
																						// 프로퍼티(Property)

			String userId = response_Json.get("id").getAsString();
			String profile_image = "http://121.65.47.74:5710/UI/default_profile.jfif";
			if(userInfoJson.get("profile_image") != null) {
				profile_image = userInfoJson.get("profile_image").getAsString();
			}
			String userNickname = userInfoJson.get("nickname").getAsString();
			userInfo = new Members();
			userInfo.setMid(userId);
			userInfo.setMname(userNickname);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return userInfo;
	}

	public String getKakaoAuthCode() {
		String url = "https://kauth.kakao.com/oauth/authorize";
		String redirect = "redirect:" + url + "?client_id=" + client_id + "&redirect_uri=" + redirect_uri
				+ "&response_type=" + response_type;
		return redirect;
	}

}
