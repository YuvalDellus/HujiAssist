package huji.postpc2021.hujiassistant;

import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import huji.postpc2021.hujiassistant.Fragments.InfoFragment;

//import android.util.Log;

public class MobileVersionFetcher extends AsyncTask<String, Void, Void> {
    /**
     * The Fetcher will fetch all the data of a given faculty, including all the Chugs in the faculty,
     * all the masluls in each Chug and all the courses in each maslul. after that the fetcher will
     * fetch the schedule for every course and all it's kdam and after courses that depend on the course.
     * The fetcher will update and organize all the data in FireStore Storage DataBase.
     */
    private String facultyId;
    private String chugId;
    private String maslulId;
    private Chug currUploadChug = null;
    private UploadMaslul currUploadMaslul = null;
    private ArrayList<UploadCourse> courses = new ArrayList<>();
    private LocalDataBase dataBase;
    FirebaseFirestore firebaseInstancedb = FirebaseFirestore.getInstance();

    private String collection_name = "coursesTestOnlyCs";


    private String number;
    private String name;
    private String points;
    private String semester;
    private String type;
    private String chug;
    private String maslul;
    private String year;

    ArrayList<String> data = new ArrayList<>();

    // ------------------------------------------ getCoursesData patterns ------------------------------------------

    Pattern notFoundPattern = Pattern.compile("<span id=\"lblError\">מסלול (.*) לא נמצא<\\/span>", Pattern.CASE_INSENSITIVE);

    Pattern chugPattern = Pattern.compile("ChugName.*>(.*)</span>", Pattern.CASE_INSENSITIVE);
    Pattern maslulPattern = Pattern.compile("lMaslulName.*>(.*)</br>", Pattern.CASE_INSENSITIVE);
    Pattern courseTypePattern = Pattern.compile("EshkolType.*>(.*)</span>", Pattern.CASE_INSENSITIVE);
    Pattern courseYearPattern = Pattern.compile("_lblYearToar.*>(.*)'</span>", Pattern.CASE_INSENSITIVE);

    Pattern numberPattern = Pattern.compile("CourseNumber.*>([0-9]{5})</a>", Pattern.CASE_INSENSITIVE);
    Pattern namePattern = Pattern.compile("CourseName.*>(.*)</a>", Pattern.CASE_INSENSITIVE);
    Pattern pointsPattern = Pattern.compile("CoursePoints.*>([0-9]*)</span>", Pattern.CASE_INSENSITIVE);
    Pattern semesterPattern = Pattern.compile("CourseSemester.*>(.*)</span>", Pattern.CASE_INSENSITIVE);

    Pattern startMaslulPointsPattern = Pattern.compile("סה\"כ נקודות בחוג", Pattern.CASE_INSENSITIVE);
    Pattern mandatoryPointsPattern = Pattern.compile("rowspan=\"2\">חובה<\\/td><td>חובה בחוג<\\/td><td>([0-9]*)<\\/td>", Pattern.CASE_INSENSITIVE);
    Pattern mandatoryMathPointsPattern = Pattern.compile("<td>לימודי מתמטיקה חובה<\\/td><td>([0-9]*)<\\/td>", Pattern.CASE_INSENSITIVE);
    Pattern mandatoryYesodPointsPattern = Pattern.compile("<td>לימודי יסוד חובה<\\/td><td>([0-9]*)<\\/td>", Pattern.CASE_INSENSITIVE);
    Pattern mandatoryChoosePointsPattern = Pattern.compile("<td colspan=\"2\">חובת בחירה<\\/td><td>([0-9]*)<\\/td>", Pattern.CASE_INSENSITIVE);
    Pattern cornerStonesPointsPattern = Pattern.compile("<td colspan=\"2\">אבני פינה<\\/td><td>([0-9]*)<\\/td>", Pattern.CASE_INSENSITIVE);
    Pattern totalMaslulPointsPattern = Pattern.compile("<td colspan=\"2\">סה\"כ נ\"ז למסלול מינימום<\\/td><td>([0-9]*)<\\/td>", Pattern.CASE_INSENSITIVE);

    ArrayList<Pattern> patterns = new ArrayList<>(Arrays.asList(numberPattern, namePattern, pointsPattern, semesterPattern));
    ArrayList<Pattern> specialPatterns = new ArrayList<>(Arrays.asList(chugPattern, maslulPattern, courseTypePattern, courseYearPattern));
    ArrayList<Pattern> pointsPatterns = new ArrayList<>(Arrays.asList(mandatoryPointsPattern, mandatoryMathPointsPattern, mandatoryYesodPointsPattern, mandatoryChoosePointsPattern, cornerStonesPointsPattern, totalMaslulPointsPattern));


    // ------------------------------------------ getCoursesDetailsData patterns ------------------------------------------

    Pattern endReadingPattern = Pattern.compile("קורסים חליפיים ברמת המסלול", Pattern.CASE_INSENSITIVE);

    // kdam/after courses type patterns
    Pattern kdamCoursePattern = Pattern.compile("TitleCourse.*>(דרישות קדם ברמת האוניברסיטה)<\\/span>", Pattern.CASE_INSENSITIVE);
    Pattern mandatoryCoursePattern = Pattern.compile("GroupTitle.*>(בחר את קורס)<\\/span>", Pattern.CASE_INSENSITIVE);
    Pattern optionalCoursePattern = Pattern.compile("GroupTitle.*>(וגם יש לבחור אחד מהקורסים הבאים)<\\/span>", Pattern.CASE_INSENSITIVE);
    Pattern afterCoursePattern = Pattern.compile("TitleKedemTo.*>(קורס זה משמש דרישת קדם לקורסים הבאים)<\\/span>", Pattern.CASE_INSENSITIVE);

    // kdam/after courses details patterns
    Pattern kdamNumberPattern = Pattern.compile("CourseNumber.*>([0-9]{5})</a>", Pattern.CASE_INSENSITIVE);
    Pattern kdamNamePattern = Pattern.compile("CourseName.*>(.*)<\\/a>", Pattern.CASE_INSENSITIVE);
    Pattern kdamPointsPattern = Pattern.compile("CoursePoints.*>([0-9]*)<\\/span>", Pattern.CASE_INSENSITIVE);
    Pattern kdamSemesterPattern = Pattern.compile("CourseSemester.*>(.*)</span>", Pattern.CASE_INSENSITIVE);

    // schedule patterns
    Pattern startingSchedulePattern = Pattern.compile("courseGridNew.*grdMoadim", Pattern.CASE_INSENSITIVE);
    Pattern lessonOrTirgulPattern = Pattern.compile("GroupType.*>(.*)</span>", Pattern.CASE_INSENSITIVE);
    Pattern groupNumberPattern = Pattern.compile("GroupNote.*>(.*)</span>", Pattern.CASE_INSENSITIVE);
    Pattern dayPattern = Pattern.compile("Day.*>(.*)</span>", Pattern.CASE_INSENSITIVE);
    Pattern startingHourPattern = Pattern.compile("From.*>(.*)</span>", Pattern.CASE_INSENSITIVE);
    Pattern endingHourPattern = Pattern.compile("To.*>(.*)</span>", Pattern.CASE_INSENSITIVE);
    Pattern locationPattern = Pattern.compile("Hall.*>(.*)</span>", Pattern.CASE_INSENSITIVE);
    Pattern groupSemesterPattern = Pattern.compile("MoedSemester.*>(.*)</span>", Pattern.CASE_INSENSITIVE);
    Pattern teachersPattern = Pattern.compile("Teachers.*>(.*)</span>", Pattern.CASE_INSENSITIVE);

    ArrayList<Pattern> coursesDetailsPatterns_List = new ArrayList<>(Arrays.asList(kdamNumberPattern, kdamNamePattern, kdamPointsPattern, kdamSemesterPattern));

    ArrayList<Pattern> schedulePatterns_List = new ArrayList<>(Arrays.asList(lessonOrTirgulPattern, groupNumberPattern, dayPattern, startingHourPattern, endingHourPattern, locationPattern, groupSemesterPattern, teachersPattern));
    private String courseId;


    @Override
    protected Void doInBackground(String... data) {

        if (dataBase == null){
            dataBase = HujiAssistentApplication.getInstance().getDataBase();
        }

        if (data[0].equals("details")){
            try {
                courses = dataBase.getCoursesToProcess();
                maslulId = data[1];
                chugId = data[2];
                getCoursesDetailsData();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
        {
            facultyId = data[0];
            chugId = data[1];
            maslulId = data[2];
            try {
                getMaslulData();
                getCoursesData();


            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }


        return null;
    }

    public void getMaslulData() throws IOException {
        URL url = new URL("https://shnaton.huji.ac.il/mapa.php/" + facultyId + "/" + chugId + "/" + "2022");
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        InputStream inputStream = httpURLConnection.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("Windows-1255")));


        Pattern pattern = Pattern.compile(" <a href=\"index.php\\/Program\\/.*\\/.*\\/.*\\/.*\" title=\".*\" onclick=\"document.documentElement.style.cursor = 'progress'\" target=\"_blank\"  >.*<\\/a>  <\\/div>", Pattern.CASE_INSENSITIVE);
        String line = bufferedReader.readLine();

        while (line != null) {  // Chug details
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {

                // Gets maslul number and name
                String currMaslulId = line.substring(36, 40);
                if (!currMaslulId.equals(maslulId)) {
                    break;
                }

                // Get maslul title
                String[] split = line.split(">");
                String fline = split[1];

                String replace1 = fline.replace("</a", "");
                String title = replace1.replace("לתכנית לימודים במסלול", "");

                // Create new maslul object
                UploadMaslul newUploadMaslul = new UploadMaslul(title, currMaslulId, chugId);

                DocumentReference document = firebaseInstancedb.collection(collection_name).document(chugId).collection("maslulimInChug").document(currMaslulId);
                document.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult();
                        document.set(newUploadMaslul);
                        currUploadMaslul = newUploadMaslul;
                    }
                });
            }
            line = bufferedReader.readLine();
        } // end of while loop
    }


    private void getCoursesData() throws IOException, InterruptedException, ExecutionException {
        /**
         * The method will extract all the Courses in all the given faculties, Chugs and Masluls,
         * and organize it both in FireStore Database and local HasMaps.
         **/


        String yearByUser = "2022";
        boolean pointsFlag = false;

        if (facultyId.equals("12"))
            facultyId = "2"; // some Faculties number need special treatment and trimming
        else if (facultyId.equals("11") || facultyId.equals("16") || facultyId.equals("30") || facultyId.length() < 2) {
            Log.e("CourseFetcher", "Error, the following data was cancelld: " + facultyId + " " + chugId + " " + maslulId);
        } else facultyId = facultyId.substring(1);

        Log.e("CourseFetcher", "processing now: " + " " + chugId + " " + maslulId);

        URL url = new URL("http://moon.cc.huji.ac.il/nano/pages/wfrMaslulDetails.aspx?year=" + yearByUser + "&faculty=2&entityId=" + chugId + "&chugId=" + chugId + "&degreeCode=71&maslulId=" + facultyId + maslulId);
        Log.e("Address", "curr url: " + "http://moon.cc.huji.ac.il/nano/pages/wfrMaslulDetails.aspx?year=" + yearByUser + "&faculty=2&entityId=" + chugId + "&chugId=" + chugId + "&degreeCode=71&maslulId=" + facultyId + maslulId);


        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        InputStream inputStream = httpURLConnection.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = bufferedReader.readLine();

        int patternIndex = 0;
        Pattern pattern;

        data.add(number);
        data.add(name);
        data.add(points);
        data.add(semester);
        data.add(chug);
        data.add(maslul);
        data.add(type);
        data.add(year);

        // ---------------------------------------- start reading and parsing ----------------------------------------


        while (line != null) {  // course details

            if (notFoundPattern.matcher(line).find()) { // page not found, wrong url
//                            Log.e("Address", "Wrong url, canceled " + url + "\nchug: " + chugId + " maslul: " + maslulId);
                firebaseInstancedb.collection(collection_name).document(chugId).collection("maslulimInChug").document(maslulId).delete();
                break;
            }

            pattern = patterns.get(patternIndex);
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                data.set(patternIndex, matcher.group(1));

                patternIndex = patternIndex + 1;

            } else if (startMaslulPointsPattern.matcher(line).find() || pointsFlag) { // course points, that will be in the end of the html, therefore the flag is justifies
                int pointsIndex = -1;
                pointsFlag = true;
                for (Pattern pat : pointsPatterns) {

                    pointsIndex = pointsIndex + 1;
                    Matcher pointsMatcher = pat.matcher(line);
                    if (pointsMatcher.find()) {
                        String value = pointsMatcher.group(1);

                        switch (pointsIndex) {
                            case 0:
                                currUploadMaslul.setMandatoryPointsTotal(value);
                                break;
                            case 1:
                                currUploadMaslul.setMandatoryMathPoints(value);
                                break;
                            case 2:
                                currUploadMaslul.setMandatoryYesodPoints(value);
                                break;
                            case 3:
                                currUploadMaslul.setMandatoryChoicePoints(value);
                                break;
                            case 4:
                                currUploadMaslul.setCornerStonesPoints(value);
                                break;
                            case 5:
                                currUploadMaslul.setTotalPoints(value);

                                DocumentReference document = firebaseInstancedb.collection(collection_name).document(chugId).collection("maslulimInChug").document(maslulId);
                                document.set(currUploadMaslul).addOnCompleteListener(task -> Log.i("maslulData", "points data updated on " + chugId + " -> " + maslulId));
                                break;
                            default:
                                break;
                        }
                    }
                }


            } else { // course year, type, chug, maslul
                int index = -1;
                for (Pattern pat : specialPatterns) {
                    Matcher specialMatcher = pat.matcher(line);
                    index = index + 1;
                    if (specialMatcher.find()) {
                        data.set(index + 4, specialMatcher.group(1));
                        if (specialMatcher.group(1).equals("וגם"))
                            data.set(index + 4, "לימודי חובת בחירה");
                    }
                }
            }

            if (patternIndex >= patterns.size()) {
                patternIndex = 0;

                DocumentReference document = firebaseInstancedb.collection(collection_name).document(chugId).collection("maslulimInChug").document(maslulId).collection("coursesInMaslul").document(data.get(0));
                document.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult();
                        if (doc.exists()) {
                            Log.e("exists", "This course already exists :" + chugId + " -> " + maslulId + " -> " + data.get(0));
                        } else {
                            UploadCourse newUploadUploadCourse = new UploadCourse(data.get(0), data.get(1), data.get(2), data.get(3), data.get(6), data.get(7), data.get(4), data.get(5), yearByUser);
                            document.set(newUploadUploadCourse).addOnSuccessListener(aVoid -> {
                                Log.i("new", "added a new course to firestore db: " + chugId + " -> " + maslulId + " -> " + newUploadUploadCourse.getNumber());
                                HujiAssistentApplication.getInstance().getInfoFragment().clickContinue();
                                courses.add(newUploadUploadCourse);
                                dataBase.setCoursesToProcess(courses);
                            });
                        }
                    }
                });
            }
            line = bufferedReader.readLine();
        } // end of reading line while loop

        System.out.println("gets all course to maslul to: " + chugId + " -> " + maslulId);

        inputStream.close();
        bufferedReader.close();

        System.out.println("finished faculty: " + facultyId);

    }


    private void getCoursesDetailsData() throws IOException {
        /**
         * The method will extract all the details of a specific Course, including its timetable
         * and the courses that depends on it.
         **/

        if (maslulId == null || chugId == null){
            maslulId = dataBase.getCurrentStudent().getMaslulId();
            chugId = dataBase.getCurrentStudent().getChugId();
        }

        ArrayList<String> data = new ArrayList<>();
        ArrayList<String> kdamCourses = new ArrayList<>();
        ArrayList<String> afterCourses = new ArrayList<>();
        ArrayList<String> schedule = new ArrayList<>();
        ArrayList<String> listToAddTo = new ArrayList<>();
        ArrayList<Pattern> patterns = new ArrayList<>();


        for (UploadCourse currCourse : courses) {

            String courseId = currCourse.getNumber();

            URL url = new URL("http://moon.cc.huji.ac.il/nano/pages/wfrCourse.aspx?faculty=2&year=2022&courseId=" + courseId);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = bufferedReader.readLine();

            String prefix = "";
            int patternIndex = 0;
            Pattern pattern;

            data.add("Lesson or Tirgul");
            data.add("Group number");
            data.add("Day");
            data.add("Starting Time");
            data.add("Ending Time");
            data.add("Location");
            data.add("Group semester");
            data.add("Teachers");

            patterns = schedulePatterns_List;
            listToAddTo = schedule;

            // ---------------------------------------- start reading and parsing ----------------------------------------

            while (line != null) {  // course details

                if (endReadingPattern.matcher(line).find())
                    break; // all data after is irrelevant

                pattern = patterns.get(patternIndex);
                Matcher matcher = pattern.matcher(line);

                if (matcher.find()) {
                    String currData = matcher.group(1);
                    if (matcher.group(1).equals("")) {
                        if (patterns == schedulePatterns_List)
                            currData = "לא נקבע עדיין";
                        else {
                            patternIndex = 0;
                            continue;
                        }
                    }
                    data.set(patternIndex, currData);
                    patternIndex = patternIndex + 1;

                } else if (kdamCoursePattern.matcher(line).find()) {  // kdam courses
                    patterns = coursesDetailsPatterns_List;
                    listToAddTo = kdamCourses;
                    data.clear();
                    data.add(number);
                    data.add(name);
                    data.add(points);
                    data.add(semester);

                    // mandatory or optional course
                } else if (optionalCoursePattern.matcher(line).find()) { // mandatory or optional course
                    prefix = prefix + "** ";

                } else if (afterCoursePattern.matcher(line).find()) { // after courses
                    prefix = "";
                    patterns = coursesDetailsPatterns_List;
                    listToAddTo = afterCourses;
                    data.clear();
                    data.add(number);
                    data.add(name);
                    data.add(points);
                    data.add(semester);
                }

                if (patternIndex >= patterns.size()) {  // detailed fulled, adding entering to the array
                    patternIndex = 0;
                    if (listToAddTo == schedule) { // initialize Schedule entry
                        CourseScheduleEntry entry = new CourseScheduleEntry(data.get(0), data.get(1), data.get(2), data.get(3), data.get(4), data.get(5), data.get(6), data.get(7));
                        DocumentReference document = firebaseInstancedb.collection(collection_name).document(chugId).collection("maslulimInChug").document(maslulId).collection("coursesInMaslul").document(courseId).collection("schedule").document(entry.getType()).collection(entry.getGroup()).document(entry.getDay());

                        document.get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot doc = task.getResult();
                                if (doc.exists()) {
                                    Log.e("exists", "This schedule already exists :" + chugId + " -> " + maslulId + " -> " + courseId + " -> " + entry.toString());
                                } else {
                                    document.set(entry).addOnSuccessListener(aVoid -> {
                                        Log.i("new", "added a new schedule entry to firestore db: " + chugId + " -> " + maslulId + " -> " + courseId + " -> " + entry.toString());
                                    });
                                }
                            }
                        });

                    } else { // initialize Course Kdam/After

                        KdamOrAfterCourse newKdamOrAfterCourse = new KdamOrAfterCourse(data.get(0), data.get(1), data.get(2), data.get(3), prefix);
                        data.clear();
                        data.add("Lesson or Tirgul");
                        data.add("Group number");
                        data.add("Day");
                        data.add("Starting Time");
                        data.add("Ending Time");
                        data.add("Location");
                        data.add("Group semester");
                        data.add("Teachers");

                        String category = "afterCourses";

                        if (listToAddTo == kdamCourses) {
                            category = "kdamCourses";
                        }

                        if (newKdamOrAfterCourse.getNumber() == null) break;

                        DocumentReference document = firebaseInstancedb.collection(collection_name).document(chugId).collection("maslulimInChug").document(maslulId).collection("coursesInMaslul").document(courseId).collection(category).document(newKdamOrAfterCourse.getNumber());
                        document.get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot doc = task.getResult();
                                if (doc.exists()) {
                                    Log.e("exists", "This kdam course already exists :" + chugId + " -> " + maslulId + " -> " + courseId);
                                } else {
                                    document.set(newKdamOrAfterCourse).addOnSuccessListener(aVoid -> {
                                        Log.i("new", "added a new courseKdam or after to firestore db: " + chugId + " -> " + maslulId + " -> " + courseId);
                                    });
                                }
                            }
                        });


                    }
                }
                line = bufferedReader.readLine();
            }
            inputStream.close();
            bufferedReader.close();

        }
    }



}