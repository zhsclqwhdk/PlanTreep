package kr.co._icia.finalProject.util;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co._icia.finalProject.util.kakaoResponse.Document;
import kr.co._icia.finalProject.util.kakaoResponse.KakaoAddress;
import kr.co._icia.finalProject.util.kakaoResponse.KakaoDirections;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Service
public class KakaoApiUtil {
	private static final String REST_API_KEY = "3963d4acc3e66c56c54f1c5090486820";

	// 키워드로 장소 검색하기 api 호출
	public static List<Point> getPointByKeyword(String keyword, Point center) throws Exception {
		HttpClient client = HttpClient.newHttpClient();
		String url = "https://dapi.kakao.com/v2/local/search/keyword.json";
		url += "?query=" + URLEncoder.encode(keyword, "UTF-8");
		url += "&x=" + center.getX();
		url += "&y=" + center.getY();
		HttpRequest request = HttpRequest.newBuilder().header("Authorization", "KakaoAK " + REST_API_KEY)
				.uri(URI.create(url)).GET().build();


		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		String responseBody = response.body();

		KakaoAddress kakaoAddress = new ObjectMapper().readValue(responseBody, KakaoAddress.class);
		List<Document> documents = kakaoAddress.getDocuments();
		if (documents.isEmpty()) {
			return null;
		}
		List<Point> pointList = new ArrayList<>();
		for (Document document : documents) {
			Point point = new Point(document.getX(), document.getY());
			point.setName(document.getPlaceName());
			point.setPhone(document.getPhone());
			point.setId(document.getId());
			point.setAddress(document.getAddressName());
			point.setCategoryGroupName(document.getCategoryGroupName());
			point.setPlaceUrl(document.getPlaceUrl());
			pointList.add(point);

		}
		return pointList;
	}

	// 자동차 길찾기 api 호출
	public static KakaoDirections getKakaoDirections(Point from, Point to) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		String url = "https://apis-navi.kakaomobility.com/v1/directions";
		url += "?origin=" + from.getX() + "," + from.getY();
		url += "&destination=" + to.getX() + "," + to.getY();
		HttpRequest request = HttpRequest.newBuilder()//
				.header("Authorization", "KakaoAK " + REST_API_KEY)//
				.header("Content-Type", "application/json")//
				.uri(URI.create(url))//
				.GET()//
				.build();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		String responseBody = response.body();

		KakaoDirections kakaoDirections = new ObjectMapper().readValue(responseBody, KakaoDirections.class);

		return kakaoDirections;
	}

	// 주소 검색하기 api 호출 >> 좌표값(위도,경도) 반환
	public static List<Point> getPointByAddress(String address) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		String url ="https://dapi.kakao.com/v2/local/search/address";
		url += "?query=" + URLEncoder.encode(address, "UTF-8");
		
		HttpRequest request = HttpRequest.newBuilder().header("Authorization", "KakaoAK " + REST_API_KEY)
				.uri(URI.create(url)).GET().build();


		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		String responseBody = response.body();

		KakaoAddress kakaoAddress = new ObjectMapper().readValue(responseBody, KakaoAddress.class);
		List<Document> documents = kakaoAddress.getDocuments();
		if (documents.isEmpty()) {
			return null;
		}
		List<Point> pointList = new ArrayList<>();
		for (Document document : documents) {
			Point point = new Point(document.getX(), document.getY());
			point.setName(document.getPlaceName());
			point.setPhone(document.getPhone());
			point.setId(document.getId());
			point.setAddress(document.getAddressName());
			point.setCategoryGroupName(document.getCategoryGroupName());
			point.setPlaceUrl(document.getPlaceUrl());
			pointList.add(point);

		}
		System.out.println(pointList);
		return pointList;
	}

	@Getter
	@Setter
	@ToString
	public static class Point {
		private Long id;
		private Double x;
		private Double y;
		private String Name;
		private String phone;
		private String address;
		private String categoryGroupName;
		private String placeUrl;

		public Point() {

		}

		public Point(Double x, Double y) {
			this.x = x;
			this.y = y;
		}

	}
}
