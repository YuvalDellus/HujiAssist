package huji.postpc2021.hujiassistant;


public class UploadMaslul {
    /**
     * A class that represent A Maslul in A Chug
     */

    private String maslulId = "";
    private String title = "";
    private String chugParentId = "";
    private String mandatoryPointsTotal = "";
    private String mandatoryMathPoints = "";
    private String mandatoryYesodPoints = "";
    private String mandatoryChoicePoints = "";
    private String cornerStonesPoints = "";
    private String totalPoints = "";


    public UploadMaslul() {
    }

    public UploadMaslul(String title1, String maslulId1, String chugParentId1) {
        title = title1;
        maslulId = maslulId1;
        chugParentId = chugParentId1;
    }

    public String toStringP() {
        return "title: " + title + "maslulId: " + maslulId + "chugParentId: " + chugParentId;
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        return maslulId;
    }

    public String getChugParentId() {
        return chugParentId;
    }

    public String getMandatoryPointsTotal() {
        return mandatoryPointsTotal;
    }

    public String getMandatoryMathPoints() {
        return mandatoryMathPoints;
    }

    public String getMandatoryYesodPoints() {
        return mandatoryYesodPoints;
    }

    public String getMandatoryChoicePoints() {
        return mandatoryChoicePoints;
    }

    public String getCornerStonesPoints() {
        return cornerStonesPoints;
    }

    public String getTotalPoints() {
        return totalPoints;
    }

    public String getMaslulId() {
        return maslulId;
    }

    public void setMandatoryPointsTotal(String mandatoryPointsTotal) {
        this.mandatoryPointsTotal = mandatoryPointsTotal;
    }

    public void setMandatoryMathPoints(String mandatoryMathPoints) {
        this.mandatoryMathPoints = mandatoryMathPoints;
    }

    public void setMandatoryYesodPoints(String mandatoryYesodPoints) {
        this.mandatoryYesodPoints = mandatoryYesodPoints;
    }

    public void setMandatoryChoicePoints(String mandatoryChoicePoints) {
        this.mandatoryChoicePoints = mandatoryChoicePoints;
    }

    public void setCornerStonesPoints(String cornerStonesPoints) {
        this.cornerStonesPoints = cornerStonesPoints;
    }

    public void setTotalPoints(String totalPoints) {
        this.totalPoints = totalPoints;
    }
}

