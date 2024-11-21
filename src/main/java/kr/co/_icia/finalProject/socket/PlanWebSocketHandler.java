package kr.co._icia.finalProject.socket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class PlanWebSocketHandler extends TextWebSocketHandler {
  
  // 접속한 유저리스트
  List<WebSocketSession> clientList = new ArrayList<>();

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    System.out.println("클라이언트 접속");
    clientList.add(session);
  }

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    String loginMid = (String) session.getAttributes().get("loginMid"); // 일정을 전송한 사람
    String msg = message.getPayload();  // 클라이언트에서 보낸 메세지
    System.out.println("클라이언트에서 보낸 메세지 : " + msg);
    
    JsonObject clientPlan = JsonParser.parseString(msg).getAsJsonObject();
    String receiveMid = clientPlan.get("sendTarget").getAsString();
    JsonArray receivePlan = clientPlan.get("planLists").getAsJsonArray();
    System.out.println("전송한 일정 : " + receivePlan);
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    System.out.println("클라이언트 접속해제");
    clientList.remove(session);
  }

  public void sharePlans(Long planId, Long memId) {
    for(WebSocketSession client : clientList) {
      if(!client.getAttributes().get("loginId").equals(memId)) {
        try {
          client.sendMessage(new TextMessage(planId.toString()));
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

}
