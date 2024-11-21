package kr.co._icia.finalProject.socket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocket
public class PlanWebSocketConfig implements WebSocketConfigurer {

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry.addHandler(shareToMemberHandler(), "/sharePlan").addInterceptors(new HttpSessionHandshakeInterceptor());
  }

  @Bean
  public PlanWebSocketHandler planWebSocketHandler() {
    return new PlanWebSocketHandler();
  }

  @Bean
  public ShareToMemberHandler shareToMemberHandler() {
    return new ShareToMemberHandler();
  }

}
