package kr.co._icia.finalProject.socket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.google.gson.Gson;

import kr.co._icia.finalProject.entity.ShareContent;
import kr.co._icia.finalProject.repository.ShareContentRepository;

public class ShareToMemberHandler extends TextWebSocketHandler {

  List<WebSocketSession> clientList = new ArrayList<>();
  
  @Autowired
  private ShareContentRepository shareContentRepository;

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    System.out.println("클라이언트 접속");
    clientList.add(session);
  }

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    System.out.println("클라이언트 접속해제");
    clientList.remove(session);
  }

  public void sendPlanToMember(String sendMid, String[] targetMid, Long[] planId) {
    Gson gson = new Gson();
    String planId_Json = gson.toJson(planId);

    Map<String, String> msgObj = new HashMap<>();
    msgObj.put("planIdList", planId_Json);
    msgObj.put("sendMid", sendMid);

    // '회원'님이 보내셨습니다.
    for (String receiveMid : targetMid) {
      for (WebSocketSession client : clientList) {
        List<ShareContent> sclist = shareContentRepository.findByMembersMid(receiveMid);

        msgObj.put("unchecksize", sclist.size() + "");
        String clientId = (String) client.getAttributes().get("loginMid");
        System.out.println("sendMid : " + sendMid);
        System.out.println("receiveMid : " + receiveMid);
        System.out.println("clientId : " + clientId);
        if (receiveMid.equals(clientId)) {
          try {
            System.out.println();
            client.sendMessage(new TextMessage(gson.toJson(msgObj)));
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }

    }

  }

}
