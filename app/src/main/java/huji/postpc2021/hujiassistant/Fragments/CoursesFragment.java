package huji.postpc2021.hujiassistant.Fragments;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
//import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import huji.postpc2021.hujiassistant.Chug;
import huji.postpc2021.hujiassistant.Course;
import huji.postpc2021.hujiassistant.CourseItemHolder;
import huji.postpc2021.hujiassistant.CoursesAdapter;
import huji.postpc2021.hujiassistant.Faculty;
import huji.postpc2021.hujiassistant.HujiAssistentApplication;
import huji.postpc2021.hujiassistant.LocalDataBase;
import huji.postpc2021.hujiassistant.Maslul;
import huji.postpc2021.hujiassistant.MobileVersionFetcher;
import huji.postpc2021.hujiassistant.R;
import huji.postpc2021.hujiassistant.StudentInfo;
import huji.postpc2021.hujiassistant.SwipeViewHolderItem;
import huji.postpc2021.hujiassistant.ViewModelApp;
import huji.postpc2021.hujiassistant.ViewModelAppMainScreen;
import huji.postpc2021.hujiassistant.databinding.FragmentCoursesBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class CoursesFragment extends Fragment {
    private ViewModelApp viewModelApp;
    private ViewModelAppMainScreen viewModelAppCourse;
    FragmentCoursesBinding binding;
    public CourseItemHolder holder = null;
    public CoursesAdapter adapter = null;
    private LocalDataBase db = null;

    public interface endRegistrationButtonClickListener {
        public void onEndRegistrationBtnClicked();
    }
    public CoursesFragment.addGradeListener addGradeListener = null;
    private String email;
    private String password;
    private String personalName;
    private String familyName;
    FirebaseFirestoreSettings settings;
    androidx.appcompat.widget.SearchView searchView;
    String facultyId;
    String chugId;
    String maslulId;
    String degreeType; // first degree, second degree...
    String year;
    String beginnigYearOfDegree;
    String beginSemesterOfDegree;
    String grade;
    TextView facultyTextView;
    StudentInfo currentStudent;
    TextView chugTextView;
    TextView maslulTextView;
    TextView yearTextView;
    TextView degreeTextView;
    TextView addGradeBtn;
    RecyclerView recyclerViewCourses;
    LinearLayoutManager coordinatorLayout;
    AutoCompleteTextView dropdowntype;

    public interface addGradeListener{
        public void onAddGradeClicked(Course item);
    }
    // FirebaseFirestore firebaseInstancedb = HujiAssistentApplication.getInstance().getDataBase().getFirestoreDB();
    FirebaseFirestore firebaseInstancedb = FirebaseFirestore.getInstance();

    ArrayList<String> coursesOfStudent = new ArrayList<>(); // todo save in db to pass between activities

    public CoursesFragment.endRegistrationButtonClickListener endRegistrationBtnListener = null;
    public CoursesAdapter.OnItemClickListener onItemClickListener = null;
    public  CoursesAdapter.AddGradeListener addGradeListenerAdapter = null;
    public CoursesAdapter.OnCheckBoxClickListener onCheckBoxClickListener = null;

    public CoursesFragment() {
        super(R.layout.fragment_courses);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCoursesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModelApp = new ViewModelProvider(requireActivity()).get(ViewModelApp.class);
        viewModelAppCourse = new ViewModelProvider(requireActivity()).get(ViewModelAppMainScreen.class);
        facultyTextView = view.findViewById(R.id.faculty);
        chugTextView = view.findViewById(R.id.chugName);
        maslulTextView = view.findViewById(R.id.maslulName);
        yearTextView = view.findViewById(R.id.year);
        degreeTextView = view.findViewById(R.id.degreeType);
        recyclerViewCourses = view.findViewById(R.id.recycleViewCourses);
        adapter = new CoursesAdapter(getContext());
        dropdowntype = view.findViewById(R.id.autocompletechoosetypeRegisterScreen);
        searchView = view.findViewById(R.id.searchCoursesScreen);

        if (holder == null) {
            holder = new CourseItemHolder(recyclerViewCourses);
        }

        ArrayList<Course> courseItems = new ArrayList<>(); // Saves the current courses list

        if (db == null) {
            db = HujiAssistentApplication.getInstance().getDataBase();
        }
        settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firebaseInstancedb.setFirestoreSettings(settings);

        viewModelApp.getStudent().observe(getViewLifecycleOwner(), item -> {
            email = item.getEmail();
            password = item.getPassword();
            personalName = item.getPersonalName();
            familyName = item.getFamilyName();
            facultyId = item.getFacultyId();
            chugId = item.getChugId();
            maslulId = item.getMaslulId();
            degreeType = item.getDegree();
            year = item.getYear();
            beginnigYearOfDegree = item.getBeginYear();
            beginSemesterOfDegree = item.getBeginSemester();

            try {
                // Get the faculty
                Task<DocumentSnapshot> faculties1 = firebaseInstancedb.collection("faculties").document(facultyId)
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                DocumentSnapshot result = task.getResult();
                                assert result != null;
                                Faculty data = result.toObject(Faculty.class);
                                assert data != null;
                                String facultyName = data.getTitle();
                                String text = facultyName + " " + facultyId;
                                facultyTextView.setText(text);
                                db.setCurrentFaculty(data);
                            }
                        });
            } catch (Exception e) {
//                Log.i("ERROR", "failed to get faculties");
            }

            String COLLECTION = "coursesTestOnlyCs";
            try {
                // Get the chug
                Task<DocumentSnapshot> chugim1 = firebaseInstancedb.collection(COLLECTION).document(chugId)
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                DocumentSnapshot result = task.getResult();
                                assert result != null;
                                Chug data = result.toObject(Chug.class);
                                assert data != null;
                                String chugName = data.title;
                                String text = chugName + " " + chugId;
                                chugTextView.setText(text);
                                db.setCurrentChug(data);
                            }
                        });
            } catch (Exception e) {
//                Log.i("ERROR", "failed to get chugim");
            }

            try {
                // Get the maslul
                Task<DocumentSnapshot> maslulim1 = firebaseInstancedb.collection(COLLECTION).document(chugId)
                        .collection("maslulimInChug").document(maslulId)
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                DocumentSnapshot result = task.getResult();
                                assert result != null;
                                Maslul data = result.toObject(Maslul.class);
                                assert data != null;
                                String maslulName = data.getTitle();
                                String text = maslulName + " " + maslulId;
                                maslulTextView.setText(text);
                                db.setCurrentMaslul(data);
                            }
                        });
            } catch (Exception e) {
//                Log.i("ERROR", "failed to get maslulim");
            }
            degreeTextView.setText(degreeType);
            yearTextView.setText(year);

            ArrayList<Course> coursesInMaslul = new ArrayList<>();
            String ROOT_COLLECTION = "coursesTestOnlyCs";

            try {
                // Get the courses
                Task<QuerySnapshot> document = firebaseInstancedb.collection(ROOT_COLLECTION).document(chugId)
                        .collection("maslulimInChug").document(maslulId).collection("coursesInMaslul")
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                List<DocumentSnapshot> documents = task.getResult().getDocuments();

                                for (DocumentSnapshot document1 : documents) {
                                    // retrieve for each chug id it's name
                                    Course course = document1.toObject(Course.class);
                                    assert course != null;
                                    coursesInMaslul.add(course);
                                }

                                db.setCoursesRegistration(coursesInMaslul);
                                ArrayList<Course> sortedCoursesInMaslul = db.sortCoursesByYearAndType(coursesInMaslul);
                                adapter.addCoursesListToAdapter(sortedCoursesInMaslul);
                                adapter.notifyDataSetChanged();
                            }
                        });
            } catch (Exception e) {
//                Log.i("ERROR", "failed to get courses");
            }

            recyclerViewCourses.setAdapter(adapter);

            coordinatorLayout = new LinearLayoutManager(getContext(),
                    RecyclerView.VERTICAL, false);

            recyclerViewCourses.setLayoutManager(new LinearLayoutManager(getContext(),
                    RecyclerView.VERTICAL, false));
        }); // end of observe

        // Search filter
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
        binding.autocompletechoosetypeRegisterScreen.setAdapter(arrayAdapter);

        dropdowntype.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedValue = (String) (parent.getItemAtPosition(position));
                if (selectedValue.equals("הכל")) {
                    ArrayList<Course> list = db.getCoursesRegistration();
                    ArrayList<Course> coursesSorted = db.sortCoursesByYearAndType(list);
                    adapter.addCoursesListToAdapter(coursesSorted);
                    adapter.notifyDataSetChanged();
                    arrayAdapter.getFilter().filter("");
                    binding.autocompletechoosetypeRegisterScreen.setAdapter(arrayAdapter);

                } else {
                    arrayAdapter.getFilter().filter("");
                    ArrayList<Course> newC = new ArrayList<>();

                    ArrayList<Course> democ = db.getCoursesRegistration();
                    for (Course demo : democ) {
                        if (demo.getType().equals(selectedValue)) {
                            newC.add(demo);
                        }
                    }

                    // Create the adapter
                    ArrayList<Course> newList = db.sortCoursesByYearAndType(newC);
                    adapter.addCoursesListToAdapter(newList);
                    adapter.notifyDataSetChanged();
                    //  recyclerViewMyCourses.setAdapter(adapter);
                }
            }
        });

        ArrayAdapter arrayAdapter1 = new ArrayAdapter(requireContext(), R.layout.dropdowntypeitem, getResources().getStringArray(R.array.coursePoints));
        arrayAdapter1.getFilter().filter("");
        arrayAdapter.getFilter().filter("");

        adapter.setGradeListener(new CoursesAdapter.AddGradeListener() {
            @Override
            public void onAddGradeClick(Course item, String grade) {
                if (addGradeListener != null) {
                    viewModelAppCourse.set(item);
                    // addGradeListener.onAddGradeClicked(item);
                }
            }
        });

        adapter.setItemClickListener(new CoursesAdapter.OnItemClickListener() {
            @Override
            public void onClick(Course item) {
                if (onItemClickListener != null) {
                }
            }
        });

        adapter.setItemCheckBoxListener(new CoursesAdapter.OnCheckBoxClickListener() {
            @Override
            public void onCheckBoxClicked(View v, Course item) {
                if (onCheckBoxClickListener != null) {

                    if (item.getChecked()) {
                        if (!coursesOfStudent.contains(item.getNumber())) {
                            coursesOfStudent.add(item.getNumber());
                            db.setCoursesRegistrationById(coursesOfStudent);

                            String text = getActivity().getResources().getString(R.string.course_number_txt) + " " +
                                    item.getNumber() + " " + getActivity().getResources().getString(R.string.added_to_the_list_of_courses);
                            item.setChecked(true);
                            Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();

                        } else {
                            String text = getActivity().getResources().getString(R.string.course_number_txt) + " "
                                    + item.getNumber() + " " + getActivity().getResources().getString(R.string.exists_in_the_list_of_courses);
                            item.setChecked(true);
                            Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        coursesOfStudent.remove(item.getNumber());
                        db.setCoursesRegistrationById(coursesOfStudent);
                        item.setChecked(false);
                        String text = getActivity().getResources().getString(R.string.course_number_txt) + " "
                                + item.getNumber() + " " + getActivity().getResources().getString(R.string.removed_from_the_list_of_courses);
                        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
                    }
                    // printCourses();
                    onCheckBoxClickListener.onCheckBoxClicked(v, item);
                }
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(false) {
            @Override
            public void handleOnBackPressed() {
            }
        };

        viewModelApp.studentInfoMutableLiveData.observe(getViewLifecycleOwner(), new Observer<StudentInfo>() {
            @Override
            public void onChanged(StudentInfo studentInfo) {
                currentStudent = viewModelApp.studentInfoMutableLiveData.getValue();
            }
        });

        Button endRegistrationBtn = view.findViewById(R.id.buttonEndRegistration);
        endRegistrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (endRegistrationBtnListener != null) {

                    if (db.getCoursesToProcess()!=null){
                        MobileVersionFetcher fetcher = new MobileVersionFetcher();
                        fetcher.execute("details",chugId, maslulId);
                    }


                    FirebaseAuth auth = db.getUsersAuthenticator();
                    auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
//                                        Log.d("RegisterActivity", "registerWithEmail:success");
                                        Toast.makeText(getActivity(), R.string.register_Successfully_message, Toast.LENGTH_LONG).show();
                                        FirebaseUser user = auth.getCurrentUser();

                                        //  printCourses();
                                        assert user != null;

                                        StudentInfo newStudent = new StudentInfo(user.getUid(), email, personalName, familyName,
                                                facultyId, chugId, maslulId, degreeType, year, beginnigYearOfDegree, beginSemesterOfDegree,
                                                coursesOfStudent);

                                        db.addStudent(user.getUid(), email, personalName, familyName,
                                                facultyId, chugId, maslulId, degreeType, year, beginnigYearOfDegree, beginSemesterOfDegree,
                                                coursesOfStudent);

                                        db.setCurrentUser(user);
                                        db.setCurrentStudent(newStudent);
                                        endRegistrationBtnListener.onEndRegistrationBtnClicked();

                                    } else {
//                                        Log.w("RegisterActivity", "registerWithEmail:failure", task.getException());
                                        Toast.makeText(getActivity(), R.string.register_failed_message, Toast.LENGTH_LONG).show();                                            //todo: don't allow to continue
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
//                            Log.i("ERROR", "failed to register");
                        }
                    });
                }
            }
        });
    } // end of on create view

    private void printCourses() {
        for (int i = 0; i < coursesOfStudent.size(); i++) {
            // System.out.println(coursesOfStudent.get(i));
        }
    }
}