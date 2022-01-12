package huji.postpc2021.hujiassistant;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
//import android.util.Log;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class LocalDataBase {

    private final ArrayList<StudentInfo> listOfStudents = new ArrayList<>();
    private final Context context;
    private final SharedPreferences sp;
    private final MutableLiveData<List<StudentInfo>> mutableLiveData = new MutableLiveData<>();
    public final LiveData<List<StudentInfo>> publicLiveData = mutableLiveData;
    private ArrayList<Course> listOfCourses = new ArrayList<>();
    private HashMap<String, String> faculty_values_names_map;
    public HashMap<String, String> ruach_faculty_values_names_map;
    private String[] faculty_values;
    public HashMap<String, Chug> chugs = new HashMap<>();
    private ArrayList<Course> coursesFromFireBase = new ArrayList<>();
    private FirebaseUser currentFbUser;
    private StudentInfo currentStudent;
    private Faculty currentFaculty;
    private Chug currentChug;
    private Maslul currentMaslul;
    private int currentPointsSum = 0;
    private int currentMandatoryChoosePoints = 0;
    private int currentMandatoryPoints = 0;
    private int currentCornerStonePoints = 0;
    private int currentChoosePoints = 0;
    private int currentSuppPoints = 0;
    private ArrayList<Course> coursesRegistration;
    private final HashMap<String, StudentInfo> students;
    private final MutableLiveData<StudentInfo> currentUserMutableLiveData = new MutableLiveData<>();
    public final LiveData<StudentInfo> currentUserLiveData = currentUserMutableLiveData;
    private final MutableLiveData<Boolean> firstLoadFlagMutableLiveData = new MutableLiveData<>();
    public final LiveData<Boolean> firstLoadFlagLiveData = firstLoadFlagMutableLiveData;
    private boolean firstUsersLoad = false;
    FirebaseFirestoreSettings settings;
    private ArrayList<Course> coursesOfCurrentStudent = new ArrayList<>();
    private ArrayList<Course> plannedCoursesOfStudent = new ArrayList<>();
    FirebaseFirestore db;
    private ArrayList<String> coursesRegistrationById = new ArrayList<>();
    private ArrayList<String> coursesPlannedById = new ArrayList<>();
    CollectionReference studentsCollection;
    private HashMap<String, String> gradesOfStudent = new HashMap<>();
    private ArrayList<CourseScheduleEntry> coursesPlannedByStudentDb = new ArrayList<>();
    private ArrayList<UploadCourse> coursesToProcess = null;

    // Courses db
    private final MutableLiveData<List<Course>> mutableLiveDataMyCourses = new MutableLiveData<>();
    public final LiveData<List<Course>> publicLiveDataMyCourses = mutableLiveDataMyCourses;

    private final FirebaseAuth mAuth;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public LocalDataBase(Context context) {
        this.context = context;
        this.sp = context.getSharedPreferences("local_db_calculation_items", Context.MODE_PRIVATE);
        this.mAuth = FirebaseAuth.getInstance();
        this.students = new HashMap<>();
        this.db = FirebaseFirestore.getInstance();
        this.studentsCollection = db.collection("students");
        this.currentStudent = new StudentInfo();
        this.readDataIdsInUse4();
        firstLoadFlagMutableLiveData.postValue(false);
        settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);


        mutableLiveDataMyCourses.setValue(new ArrayList<>(listOfCourses));
        sortCourseItems();

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<Course> getMyCoursesList() {
        sortCourseItems();
        return new ArrayList<>(listOfCourses);
    }

    public void addCourseId(String courseId) {
        if (courseId != null) {
            ArrayList<String> courses = this.currentStudent.getCourses();
            courses.add(courseId);
            this.currentStudent.setCourses(courses);

            // Adds the course to the list of courses
            for (Course course : this.coursesFromFireBase) {
                if (course.getNumber().equals(courseId)) {
                    this.coursesOfCurrentStudent.add(course);
                }
            }

            this.studentsCollection.document(this.currentStudent.getId()).set(this.currentStudent).addOnSuccessListener(aVoid -> {
//                Log.i("UPLOAD_SUCCESS", "upload finished");
            });
        }
    }

    public void updateGrade(String number, String grade) {
        this.gradesOfStudent.put(number, grade); // save in local
        // upload to firebase
        this.currentStudent.setCoursesGrades(new HashMap<>(this.gradesOfStudent));
        this.studentsCollection.document(this.currentStudent.getId()).set(this.currentStudent).addOnSuccessListener(aVoid -> {
//            Log.i("UPLOAD_SUCCESS", "upload of grade finished");
        });
    }

    public void uploadGrades() {
        this.currentStudent.setCoursesGrades(new HashMap<>(this.gradesOfStudent));
        this.studentsCollection.document(this.currentStudent.getId()).set(this.currentStudent).addOnSuccessListener(aVoid -> {
//            Log.i("UPLOAD_SUCCESS", "upload of grade finished");
        });
    }

    public void setCoursesRegistrationById(ArrayList<String> coursesRegistrationById_) {
        this.coursesRegistrationById = new ArrayList<>(coursesRegistrationById_);
    }

    public void updatePlannedCourses() {
        // Saves the list for the student
        this.currentStudent.setPlanned(new ArrayList<>(this.coursesPlannedById));

        // upload the list to firebase
        this.studentsCollection.document(this.currentStudent.getId()).set(this.currentStudent).addOnSuccessListener(aVoid -> {
//            Log.i("UPLOADED_SUCCESSFULLY", "upload of planned courses finished");
        });
    }

    public void setCoursesPlannedById(ArrayList<String> coursesPlannedById_) {
        this.coursesPlannedById = new ArrayList<>(coursesPlannedById_);
    }

    public ArrayList<String> getCoursesRegistrationById() {
        return new ArrayList<>(this.coursesRegistrationById);
    }

    public ArrayList<String> getCoursesPlannedById() {
        return new ArrayList<>(this.coursesPlannedById);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void removeCourseGrade(String number) {
        this.gradesOfStudent.remove(number);

        this.currentStudent.setCoursesGrades(new HashMap<>(this.gradesOfStudent));
        this.studentsCollection.document(this.currentStudent.getId()).set(this.currentStudent).addOnSuccessListener(aVoid -> {
//            Log.i("UPLOAD_SUCCESS", "upload of grade finished");
        });
    }

    public void setGradesOfStudentMap(HashMap<String, String> grades) {
        this.gradesOfStudent = new HashMap<>(grades);
    }

    public HashMap<String, String> getGradesOfStudent() {
        return new HashMap<>(this.gradesOfStudent);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void sendBroadcastDbChanged() {
        Intent broadcast = new Intent("changed_db");
        broadcast.putExtra("new_list", getMyCoursesList());
        context.sendBroadcast(broadcast);
    }

    public void saveLocale(String lang) {
        SharedPreferences.Editor editor = context.getSharedPreferences("settings", context.MODE_PRIVATE).edit();
        editor.putString("My_lang", lang);
        editor.apply();
    }

    public int getLanguageIndex() {
        SharedPreferences prefs = context.getSharedPreferences("settings", context.MODE_PRIVATE);
        String lang = prefs.getString("My_lang", "");
        if (lang.equals("he")) {
            return 1;
        } else {
            return 0;
        }
    }

    public String loadLocale() {
        SharedPreferences prefs = context.getSharedPreferences("settings", context.MODE_PRIVATE);
        return prefs.getString("My_lang", "");
    }

    public HashMap<String, String> getRuach_faculty_values_names_map() {
        return ruach_faculty_values_names_map;
    }

    public FirebaseAuth getUsersAuthenticator() {
        return mAuth;
    }

    public void setCurrentFaculty(Faculty faculty) {
        this.currentFaculty = faculty;
    }

    public Faculty getCurrentFaculty() {
        return this.currentFaculty;
    }

    public void setCurrentChug(Chug chug) {
        this.currentChug = chug;
    }

    public Chug getCurrentChug() {
        return this.currentChug;
    }

    public void setCoursesFromFireBase(ArrayList<Course> coursesToSet) {
        this.coursesFromFireBase = new ArrayList<>(coursesToSet);
    }

    public void setCurrentPointsSum(int sum) {
        this.currentPointsSum = sum;
    }

    public void setCurrentMandatoryPoints(int currentMandatoryPoints_) {
        this.currentMandatoryPoints = currentMandatoryPoints_;
    }

    public void setCurrentMandatoryChoosePoints(int currentMandatoryChoosePoints_) {
        this.currentMandatoryChoosePoints = currentMandatoryChoosePoints_;
    }

    public void setCurrentCornerStonesPoints(int currentCornerStonePoints_) {
        this.currentCornerStonePoints = currentCornerStonePoints_;
    }

    public int getCurrentCornerStonesPoints() {
        return this.currentCornerStonePoints;
    }

    public int getCurrentMandatoryPoints() {
        return this.currentMandatoryPoints;
    }

    public int getCurrentMandatoryChoosePoints() {
        return this.currentMandatoryChoosePoints;
    }

    public int getCurrentPointsSum() {
        return this.currentPointsSum;
    }

    public void setCoursesRegistration(ArrayList<Course> list) {
        this.coursesRegistration = new ArrayList<>(list);
    }

    public int getCurrentChoosePoints() {
        return this.currentChoosePoints;
    }

    public int getCurrentCornerStonePoints() {
        return this.currentCornerStonePoints;
    }

    public int getCurrentSuppPoints() {
        return this.currentSuppPoints;
    }

    public ArrayList<Course> getCoursesRegistration() {
        return new ArrayList<>(this.coursesRegistration);
    }

    public ArrayList<Course> getCoursesFromFireBase() {
        return new ArrayList<>(this.coursesFromFireBase);
    }

    public void setCurrentMaslul(Maslul maslul) {
        this.currentMaslul = maslul;
    }

    public Maslul getCurrentMaslul() {
        return this.currentMaslul;
    }

    public StudentInfo getCurrentUser() {
        return this.currentStudent;
    }

    public void saveUserToSp(String id, String email, String password) {
        SharedPreferences.Editor spEditor = sp.edit();
        spEditor.apply();
    }

    public void logoutUser() {
        mAuth.signOut();
        this.currentFbUser = null;
        this.currentStudent = new StudentInfo();
        SharedPreferences.Editor spEditor = sp.edit();
        spEditor.apply();
    }

    public boolean emailUserExists(String email) {
        for (StudentInfo student : students.values()) {
            if (student.getEmail().equals(email)) return true;
        }
        return false;
    }

    public void setCurrentChoosePoints(int currentChoosePoints_) {
        this.currentChoosePoints = currentChoosePoints_;
    }

    public void setCurrentSuppPoints(int currentSuppPoints_) {
        this.currentSuppPoints = currentSuppPoints_;
    }

    public void addStudent(String studentId, String email, String personalName,
                           String familyName, String facultyId, String chugId, String maslulId,
                           String degreeType, String year, String beginYear, String beginSemester,
                           ArrayList<String> courses) {

        StudentInfo newStudent = new StudentInfo(studentId, email, personalName, familyName,
                facultyId, chugId, maslulId, degreeType, year, beginYear, beginSemester,
                courses);

        Map<String, StudentInfo> newUser = new HashMap<>();
        setCurrentStudent(newStudent);
        newUser.put(newStudent.getId(), newStudent);
        this.studentsCollection.document(newStudent.getId()).set(newStudent);
    }

    public void updateStudent(String name, String email, String facultyId, String chugId, String maslulId, String degree, String year, String id) {

        StudentInfo newUser = new StudentInfo(name, name, email, facultyId, chugId, maslulId, degree, year, id);
        Map<String, StudentInfo> updatedUser = new HashMap<>();
        updatedUser.put(newUser.getId(), newUser);
        this.studentsCollection.document(newUser.getId()).set(updatedUser).addOnSuccessListener(aVoid -> {
            if (newUser.getId().equals(currentStudent.getId())) {
                currentStudent = newUser;
            } else {
//                Log.d("sameUserCheck", "not the current user");
            }
        });
    }

    public void updateYear(String year) {
        this.currentStudent.setYear(year);
        this.studentsCollection.document(this.currentStudent.getId()).set(this.currentStudent).addOnSuccessListener(aVoid -> {
//            Log.i("UPLOAD_SUCCESS", "upload finished");
        });
    }

    public void updateDegree(String degree) {
        this.currentStudent.setDegree(degree);
        this.studentsCollection.document(this.currentStudent.getId()).set(this.currentStudent).addOnSuccessListener(aVoid -> {
//            Log.i("UPLOAD_SUCCESS", "upload finished");
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void removeCourseFromCurrentList(String itemId) {

        ArrayList<String> coursesIdList = new ArrayList<>(this.currentStudent.getCourses());
        coursesIdList.remove(itemId);
        this.currentStudent.setCourses(coursesIdList);

        Course toDelete = null;
        for (Course item : this.coursesOfCurrentStudent) {
            if (item.getNumber().equals(itemId)) {
                toDelete = item;
                break;
            }
        }
        if (toDelete != null) {
            this.coursesOfCurrentStudent.remove(toDelete);
        }

        SharedPreferences.Editor editor = sp.edit();
        if (toDelete != null) {
            editor.remove(toDelete.getNumber()); // remove the key
        }
        editor.apply();

        this.studentsCollection.document(this.currentStudent.getId()).set(this.currentStudent).addOnSuccessListener(aVoid -> {
//            Log.i("UPLOAD_SUCCESS", "upload finished");
        });

    }

    public void setCurrentUser(FirebaseUser user) {
        if (user != null) {
            this.currentFbUser = user;
            if (students.containsKey(user.getUid())) {
                this.currentStudent = students.get(user.getUid());
                currentUserMutableLiveData.setValue(this.currentStudent);
            }
        }
    }

    public void readDataIdsInUse4() {
        db.collection("students")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
//                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        students.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            StudentInfo user = doc.toObject(StudentInfo.class);
                            students.put(user.getId(), user);
                        }
                        setCurrentUser(currentFbUser);
                        firstLoadFlagMutableLiveData.postValue(true);
                        firstUsersLoad = true;
                    }
                });
    }

    public void setCurrentStudent(StudentInfo studentInfo) {
        this.currentStudent = studentInfo;
    }

    public void setCoursesOfCurrentStudent(ArrayList<Course> courses) {
        this.coursesOfCurrentStudent = courses;
    }

    public void setPlannedCoursesOfCurrentStudent(ArrayList<Course> planCourses) {
        this.plannedCoursesOfStudent = planCourses;
    }

    public ArrayList<Course> getCoursesOfCurrentStudent() {
        return new ArrayList<Course>(this.coursesOfCurrentStudent);
    }

    public void AddCourse() {

    }

    public void deleteCourse() {

    }

    public StudentInfo getCurrentStudent() {
        return this.currentStudent;
    }

    public StudentInfo getStudent(String studentId) {
        if (students.containsKey(studentId)) {
            return students.get(studentId);
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void sortCourseItems() {
        this.listOfCourses.sort(new Comparator<Course>() {
            @Override
            public int compare(Course o1, Course o2) {
                if (o1.getType().equals(Course.Type.Mandatory.toString()) && o2.getType().equals(Course.Type.Mandatory.toString())) {
                    String num1 = o1.getPoints();
                    String num2 = o2.getPoints();
                    return num2.compareTo(num1);
                } else if (o1.getType().equals(Course.Type.MandatoryChoose.toString()) && o2.getType().equals(Course.Type.Mandatory.toString())) {
                    String num1 = o1.getPoints();
                    String num2 = o2.getPoints();
                    return num1.compareTo(num2);
                }
                return 0;
            }
        });
    }

    public FirebaseFirestore getFirestoreDB() {
        return db;
    }

    public ArrayList<Course> getCoursesToComplete() {
        /**
         * Returns all the courses of the current Student which Still need to be Done.
         */
        ArrayList<Course> coursesToComplete = (ArrayList<Course>) this.coursesFromFireBase.clone();
        coursesToComplete.removeAll(this.coursesOfCurrentStudent);

        for (String s : this.currentStudent.getPlanned()) {
            for (Course c : coursesToComplete) {
                if (c.getNumber().equals(s)) {
                    c.setPlannedChecked(true);
                }
            }
        }
        return coursesToComplete;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<Course> getCoursesToComplete_PerYear(String year) {
        /**
         * Returns all the courses the student has to complete from given year and prior years.
         * currently deprecated, you may use getCoursesToPlan(year, null, null)
         */

        ArrayList<String> years = new ArrayList<>();

        switch (year) {

            case "שנה ו": // is there case like this?
                years.add(year);
                break;
            case "שנה ה":
                years.add("שנה ה");
                break;
            case "שנה ד":
                years.add("שנה ד");
                break;
            case "שנה ג":
                years.add("שנה ג");
                break;
            case "שנה ב":
                years.add("שנה ב");
                break;
            case "שנה א":
                years.add("שנה א");
                break;
        }

        ArrayList<Course> coursesToComplete = getCoursesToComplete();
        ArrayList<Course> coursesToComplete_ThisYear = new ArrayList<>();

        for (String currYear : years) {
            coursesToComplete_ThisYear.addAll(coursesToComplete.stream().filter(Course -> Course.getYear().equals(currYear)).collect(Collectors.toCollection(ArrayList::new)));
        }

        return coursesToComplete_ThisYear;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<Course> getCoursesToComplete_PerSemester(String year, String semester) {
        /**
         * Returns all the courses the student has to complete occurring at specific semester
         * at specific year.
         * currently deprecated, you may use getCoursesToPlan(null, semester, null)
         */

        ArrayList<Course> coursesToComplete_perYear = getCoursesToComplete_PerYear(year);

        if (semester.equals("all")) return coursesToComplete_perYear;

        ArrayList<Course> coursesToComplete_ThisSemester = coursesToComplete_perYear.stream().filter(Course -> Course.getSemester().equals(semester)).collect(Collectors.toCollection(ArrayList::new));
        if (!semester.equals("א' או ב'"))
            coursesToComplete_ThisSemester.addAll(coursesToComplete_perYear.stream().filter(Course -> Course.getSemester().equals("א' או ב'")).collect(Collectors.toCollection(ArrayList::new)));

        return coursesToComplete_ThisSemester;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<Course> getCoursesToComplete_byPoints(String points) {
        /**
         * Returns all the courses the student need to complete by their points value.
         * currently deprecated, you may use getCoursesToPlan(null, null, points)
         */
        ArrayList<Course> coursesToComplete = getCoursesToComplete();
        ArrayList<Course> coursesToComplete_byPoints = coursesToComplete.stream().filter(Course -> Course.getPoints().equals(points)).collect(Collectors.toCollection(ArrayList::new));
        return coursesToComplete_byPoints;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<Course> getCoursesToComplete_byPoints(String points, ArrayList<Course> coursesToFilter) {
        /**
         * Returns all the courses the student need to complete by their points value, for a specific
         * courses list.
         */
        ArrayList<Course> coursesToComplete = ((ArrayList<Course>) coursesToFilter.clone());

        if (points.equals("הכל")) return coursesToComplete;

        ArrayList<Course> coursesToComplete_byPoints = coursesToComplete.stream().filter(Course -> Course.getPoints().equals(points)).collect(Collectors.toCollection(ArrayList::new));
        return coursesToComplete_byPoints;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<Course> getCoursesToPlan(String year, String semester, String points) {
        /**
         * Returns all the courses the student need to complete by their year, semester and points.
         * you can use this method to filter by any of these parameters alone or by couples, just leave
         * the parameter you don't need as null.
         */
        ArrayList<Course> coursesToComplete = getCoursesToComplete();
        ArrayList<Course> filterBySemester = new ArrayList<>();

        if (!(year == null || year.equals("הכל"))) {
            coursesToComplete = coursesToComplete.stream().filter(Course -> Course.getYear().equals(year)).collect(Collectors.toCollection(ArrayList::new));
        }

        if (!(semester == null || semester.equals("הכל"))) {
            ArrayList<Course> temp = coursesToComplete.stream().filter(Course -> Course.getSemester().equals(semester)).collect(Collectors.toCollection(ArrayList::new));
            coursesToComplete.removeAll(temp);
            if (!(semester.equals("קורס שנתי") || semester.equals("א' או ב'"))) {
                ArrayList<Course> aAndBCourses = coursesToComplete.stream().filter(Course -> Course.getSemester().equals("א' או ב'")).collect(Collectors.toCollection(ArrayList::new));
                filterBySemester.addAll(aAndBCourses);
            }
            filterBySemester.addAll(0, temp);
            coursesToComplete = filterBySemester;
        }

        if (!(points == null || points.equals("ה"))) { //ה for הכל
            coursesToComplete = coursesToComplete.stream().filter(Course -> Course.getPoints().equals(points)).collect(Collectors.toCollection(ArrayList::new));
        }

        return coursesToComplete;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<Course> sortCoursesByYearAndType(ArrayList<Course> coursesToFilter) {
        /**
         * Used to sort the Array by year one two and three..
         * And do sub-sort by course type
         * !!!
         * Notice: the method adds  Course objects as Separators, your Adapter need to take it in count
         * and hide and checkBos or irrelevant text in the holder for them not to show.
         * !!!
         */
        ArrayList<Course> sortedList = new ArrayList<>();


        ArrayList<String> yearsList = new ArrayList<>();
        yearsList.add("שנה א");
        yearsList.add("שנה ב");
        yearsList.add("שנה ג");
        yearsList.add("שנה ד");
        yearsList.add("שנה ה");

        ArrayList<Course> hovaCourses;
        ArrayList<Course> bchiraCourses;
        ArrayList<Course> hovatBchiraCourses;

        for (String year : yearsList) {

            Course separator = new Course(year, "", Course.Type.Mandatory, "0", "");
            separator.setPlanned(true);

            ArrayList<Course> currYearCourses = coursesToFilter.stream().filter(Course -> Course.getYear().equals(year)).collect(Collectors.toCollection(ArrayList::new));

            hovaCourses = currYearCourses.stream().filter(Course -> Course.getType().equals("לימודי חובה")).collect(Collectors.toCollection(ArrayList::new));
            bchiraCourses = currYearCourses.stream().filter(Course -> Course.getType().equals("קורסי בחירה")).collect(Collectors.toCollection(ArrayList::new));
            hovatBchiraCourses = currYearCourses.stream().filter(Course -> Course.getType().equals("לימודי חובת בחירה")).collect(Collectors.toCollection(ArrayList::new));

            sortedList.add(separator);
            sortedList.addAll(hovaCourses);
            sortedList.addAll(bchiraCourses);
            sortedList.addAll(hovatBchiraCourses);
        }

        return sortedList;
    }


    public String courseArrayToString(ArrayList<Course> coursesArray) {
        /**
         * Converts Course Object to a string, keeps only the dynamic data (look at doc in Course class),
         * separates each data bit by "|" token.
         */
        StringBuilder coursesString = new StringBuilder();
        Boolean notFirstFlag = false;

        for (Course course : coursesArray) {
            if (course.getNumber().equals("")) { // title Course (שנה א) or an error
                continue;
            }
            if (notFirstFlag) {
                coursesString.append("@");
            }
            coursesString.append(course.toStringToSP());
            notFirstFlag = true;
        }

        return coursesString.toString();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<Course> stringToCourseArray(String stringCourses) {
        /**
         * Takes list of courses as a string form, and rebuild it as an Array of courses.
         * The method does not construct new Course Objects, only update it's dynamic
         * data on existing courses.
         * useful specially for sp.
         */

        ArrayList<Course> coursesToComplete = getCoursesToComplete();
        ArrayList<Course> output = new ArrayList<>();
        String number;
        String grade;
        Boolean isChecked;
        Boolean isPlanned;
        Boolean isPlannedChecked;
        Boolean isFinished;

        String[] coursesStringArray = stringCourses.split("@");

        for (String courseData : coursesStringArray) {
            String[] split = courseData.split("\\|");
            number = split[0];
            grade = split[1];
            isChecked = Boolean.parseBoolean(split[2]);
            isPlanned = Boolean.parseBoolean(split[3]);
            isPlannedChecked = Boolean.parseBoolean(split[4]);
            isFinished = Boolean.parseBoolean(split[5]);

            String finalNumber = number;
            Course currCourse = coursesToComplete.stream().filter(Course -> Course.getNumber().equals(finalNumber)).findFirst().get();
            currCourse.setGrade(grade);
            currCourse.setChecked(isChecked);
            currCourse.setPlanned(isPlanned);
            currCourse.setPlannedChecked(isPlannedChecked);
            currCourse.setIsFinished(isFinished);

            output.add(currCourse);
        }

        return output;
    }

    public SharedPreferences getSp() {
        return this.sp;
    }

    public ArrayList<UploadCourse> getCoursesToProcess() {
        return coursesToProcess;
    }

    public void setCoursesToProcess(ArrayList<UploadCourse> coursesToProcess) {
        this.coursesToProcess = coursesToProcess;
    }
}
