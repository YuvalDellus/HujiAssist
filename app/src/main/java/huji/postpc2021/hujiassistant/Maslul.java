package huji.postpc2021.hujiassistant;

import java.util.ArrayList;

public class Maslul {

    private String maslulId = "";
    private String title = "";
    private String chugParentId = "";
    private String cornerStonesPoints = "";
    private String mandatoryChoicePoints = "";
    private String mandatoryMathPoints = "";
    private String mandatoryPointsTotal = "";
    private String totalPoints = "";

    public ArrayList<String> degrees = new ArrayList<>();

    public Maslul() {

    }

    public Maslul(String title1, String maslulId1, String chugParentId1) {
        title = title1;
        maslulId = maslulId1;
        chugParentId = chugParentId1;
        degrees = new ArrayList<>();
    }

    public String toStringP() {
        return "title: " + title + "maslulId: " + maslulId + "chugParentId: " + chugParentId;
    }

    public String getTitle() {
        return title;
    }

    public String getMaslulId() {
        return maslulId;
    }

    public String getChugParentId() {
        return chugParentId;
    }

    public String getCornerStonesPoints() {
        return cornerStonesPoints;
    }

    public String getMandatoryChoicePoints() {
        return mandatoryChoicePoints;
    }

    public String getMandatoryMathPoints() {
        return mandatoryMathPoints;
    }

    public String getMandatoryPointsTotal() {
        return mandatoryPointsTotal;
    }

    public String getTotalPoints() {
        return totalPoints;
    }
}
