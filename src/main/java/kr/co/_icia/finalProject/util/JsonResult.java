package kr.co._icia.finalProject.util;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
public class JsonResult {
 public enum Code { //header격
   SUCC, FAIL
 }
 private Code code = Code.SUCC;
 private String msg;
 private Map<String, Object> data;
 public void addData(String key, Object value) {    //body격
   if (data == null) {
     data = new HashMap<>();
   }
   data.put(key, value);
 }
}

