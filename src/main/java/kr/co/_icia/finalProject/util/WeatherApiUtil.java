package kr.co._icia.finalProject.util;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class WeatherApiUtil {

  // 날씨 api 호출
  public static String getWeatherApi(String lat, String lon) throws IOException, InterruptedException {
    
    final String APIKEY = "893cf52ec5d85fdacc921e3d4ad19acb";
    
    HttpClient httpclient = HttpClient.newHttpClient();
    String url = "https://api.openweathermap.org/data/2.5/forecast?lat="+lat+"&lon="+lon+"&lang=kr&appid=" + APIKEY;
    HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

    HttpResponse<String> response = httpclient.send(request, HttpResponse.BodyHandlers.ofString());
    String responseBody = response.body();
    
    return responseBody;
  }
}
