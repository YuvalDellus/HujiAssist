package huji.postpc2021.hujiassistant;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Chug {

    public String title = "";
    public String chugId = "";
    public String facultyParentId = "";
    public ArrayList<Maslul> listOfMaslulim = new ArrayList<>();

    public Chug() {

    }

    public Chug(String title1, String chugId1, String facultyParentId1) {
        title = title1;
        chugId = chugId1;
        facultyParentId = facultyParentId1;
        listOfMaslulim = new ArrayList<>();
    }

    public String toStringP() {
        return "title: " + title + "chugId: " + chugId + "faculty: " + facultyParentId;
    }

    public void addMaslulToChug(Maslul maslul) {
        listOfMaslulim.add(maslul);
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        return chugId;
    }


}
