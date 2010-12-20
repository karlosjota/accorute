package su.msu.cs.lvk.accorute.http.model;

import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import org.apache.commons.lang.NotImplementedException;

import java.io.Serializable;
import java.util.Date;

public class Conversation implements Serializable {
    private EntityID cid = EntityID.NOT_INITIALIZED;
    private EntityID ctxID = EntityID.NOT_INITIALIZED;
    private Date time;
    private Request request;
    private Response response;
    //indicator of the designation of the conversation (i.e. login, navigatin, attack, configuration test, etc.)
    private String designation = null;
    //indicator of the entity that generated and issued this conversation
    private String originator = null;
    //arbitary message indicating conversation status (i.e. processed, pending, waiting user interaction, etc.)
    private String status = null;
    //indicates a user who submitted this request
    private String user = null;
    //indicates whether response represent an error code or an application error
    private boolean successful = true;
    //any additional data
    private transient Object additionalData = null;

    public Object getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(Object additionalData) {
        this.additionalData = additionalData;
    }

    public String getUser() {
        return this.user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public EntityID getId() {
        return cid;
    }

    public void setId(EntityID cid) {
        this.cid = cid;
    }

    public String getOriginator() {
        return originator;
    }

    public void setOriginator(String originator) {
        this.originator = originator;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Request getRequest() {
        return request;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public Conversation() {
        time = new Date();
    }

    public Conversation(Request request) {
        this.request = request;
        response = null;
        time = new Date();
    }
    public Conversation(WebRequest request, WebResponse response) {
        throw new NotImplementedException();
    }
    public Conversation(Request request, Response response) {
        this.request = request;
        this.response = response;
        time = new Date();
    }

    public Conversation(Request request, Response response, Date time) {
        this.request = request;
        this.response = response;
        this.time = time;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public String toString() {
        String result = "";

        result += "Conversation ID is " + this.getId().toString() + System.getProperty("line.separator");
        result += "Conversation is " + (this.isSuccessful() ? "successful" : "unsuccessful") + System.getProperty("line.separator");
        if (designation != null)
            result += "Conversation designation is " + this.getDesignation() + System.getProperty("line.separator");
        if (originator != null)
            result += "Conversation source is " + this.getOriginator() + System.getProperty("line.separator");
        if (user != null)
            result += "Conversation is submitted by user " + this.getUser() + System.getProperty("line.separator");
        if (status != null)
            result += "Conversation status is " + this.getStatus() + System.getProperty("line.separator");
        if (originator != null)
            result += "Conversation source is " + this.getOriginator() + System.getProperty("line.separator");
        result += "Created at " + this.getTime() + System.getProperty("line.separator");
        if (request != null) {
            result += "------------------ Request -------------------------" + System.getProperty("line.separator") + request.toString();
        }

        if (response != null) {
            result += "-------------------Response ------------------------" + System.getProperty("line.separator") + response.toString() + System.getProperty("line.separator");
        }

        return result;
    }
}
