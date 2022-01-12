package huji.postpc2021.hujiassistant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class StudentInfo {

    private String personalName;
    private String familyName;
    private String email;
    private String password;
    private String id;
    private String facultyId; // faculty name
    private String chugId;
    private String maslulId;
    private String degree;
    private String year;
    private String beginYear;
    private String beginSemester;

    // Array list
    // The courses the student has finished
    private ArrayList<String> courses = new ArrayList<>();
    private ArrayList<String> coursesMadeByStudent; // list of course id's

    // Hash map that saves the grades of the student by course id
    private HashMap<String, String> coursesGrades = null;

    //private ArrayList<String> coursesPlannedByStudent = new ArrayList<>();;
    private ArrayList<CourseScheduleEntry> schedulePlannedByStudent = null;

    // Saves the planned courses of the student
    private ArrayList<String> plannedCourses = null;

    public StudentInfo() {

    }

    public StudentInfo(String email_, String password_, String personalName_, String familyName_) {
        this.id = UUID.randomUUID().toString();
        this.personalName = personalName_;
        this.password = password_;
        this.familyName = familyName_;
        this.email = email_;
    }

    public StudentInfo(String studentId, String email) {
        this.id = studentId;
        this.email = email;
    }

    public StudentInfo(String facultyId_, String chugId_, String maslulId_, String degree_, String year_, String beginYear_,
                       String beginSemester_, ArrayList<String> courses_) {
        this.id = UUID.randomUUID().toString();
        this.facultyId = facultyId_;
        this.chugId = chugId_;
        this.maslulId = maslulId_;
        this.degree = degree_;
        this.year = year_;
        this.beginYear = beginYear_;
        this.beginSemester = beginSemester_;
        this.courses = new ArrayList<>(courses_);
    }

    public StudentInfo(String personalName_, String familyName_, String email, String facultyId, String chugId, String maslulId, String degree, String year, String id) {
        this.personalName = personalName_;
        this.familyName = familyName_;
        this.email = email;
        this.facultyId = facultyId;
        this.chugId = chugId;
        this.maslulId = maslulId;
        this.degree = degree;
        this.year = year;
        this.id = id;
    }

    // FOR LOCAL DB CLASS
    public StudentInfo(String id_, String email_, String personalName_, String familyName_, String facultyId_,
                       String chugId_, String maslulId_, String degree_, String year_, String beginYear_, String beginSemester_,
                       ArrayList<String> courses_) {

        this.personalName = personalName_;
        this.familyName = familyName_;
        this.email = email_;
        this.facultyId = facultyId_;
        this.chugId = chugId_;
        this.maslulId = maslulId_;
        this.degree = degree_;
        this.year = year_;
        this.beginYear = beginYear_;
        this.beginSemester = beginSemester_;
        this.id = id_;
        this.courses = new ArrayList<>(courses_);
        this.coursesGrades = new HashMap<>();
        this.plannedCourses = new ArrayList<>();
    }

    public void setPersonalName(String name) {
        this.personalName = name;
    }

    public void setFamilyName(String name) {
        this.familyName = name;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setDegree(String degreeName) {
        this.degree = degreeName;
    }

    public void setBeginYear(String year_) {
        this.beginYear = year_;
    }

    public void setBeginSemester(String beginSemester_) {
        this.beginSemester = beginSemester_;
    }

    public void setCourses(ArrayList<String> courses) {
        this.courses = new ArrayList<>(courses);
    }

    public void setCoursesGrades(HashMap<String, String> coursesGrades) {
        this.coursesGrades = new HashMap<>(coursesGrades);
    }

    public void setPlanned(ArrayList<String> list) {
        this.plannedCourses = new ArrayList<>(list);
    }

    public ArrayList<String> getPlanned() {
        return new ArrayList(this.plannedCourses);
    }

    public HashMap<String, String> getCoursesGrades() {
        return new HashMap<>(this.coursesGrades);
    }

    public ArrayList<String> getCourses() {
        return new ArrayList<>(this.courses);
    }

    public void setFacultyId(String facultyId_) {
        this.facultyId = facultyId_;
    }

    public void setChugId(String chugId_) {
        this.chugId = chugId_;
    }

    public void setMaslulId(String maslulId_) {
        this.maslulId = maslulId_;
    }

    public String toStringP() {
        return " personal name: " + personalName + " family name: " + familyName + " email: " + email + " faculty id: " +
                facultyId + "chug id: " + chugId + " maslulId: " + maslulId + " degree: " + degree + " year: " + year +
                " begin year: " + beginYear + " begin semester: " + beginSemester;
    }

    public String getPassword() {
        return password;
    }

    public String getPersonalName() {
        return personalName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public String getFacultyId() {
        return facultyId;
    }

    public String getChugId() {
        return chugId;
    }

    public String getMaslulId() {
        return maslulId;
    }

    public String getDegree() {
        return degree;
    }

    public String getYear() {
        return year;
    }

    public String getBeginYear() {
        return beginYear;
    }

    public String getBeginSemester() {
        return beginSemester;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void printCourses() {
//        for (String id : this.courses) {
//            System.out.println("course3: " + id);
//        }
    }

    public ArrayList<CourseScheduleEntry> getSchedulePlannedByStudent() {
        return schedulePlannedByStudent;
    }

    public void setSchedulePlannedByStudent(ArrayList<CourseScheduleEntry> schedulePlannedByStudent) {
        this.schedulePlannedByStudent = schedulePlannedByStudent;
    }

    public void addScheduleEntry(CourseScheduleEntry entry) {
        this.schedulePlannedByStudent.add(entry);
    }

    public void removeScheduleEntry(CourseScheduleEntry entry) {
        if (this.schedulePlannedByStudent.contains(entry)) {
            this.schedulePlannedByStudent.remove(entry);
        }
    }

    // public ArrayList<String> getCoursesPlannedByStudent() {
    //   return plannedCourses;
    // }

    // public void setCoursesPlannedByStudent(ArrayList<String> coursesPlannedByStudent) {
    //   System.out.println("nn" + coursesPlannedByStudent);
    //  this.plannedCourses = new ArrayList<>(coursesPlannedByStudent);
    //  }

    //public void addCoursePlannedByStudent(String courseId){
    //   this.coursesPlannedByStudent.add(courseId);
    // }
    // public void updateCoursePlannedByStudentList(String courseId){
    //  if (coursesPlannedByStudent.contains(courseId)){

    //    this.coursesPlannedByStudent.add(courseId);
    // }else {

    //      this.coursesPlannedByStudent.remove(courseId);
    //  }
    // }

    // public void removeCoursePlannedByStudent(String courseId){
    //   if (this.coursesPlannedByStudent.contains(courseId)){
    //        this.coursesPlannedByStudent.remove(courseId);
    //   }
    //}
}
