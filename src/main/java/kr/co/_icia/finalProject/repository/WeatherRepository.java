package kr.co._icia.finalProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co._icia.finalProject.entity.Weather;

public interface WeatherRepository extends JpaRepository<Weather, Long>{
	
}
