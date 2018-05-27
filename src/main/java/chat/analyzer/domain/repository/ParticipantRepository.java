package chat.analyzer.domain.repository;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import chat.analyzer.domain.DTO.UserOnlinePresenceDTO;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Created by mozammal on 4/12/17. */
@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ParticipantRepository {

  private Map<String, UserOnlinePresenceDTO> activeSessions;

  public ParticipantRepository() {
    this.activeSessions = new ConcurrentHashMap<>();
  }

  public void add(String sessionId, UserOnlinePresenceDTO event) {
    activeSessions.putIfAbsent(sessionId, event);
  }

  public UserOnlinePresenceDTO getParticipant(String sessionId) {
    return activeSessions.get(sessionId);
  }

  public void removeParticipant(String sessionId) {
    activeSessions.remove(sessionId);
  }

  public Map<String, UserOnlinePresenceDTO> getActiveSessions() {
    return activeSessions;
  }

  public void setActiveSessions(Map<String, UserOnlinePresenceDTO> activeSessions) {
    this.activeSessions = activeSessions;
  }
}
