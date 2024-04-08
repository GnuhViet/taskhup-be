package com.taskhub.project.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    private final SimpMessageSendingOperations messageTemplate;

    // @EventListener
    // public void handleWebSocketDisconnectListener(
    //         SessionDisconnectEvent event
    // ) {
    //     var headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
    //     var client = (ChatClient) headerAccessor.getSessionAttributes().get("client");
    //     if (client != null) {
    //         log.info("Client disconnected: {}", client);
    //         client.setType(MessageType.LEAVE);
    //         messageTemplate.convertAndSend("/topic/" + client.getRoomID(), client);
    //     }
    // }

}
