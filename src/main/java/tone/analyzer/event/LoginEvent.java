package tone.analyzer.event;

import java.util.Date;

/**
 * Created by mozammal on 4/12/17.
 */
public class LoginEvent {

    private String userName;

    private Date time;

    private boolean active;

    public LoginEvent(String username) {
        this.userName = username;
        this.time = new Date();
        this.active = true;
    }

    public LoginEvent(String username, boolean active) {
        this.userName = username;
        this.time = new Date();
        this.active = active;
    }

    @Override
    public int hashCode() {
        int result = userName.hashCode();
        result = 31 * result + (time != null ? time.hashCode() : 0);
        result = 31 * result + (active ? 1 : 0);
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
