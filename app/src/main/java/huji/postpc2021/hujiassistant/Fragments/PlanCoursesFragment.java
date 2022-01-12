package huji.postpc2021.hujiassistant.Fragments;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class PlanCoursesFragment extends Fragment {
    private ViewModelAppMainScreen viewModelAppMainScreen;
    public PlanCoursesAdapter adapter = null;
    public LocalDataBase dataBase = null;
    public PlanCoursesAdapter.OnCheckBoxClickListener onCheckBoxClickListener = null;
    public PlanCoursesAdapter.OnItemClickListener onItemClickListener = null;
    private ArrayList<Course> coursesToShow = null;
    private String filterYear = null;
    private String filterSemester = null;
    private String filterPoints = null;

    FirebaseFirestoreSettings settings;
    FragmentPlancoursesBinding binding;
    LinearLayoutManager coordinatorLayout;
    RecyclerView recyclerViewMyCourses;
    FirebaseFirestore firebaseInstancedb = FirebaseFirestore.getInstance();
    SharedPreferences sp = null;

    TextView studentNameTextView;
    TextView facultyTextView;
    TextView chugTextView;
    TextView maslulTextView;
    TextView degreeTextView;
    TextView yearTextView;
    Button approvePlannedBtn;
    AutoCompleteTextView autoCompleteChooseYear;
    AutoCompleteTextView autoCompleteChooseSemester;
    AutoCompleteTextView autoCompleteChoosePoints;
    CheckBox showOnlyChosePlanned_CheckBox;
    ArrayList<String> plannedCoursesOfStudent = new ArrayList<>(); // Saves the checked courses numbers

    public PlanCoursesFragment() {
        super(R.layout.fragment_plancourses);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPlancoursesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        facultyTextView = view.findViewById(R.id.nameOfFaculty);
        chugTextView = view.findViewById(R.id.nameOfChug);
        maslulTextView = view.findViewById(R.id.nameOfMaslul);
        degreeTextView = view.findViewById(R.id.nameOfDegree);
        yearTextView = view.findViewById(R.id.yearOfDegree);
        autoCompleteChooseYear = view.findViewById(R.id.autocompletechooseyear);
        autoCompleteChooseSemester = view.findViewById(R.id.autocompletechoosesemester);
        autoCompleteChoosePoints = view.findViewById(R.id.autocompletechoosepoints);
        recyclerViewMyCourses = view.findViewById(R.id.recycleViewMyCourses);
        showOnlyChosePlanned_CheckBox = view.findViewById(R.id.showPlanedOnlyCheckBox);
        studentNameTextView = view.findViewById(R.id.nameOfStudentTextView);
        viewModelAppMainScreen = new ViewModelProvider(requireActivity()).get(ViewModelAppMainScreen.class);
        adapter = new PlanCoursesAdapter(getContext());
        approvePlannedBtn = view.findViewById(R.id.approvePlannedCoursesBtn);

        if (dataBase == null) {
            dataBase = HujiAssistentApplication.getInstance().getDataBase();
        }

        if (coursesToShow == null) {
            coursesToShow = dataBase.getCoursesToComplete();
        }

        if (sp == null) {
            sp = dataBase.getSp();
        }

        settings = new FirebaseFirestoreSettings.Builder().setPersistenceEnabled(true).build();
        firebaseInstancedb.setFirestoreSettings(settings);

        StudentInfo currentStudent = dataBase.getCurrentUser();

        // Update data on screen
        String name = studentNameTextView.getText() + " " + currentStudent.getPersonalName() + " " + currentStudent.getFamilyName();
        studentNameTextView.setText(name);

        String faculty = facultyTextView.getText() + " " + currentStudent.getFacultyId() + " " + dataBase.getCurrentFaculty().getTitle();
        facultyTextView.setText(faculty);

        String chug = chugTextView.getText() + " " + currentStudent.getChugId() + " " + dataBase.getCurrentChug().getTitle();
        chugTextView.setText(chug);

        String maslul = maslulTextView.getText() + " " + currentStudent.getMaslulId() + " " + dataBase.getCurrentMaslul().getTitle();
        maslulTextView.setText(maslul);

        String degree = currentStudent.getDegree();
        if (degree != null) {
            degree = degreeTextView.getText() + " " + currentStudent.getDegree();
            degreeTextView.setText(degree);
        }

        String year = currentStudent.getYear();
        if (year != null) {
            year = yearTextView.getText() + " " + currentStudent.getYear();
            yearTextView.setText(year);
        }

        // Year Choose
        ArrayAdapter arrayYearAdapter = new ArrayAdapter(requireContext(), R.layout.dropdownfacultyitem, getResources().getStringArray(R.array.yearArray));
        arrayYearAdapter.getFilter().filter("");
        binding.autocompletechooseyear.setAdapter(arrayYearAdapter);

        autoCompleteChooseYear.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                filterYear = (String) (parent.getItemAtPosition(position));

                ArrayList<Course> list = dataBase.getCoursesToPlan(filterYear, filterSemester, filterPoints);
                coursesToShow = list;
                adapter.addCoursesListToAdapter(dataBase.sortCoursesByYearAndType(list));
                adapter.notifyDataSetChanged();
                if (list.size() == 0)
                    Toast.makeText(getContext(), getResources().getString(R.string.no_courses), Toast.LENGTH_SHORT).show();

                if (showOnlyChosePlanned_CheckBox.isChecked()) { // check if onlyShowPlaned is clicked, and if so response accordingly
                    showOnlyPlanedLogic();
                }
            }
        });

        // Semester Choose
        ArrayAdapter arraySemesterAdapter = new ArrayAdapter(requireContext(), R.layout.dropdownfacultyitem, getResources().getStringArray(R.array.semesterArray));
        arraySemesterAdapter.getFilter().filter("");
        binding.autocompletechoosesemester.setAdapter(arraySemesterAdapter);

        autoCompleteChooseSemester.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                filterSemester = ((String) (parent.getItemAtPosition(position)));
                if (!(filterSemester.equals("קורס שנתי") || filterSemester.equals("הכל"))) {
                    if (filterSemester.equals("סמסטר א או ב")) {
                        filterSemester = "א' או ב'";
                    } else filterSemester = filterSemester.substring(6) + "'";
                }

                dataBase.getCurrentStudent().getCourses(); // courses the student finished in string array
                ArrayList<Course> list = dataBase.getCoursesToPlan(filterYear, filterSemester, filterPoints);

                coursesToShow = list;
                adapter.addCoursesListToAdapter(dataBase.sortCoursesByYearAndType(list));
                adapter.notifyDataSetChanged();
                if (list.size() == 0)
                    Toast.makeText(getContext(), getResources().getString(R.string.no_courses), Toast.LENGTH_SHORT).show();

                if (showOnlyChosePlanned_CheckBox.isChecked()) { // check if onlyShowPlaned is clicked, and if so response accordingly
                    showOnlyPlanedLogic();
                }
            }
        });


        // Points Choose
        ArrayAdapter arrayPointsAdapter = new ArrayAdapter(requireContext(), R.layout.dropdownfacultyitem, getResources().getStringArray(R.array.pointsArray));
        arrayPointsAdapter.getFilter().filter("");
        binding.autocompletechoosepoints.setAdapter(arrayPointsAdapter);

        autoCompleteChoosePoints.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                filterPoints = ((String) (parent.getItemAtPosition(position))).substring(0, 1);
                ArrayList<Course> list = dataBase.getCoursesToPlan(filterYear, filterSemester, filterPoints);

                adapter.addCoursesListToAdapter(dataBase.sortCoursesByYearAndType(list));
                adapter.notifyDataSetChanged();
                if (list.size() == 0)
                    Toast.makeText(getContext(), getResources().getString(R.string.no_courses), Toast.LENGTH_SHORT).show();

                if (showOnlyChosePlanned_CheckBox.isChecked()) { // check if onlyShowPlaned is clicked, and if so response accordingly
                    showOnlyPlanedLogic();
                }
            }
        });


        // Create the adapter
        adapter.addCoursesListToAdapter(dataBase.sortCoursesByYearAndType(coursesToShow)); // courses List To Show --------------------------
        adapter.notifyDataSetChanged();
        recyclerViewMyCourses.setAdapter(adapter);

        if (showOnlyChosePlanned_CheckBox.isChecked()) { // check if onlyShowPlaned is clicked, and if so response accordingly
            showOnlyPlanedLogic();
        }

        coordinatorLayout = new LinearLayoutManager(getContext(),
                RecyclerView.VERTICAL, false);

        recyclerViewMyCourses.setLayoutManager(new LinearLayoutManager(getContext(),
                RecyclerView.VERTICAL, false));

        // opening the Course info fragment when clicking on a course
        adapter.setItemClickListener(new PlanCoursesAdapter.OnItemClickListener() {
            @Override
            public void onClick(Course item) {
                if (onItemClickListener != null) {
                    viewModelAppMainScreen.set(item);
                    onItemClickListener.onClick(item);
                }
            }
        });

        //adding new course to planned courses by checkbox
        adapter.setItemCheckBoxListener(new PlanCoursesAdapter.OnCheckBoxClickListener() {
            @Override
            public void onCheckBoxClicked(View v, Course item) {

                plannedCoursesOfStudent = dataBase.getCoursesPlannedById();

                if (item.getPlannedChecked()) {
                    if (!plannedCoursesOfStudent.contains(item.getNumber())) {
                        plannedCoursesOfStudent.add(item.getNumber());
                        // Adds the new checked course to the list of planned courses in database
                        dataBase.setCoursesPlannedById(plannedCoursesOfStudent);

                        String text = getActivity().getResources().getString(R.string.course_number_txt) + " " +
                                item.getNumber() + " " + getActivity().getResources().getString(R.string.added_to_the_list_of_courses);
                        item.setPlannedChecked(true);
                        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();

                    } else {
                        String text = getActivity().getResources().getString(R.string.course_number_txt) + " "
                                + item.getNumber() + " " + getActivity().getResources().getString(R.string.exists_in_the_list_of_courses);
                        item.setPlannedChecked(true);
                        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    plannedCoursesOfStudent.remove(item.getNumber());
                    // Removes the un-checked course from the list of planned courses in database
                    dataBase.setCoursesPlannedById(plannedCoursesOfStudent);
                    item.setPlannedChecked(false);
                    String text = getActivity().getResources().getString(R.string.course_number_txt) + " "
                            + item.getNumber() + " " + getActivity().getResources().getString(R.string.removed_from_the_list_of_courses);
                    Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
                }
            }
        });


        approvePlannedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> courses = dataBase.getCoursesPlannedById();
                Toast.makeText(getActivity(), R.string.savedsuccessfully, Toast.LENGTH_LONG).show();
                dataBase.updatePlannedCourses();
            }
        });


        showOnlyChosePlanned_CheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOnlyPlanedLogic();
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showOnlyPlanedLogic() {
        if (showOnlyChosePlanned_CheckBox.isChecked()) {
            ArrayList<Course> planedOnlyList;
            planedOnlyList = adapter.getItems().stream().filter(Course::isPlanned).collect(Collectors.toCollection(ArrayList::new));
            adapter.addCoursesListToAdapter(dataBase.sortCoursesByYearAndType(planedOnlyList));
            adapter.notifyDataSetChanged();
        } else {
            adapter.addCoursesListToAdapter(dataBase.sortCoursesByYearAndType(coursesToShow));
            adapter.notifyDataSetChanged();
        }
    }

    //  for now the screen orientation is disabled, when it will be implemented all over the app
    //  re enabled these two method and fix if needed.
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onResume() {
        super.onResume();

        if (dataBase == null) {
            dataBase = HujiAssistentApplication.getInstance().getDataBase();
        }

        if (sp == null){
            sp = dataBase.getSp();
        }

        ArrayAdapter arrayYearAdapter = new ArrayAdapter(requireContext(), R.layout.dropdownfacultyitem, getResources().getStringArray(R.array.yearArray));
        arrayYearAdapter.getFilter().filter("");
        binding.autocompletechooseyear.setAdapter(arrayYearAdapter);

        ArrayAdapter arraySemesterAdapter = new ArrayAdapter(requireContext(), R.layout.dropdownfacultyitem, getResources().getStringArray(R.array.semesterArray));
        arraySemesterAdapter.getFilter().filter("");
        binding.autocompletechoosesemester.setAdapter(arraySemesterAdapter);

        ArrayAdapter arrayPointsAdapter = new ArrayAdapter(requireContext(), R.layout.dropdownfacultyitem, getResources().getStringArray(R.array.pointsArray));
        arrayPointsAdapter.getFilter().filter("");
        binding.autocompletechoosepoints.setAdapter(arrayPointsAdapter);


//        String year_text_to_save = sp.getString("year text to save", "");
//        String semester_text_to_save = sp.getString("semester text to save", "");
//        String point_text_to_save = sp.getString("point text to save", "");
//
//        String year_value_to_save = sp.getString("year value to save", "");
//        String semester_value_to_save = sp.getString("semester value to save", "");
//        String point_value_to_save = sp.getString("point value to save", "");
//
//        String adapter_list_to_save = sp.getString("adapter list to save", "");
//        String checkBox_status_to_save = sp.getString("checkBox status to save", "");
//
//        if (!year_text_to_save.equals("")){
//            autoCompleteChooseYear.setText(year_text_to_save);
//            filterYear = year_value_to_save;
//        }
//        if (!semester_text_to_save.equals("")){
//            autoCompleteChooseSemester.setText(semester_text_to_save);
//            filterSemester = semester_value_to_save;
//        }
//        if (!point_text_to_save.equals("")){
//            autoCompleteChoosePoints.setText(point_text_to_save);
//            filterPoints = point_value_to_save;
//        }
//
//        ArrayList<Course> courses = dataBase.stringToCourseArray(adapter_list_to_save);
//        showOnlyChosePlanned_CheckBox.setChecked(Boolean.parseBoolean(checkBox_status_to_save));
//
//
//        adapter.addCoursesListToAdapter(dataBase.sortCoursesByYearAndType(courses));
//        adapter.notifyDataSetChanged();
//
//        if (showOnlyChosePlanned_CheckBox.isChecked()) { // check if onlyShowPlaned is clicked, and if so response accordingly
//            showOnlyPlanedLogic();
//        }
    }
//
//    @Override
//    public void onSaveInstanceState(@NonNull Bundle outState) {
//        super.onSaveInstanceState(outState);
//        System.out.println("-------------------------- saved data");
//
//        String yearTextToSave = autoCompleteChooseYear.getText().toString();
//        String semesterTextToSave = autoCompleteChooseSemester.getText().toString();
//        String pointTextToSave = autoCompleteChoosePoints.getText().toString();
//        ArrayList<Course> adapterList = adapter.getItems();
//        boolean checkBoxStatus = showOnlyChosePlanned_CheckBox.isChecked();
//
//        SharedPreferences.Editor editor = sp.edit();
//        editor.putString("year to text save", yearTextToSave);
//        editor.putString("semester text to save", semesterTextToSave);
//        editor.putString("point text to save", pointTextToSave);
//
//        editor.putString("year value to save", filterYear);
//        editor.putString("semester value to save", filterSemester);
//        editor.putString("point value to save", filterPoints);
//
//        editor.putString("adapter list to save", dataBase.courseArrayToString(adapterList));
//        editor.putString("checkBox status to save", "" + checkBoxStatus);
//        editor.apply();
//
//    }

}