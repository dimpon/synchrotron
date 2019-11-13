package org.synchrotron.autoconfigure;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class SocketHandler  extends TextWebSocketHandler {

	private static final Logger logger = LoggerFactory.getLogger(SocketHandler.class);

	List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) throws InterruptedException, IOException {
		session.sendMessage(new TextMessage("Hello " + message.getPayload() + " !"));

		doItnTimes();
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		InetSocketAddress clientAddress = session.getRemoteAddress();

		sessions.add(session);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		logger.info("Connection closed by {}:{}", session.getRemoteAddress().getHostString(), session.getRemoteAddress().getPort());
		super.afterConnectionClosed(session, status);
	}

	private void doItnTimes() {

		CompletableFuture.supplyAsync(() -> {

			for (int i = 0; i < 5; i++) {
				for (WebSocketSession webSocketSession : sessions) {

					logger.info("yooo!");

					try {

						TimeUnit.SECONDS.sleep(5);

						webSocketSession.sendMessage(new TextMessage("Hello " + System.currentTimeMillis() + " !"));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}
			return null;

		});

	}

}
