package huji.postpc2021.hujiassistant;

import java.util.ArrayList;

public class Faculty {

    private String title = "";
    private String facultyId = "";
    private ArrayList<Chug> chugimInFaculty = new ArrayList<>();

    public Faculty() {

    }

    public Faculty(String title1, String facultyId1) {
        title = title1;
        facultyId = facultyId1;
        chugimInFaculty = new ArrayList<>();
    }

    public String toStringP() {
        return "title: " + title + "facultyId: " + facultyId;
    }

    public void addChugToFaculty(Chug chug) {
        chugimInFaculty.add(chug);
    }

    public String getFacultyId() {
        return facultyId;
    }

    public String getTitle() {
        return this.title;
    }
}
