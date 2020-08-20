public class Scope {
    private String scope;
    private String time;
    private String weekday;
    private String location;

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getWeekday() {
        return weekday;
    }

    public void setWeekday(String weekday) {
        this.weekday = weekday;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Scope(String scope, String time, String weekday, String location) {
        this.scope = scope;
        this.time = time;
        this.weekday = weekday;
        this.location = location;
    }

    public Scope() {
    }

}
