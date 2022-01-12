package huji.postpc2021.hujiassistant.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import huji.postpc2021.hujiassistant.Chug;
import huji.postpc2021.hujiassistant.HujiAssistentApplication;
import huji.postpc2021.hujiassistant.LocalDataBase;
import huji.postpc2021.hujiassistant.Maslul;
import huji.postpc2021.hujiassistant.MobileVersionFetcher;
import huji.postpc2021.hujiassistant.R;
import huji.postpc2021.hujiassistant.StudentInfo;
import huji.postpc2021.hujiassistant.UploadChug;
import huji.postpc2021.hujiassistant.UploadMaslul;
import huji.postpc2021.hujiassistant.ViewModelApp;
import huji.postpc2021.hujiassistant.databinding.FragmentInfoBinding;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This fragment is used in the registration screens, to add details about the degree:
 * The user is able to choose it's faculty, chug, maslul and add more details about it's degree.
 */
public class InfoFragment extends Fragment {

    public InfoFragment() {
        super(R.layout.fragment_info);
    }

    FirebaseFirestore firebaseInstancedb = FirebaseFirestore.getInstance();

    /* On item selected in faculty auto complete text listener*/
    public interface itemSelectedDropDownFaculty {
        public void onFacultyItemSelected();
    }

    public InfoFragment.itemSelectedDropDownFaculty itemSelectedDropDownFacultyListener = null;

    HashMap<Integer, String> faculties_position_map = new HashMap<>();
    FragmentInfoBinding binding;
    AutoCompleteTextView dropdownHugim;
    AutoCompleteTextView dropdownFaculty;
    AutoCompleteTextView dropdownMaslulim;
    AutoCompleteTextView dropdowndegree;
    AutoCompleteTextView dropdownYear;
    AutoCompleteTextView dropdownyearbegindegree;
    AutoCompleteTextView dropdownsemesterbegindegree;
    ArrayList<String> degreeList = new ArrayList<>();
    StudentInfo currentStudent;
    private ViewModelApp viewModelApp;
    FirebaseFirestoreSettings settings;
    String facultyId;
    String chugId;
    String maslulId;
    String selectedDegree;
    String selectedYear;
    String selectedBeginYear;
    String selectedBeginSemester;
    String personalName;
    String familyName;
    String email;
    boolean validDegree = false;
    boolean isFacultyValid = false;
    boolean isChugValid = false;
    boolean isMaslulValid = false;
    public LocalDataBase dataBase;
    MobileVersionFetcher fetcher = new MobileVersionFetcher();

    public interface continueButtonClickListener {
        public void continueBtnClicked();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // return super.onCreateView(inflater, container, savedInstanceState);
        HujiAssistentApplication.getInstance().setInfoFragment(this);

        binding = FragmentInfoBinding.inflate(inflater, container, false);
        faculties_position_map.put(0, "01");
        faculties_position_map.put(1, "02");
        faculties_position_map.put(2, "03");
        faculties_position_map.put(3, "04");
        faculties_position_map.put(4, "05");
        faculties_position_map.put(5, "06");
        faculties_position_map.put(6, "07");
        faculties_position_map.put(7, "08");
        faculties_position_map.put(8, "09");
        faculties_position_map.put(9, "11");
        faculties_position_map.put(10, "12");
        faculties_position_map.put(11, "16");
        faculties_position_map.put(12, "30");

        settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firebaseInstancedb.setFirestoreSettings(settings);

        TextView textView = requireActivity().findViewById(R.id.change_language_textView);
        textView.setVisibility(View.INVISIBLE);

        // Get values recourse
        String[] facultyArray = getResources().getStringArray(R.array.faculty);

        // Get items to show in drop down faculty
        ArrayAdapter arrayAdapter = new ArrayAdapter(requireContext(), R.layout.dropdownfacultyitem, facultyArray);
        arrayAdapter.getFilter().filter("");
        binding.autoCompleteTextViewFaculty.setAdapter(arrayAdapter);
        return binding.getRoot();
    }

    public InfoFragment.continueButtonClickListener continueListener = null;

    /* Checks validation for the fields in the screen */
    private void checkValidation() {

        // Check fields not empty
        if (dropdownFaculty.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), getResources().getString(R.string.please_enter_faculty_msg), Toast.LENGTH_LONG).show();
            isFacultyValid = false;
        } else {
            isFacultyValid = true;
            if (dropdownHugim.getText().toString().isEmpty()) {
                Toast.makeText(getActivity(), getResources().getString(R.string.please_enter_chug_msg), Toast.LENGTH_LONG).show();
                isChugValid = false;
            } else {
                isChugValid = true;

                if (dropdownMaslulim.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.please_enter_maslul_msg), Toast.LENGTH_LONG).show();
                    isMaslulValid = false;
                } else {
                    isMaslulValid = true;
                }
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // db
        if (dataBase == null) {
            dataBase = HujiAssistentApplication.getInstance().getDataBase();
        }
        String[] facultyArray = getResources().getStringArray(R.array.faculty);
        // Get items to show in drop down faculty

        ArrayAdapter arrayAdapter = new ArrayAdapter(requireContext(), R.layout.dropdownfacultyitem, facultyArray);
        arrayAdapter.getFilter().filter("");
        binding.autoCompleteTextViewFaculty.setAdapter(arrayAdapter);

        String[] beginSemesterArray = getResources().getStringArray(R.array.beginsemesterarray);
        binding.autoCompleteSemesterBeginDegree.setAdapter(new ArrayAdapter(requireContext(), R.layout.dropdownbeginsemesteritem, beginSemesterArray));
        arrayAdapter.getFilter().filter("");

        String[] beginYearArray = getResources().getStringArray(R.array.beginyeararray);
        binding.autoCompleteYearBeginDegree.setAdapter(new ArrayAdapter(requireContext(), R.layout.dropdownbeginyearitem, beginYearArray));
        arrayAdapter.getFilter().filter("");

        // Finds views
        AutoCompleteTextView autoCompleteTextViewFaculty = (AutoCompleteTextView) getView().findViewById(R.id.autoCompleteTextViewFaculty);
        AutoCompleteTextView autoCompleteTextViewChug = (AutoCompleteTextView) getView().findViewById(R.id.autoCompleteTextViewChug);
        AutoCompleteTextView autoCompleteTextViewDegree = (AutoCompleteTextView) getView().findViewById(R.id.autoCompleteTextViewDegree);
        AutoCompleteTextView autoCompleteTextViewMaslul = (AutoCompleteTextView) getView().findViewById(R.id.autoCompleteTextViewMaslul);
        AutoCompleteTextView autoCompleteTextViewYear = (AutoCompleteTextView) getView().findViewById(R.id.autoCompleteTextViewYear);
        ProgressBar progressBar = getView().findViewById(R.id.progressBar);

        autoCompleteTextViewChug.setEnabled(false);
        autoCompleteTextViewDegree.setEnabled(false);
        autoCompleteTextViewMaslul.setEnabled(false);
        autoCompleteTextViewYear.setEnabled(false);
        progressBar.setVisibility(View.INVISIBLE);

        viewModelApp = new ViewModelProvider(requireActivity()).get(ViewModelApp.class);

        viewModelApp.getStudent().observe(getViewLifecycleOwner(), item -> {
            personalName = item.getPersonalName();
            familyName = item.getFamilyName();
            email = item.getEmail();
            currentStudent = viewModelApp.getStudent().getValue();
        });

        view.findViewById(R.id.continuePersBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (continueListener != null) {

                    checkValidation();

                    if (isFacultyValid && isChugValid && isMaslulValid) {
                        currentStudent.setFacultyId(facultyId);
                        currentStudent.setChugId(chugId);
                        currentStudent.setMaslulId(maslulId);

                        if (!dropdowndegree.getText().toString().isEmpty()) {
                            selectedDegree = dropdowndegree.getText().toString();
                            currentStudent.setDegree(selectedDegree);
                        }
                        if (!dropdownYear.getText().toString().isEmpty()) {
                            selectedYear = dropdownYear.getText().toString();
                            currentStudent.setYear(selectedYear);
                        }
                        if (!dropdownyearbegindegree.getText().toString().isEmpty()) {
                            selectedBeginYear = dropdownyearbegindegree.getText().toString();
                            currentStudent.setBeginYear(selectedBeginYear);
                        }
                        if (!dropdownsemesterbegindegree.getText().toString().isEmpty()) {
                            selectedBeginSemester = dropdownsemesterbegindegree.getText().toString();
                            currentStudent.setBeginSemester(selectedBeginSemester);
                        }


                        firebaseInstancedb.collection("coursesTestOnlyCs").whereEqualTo("chugId", chugId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot chugQueryDocumentSnapshots) {
                                if (!chugQueryDocumentSnapshots.isEmpty()) {
                                    firebaseInstancedb.collection("coursesTestOnlyCs").document(chugId).collection("maslulimInChug").whereEqualTo("id", maslulId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot maslulQueryDocumentSnapshots) {
                                            if (!maslulQueryDocumentSnapshots.isEmpty()) {
                                                viewModelApp.setStudent(currentStudent);
                                                continueListener.continueBtnClicked();
                                            } else {
                                                Toast.makeText(getContext(), "We're sorry, The Maslul" + dropdownMaslulim.getText().toString() + " is not in our Database yet", Toast.LENGTH_LONG).show();

                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(getContext(), "We're sorry, The Chug " + dropdownHugim.getText().toString() + " is not in our Database yet", Toast.LENGTH_LONG).show();

                                    firebaseInstancedb.collection("faculties").document(facultyId).collection("chugimInFaculty").document(chugId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            String title = documentSnapshot.get("title").toString();
                                            UploadChug newUploadChug = new UploadChug(title, chugId, facultyId);

                                            fetcher.execute(facultyId, chugId, maslulId);
                                            viewModelApp.setStudent(currentStudent);

                                        }
                                    });
                                }
                            }
                        });

                    }
                }
            }
        });

        dropdownHugim = view.findViewById(R.id.autoCompleteTextViewChug);
        dropdownFaculty = view.findViewById(R.id.autoCompleteTextViewFaculty);
        dropdownMaslulim = view.findViewById(R.id.autoCompleteTextViewMaslul);
        dropdowndegree = view.findViewById(R.id.autoCompleteTextViewDegree);
        dropdownYear = view.findViewById(R.id.autoCompleteTextViewYear);
        dropdownyearbegindegree = view.findViewById(R.id.autoCompleteYearBeginDegree);
        dropdownsemesterbegindegree = view.findViewById(R.id.autoCompleteSemesterBeginDegree);

        dropdownFaculty.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                ArrayList<String> s = new ArrayList<>();
                arrayAdapter.getFilter().filter("");
                autoCompleteTextViewChug.setAdapter(new ArrayAdapter(requireContext(), R.layout.dropdownhujiitem, s));

                progressBar.setVisibility(View.VISIBLE);
                facultyId = faculties_position_map.get(position);

                Task<QuerySnapshot> document = firebaseInstancedb.collection("faculties").document(facultyId)
                        .collection("chugimInFaculty").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                progressBar.setVisibility(View.INVISIBLE);
                                autoCompleteTextViewChug.setEnabled(true);
                                List<DocumentSnapshot> documents = task.getResult().getDocuments();
                                ArrayList<String> chugimInFaculty = new ArrayList<>();

                                for (DocumentSnapshot document1 : documents) {
                                    // retrieve for each chug id it's name
                                    String docId = document1.getId().toString();
                                    Chug chug = document1.toObject(Chug.class);
                                    String chugTitle = chug.getTitle();
                                    chugimInFaculty.add(chugTitle);
                                }
                                arrayAdapter.getFilter().filter("");
                                binding.autoCompleteTextViewChug.setAdapter(new ArrayAdapter(requireContext(), R.layout.dropdownhujiitem, chugimInFaculty));
                            }
                        });
            }
        });

        dropdownHugim.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                progressBar.setVisibility(View.VISIBLE);
                String selection = (String) parent.getItemAtPosition(position);
                ArrayList<String> s = new ArrayList<>();
                arrayAdapter.getFilter().filter("");
                autoCompleteTextViewMaslul.setAdapter(new ArrayAdapter(requireContext(), R.layout.dropdownmaslulitem, s));

                // Get the chosen document
                Task<QuerySnapshot> document = firebaseInstancedb.collection("faculties").
                        document(facultyId).collection("chugimInFaculty")
                        .whereEqualTo("title", selection)
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                progressBar.setVisibility(View.INVISIBLE);
                                autoCompleteTextViewMaslul.setEnabled(true);
                                List<DocumentSnapshot> documents = task.getResult().getDocuments();

                                for (DocumentSnapshot document1 : documents) {
                                    // retrieve for each chug id it's name
                                    Chug chug = document1.toObject(Chug.class);
                                    chugId = chug.getId();

                                    Task<QuerySnapshot> querySnapshotTask = firebaseInstancedb.collection("faculties").document(facultyId)
                                            .collection("chugimInFaculty").document(document1.getId().toString())
                                            .collection("maslulimInChug").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    List<DocumentSnapshot> documents1 = task.getResult().getDocuments();
                                                    ArrayList<String> maslulimInFaculty = new ArrayList<>();

                                                    for (DocumentSnapshot document2 : documents1) {
                                                        Maslul maslul = document2.toObject(Maslul.class);
                                                        String maslulTitle = maslul.getTitle();
                                                        maslulimInFaculty.add(maslulTitle);
                                                    }
                                                    binding.autoCompleteTextViewMaslul.setAdapter(new ArrayAdapter(requireContext(), R.layout.dropdownmaslulitem, maslulimInFaculty));
                                                }
                                            });
                                }
                            }
                        });
            }
        });


        dropdownMaslulim.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);

                // Save id's
                Task<QuerySnapshot> document = firebaseInstancedb.collection("faculties").
                        document(facultyId).collection("chugimInFaculty").document(chugId)
                        .collection("maslulimInChug").whereEqualTo("title", selection)
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                progressBar.setVisibility(View.INVISIBLE);
                                autoCompleteTextViewDegree.setEnabled(true);
                                List<DocumentSnapshot> documents = task.getResult().getDocuments();

                                for (DocumentSnapshot document1 : documents) {
                                    // retrieve for each chug id it's name
                                    Maslul maslul = document1.toObject(Maslul.class);
                                    maslulId = maslul.getMaslulId();
                                }
                                String[] degreeArray = getResources().getStringArray(R.array.degreeTypesList);
                                binding.autoCompleteTextViewDegree.setAdapter(new ArrayAdapter(requireContext(), R.layout.dropdowndegreeitem, degreeArray));
                            }
                        });
            }
        });

        dropdowndegree.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedDegree = (String) parent.getItemAtPosition(position);
                autoCompleteTextViewYear.setEnabled(true);
                String[] yearArray = getResources().getStringArray(R.array.yearArray);
                arrayAdapter.getFilter().filter("");
                binding.autoCompleteTextViewYear.setAdapter(new ArrayAdapter(requireContext(), R.layout.dropdownyearitem, yearArray));
            }
        });

        dropdownYear.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedYear = (String) parent.getItemAtPosition(position);
                dropdownyearbegindegree.setEnabled(true);
                String[] beginYearArray = getResources().getStringArray(R.array.beginyeararray);
                arrayAdapter.getFilter().filter("");
                binding.autoCompleteYearBeginDegree.setAdapter(new ArrayAdapter(requireContext(), R.layout.dropdownbeginyearitem, beginYearArray));
            }
        });

        dropdownyearbegindegree.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedBeginYear = (String) parent.getItemAtPosition(position);
                dropdownsemesterbegindegree.setEnabled(true);
                String[] beginSemesterArray = getResources().getStringArray(R.array.beginsemesterarray);
                arrayAdapter.getFilter().filter("");
                binding.autoCompleteSemesterBeginDegree.setAdapter(new ArrayAdapter(requireContext(), R.layout.dropdownbeginsemesteritem, beginSemesterArray));
            }
        });

        dropdownyearbegindegree.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedBeginSemester = (String) parent.getItemAtPosition(position);
            }
        });
    }

    public void clickContinue() {
        continueListener.continueBtnClicked();

    }

    @Override
    public void onResume() {
        super.onResume();
        ArrayAdapter arrayAdapter = new ArrayAdapter(requireContext(), R.layout.dropdownfacultyitem, getResources().getStringArray(R.array.faculty));
        arrayAdapter.getFilter().filter("");
        binding.autoCompleteTextViewFaculty.setAdapter(arrayAdapter);

        String[] beginSemesterArray = getResources().getStringArray(R.array.beginsemesterarray);
        binding.autoCompleteSemesterBeginDegree.setAdapter(new ArrayAdapter(requireContext(), R.layout.dropdownbeginsemesteritem, beginSemesterArray));
        arrayAdapter.getFilter().filter("");

        String[] beginYearArray = getResources().getStringArray(R.array.beginyeararray);
        binding.autoCompleteYearBeginDegree.setAdapter(new ArrayAdapter(requireContext(), R.layout.dropdownbeginyearitem, beginYearArray));
        arrayAdapter.getFilter().filter("");

    }


}
