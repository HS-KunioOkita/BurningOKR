package org.burningokr.service;

import org.burningokr.config.WebsocketAuthenticationChannelInterceptor;
import org.burningokr.model.monitoring.UserId;
import org.burningokr.model.users.User;
import org.burningokr.service.userhandling.UserService;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class WebsocketUserService {

  private final UserService userService;

  public WebsocketUserService(UserService userService) {
    this.userService = userService;
  }

  public User findByAccessor(StompHeaderAccessor stompHeaderAccessor) {
    Map<String, Object> sessionAttributes = stompHeaderAccessor.getSessionAttributes();
    if (!sessionAttributes.containsKey(
            WebsocketAuthenticationChannelInterceptor.USER_SESSION_ATTRIBUTE_KEY)
        || sessionAttributes
                .get(WebsocketAuthenticationChannelInterceptor.USER_SESSION_ATTRIBUTE_KEY)
                .getClass()
            != UserId.class) {
      throw new RuntimeException("StompHeaderAccessor does not contain (valid) userId.");
    }
    UserId userId =
        (UserId)
            sessionAttributes.get(
                WebsocketAuthenticationChannelInterceptor.USER_SESSION_ATTRIBUTE_KEY);
    return userService.findById(userId.getUserId());
  }
}
