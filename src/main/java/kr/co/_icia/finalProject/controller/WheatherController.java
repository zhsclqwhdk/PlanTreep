package kr.co._icia.finalProject.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import kr.co._icia.finalProject.entity.Weather;
import kr.co._icia.finalProject.util.WeatherApiUtil;

@Controller
public class WheatherController {
	
	@GetMapping("/calendar")
	public String calendar(Model model) {
		String responseBody = null;
		try {
			responseBody = WeatherApiUtil.getWeatherApi("37.5642135", "127.0016985");
			JsonObject res = JsonParser.parseString(responseBody).getAsJsonObject();

			JsonArray resArr = res.get("list").getAsJsonArray();
			List<Weather> wheatherList = new ArrayList<>();
			for (JsonElement resElement : resArr) {
				Weather wheather = new Weather();
				JsonObject mainObj = resElement.getAsJsonObject().get("main").getAsJsonObject();
				JsonObject weatherObj = resElement.getAsJsonObject().get("weather").getAsJsonArray().get(0)
						.getAsJsonObject();
				JsonObject cloudsObj = resElement.getAsJsonObject().get("clouds").getAsJsonObject();
				JsonObject windObj = resElement.getAsJsonObject().get("wind").getAsJsonObject();
				JsonObject sysObj = resElement.getAsJsonObject().get("sys").getAsJsonObject();

				int temp = (int) Math.round(mainObj.get("temp").getAsDouble() - 273.15);
				wheather.setTemp(temp + "");

				int temp_C = (int) Math.round(mainObj.get("feels_like").getAsDouble() - 273.15);
				wheather.setFeels_like(temp_C + "");

				int temp_min = (int) Math.round(mainObj.get("temp_min").getAsDouble() - 273.15);
				wheather.setTemp_min(temp_min + "");

				int temp_max = (int) Math.round(mainObj.get("temp_max").getAsDouble() - 273.15);
				wheather.setTemp_max(temp_max + "");

				wheather.setId(weatherObj.get("id").getAsLong());
				wheather.setMain(weatherObj.get("main").getAsString());
				wheather.setDescription(weatherObj.get("description").getAsString());
				wheather.setIcon(weatherObj.get("icon").getAsString());
				wheather.setAll(cloudsObj.get("all").getAsString());
				wheather.setSpeed(windObj.get("speed").getAsString());
				wheather.setDeg(windObj.get("deg").getAsString());
				wheather.setGust(windObj.get("gust").getAsString());
				wheather.setVisibility(resElement.getAsJsonObject().get("visibility").getAsString());
				wheather.setPop(resElement.getAsJsonObject().get("pop").getAsString());
				wheather.setPod(sysObj.get("pod").getAsString());
				wheather.setDt_txt(resElement.getAsJsonObject().get("dt_txt").getAsString());
				wheatherList.add(wheather);
			}
			model.addAttribute("wheatherList", wheatherList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "calendar";
	}
	
	@GetMapping("WheatherMethod")
	@ResponseBody
	public List<Weather> getWeather(@RequestParam("lat") String lat, @RequestParam("lon") String lon)
			throws IOException, InterruptedException {

		String responseBody = WeatherApiUtil.getWeatherApi(lat, lon);

		JsonObject res = JsonParser.parseString(responseBody).getAsJsonObject();

		JsonArray resArr = res.get("list").getAsJsonArray();
		List<Weather> wheatherList = new ArrayList<>();
		for (JsonElement resElement : resArr) {
			Weather wheather = new Weather();
			JsonObject mainObj = resElement.getAsJsonObject().get("main").getAsJsonObject();
			JsonObject weatherObj = resElement.getAsJsonObject().get("weather").getAsJsonArray().get(0)
					.getAsJsonObject();
			JsonObject cloudsObj = resElement.getAsJsonObject().get("clouds").getAsJsonObject();
			JsonObject windObj = resElement.getAsJsonObject().get("wind").getAsJsonObject();
			JsonObject sysObj = resElement.getAsJsonObject().get("sys").getAsJsonObject();

			int temp = (int) Math.round(mainObj.get("temp").getAsDouble() - 273.15);
			wheather.setTemp(temp + "");

			int temp_C = (int) Math.round(mainObj.get("feels_like").getAsDouble() - 273.15);
			wheather.setFeels_like(temp_C + "");

			int temp_min = (int) Math.round(mainObj.get("temp_min").getAsDouble() - 273.15);
			wheather.setTemp_min(temp_min + "");

			int temp_max = (int) Math.round(mainObj.get("temp_max").getAsDouble() - 273.15);
			wheather.setTemp_max(temp_max + "");

			wheather.setId(weatherObj.get("id").getAsLong());
			wheather.setMain(weatherObj.get("main").getAsString());
			wheather.setDescription(weatherObj.get("description").getAsString());
			wheather.setIcon(weatherObj.get("icon").getAsString());
			wheather.setAll(cloudsObj.get("all").getAsString());
			wheather.setSpeed(windObj.get("speed").getAsString());
			wheather.setDeg(windObj.get("deg").getAsString());
			wheather.setGust(windObj.get("gust").getAsString());
			wheather.setVisibility(resElement.getAsJsonObject().get("visibility").getAsString());
			wheather.setPop(resElement.getAsJsonObject().get("pop").getAsString());
			wheather.setPod(sysObj.get("pod").getAsString());
			wheather.setDt_txt(resElement.getAsJsonObject().get("dt_txt").getAsString());
			wheatherList.add(wheather);
		}
		
		
		return wheatherList;
	}
	
}