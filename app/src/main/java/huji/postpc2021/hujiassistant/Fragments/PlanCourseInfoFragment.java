package huji.postpc2021.hujiassistant.Fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import huji.postpc2021.hujiassistant.databinding.FragmentPlancoursesBinding;
import huji.postpc2021.hujiassistant.CourseScheduleEntry;
import huji.postpc2021.hujiassistant.HujiAssistentApplication;
import huji.postpc2021.hujiassistant.KdamCoursesAdapter;
import huji.postpc2021.hujiassistant.KdamOrAfterCourse;
import huji.postpc2021.hujiassistant.LocalDataBase;
import huji.postpc2021.hujiassistant.R;
import huji.postpc2021.hujiassistant.ScheduleAdapter;
import huji.postpc2021.hujiassistant.ViewModelAppMainScreen;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
    Shows the details of a course item in plan courses fragment
    (Prev courses and schedule)
 */
public class PlanCourseInfoFragment extends Fragment {

    private ViewModelAppMainScreen viewModelAppMainScreen;
    String courseName;
    String courseNumber;
    String coursePoints;
    String type;
    String semester;
    String year;
    TextView courseNameTextView;
    TextView courseNumberTextView;
    TextView courseTypeTextView;
    TextView coursePointsTextView;
    TextView courseYearTextView;
    TextView courseSemesterTextView;
    ProgressBar kdamLoadingBar;
    ProgressBar scheduleLoadingBar;
    public ScheduleAdapter scheduleAdapter = null;
    public KdamCoursesAdapter.OnCheckBoxClickListener onCheckBoxClickListener = null;

    private LocalDataBase db = null;
    FirebaseFirestore firebaseInstancedb = FirebaseFirestore.getInstance();
    RecyclerView recyclerViewKdamCourses;
    RecyclerView recyclerViewSchedule;
    FirebaseFirestoreSettings settings;
    LinearLayoutManager coordinatorLayout1;
    LinearLayoutManager coordinatorLayout2;
    String ROOT_COLLECTION = "coursesTestOnlyCs";
    TextView noKdamCoursesTextView;
    TextView noScheduleTextView;
    KdamCoursesAdapter kdamCoursesAdapter;

    public PlanCourseInfoFragment() {
        super(R.layout.plancourseinfo_fragment);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        courseNameTextView = view.findViewById(R.id.planCourseInfoName);
        courseNumberTextView = view.findViewById(R.id.planCourseInfoNumber);
        courseTypeTextView = view.findViewById(R.id.planCourseInfoType1);
        coursePointsTextView = view.findViewById(R.id.planCourseInfoPoints1);
        courseYearTextView = view.findViewById(R.id.planCourseInfoYear1);
        courseSemesterTextView = view.findViewById(R.id.planCourseInfoSemester1);
        kdamLoadingBar = view.findViewById(R.id.kdamProgressBar);
        scheduleLoadingBar = view.findViewById(R.id.scheduleProgressBar);
        noKdamCoursesTextView = view.findViewById(R.id.planNoKdamCoursesTextView);
        noScheduleTextView = view.findViewById(R.id.planScheduleTextView);
        recyclerViewSchedule = view.findViewById(R.id.planAftercoursessrecycleview);

        recyclerViewKdamCourses = view.findViewById(R.id.planKdamcoursessrecycleview);
        kdamCoursesAdapter = new KdamCoursesAdapter(getContext());
        viewModelAppMainScreen = new ViewModelProvider(requireActivity()).get(ViewModelAppMainScreen.class);
        scheduleAdapter = new ScheduleAdapter(); // schedule Adapter


        if (db == null) {
            db = HujiAssistentApplication.getInstance().getDataBase();
        }

        settings = new FirebaseFirestoreSettings.Builder().setPersistenceEnabled(true).build();
        firebaseInstancedb.setFirestoreSettings(settings);

        viewModelAppMainScreen.get().observe(getViewLifecycleOwner(), item -> {
            courseName = item.getName();
            courseNumber = item.getNumber();
            coursePoints = item.getPoints();
            type = item.getType();
            semester = item.getSemester();
            year = item.getYear();
            String name = courseNameTextView.getText() + " " + courseName;
            courseNameTextView.setText(name);
            String number = courseNumberTextView.getText() + " " + courseNumber;
            courseNumberTextView.setText(number);
            courseTypeTextView.setText(courseTypeTextView.getText() + ": " + type);
            coursePointsTextView.setText(coursePointsTextView.getText() + ": " + coursePoints);
            courseYearTextView.setText(courseYearTextView.getText() + ": " + year);
            courseSemesterTextView.setText(courseSemesterTextView.getText() + ": " + semester);

            kdamLoadingBar.setVisibility(View.VISIBLE);
            scheduleLoadingBar.setVisibility(View.VISIBLE);


            getKdamCourses();
            getSchedule();

        });
    }

    public void getKdamCourses() {

        try {
            Task<QuerySnapshot> document = firebaseInstancedb.collection(ROOT_COLLECTION).document(db.getCurrentStudent().getChugId())
                    .collection("maslulimInChug").document(db.getCurrentStudent().getMaslulId()).collection("coursesInMaslul")
                    .document(courseNumber)
                    .collection("kdamCourses")
                    .get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> documents = task.getResult().getDocuments();
                            ArrayList<KdamOrAfterCourse> kdamCourses = new ArrayList<>();

                            for (DocumentSnapshot document1 : documents) {
                                KdamOrAfterCourse course = document1.toObject(KdamOrAfterCourse.class);
                                assert course != null;
                                kdamCourses.add(course);
                            }
                            kdamLoadingBar.setVisibility(View.INVISIBLE);

                            kdamCoursesAdapter.addKdamCoursesListToAdapter(kdamCourses);
                            recyclerViewKdamCourses.setAdapter(kdamCoursesAdapter);

                            coordinatorLayout1 = new LinearLayoutManager(getContext(),
                                    RecyclerView.VERTICAL, false);

                            recyclerViewKdamCourses.setLayoutManager(new LinearLayoutManager(getContext(),
                                    RecyclerView.VERTICAL, false));

                            if (kdamCourses.isEmpty()) {
                                noKdamCoursesTextView.setVisibility(View.VISIBLE);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            noKdamCoursesTextView.setVisibility(View.VISIBLE);
                        }
                    });
        } catch (Exception e) {
            noKdamCoursesTextView.setVisibility(View.VISIBLE);
        }
    }


    public void getSchedule() {
        // this is really ugly code and need to be fixed, the FireStore Database Schedule section need to be rebuilt
        ArrayList<String> types = new ArrayList<>(Arrays.asList("שעור", "תרגיל", "סמינר", "מעבדה", "סמינר ושעור", "סמינר ותרגול"));
        ArrayList<String> groups = new ArrayList<>(Arrays.asList("א'", "ב'", "ג'", "ד'", "ה'", "ו'", "ז'", "ח'", "ט'", "י'"));
        ArrayList<String> days = new ArrayList<>(Arrays.asList("א", "ב", "ג", "ד", "ה", "ו"));
        ArrayList<CourseScheduleEntry> coursesSchedule = new ArrayList<>();

        try {
            for (String type : types) {
                for (String group : groups) {
                    for (String day : days) {

                        firebaseInstancedb.collection(ROOT_COLLECTION).document(db.getCurrentStudent().getChugId())
                                .collection("maslulimInChug").document(db.getCurrentStudent().getMaslulId()).collection("coursesInMaslul")
                                .document(courseNumber)
                                .collection("schedule").document(type).collection(group).document(day)
                                .get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                if (task.getResult().exists()) {
                                    CourseScheduleEntry entry = task.getResult().toObject(CourseScheduleEntry.class);
                                    coursesSchedule.add(entry);
                                    scheduleLoadingBar.setVisibility(View.INVISIBLE);
                                    noScheduleTextView.setVisibility(View.INVISIBLE);
                                }
                            }

                            scheduleAdapter.addScheduleListToAdapter(coursesSchedule);
                            recyclerViewSchedule.setAdapter(scheduleAdapter);

                            coordinatorLayout2 = new LinearLayoutManager(getContext(),
                                    RecyclerView.VERTICAL, false);

                            recyclerViewSchedule.setLayoutManager(new LinearLayoutManager(getContext(),
                                    RecyclerView.VERTICAL, false));


                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                noScheduleTextView.setVisibility(View.VISIBLE);
                            }
                        });

                    }
                }
            }
        } catch (Exception e) {
            noScheduleTextView.setVisibility(View.VISIBLE);
        }

    }
}