package huji.postpc2021.hujiassistant;

public class CourseScheduleEntry {
    private String type;
    private String groupNumber;
    private String day;
    private String starting;
    private String ending;
    private String location;
    private String group;
    private String teacher;

    public CourseScheduleEntry(String type, String groupNumber, String day, String starting, String ending, String location, String group, String teacher) {
        this.type = type;
        this.groupNumber = groupNumber;
        this.day = day;
        this.starting = starting;
        this.ending = ending;
        this.location = location;
        this.group = group;
        this.teacher = teacher;
    }

    public CourseScheduleEntry() {
    }


    public String getType() {
        return type;
    }

    public String getGroupNumber() {
        return groupNumber;
    }

    public String getDay() {
        return day;
    }

    public String getStarting() {
        return starting;
    }

    public String getEnding() {
        return ending;
    }

    public String getLocation() {
        return location;
    }

    public String getGroup() {
        return group;
    }

    public String getTeacher() {
        return teacher;
    }

    @Override
    public String toString() {
        return "CourseScheduleEntry{" +
                "type='" + type + '\'' +
                ", groupNumber='" + groupNumber + '\'' +
                ", day='" + day + '\'' +
                ", starting='" + starting + '\'' +
                ", ending='" + ending + '\'' +
                ", location='" + location + '\'' +
                ", group='" + group + '\'' +
                ", teacher='" + teacher + '\'' +
                '}';
    }
}
