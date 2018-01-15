package tone.analyzer.domain.model;

/**
 * Created by Dell on 1/15/2018.
 */
public class NewUserInvitationNotification {

    private String sender;

    private String receiver;

    private String token;

    private String url;

    public NewUserInvitationNotification() {
    }

    public NewUserInvitationNotification(String sender, String receiver, String token, String url) {
        this.sender = sender;
        this.receiver = receiver;
        this.token = token;
        this.url = url;
    }

    public String getSender() {
        return sender;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "NewUserInvitationNotification{" +
                "sender='" + sender + '\'' +
                '}';
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
