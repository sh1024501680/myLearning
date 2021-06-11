package networkflow_analysis.beans;

public class ApacheLogEvent {
    private String ip;
    private String userId;
    private Long timeStamp;
    private String method;
    private String url;

    public ApacheLogEvent() {
    }

    public ApacheLogEvent(String ip, String userId, Long timeStamp, String method, String url) {
        this.ip = ip;
        this.userId = userId;
        this.timeStamp = timeStamp;
        this.method = method;
        this.url = url;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "networkflow_analysis.beans.ApacheLogEvent{" +
                "ip='" + ip + '\'' +
                ", userId='" + userId + '\'' +
                ", timeStamp=" + timeStamp +
                ", method='" + method + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
