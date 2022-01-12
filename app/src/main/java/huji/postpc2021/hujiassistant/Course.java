package huji.postpc2021.hujiassistant;

import java.util.ArrayList;
import java.util.UUID;

public class Course {

    private String name = "";
    private String number = "";
    private String points = "";
    private String semester = "";
    private String type = "";
    private String year = "";
    public LocalDataBase dataBase = HujiAssistentApplication.getInstance().getDataBase();
    private int grade = -1;
    private String gradeS = " ";
    private boolean isFinished = false; // is the course done

    private ArrayList<Course> prevCourses;
    private boolean isMandatory;
    private String nameOfDegree;
    private boolean isChecked = false;
    private boolean isPlannedChecked = false;
    private boolean isPlanned = false;

    public enum Type {
        Mandatory, MandatoryChoose, Choose, Supplemental, CornerStones
    }

    public String getGradeFromDb() {
        if (dataBase.getGradesOfStudent().containsKey(number)) {
            return dataBase.getGradesOfStudent().get(number);
        }
        return "";
    }

    public Course() {

    }

    public String toStringP() {
        return "name: " + name + " number " + number + " type " + type + " points " + points + " semester " + semester
                + " year: " + year;
    }

    public String toStringToSP() {
        /**
         * Need to save only the dynamic data, aka, grade and different booleans, and one identificator,
         * aka Course number
         */
        return number + "|" + gradeS + "|" + isChecked + "|" + isPlanned + "|" + isPlannedChecked + "|" + isFinished;
    }

    public Course(String name_, String number_, Type type_, String points_, String semester_) {
        name = name_;
        number = number_;
        type = type_.toString();
        this.points = points_;
        this.semester = semester_;
    }

    public boolean isPlanned() {
        return isPlanned;
    }

    public void setPlanned(boolean planned) {
        isPlanned = planned;
    }

    public void setChecked(boolean val) {
        this.isChecked = val;
    }

    public void setPlannedChecked(boolean val) {
        this.isPlannedChecked = val;
    }

    public boolean getChecked() {
        return this.isChecked;
    }

    public boolean getPlannedChecked() {
        return this.isPlannedChecked;
    }

    public boolean getIsFinished() {
        return this.isFinished;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public String getPoints() {
        return points;
    }

    public String getSemester() {
        return semester;
    }

    public String getYear() {
        return year;
    }

    public String getGrade() {
        return gradeS;
    }

    public void setGrade(String grade_) {
        gradeS = grade_;
    }

    public void setType(String type_) {
        type = type_;
    }

    public void setIsFinished(boolean isFinished_) {
        isFinished = isFinished_;
    }

    public String getType() {
        return type;
    }
}