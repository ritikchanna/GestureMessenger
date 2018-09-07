package leotik.labs.gesturemessenger.POJO;

public class ChatPOJO {
    private String User;
    private String Time;
    private String Message;
    private String Status;
    private String Side;

    public String getUser() {
        return User;
    }

    public void setSender(String sender) {
        User = sender;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getSide() {
        return Side;
    }

    public void setSide(String side) {
        Side = side;
    }
}
