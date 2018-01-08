package tone.analyzer.event;

import java.util.Date;

/**
 * Created by mozammal on 4/12/17.
 */
public class LoginEvent {

    private String userName;

    private Date time;

    private boolean online;


    private String id;

    public LoginEvent(String username) {
        this.userName = username;
        this.time = new Date();
        this.online = true;
    }

    public LoginEvent(String username, boolean active) {
        this.userName = username;
        this.time = new Date();
        this.online = active;
    }

    @Override
    public int hashCode() {
        int result = userName.hashCode();
        result = 31 * result + (time != null ? time.hashCode() : 0);
        result = 31 * result + (online ? 1 : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LoginEvent))
            return false;
        LoginEvent other = (LoginEvent) o;
        return other.getUserName().equals(this.userName);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
