package huji.postpc2021.hujiassistant.Activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
//import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import huji.postpc2021.hujiassistant.Chug;
import huji.postpc2021.hujiassistant.Faculty;
import huji.postpc2021.hujiassistant.Fragments.EditProfileFragment;
import huji.postpc2021.hujiassistant.Fragments.PlanCourseInfoFragment;
import huji.postpc2021.hujiassistant.Fragments.PlanCoursesFragment;
import huji.postpc2021.hujiassistant.Fragments.SettingsFragment;
import huji.postpc2021.hujiassistant.Fragments.ShowFilesFragment;
import huji.postpc2021.hujiassistant.Maslul;
import huji.postpc2021.hujiassistant.PlanCoursesAdapter;
import huji.postpc2021.hujiassistant.R;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;

import huji.postpc2021.hujiassistant.Course;
import huji.postpc2021.hujiassistant.CoursesAdapter;
import huji.postpc2021.hujiassistant.Fragments.AddCourseFragment;
import huji.postpc2021.hujiassistant.Fragments.CourseInfoFragment;
import huji.postpc2021.hujiassistant.Fragments.CoursesFragment;
import huji.postpc2021.hujiassistant.Fragments.MainScreenFragment;
import huji.postpc2021.hujiassistant.Fragments.MyCoursesFragment;
import huji.postpc2021.hujiassistant.HujiAssistentApplication;
import huji.postpc2021.hujiassistant.LocalDataBase;
import huji.postpc2021.hujiassistant.StudentInfo;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Locale;

import android.content.res.Configuration;

import java.util.ArrayList;
import java.util.List;

public class MainScreenActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public LocalDataBase dataBase = null;
    FragmentManager fragmentManager;
    MainScreenFragment mainscreenfragment;
    MainScreenFragment mainscreenfragment2;
    EditProfileFragment editProfileFragment2;
    MyCoursesFragment myCoursesFragment;
    MyCoursesFragment myCoursesFragment2;
    SettingsFragment settingsFragment;
    SettingsFragment settingsFragment2;
    PlanCoursesFragment planCoursesFragment2;
    AddCourseFragment addCourseFragment2;
    ProgressBar progressBar;
    FirebaseFirestoreSettings settings;
    String currentPhotoPath;
    private DrawerLayout moreInfoDrawerLayout;
    private ImageView logoutImageView;
    private TextView changeLanguageTextView;
    ListenerRegistration listener;
    FirebaseFirestore firebaseInstancedb = FirebaseFirestore.getInstance();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainscreen2);

        // db
        if (dataBase == null) {
            dataBase = HujiAssistentApplication.getInstance().getDataBase();
        }

        settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firebaseInstancedb.setFirestoreSettings(settings);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        // application singleton
        HujiAssistentApplication application = (HujiAssistentApplication) getApplication();

        // firebase listener for changes in current student
        try {
            final DocumentReference docRef = firebaseInstancedb.collection("students").document(dataBase.getCurrentUser().getId());
            docRef.addSnapshotListener((value, error) -> {
                if (error != null) {
//                    Log.w("ERROR", "Listen failure", error);
                    return;
                }
                if ((value != null) && (value.exists())) {
                    StudentInfo currentStudent = value.toObject(StudentInfo.class);
                    assert currentStudent != null;
                    dataBase.setCurrentStudent(currentStudent);
                    dataBase.setGradesOfStudentMap(currentStudent.getCoursesGrades());
                    currentStudent.printCourses();
                    getNewCoursesList();
                    getFacultyInfo();
                    getChugInfo();
                    getMaslulInfo();
                }
            });
        } catch (Exception e) {
//            Log.i("ERROR", "error loading data from firebase");
        }




        logoutImageView = findViewById(R.id.logoutImageView);
        progressBar = findViewById(R.id.progressBar1);
        progressBar.setVisibility(View.INVISIBLE);
        changeLanguageTextView = findViewById(R.id.change_language_textView);

        moreInfoDrawerLayout = findViewById(R.id.drawer_layout_more_info);
        NavigationView navigationView = findViewById(R.id.nav_view);
        moreInfoDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentContainerView mainFragmentView = findViewById(R.id.mainfragment);
        CoursesFragment coursesFragment = new CoursesFragment();
        myCoursesFragment = new MyCoursesFragment();
        myCoursesFragment2 = new MyCoursesFragment();
        CourseInfoFragment courseInfoFragment = new CourseInfoFragment();
        PlanCourseInfoFragment planCourseInfoFragment = new PlanCourseInfoFragment();
        ShowFilesFragment showFilesFragment = new ShowFilesFragment();
        AddCourseFragment addCourseFragment = new AddCourseFragment();
        //  PlannedCoursesFragment plannedCoursesFragment = new PlannedCoursesFragment();
        EditProfileFragment editProfileFragment = new EditProfileFragment();
        PlanCoursesFragment planCoursesFragment = new PlanCoursesFragment();
        SettingsFragment settingsFragment = new SettingsFragment();
        CourseInfoFragment courseInfoFragment1 = new CourseInfoFragment();

        addCourseFragment2 = new AddCourseFragment();
        editProfileFragment2 = new EditProfileFragment();
        planCoursesFragment2 = new PlanCoursesFragment();
        settingsFragment2 = new SettingsFragment();

        FloatingActionButton openCameraBtn = findViewById(R.id.open_camera_floating_button);

        mainscreenfragment = new MainScreenFragment();
        mainscreenfragment2 = new MainScreenFragment();

        getSupportFragmentManager().beginTransaction().replace(mainFragmentView.getId(), mainscreenfragment, "MAIN_FRAGMENT")
                .commit();

        findViewById(R.id.buttonMoreInfoMapActivity).setOnClickListener(v -> {
            moreInfoDrawerLayout.openDrawer(GravityCompat.START);
        });

        findViewById(R.id.settings).setOnClickListener(v -> {
            SettingsFragment myFragment = (SettingsFragment) getSupportFragmentManager().findFragmentByTag("SETTINGS_FRAGMENT");
            if (myFragment != null && myFragment.isVisible()) {
                return;
            }

            getSupportFragmentManager().beginTransaction().setCustomAnimations(
                    R.anim.fade_in,  // enter
                    R.anim.slide_out,  // exit
                    R.anim.slide_in,   // popEnter
                    R.anim.fade_out  // popExit
            )
                    .replace(mainFragmentView.getId(), settingsFragment, "SETTINGS_FRAGMENT")
                    .addToBackStack(null)
                    .commit();
        });

        findViewById(R.id.qrscan).setOnClickListener(v -> {
            startActivity(new Intent(this, ScanQrActivity.class));
            finish();
        });

        openCameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainScreenActivity.this, CaptureImageActivity.class));
            }
        });

        mainscreenfragment.coursesPlanButtonListenerBtn = new MainScreenFragment.coursesPlanButtonListenerBtn() {
            @Override
            public void onPlanCoursesButtonClicked() {
                getSupportFragmentManager().beginTransaction().setCustomAnimations(
                        R.anim.fade_in,  // enter
                        R.anim.slide_out,  // exit
                        R.anim.slide_in,   // popEnter
                        R.anim.fade_out  // popExit
                )
                        .replace(mainFragmentView.getId(), planCoursesFragment, "PLAN_COURSES_FRAGMENT").addToBackStack(null).commit();
            }
        };

        mainscreenfragment.onSearchClickListener = new MainScreenFragment.onSearchClickListener() {
            @Override
            public void onSearchClick() {
                getSupportFragmentManager().beginTransaction().setCustomAnimations(
                        R.anim.fade_in,  // enter
                        R.anim.slide_out,  // exit
                        R.anim.slide_in,   // popEnter
                        R.anim.fade_out  // popExit
                )
                        .replace(mainFragmentView.getId(), courseInfoFragment1, "INFO_COURSE_FRAGMENT").addToBackStack(null).commit();
            }
        };

        showFilesFragment.filesClickedListener = new ShowFilesFragment.showFilesClicked() {
            @Override
            public void onFilesShowClick() {

            }
        };

        showFilesFragment.imagesClickedListener = new ShowFilesFragment.showImagesClicked() {
            @Override
            public void onImageShowClick() {

            }
        };

        mainscreenfragment.showFilesListener = new MainScreenFragment.showFilesListener() {
            @Override
            public void onShowFilesButtonClicked() {
                getSupportFragmentManager().beginTransaction().setCustomAnimations(
                        R.anim.fade_in,  // enter
                        R.anim.slide_out,  // exit
                        R.anim.slide_in,   // popEnter
                        R.anim.fade_out  // popExit
                )
                        .replace(mainFragmentView.getId(), showFilesFragment, "FILES_FRAGMENT").addToBackStack(null).commit();
            }
        };

        // mainscreenfragment.coursesPlanScreenListenerBtn = new MainScreenFragment.coursesPlanScreenListenerBtn() {
        //    @Override
        //  public void onPlanCoursesScreenClicked() {
        //    getSupportFragmentManager().beginTransaction().setCustomAnimations(
        //        R.anim.fade_in,  // enter
        //      R.anim.slide_out,  // exit
        //     R.anim.slide_in,   // popEnter
        //     R.anim.fade_out  // popExit
        //   )
        //       .replace(mainFragmentView.getId(), plannedCoursesFragment, "PLAN_SCREEN_FRAGMENT").addToBackStack(null).commit();
        //  }
        //   };

        mainscreenfragment.editInfoButtonListener = new MainScreenFragment.editInfoButtonListener() {

            @Override
            public void onEditInfoButtonClicked() {
                getSupportFragmentManager().beginTransaction().setCustomAnimations(
                        R.anim.fade_in,  // enter
                        R.anim.slide_out,  // exit
                        R.anim.slide_in,   // popEnter
                        R.anim.fade_out  // popExit
                )
                        .replace(mainFragmentView.getId(), editProfileFragment, "EDIT_INFO_FRAGMENT").addToBackStack(null).commit();
            }
        };

        mainscreenfragment.showAttendanceButtonListener = new MainScreenFragment.showAttendanceButtonListener() {
            @Override
            public void onShowAttendanceButtonClicked() {
                startActivity(new Intent(MainScreenActivity.this, ShowAttendanceActivity.class));
                finish();
            }
        };

        mainscreenfragment.uploadPicturesButtonListenerBtn = new MainScreenFragment.UploadPictureButtonListener() {
            @Override
            public void onUploadPictureButtonClicked() {
                startActivity(new Intent(MainScreenActivity.this, CaptureImageActivity.class));
                finish();
            }
        };

        myCoursesFragment.addCourseListener = new MyCoursesFragment.addCourseButtonClickListener() {
            @Override
            public void addCourseBtnClicked() {
                getSupportFragmentManager().beginTransaction().setCustomAnimations(
                        R.anim.fade_in,  // enter
                        R.anim.slide_out,  // exit
                        R.anim.slide_in,   // popEnter
                        R.anim.fade_out  // popExit
                )
                        .replace(mainFragmentView.getId(), addCourseFragment, "ADD_COURSES_FRAGMENT").addToBackStack(null).commit();
            }
        };

        myCoursesFragment2.addCourseListener = new MyCoursesFragment.addCourseButtonClickListener() {
            @Override
            public void addCourseBtnClicked() {
                getSupportFragmentManager().beginTransaction().setCustomAnimations(
                        R.anim.fade_in,  // enter
                        R.anim.slide_out,  // exit
                        R.anim.slide_in,   // popEnter
                        R.anim.fade_out  // popExit
                )
                        .replace(mainFragmentView.getId(), addCourseFragment, "ADD_COURSES_FRAGMENT").addToBackStack(null).commit();
            }
        };

        mainscreenfragment.myCoursesButtonListenerBtn = new MainScreenFragment.myCoursesButtonListener() {
            @Override
            public void onMyCoursesButtonClicked() {
                getSupportFragmentManager().beginTransaction().setCustomAnimations(
                        R.anim.fade_in,  // enter
                        R.anim.slide_out,  // exit
                        R.anim.slide_in,   // popEnter
                        R.anim.fade_out  // popExit
                )
                        .replace(mainFragmentView.getId(), myCoursesFragment, "MY_COURSES_FRAGMENT").addToBackStack(null).commit();
            }
        };

        myCoursesFragment.deleteClickListener = new CoursesAdapter.DeleteClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDeleteClick(View v, Course item) {
            }
        };

        myCoursesFragment2.deleteClickListener = new CoursesAdapter.DeleteClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDeleteClick(View v, Course item) {
            }
        };

        settingsFragment.sendEmailBtnListener = new SettingsFragment.sendEmailBtnListener() {
            @Override
            public void onSendEmailBtnClicked(String email) {
                sendEmail(email);
            }
        };

        myCoursesFragment.onItemClickListener = new CoursesAdapter.OnItemClickListener() {
            @Override
            public void onClick(Course item) {
                getSupportFragmentManager().beginTransaction().setCustomAnimations(
                        R.anim.fade_in,  // enter
                        R.anim.slide_out,  // exit
                        R.anim.slide_in,   // popEnter
                        R.anim.fade_out  // popExit
                )
                        .replace(mainFragmentView.getId(), courseInfoFragment, "SELECT_COURSE_ITEM_FRAGMENT").addToBackStack(null).commit();
            }
        };

        planCoursesFragment.onItemClickListener = new PlanCoursesAdapter.OnItemClickListener() {
            @Override
            public void onClick(Course item) {
                getSupportFragmentManager().beginTransaction().setCustomAnimations(
                        R.anim.fade_in,  // enter
                        R.anim.slide_out,  // exit
                        R.anim.slide_in,   // popEnter
                        R.anim.fade_out  // popExit
                )
                        .replace(mainFragmentView.getId(), planCourseInfoFragment, "SELECT_PLANED_COURSE_INFO_FRAGMENT").addToBackStack(null).commit();
            }
        };

        settingsFragment.changeLanguageBtnListener = new SettingsFragment.changeLanguageBtnListener() {
            @Override
            public void onClickChangeLanguage() {
                showChangeLanguageDialog();
            }
        };

        myCoursesFragment2.onItemClickListener = new CoursesAdapter.OnItemClickListener() {
            @Override
            public void onClick(Course item) {
                getSupportFragmentManager().beginTransaction().setCustomAnimations(
                        R.anim.fade_in,  // enter
                        R.anim.slide_out,  // exit
                        R.anim.slide_in,   // popEnter
                        R.anim.fade_out  // popExit
                )
                        .replace(mainFragmentView.getId(), courseInfoFragment, "SELECT_COURSE_ITEM_FRAGMENT").addToBackStack(null).commit();
            }
        };

        logoutImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE: {
                            dataBase.logoutUser();
                            startActivity(new Intent(MainScreenActivity.this, MainActivity.class));
                            finish();
                            break;
                        }
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(MainScreenActivity.this);
                builder.setMessage(R.string.logout).setPositiveButton(R.string.positive_answer, dialogClickListener)
                        .setNegativeButton(R.string.negative_answer, dialogClickListener).show();
            }
        });
    }

    private void showChangeLanguageDialog() {
        final String[] listItems = {"English", "עברית"};
        AlertDialog.Builder mbuilder = new AlertDialog.Builder(MainScreenActivity.this);
        mbuilder.setTitle("Choose Languae...");
        int languageIndex = dataBase.getLanguageIndex();
        mbuilder.setSingleChoiceItems(listItems, languageIndex, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    setLocale("en");
                } else if (which == 1) {
                    setLocale("he");
                }

                dialog.dismiss();
            }
        });

        AlertDialog mDialog = mbuilder.create();
        mDialog.show();
    }

    private void goToUrl(String s) {
        Uri url = Uri.parse(s);
        startActivity(new Intent(Intent.ACTION_VIEW, url));
    }

    @SuppressLint("IntentReset")
    private void sendEmail(String email) {
        String[] emailList = email.split(",");

        StudentInfo currentUser = dataBase.getCurrentStudent();
        String[] CC = {""};
        String link = "link"; // set link
        String emailSubject = getResources().getString(R.string.mailheader) + " " + " Huji assistent";
        String emailFrom = currentUser.getPersonalName() + " " + currentUser.getFamilyName();
        String emailText = getResources().getString(R.string.hellomail) + " " + emailFrom + " " + getResources().getString(R.string.invitationmail)
                + " " + "Huji assistent!";

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("message/rfc822");

        emailIntent.putExtra(Intent.EXTRA_EMAIL, emailList);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, emailText);

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "There is no email client installed.", Toast.LENGTH_LONG).show();
        }
    }

    private void getChugInfo() {
        if (dataBase.getCurrentChug() == null) {
            try {
                String COLLECTION = "coursesTestOnlyCs";
                Task<DocumentSnapshot> chugim1 = firebaseInstancedb.collection(COLLECTION).document(dataBase.getCurrentStudent().getChugId())
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                DocumentSnapshot result = task.getResult();
                                assert result != null;
                                Chug data = result.toObject(Chug.class);
                                assert data != null;
                                String chugName = data.getTitle();
                                dataBase.setCurrentChug(data);
                            }
                        });
            } catch (Exception e) {
//                Log.i("ERROR_CHUG", "couldn't get chug");
            }
        }
    }

    private void getFacultyInfo() {
        if (dataBase.getCurrentFaculty() == null) {
            try {
                String COLLECTION = "faculties";
                Task<DocumentSnapshot> faculties1 = firebaseInstancedb.collection(COLLECTION).document(dataBase.getCurrentStudent().getFacultyId())
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                DocumentSnapshot result = task.getResult();
                                assert result != null;
                                Faculty data = result.toObject(Faculty.class);
                                assert data != null;
                                String facultyName = data.getTitle();
                                dataBase.setCurrentFaculty(data);
                            }
                        });
            } catch (Exception e) {
//                Log.i("ERROR_FACULTY", "couldn't get faculty");
            }
        }
    }

    private void getMaslulInfo() {
        // If the sign up process was from other phone or the data was erased
        if (dataBase.getCurrentMaslul() == null) {
            try {
                String COLLECTION = "coursesTestOnlyCs";
                Task<DocumentSnapshot> maslulim1 = firebaseInstancedb.collection(COLLECTION).document(dataBase.getCurrentStudent().getChugId())
                        .collection("maslulimInChug").document(dataBase.getCurrentStudent().getMaslulId())
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                DocumentSnapshot result = task.getResult();
                                assert result != null;
                                Maslul data = result.toObject(Maslul.class);
                                assert data != null;
                                String maslulName = data.getTitle();
                                String totalPoints = data.getTotalPoints();
                                dataBase.setCurrentMaslul(data);
                            }
                        });
            } catch (Exception e) {
//                Log.i("ERROR_FACULTY", "couldn't get maslul");
            }
        }
    }

    private void getNewCoursesList() {

        ArrayList<String> coursesOfStudentById = dataBase.getCurrentStudent().getCourses();
        ArrayList<Course> coursesOfStudentByCourse = new ArrayList<>();

        // Get the id's of the planned courses that are saved in firebase
        ArrayList<String> coursesOfStudentPlannedById = dataBase.getCurrentStudent().getPlanned();
        dataBase.setCoursesPlannedById(coursesOfStudentPlannedById);
        ArrayList<Course> plannedCoursesOfStudentByCourse = new ArrayList<>();

        String ROOT_COLLECTION = "coursesTestOnlyCs";

        // Gets the courses of the student from firebase and updates in local data base
        Task<QuerySnapshot> courses1 = firebaseInstancedb.collection(ROOT_COLLECTION).document(dataBase.getCurrentUser().getChugId())
                .collection("maslulimInChug").document(dataBase.getCurrentUser().getMaslulId())
                .collection("coursesInMaslul")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        ArrayList<Course> coursesFromFireBase = new ArrayList<>();
                        for (DocumentSnapshot document1 : documents) {
                            Course course = document1.toObject(Course.class);
                            coursesFromFireBase.add(course);
                        }

                        int currentPointsSum = 0; // points in degree
                        int currentMandatoryPoints = 0; // hova points
                        int currentCornerStonePoints = 0; // avnei pina
                        int currentMandatoryChoosePoints = 0; // hova choose
                        int currentChoosePoints = 0; // choose
                        int currentSuppPoints = 0; // mashlimim

                        // Save the list of all courses in the student's maslul
                        dataBase.setCoursesFromFireBase(coursesFromFireBase);

                        for (String id : coursesOfStudentPlannedById) {
                            for (Course course : coursesFromFireBase) {
                                if (course.getNumber().equals(id)) {
                                    course.setPlannedChecked(true);
                                    plannedCoursesOfStudentByCourse.add(course);
                                }
                            }
                        }

                        for (String id : coursesOfStudentById) {
                            for (Course course : coursesFromFireBase) {
                                if (course.getNumber().equals(id)) {
                                    course.setIsFinished(true);
                                    currentPointsSum += Integer.parseInt(course.getPoints());
                                    String type = course.getType();
                                    switch (type) {
                                        case "לימודי חובה":
                                            currentMandatoryPoints += Integer.parseInt(course.getPoints());
                                            break;
                                        case "לימודי חובת בחירה":
                                            currentMandatoryChoosePoints += Integer.parseInt(course.getPoints());
                                            break;
                                        case "קורסי בחירה":
                                            currentChoosePoints += Integer.parseInt(course.getPoints());
                                            break;
                                        case "משלימים":
                                            currentSuppPoints += Integer.parseInt(course.getPoints());
                                            break;
                                        case "אבני פינה":
                                            currentCornerStonePoints += Integer.parseInt(course.getPoints());
                                            break;
                                    }
                                    coursesOfStudentByCourse.add(course);
                                }
                            }
                        }
                        // Save the current points sum of the current student
                        dataBase.setCurrentPointsSum(currentPointsSum);
                        dataBase.setCurrentMandatoryPoints(currentMandatoryPoints);
                        dataBase.setCurrentMandatoryChoosePoints(currentMandatoryChoosePoints);
                        dataBase.setCurrentChoosePoints(currentChoosePoints);
                        dataBase.setCurrentSuppPoints(currentSuppPoints);
                        dataBase.setCurrentCornerStonesPoints(currentCornerStonePoints);

                        // show changes on course list in adapter
                        dataBase.setCoursesOfCurrentStudent(coursesOfStudentByCourse);
                        dataBase.setPlannedCoursesOfCurrentStudent(plannedCoursesOfStudentByCourse);
                    }
                });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.aguda:
                goToUrl("https://www.aguda.org.il/");
                break;
            case R.id.kolzchut:
                goToUrl("https://www.kolzchut.org.il/he/%D7%A1%D7%98%D7%95%D7%93%D7%A0%D7%98%D7%99%D7%9D");
                break;
            case R.id.link_to_drive:
                goToUrl("https://drive.google.com/drive/u/3/folders/0B4NFaiXelmmkelRNeFpRMHlVX2M?resourcekey=0-5b6GE4omL97bd0wCXvzv4w");
                break;
            case R.id.rishumnet:
                goToUrl("https://rishum-net.huji.ac.il/site/student/login.asp");
                break;
            case R.id.recommendations:
                goToUrl("https://drive.google.com/drive/u/3/folders/1BtcBIpl_zTht1KWKR7Qjl_RoavkmfQG0");
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (moreInfoDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            moreInfoDrawerLayout.closeDrawer(GravityCompat.START);
            return;
        } else {
            MainScreenFragment myFragment = (MainScreenFragment) getSupportFragmentManager().findFragmentByTag("MAIN_FRAGMENT");
            SettingsFragment settingsFragment = (SettingsFragment) getSupportFragmentManager().findFragmentByTag("SETTINGS_FRAGMENT");
            SettingsFragment settingsFragment2 = (SettingsFragment) getSupportFragmentManager().findFragmentByTag("SETTINGS_FRAGMENT2");

            if (myFragment != null && myFragment.isVisible()) {
                DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE: {
                            finishAffinity();
                            System.exit(0);
                            break;
                        }
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.close_app).setPositiveButton(R.string.positive_answer, dialogClickListener)
                        .setNegativeButton(R.string.negative_answer, dialogClickListener).show();
            } else if ((settingsFragment != null && settingsFragment.isVisible())) {
                changeSettingsFragment(settingsFragment, mainscreenfragment, "MAIN_FRAGMENT");
            } else if ((settingsFragment2 != null && settingsFragment2.isVisible())) {
                changeSettingsFragment(settingsFragment2, mainscreenfragment, "MAIN_FRAGMENT");
            } else {
                super.onBackPressed();
            }
        }
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
        dataBase.saveLocale(lang);

        fragmentManager = getSupportFragmentManager();

        // settingsFragment
        SettingsFragment settingsFragment4 = (SettingsFragment) getSupportFragmentManager().findFragmentByTag("SETTINGS_FRAGMENT");
        SettingsFragment settingsFragment3 = (SettingsFragment) getSupportFragmentManager().findFragmentByTag("SETTINGS_FRAGMENT2");

        if (settingsFragment3 != null && settingsFragment3.isVisible()) {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(
                    R.anim.fade_in,  // enter
                    R.anim.slide_out,  // exit
                    R.anim.slide_in,   // popEnter
                    R.anim.fade_out  // popExit
            )
                    .replace(settingsFragment3.getId(), settingsFragment, "SETTINGS_FRAGMENT").addToBackStack(null).commit();
        }

        if (settingsFragment4 != null && settingsFragment4.isVisible()) {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(
                    R.anim.fade_in,  // enter
                    R.anim.slide_out,  // exit
                    R.anim.slide_in,   // popEnter
                    R.anim.fade_out  // popExit
            )
                    .replace(settingsFragment4.getId(), settingsFragment2, "SETTINGS_FRAGMENT2").addToBackStack(null).commit();
        }
    }


    private void changeSettingsFragment(SettingsFragment fragmentFrom, MainScreenFragment fragmentTo, String tag) {
        if (fragmentFrom != null && fragmentFrom.isVisible()) {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(
                    R.anim.fade_in,  // enter
                    R.anim.slide_out,  // exit
                    R.anim.slide_in,   // popEnter
                    R.anim.fade_out  // popExits
            )
                    .replace(fragmentFrom.getId(), fragmentTo, tag).addToBackStack(null).commit();
        }
    }
}
