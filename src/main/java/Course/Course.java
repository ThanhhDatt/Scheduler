package Course;

import java.util.ArrayList;

/**
 * Init Course entity to get all info from course
 * And store course properties from sheets to course object
 * Course scope are listed object because one course has many scopes
 **/
public class Course {
    private Integer id;
    private String name;
    private String courseCode;
    private ArrayList<Scope> scope;

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

    public ArrayList<Scope> getScope() {
        return scope;
    }

    public void setScope(ArrayList<Scope> scope) {
        this.scope = scope;
    }

    public Course(Integer id, String name, String courseCode, ArrayList<Scope> scope) {
        this.id = id;
        this.name = name;
        this.courseCode = courseCode;
        this.scope = scope;
    }

    public Course() {
    }


}
