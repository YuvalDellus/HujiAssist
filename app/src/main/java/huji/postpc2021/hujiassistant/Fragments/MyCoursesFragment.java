package huji.postpc2021.hujiassistant.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
//import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import huji.postpc2021.hujiassistant.CourseItemHolder;
import huji.postpc2021.hujiassistant.CoursesAdapter;
import huji.postpc2021.hujiassistant.HujiAssistentApplication;
import huji.postpc2021.hujiassistant.LocalDataBase;
import huji.postpc2021.hujiassistant.R;
import huji.postpc2021.hujiassistant.StudentInfo;
import huji.postpc2021.hujiassistant.ViewModelApp;
import huji.postpc2021.hujiassistant.Course;
import huji.postpc2021.hujiassistant.HujiAssistentApplication;
import huji.postpc2021.hujiassistant.LocalDataBase;
import huji.postpc2021.hujiassistant.PlanCoursesAdapter;
import huji.postpc2021.hujiassistant.R;
import huji.postpc2021.hujiassistant.StudentInfo;
import huji.postpc2021.hujiassistant.ViewModelAppMainScreen;
import huji.postpc2021.hujiassistant.databinding.FragmentMycoursesBinding;
import huji.postpc2021.hujiassistant.databinding.FragmentPlancoursesBinding;
import huji.postpc2021.hujiassistant.CourseScheduleEntry;
import huji.postpc2021.hujiassistant.HujiAssistentApplication;
import huji.postpc2021.hujiassistant.KdamCoursesAdapter;
import huji.postpc2021.hujiassistant.KdamOrAfterCourse;
import huji.postpc2021.hujiassistant.LocalDataBase;
import huji.postpc2021.hujiassistant.R;
import huji.postpc2021.hujiassistant.ScheduleAdapter;
import huji.postpc2021.hujiassistant.ViewModelAppMainScreen;
import huji.postpc2021.hujiassistant.Maslul;



import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import java.util.ArrayList;

public class MyCoursesFragment extends Fragment {

    private ViewModelAppMainScreen viewModelAppMainScreen;
    FirebaseFirestoreSettings settings;
    FragmentMycoursesBinding binding;
    public CourseItemHolder holder = null;
    LinearLayoutManager coordinatorLayout;
    public CoursesAdapter adapter = null;
    public LocalDataBase dataBase = null;
    public CoursesAdapter.OnCheckBoxClickListener onCheckBoxClickListener = null;
    public CoursesAdapter.AddGradeListener addGradeListener = null;
    RecyclerView recyclerViewMyCourses;
    AutoCompleteTextView autocompletechoosetype;
    ArrayList<String> coursesId = new ArrayList<>();
    public CoursesAdapter.OnItemClickListener onItemClickListener = null;
    public CoursesAdapter.DeleteClickListener deleteClickListener = null;
    FirebaseFirestore firebaseInstancedb = FirebaseFirestore.getInstance();

    TextView studentNameTextView;
    TextView facultyTextView;
    TextView chugTextView;
    TextView maslulTextView;
    TextView degreeTextView;
    TextView yearTextView;
    TextView textViewTotalPoints;
    TextView textViewTotalPointsDegree;
    TextView textViewTotalHovaPoints;
    TextView textViewTotalHovaChoosePoints;
    TextView textViewTotalChoosePoints;
    TextView textViewTotalSuppPoints;
    TextView textViewTotalCornerStonePoints;
    TextView averageTxt;
    TextView saveBtn;
    ArrayList<CharSequence> arrayListCollection = new ArrayList<>();

    public interface addCourseButtonClickListener{
        public void addCourseBtnClicked();
    }

    public MyCoursesFragment(){
        super(R.layout.fragment_mycourses);
    }
    public MyCoursesFragment.addCourseButtonClickListener addCourseListener = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMycoursesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModelAppMainScreen = new ViewModelProvider(requireActivity()).get(ViewModelAppMainScreen.class);
        recyclerViewMyCourses = view.findViewById(R.id.recycleViewMyCourses);
        adapter = new CoursesAdapter(getContext());
        FloatingActionButton addCourseBtn = view.findViewById(R.id.addCourseBtn);
        androidx.appcompat.widget.SearchView searchView = view.findViewById(R.id.search1);
        averageTxt = view.findViewById(R.id.textViewDegreeAverage);
        saveBtn = view.findViewById(R.id.coursesSaveBtn);

        if (dataBase == null){
            dataBase = HujiAssistentApplication.getInstance().getDataBase();
        }

        settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firebaseInstancedb.setFirestoreSettings(settings);

        StudentInfo currentStudent = dataBase.getCurrentUser();
        studentNameTextView = view.findViewById(R.id.nameOfStudentTextView);
        facultyTextView = view.findViewById(R.id.nameOfFaculty);
        chugTextView = view.findViewById(R.id.nameOfChug);
        maslulTextView = view.findViewById(R.id.nameOfMaslul);
        degreeTextView = view.findViewById(R.id.nameOfDegree);
        yearTextView = view.findViewById(R.id.yearOfDegree);
        autocompletechoosetype = view.findViewById(R.id.autocompletechoosetype1);

        // Update data on screen
        String name = studentNameTextView.getText() +" " + currentStudent.getPersonalName() + " " + currentStudent.getFamilyName();
        studentNameTextView.setText(name);

        String faculty = facultyTextView.getText() + " " + currentStudent.getFacultyId() + " " + dataBase.getCurrentFaculty().getTitle();
        facultyTextView.setText(faculty);

        String chug = chugTextView.getText() + " " + currentStudent.getChugId() + " " + dataBase.getCurrentChug().getTitle();
        chugTextView.setText(chug);

        String maslul = maslulTextView.getText() + " " + currentStudent.getMaslulId() + " " + dataBase.getCurrentMaslul().getTitle();
        maslulTextView.setText(maslul);

        String degree = currentStudent.getDegree();
        if (degree != null){
            degree = degreeTextView.getText() + " " + currentStudent.getDegree();
            degreeTextView.setText(degree);
        }

        String year = currentStudent.getYear();
        if (year != null) {
            year = yearTextView.getText() + " " + currentStudent.getYear();
            yearTextView.setText(year);
        }

        // Update points on screen
        textViewTotalPoints = view.findViewById(R.id.textViewTotalPoints);
        String points_desc = textViewTotalPoints.getText().toString();
        int currentPointsSum = dataBase.getCurrentPointsSum();
        String text = textViewTotalPoints.getText() + " " + currentPointsSum;
        textViewTotalPoints.setText(text);

        Maslul currentMaslul = dataBase.getCurrentMaslul();
        // Total
        String totalPoints = currentMaslul.getTotalPoints();
        textViewTotalPointsDegree = view.findViewById(R.id.textViewTotalPointsDegree);
        String text1 = textViewTotalPointsDegree.getText() + " " + totalPoints;
        textViewTotalPointsDegree.setText(text1);

        // Hova
        textViewTotalHovaPoints = view.findViewById(R.id.textViewTotalHovaPoints);
        int currentMandatoryChoosePoints = dataBase.getCurrentMandatoryChoosePoints();


        int currentMandatoryPoints = dataBase.getCurrentMandatoryPoints();

        String text5 = textViewTotalHovaPoints.getText() + " " + currentMandatoryPoints + " " + getResources().getString(R.string.outof)
                 +  " " + currentMaslul.getMandatoryPointsTotal();
        textViewTotalHovaPoints.setText(text5);

        // Choose hove
        textViewTotalHovaChoosePoints = view.findViewById(R.id.textViewTotalHovaChoosePoints);
        textViewTotalChoosePoints = view.findViewById(R.id.textViewTotalChoosePoints);
        textViewTotalSuppPoints = view.findViewById(R.id.textViewTotalSuppPoints);
        textViewTotalCornerStonePoints = view.findViewById(R.id.textViewTotalCornerStonePoints);

        int totalChoose = 16;

        String text4 = textViewTotalCornerStonePoints.getText() + " " + currentMaslul.getCornerStonesPoints();
        textViewTotalCornerStonePoints.setText(text4);

        int currentChoosePoints = dataBase.getCurrentChoosePoints();
        int currentSuppPoints = dataBase.getCurrentSuppPoints();
        int currentCornerPoints = dataBase.getCurrentCornerStonePoints();

        String text3 = textViewTotalHovaChoosePoints.getText() + " " + currentMandatoryChoosePoints + " " + getResources().getString(R.string.outof)
                + " " + currentMaslul.getMandatoryChoicePoints();
        textViewTotalHovaChoosePoints.setText(text3);

        String text6 = textViewTotalChoosePoints.getText() + " " + currentChoosePoints + " ";
        textViewTotalChoosePoints.setText(text6);

        double average = calculateAverage();
        String averageText = getResources().getString(R.string.average) + " " + average;
        averageTxt.setText(averageText);

        ArrayList<String> coursesOfStudentById = currentStudent.getCourses();
        ArrayList<Course> coursesFromFireBase = new ArrayList<>();
        ArrayList<Course> coursesForAdapter = new ArrayList<>();

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // upload grades to firebase
                Toast.makeText(getContext(), getResources().getString(R.string.savedsuccessfully), Toast.LENGTH_LONG).show();
                calculateAverage();
                dataBase.uploadGrades();
            }
        });

        adapter.setOnPopUpListener(new CoursesAdapter.OnPopUpApproveListener() {
            @Override
            public void OnPopUpClick(String item_number, String grade) {

                try {
                    int gradeNumber = Integer.parseInt(grade);
                    if (gradeNumber < 0 || gradeNumber > 100){
                        Toast.makeText(getContext(), getResources().getString(R.string.invalidGrade), Toast.LENGTH_LONG).show();
                    }
                    else {
                        dataBase.updateGrade(item_number, grade);
                        double average = calculateAverage();
                        String averageText = getResources().getString(R.string.average) + " " + average;
                        averageTxt.setText(averageText);
                        Toast.makeText(getContext(), getResources().getString(R.string.gradeAdded), Toast.LENGTH_LONG).show();

                        ArrayList<Course> coursesOfCurrentStudent = dataBase.getCoursesOfCurrentStudent();
                        adapter.addCoursesListToAdapter(coursesOfCurrentStudent);

                        adapter.notifyDataSetChanged();
                    }
                }
                catch (Exception e){
                    if (!grade.equals("")) {
                        Toast.makeText(getContext(), getResources().getString(R.string.invalidGrade), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        adapter.setGradeListener(new CoursesAdapter.AddGradeListener() {
            @Override
            public void onAddGradeClick(Course item, String grade) {
                if (addGradeListener != null) {
                    viewModelAppMainScreen.set(item);

                    try {
                        int gradeNumber = Integer.parseInt(grade);
                        if (gradeNumber < 0 || gradeNumber > 100){
                            Toast.makeText(getContext(), getResources().getString(R.string.invalidGrade), Toast.LENGTH_LONG).show();
                        }
                        else {
                            double average = calculateAverage();
                            String averageText = getResources().getString(R.string.average) + " " + average;
                            averageTxt.setText(averageText);
                        }
                    }
                    catch (Exception e){
                        if (!grade.equals("")) {
                            Toast.makeText(getContext(), getResources().getString(R.string.invalidGrade), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });

        addCourseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCourseListener.addCourseBtnClicked();
            }
        });

        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                    adapter.getFilter().filter(newText);
                return false;
            }
        });

        ArrayAdapter arrayAdapter = new ArrayAdapter(requireContext(), R.layout.dropdowntypeitem, getResources().getStringArray(R.array.courseType));
        arrayAdapter.getFilter().filter("");
        binding.autocompletechoosetype1.setAdapter(arrayAdapter);
        autocompletechoosetype.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedValue = (String)(parent.getItemAtPosition(position));
                if (selectedValue.equals("הכל")){
                    ArrayList<Course> list = dataBase.getCoursesOfCurrentStudent();
                    adapter.addCoursesListToAdapter(list);
                    adapter.notifyDataSetChanged();
                    arrayAdapter.getFilter().filter("");
                    binding.autocompletechoosetype1.setAdapter(arrayAdapter);
                }
                else {
                    arrayAdapter.getFilter().filter("");
                    ArrayList<Course> newC = new ArrayList<>();

                    ArrayList<Course> democ = dataBase.getCoursesOfCurrentStudent();
                    for (Course demo : democ) {
                        if (demo.getType().equals(selectedValue)) {
                            newC.add(demo);
                        }
                    }

                    // Create the adapter
                    adapter.addCoursesListToAdapter(newC);
                    adapter.notifyDataSetChanged();
                }
            }
        });

        if (dataBase == null){
            dataBase = HujiAssistentApplication.getInstance().getDataBase();
        }

        if (holder == null) {
            holder = new CourseItemHolder(recyclerViewMyCourses);
        }

        StudentInfo currentUser = dataBase.getCurrentUser();

        try {
            Task<DocumentSnapshot> document = firebaseInstancedb.collection("students").document(currentUser.getId())
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot result = task.getResult();
                            assert result != null;
                            StudentInfo data = result.toObject(StudentInfo.class);
                            assert data != null;
                            coursesId = new ArrayList<>(data.getCourses());
                            dataBase.setCurrentStudent(data);
                        }
                    });
        }
        catch (Exception e){
//            Log.i("ERROR", "failed to get the courses of the student");
        }

        ArrayList<String> coursesIds = dataBase.getCurrentUser().getCourses();
        // get the courses from fire store
        ArrayList<Course> courseItems = dataBase.getCoursesOfCurrentStudent();

        // Create the adapter
        adapter.addCoursesListToAdapter(courseItems);
        adapter.notifyDataSetChanged();
        recyclerViewMyCourses.setAdapter(adapter);

        coordinatorLayout = new LinearLayoutManager(getContext(),
                RecyclerView.VERTICAL, false);

        recyclerViewMyCourses.setLayoutManager(new LinearLayoutManager(getContext(),
                RecyclerView.VERTICAL, false));

        adapter.setItemClickListener(new CoursesAdapter.OnItemClickListener() {
            @Override
            public void onClick(Course item) {
                if (onItemClickListener != null){
                    viewModelAppMainScreen.set(item);
                    onItemClickListener.onClick(item);
                }
            }
        });

        adapter.setDeleteListener(new CoursesAdapter.DeleteClickListener() {
            @Override
            public void onDeleteClick(View v, Course item) {
                if (deleteClickListener != null){
                    deleteClickListener.onDeleteClick(v, item);
                    DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE: {
                                dataBase.removeCourseFromCurrentList(item.getNumber()); // remove from courses list in db and upload to firebase
                                // Calculate points
                                ArrayList<Course> courseItems = dataBase.getCoursesOfCurrentStudent();
                                dataBase.removeCourseGrade(item.getNumber());

                                double average = calculateAverage();
                                String averageText = getResources().getString(R.string.average) + " " + average;
                                averageTxt.setText(averageText);

                                int newPoints = calculateNewPointsSum(item.getPoints());
                                String text = points_desc + " " + newPoints;
                                textViewTotalPoints.setText(text);

                                adapter.addCoursesListToAdapter(courseItems);
                                adapter.notifyDataSetChanged();
                                Maslul currentMaslul = dataBase.getCurrentMaslul();

                                int pointsToReduce = Integer.parseInt(item.getPoints()); // Get the number of points to reduce
                                String type = item.getType();
                                switch (type){
                                    case "לימודי חובה":
                                        int currentMandatoryPoints1 = dataBase.getCurrentMandatoryPoints();
                                        int newMandatoryPoints = currentMandatoryPoints1 - pointsToReduce;
                                        String textToSet = getResources().getString(R.string.mandatory) +" " + newMandatoryPoints + " " +getResources().getString(R.string.outof) +  " " + currentMaslul.getMandatoryPointsTotal();
                                        textViewTotalHovaPoints.setText(textToSet);
                                        dataBase.setCurrentMandatoryPoints(newMandatoryPoints);
                                        break;
                                    case "לימודי חובת בחירה":
                                        int currentMandatoryChoosePoints1 = dataBase.getCurrentMandatoryChoosePoints();
                                        int newMandatoryChoosePoints = currentMandatoryChoosePoints1 - pointsToReduce;
                                        textToSet = getResources().getString(R.string.mandatorychoose) +" "+ newMandatoryChoosePoints +  " " +getResources().getString(R.string.outof) +  " " + currentMaslul.getMandatoryChoicePoints();
                                        textViewTotalHovaChoosePoints.setText(textToSet);
                                        dataBase.setCurrentMandatoryChoosePoints(newMandatoryChoosePoints);
                                        break;
                                    case "קורסי בחירה":
                                        int currentChoosePoints1 = dataBase.getCurrentChoosePoints();
                                        int newChoosePoints = currentChoosePoints1 - pointsToReduce;
                                        textToSet = getResources().getString(R.string.choose) +" " + newChoosePoints;
                                        textViewTotalChoosePoints.setText(textToSet);
                                        dataBase.setCurrentChoosePoints(newChoosePoints);
                                        break;
                                    case "משלימים":
                                        int currentSuppPoints1 = dataBase.getCurrentSuppPoints();
                                        int newSuppPoints = currentSuppPoints1 - pointsToReduce;
                                        textToSet = getResources().getString(R.string.suppcourses) +" " + newSuppPoints;
                                        textViewTotalSuppPoints.setText(textToSet);
                                        dataBase.setCurrentSuppPoints(newSuppPoints);
                                        break;
                                    case "אבני פינה":
                                        int currentCornerStonePoints1 = dataBase.getCurrentCornerStonesPoints();
                                        int newCSPoints = currentCornerStonePoints1 - pointsToReduce;
                                        textToSet =getResources().getString(R.string.cornerstonescourses) +" " + newCSPoints;
                                        textViewTotalCornerStonePoints.setText(textToSet);
                                        dataBase.setCurrentCornerStonesPoints(newCSPoints);
                                        break;
                                    default:
                                        break;
                                } // end of switch case
                                break;
                            }
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(getResources().getString(R.string.delete_course_alert))
                            .setPositiveButton(R.string.positive_answer, dialogClickListener)
                            .setNegativeButton(R.string.negative_answer, dialogClickListener).show();
                }
            }
        });
    }

    private void printCourses(){
        for (int i = 0; i < coursesId.size(); i++){
        }
    }

    private int calculateNewPointsSum(String points){
        int currentPointsSum = dataBase.getCurrentPointsSum();
        int pointsToRemove = Integer.parseInt(points);
        int newPoints = currentPointsSum - pointsToRemove;
        dataBase.setCurrentPointsSum(newPoints);
        return newPoints;
    }

    private double calculateAverage(){
        // If there are no grades, return 0
        if (dataBase.getGradesOfStudent().size() == 0){
            return 0;
        }
        double average = 0;
        int numberOfCourses = 0;
        int totalPointsSum = 0;

        // else, get the grades and calculate average
        ArrayList<Course> coursesOfCurrentStudent = dataBase.getCoursesOfCurrentStudent();
        // number should be the number of courses that have a grade
        numberOfCourses = dataBase.getGradesOfStudent().size();

        for (Course course : coursesOfCurrentStudent){
            int calculated = 0;
            int points = Integer.parseInt(course.getPoints()); // 5
            String gradeStr = dataBase.getGradesOfStudent().get(course.getNumber());
        if ((gradeStr != null) && (!(gradeStr.equals("")))) {
                int grade = Integer.parseInt(gradeStr);
                calculated = points * grade;
                average += calculated;
                totalPointsSum += points;
            }
        }

        double averageCalculation = Math.round((double)(average / totalPointsSum));
        return averageCalculation;
    }

    public void collectInput(EditText txt){
        // convert edit text to string
        String getInput = txt.getText().toString();

        // ensure that user input bar is not empty
        if (getInput ==null || getInput.trim().equals("")){
            Toast.makeText(getContext(), "Please add a group name", Toast.LENGTH_LONG).show();
        }
        // add input into an data collection array list
        else {
            arrayListCollection.add(getInput);
            adapter.notifyDataSetChanged();
        }
    }
}
