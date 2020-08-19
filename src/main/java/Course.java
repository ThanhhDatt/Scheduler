public class Course {
    private Integer id;
    private String name;
    private String courseCode;
    private Scope scope;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public Course(Integer id, String name, String courseCode, Scope scope) {
        this.id = id;
        this.name = name;
        this.courseCode = courseCode;
        this.scope = scope;
    }

    public Course() {
    }
}
