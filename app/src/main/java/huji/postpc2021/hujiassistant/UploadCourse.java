package huji.postpc2021.hujiassistant;

//import android.util.Log;

import java.io.Serializable;

public class UploadCourse implements Serializable {
    /**
     * A class that represent a Course in a Maslul in A Chug
     */

    private String number;
    private String name;
    private String points;
    private String semester;
    private String type;
    private String year;
    private String chug;
    private String maslul;
    private String prefix;


    public UploadCourse(String number, String name, String points, String semester, String type, String year, String chug, String maslul, String prefix) { // course constructor
        this.number = number;
        this.name = name;
        this.points = points;
        this.semester = semester;
        this.type = type;
        this.year = year;
        this.chug = chug;
        this.maslul = maslul;
        this.prefix = prefix;

    }

    public UploadCourse(String name, String number) {
        this.name = name;
        this.number = number;
    }

    public void printCourse(UploadCourse uploadCourse) {
//        System.out.println("\nnumber: " + uploadCourse.number + "\n"
//                + "name: " + uploadCourse.name + "\n"
//                + "points: " + uploadCourse.points + "\n"
//                + "semester: " + uploadCourse.semester + "\n"
//                + "type: " + uploadCourse.type + "\n"
//                + "year: " + uploadCourse.year + "\n"
//        );
    }


    public String getPrefix() {
        return prefix;
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

    public String getType() {
        return type;
    }

    public String getYear() {
        return year;
    }

    public String getChug() {
        return chug;
    }

    public String getMaslul() {
        return maslul;
    }
}

