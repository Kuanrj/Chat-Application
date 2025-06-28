package com.learning.ChatApp.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.ChatApp.model.ChatMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatWebSocketHandler extends TextWebSocketHandler {
    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    private final List<ChatMessage> chatHistory = new ArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        for(ChatMessage message : chatHistory){
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        ChatMessage chatMessage = objectMapper.readValue(message.getPayload(), ChatMessage.class);
        chatHistory.add(chatMessage);
        if (chatHistory.size() > 100) chatHistory.remove(0); // Limit history
        for (WebSocketSession s : sessions) {
            try {
                s.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMessage)));
            } catch (IOException e) {
                sessions.remove(s);
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
    }
}
