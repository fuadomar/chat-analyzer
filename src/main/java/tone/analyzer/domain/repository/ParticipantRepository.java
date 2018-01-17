package tone.analyzer.domain.repository;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import tone.analyzer.event.LoginEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Created by mozammal on 4/12/17. */
@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ParticipantRepository {

  private Map<String, LoginEvent> activeSessions;

  public ParticipantRepository() {
    this.activeSessions = new ConcurrentHashMap<>();
  }

  public void add(String sessionId, LoginEvent event) {
    activeSessions.putIfAbsent(sessionId, event);
  }

  public LoginEvent getParticipant(String sessionId) {
    return activeSessions.get(sessionId);
  }

  public void removeParticipant(String sessionId) {
    activeSessions.remove(sessionId);
  }

  public Map<String, LoginEvent> getActiveSessions() {
    return activeSessions;
  }

  public void setActiveSessions(Map<String, LoginEvent> activeSessions) {
    this.activeSessions = activeSessions;
  }
}
