package tone.analyzer.domain.entity;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by user on 1/8/2018.
 */
@Document
public class EmailInvitation implements Serializable {

    @Id
    private String id;

    private String sender;

    private String receiver;

    private String token;

    public EmailInvitation() {
    }

    public EmailInvitation(String sender, String receiver, String token) {
        this.sender = sender;
        this.receiver = receiver;
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
