package loginfail_detect.beans;

public class LoginEvent {
    private Long userId;
    private String ip;
    private String loginStatus;
    private Long timestamp;

    public LoginEvent() {
    }

    public LoginEvent(Long userId, String ip, String loginStatus, Long timestamp) {
        this.userId = userId;
        this.ip = ip;
        this.loginStatus = loginStatus;
        this.timestamp = timestamp;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(String loginStatus) {
        this.loginStatus = loginStatus;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "LoginEvent{" +
                "userId=" + userId +
                ", ip='" + ip + '\'' +
                ", loginStatus='" + loginStatus + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
