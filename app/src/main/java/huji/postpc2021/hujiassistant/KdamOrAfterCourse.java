package huji.postpc2021.hujiassistant;

public class KdamOrAfterCourse {
    private String number = "";
    private String name = "";
    private String points = "";
    private String semester = "";
    private String group = "";

    public KdamOrAfterCourse() {

    }

    public KdamOrAfterCourse(String number_, String name_, String points_, String semester_, String group_) {
        this.number = number_;
        this.name = name_;
        this.points = points_;
        this.semester = semester_;
        this.group = semester_;
    }

    public String getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public String getPoints() {
        return points;
    }

    public String getSemester() {
        return semester;
    }

    public String getGroup() {
        return group;
    }
}
