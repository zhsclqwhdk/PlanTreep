package kr.co._icia.finalProject.service;



import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import kr.co._icia.finalProject.entity.Members;
import kr.co._icia.finalProject.entity.Weather;

@Service
public class ApiService {
	public ArrayList<Weather> wheater( List<String> dates ) throws Exception {
		StringBuilder urlBuilder = new StringBuilder(
				"http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst"); /* URL */
		urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8")
				+ "=yQXw%2FNnETe%2FHMkDWArHfqhj1R%2F9MdR2C6Bb%2FV92U6EWUBqFjalHDcz6ykyHbIOIPjJ8T659Q56GR53plbJ0YTA%3D%3D"); // 서비스키
		urlBuilder
				.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /* 페이지번호 */
		urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "="
				+ URLEncoder.encode("100", "UTF-8")); /* 한 페이지 결과 수 */
		urlBuilder.append("&" + URLEncoder.encode("dataType", "UTF-8") + "="
				+ URLEncoder.encode("JSON", "UTF-8")); /* 요청자료형식(XML/JSON) */
		urlBuilder.append("&" + URLEncoder.encode("base_date", "UTF-8") + "="
				+ URLEncoder.encode("UTF-8")); /* ‘24년 9월 3일 발표 */
		urlBuilder.append("&" + URLEncoder.encode("base_time", "UTF-8") + "="
				+ URLEncoder.encode("0500", "UTF-8")); /* 06시 발표(정시단위) */
		urlBuilder.append(
				"&" + URLEncoder.encode("nx", "UTF-8") + "=" + URLEncoder.encode("37", "UTF-8")); /* 예보지점의 X 좌표값 */
		urlBuilder.append(
				"&" + URLEncoder.encode("ny", "UTF-8") + "=" + URLEncoder.encode("126", "UTF-8")); /* 예보지점의 Y 좌표값 */
		URL url = new URL(urlBuilder.toString());
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Content-type", "application/json");
		System.out.println("Response code: " + conn.getResponseCode());
		BufferedReader rd;
		if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		} else {
			rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
		}
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = rd.readLine()) != null) {
			sb.append(line);
		}
		rd.close();
		conn.disconnect();
// System.out.println(sb.toString()); // 정류소 목록 JSON {key : value}
// 응답 데이터를 JSON 으로 변환
		JsonObject response_Json = JsonParser.parseString(sb.toString()).getAsJsonObject();
// 정류소 정보가 담긴 item
		JsonObject items_value = response_Json.get("response").getAsJsonObject().get("body").getAsJsonObject()
				.get("items").getAsJsonObject(); // 순서대로 써야 제대로나온다.

		System.out.println(items_value);
		JsonArray itemArr = items_value.get("item").getAsJsonArray();
		ArrayList<Weather> stationList = new ArrayList<>();
		for (JsonElement item : itemArr) {
			Gson gson = new Gson();
// 정류소 정보JSON을 정류소 정보 클래스로 변환
			Weather busStation = gson.fromJson(item, Weather.class);
			System.out.println(busStation);
			stationList.add(busStation);
		}
		return stationList;
	}
	private final String client_secret = "PFoQgq1AxtmT5nbDmSBteYsk57P4TjMy";

	public String getAutoToken(String client_id, String redirect_url, String code) throws Exception {

		String access_token = null;
		try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
			ClassicHttpRequest httpPost = ClassicRequestBuilder.post("https://kauth.kakao.com/oauth/token")
					.setEntity(new UrlEncodedFormEntity(
							Arrays.asList(new BasicNameValuePair("grant_type", "authorization_code"),
									new BasicNameValuePair("client_id", client_id),
									new BasicNameValuePair("redirect_uri", redirect_url),
									new BasicNameValuePair("code", code))))
					.build();

			httpPost.setHeader("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
			String responseBody = httpclient.execute(httpPost, response -> {
				System.out.println(response.getCode() + " " + response.getReasonPhrase());
				final HttpEntity entity2 = response.getEntity();

				String responseData = EntityUtils.toString(entity2, "UTF-8");
				System.out.println(responseData);

				EntityUtils.consume(entity2);
				return responseData;
			});

			JsonObject response_Json = JsonParser.parseString(responseBody).getAsJsonObject();

			access_token = response_Json.get("access_token").getAsString();

			System.out.println(access_token);

		}

		return access_token;
	}

	public Members kakaoUserInfo(String token) throws Exception {
		String userNick = null;

		try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
			ClassicHttpRequest httpGet = ClassicRequestBuilder.get("https://kapi.kakao.com/v2/user/me").build();
			httpGet.setHeader("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
			httpGet.setHeader("Authorization", "Bearer " + token);

			String responseBody = httpclient.execute(httpGet, response -> {
				final HttpEntity entity1 = response.getEntity();
				String responseData = EntityUtils.toString(entity1, "UTF-8");

				EntityUtils.consume(entity1);
				return responseData;
			});
			JsonObject response = JsonParser.parseString(responseBody).getAsJsonObject();
			System.out.println(response);

			String id = response.get("id").getAsString();
			JsonObject properties = response.get("properties").getAsJsonObject();
			String nickname = properties.get("nickname").getAsString();
			String prifile = properties.get("profile_image").getAsString();

			Members minfo = new Members();
			minfo.setMid(id);
			minfo.setMname(nickname);
			minfo.setMprofilename(prifile);

			return minfo;
		}
	}
}