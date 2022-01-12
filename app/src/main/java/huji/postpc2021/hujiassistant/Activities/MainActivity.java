package huji.postpc2021.hujiassistant.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentContainerView;

import huji.postpc2021.hujiassistant.Course;
import huji.postpc2021.hujiassistant.CoursesAdapter;
import huji.postpc2021.hujiassistant.Fragments.CourseInfoFragment;
import huji.postpc2021.hujiassistant.Fragments.CoursesFragment;
import huji.postpc2021.hujiassistant.Fragments.FirstFragment;
import huji.postpc2021.hujiassistant.Fragments.InfoFragment;
import huji.postpc2021.hujiassistant.Fragments.MainScreenFragment;
import huji.postpc2021.hujiassistant.Fragments.PlanCourseInfoFragment;
import huji.postpc2021.hujiassistant.Fragments.PlanCoursesFragment;
import huji.postpc2021.hujiassistant.Fragments.TextViewFragment;
import huji.postpc2021.hujiassistant.Fragments.TopFragment;
import huji.postpc2021.hujiassistant.HujiAssistentApplication;
import huji.postpc2021.hujiassistant.LocalDataBase;
import huji.postpc2021.hujiassistant.R;
import huji.postpc2021.hujiassistant.Fragments.RegisterFragment;

import com.google.android.material.navigation.NavigationView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public interface PopUpInterface {
        public String grade(String grade);
    }

    private DrawerLayout moreInfoDrawerLayout;
    public LocalDataBase dataBase = null;
    String grade;
    private TextView changeLanguageTextView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // db
        if (dataBase == null) {
            dataBase = HujiAssistentApplication.getInstance().getDataBase();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        // application singleton
        HujiAssistentApplication application = (HujiAssistentApplication) getApplication();

        moreInfoDrawerLayout = findViewById(R.id.drawer_layout_more_info);
        NavigationView navigationView = findViewById(R.id.nav_view);
        moreInfoDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        navigationView.setNavigationItemSelectedListener(this);

        // Gets fragments
        FragmentContainerView loginFragment = findViewById(R.id.loginfragment);
        FragmentContainerView topF = findViewById(R.id.topFragment);
        changeLanguageTextView = findViewById(R.id.change_language_textView);
        TopFragment f = new TopFragment();
        FirstFragment firstFragment = new FirstFragment();
        TextViewFragment secondFragment = new TextViewFragment();
        RegisterFragment registerFragment = new RegisterFragment();
        CoursesFragment coursesFragment = new CoursesFragment();
        InfoFragment infoFragment = new InfoFragment();
        MainScreenFragment mainScreenFragment = new MainScreenFragment();
        CourseInfoFragment courseInfoFragment = new CourseInfoFragment();
        PlanCourseInfoFragment planCourseInfoFragment = new PlanCourseInfoFragment();
        PlanCoursesFragment planCoursesFragment = new PlanCoursesFragment();

        getSupportFragmentManager().beginTransaction().replace(loginFragment.getId(), firstFragment, "FIRST_FRAGMENT")
                .commit();

        findViewById(R.id.buttonMoreInfoMapActivity).setOnClickListener(v -> {
            moreInfoDrawerLayout.openDrawer(GravityCompat.START);
        });

        firstFragment.newUserBtnListener = new FirstFragment.newUserButtonClickListener() {
            @Override
            public void onNewUserBtnClicked() {

                getSupportFragmentManager().beginTransaction().setCustomAnimations(
                        R.anim.fade_in,  // enter
                        R.anim.slide_out,  // exit
                        R.anim.slide_in,   // popEnter
                        R.anim.fade_out  // popExit
                )
                        .replace(loginFragment.getId(), registerFragment, "REGISTER_FRAGMENT").addToBackStack(null).commit();
            }
        };

        firstFragment.existingUserBtnListener = new FirstFragment.existingUserButtonClickListener() {
            @Override
            public void onExistingUserBtnClicked() {
                getSupportFragmentManager().beginTransaction().setCustomAnimations(
                        R.anim.fade_in,  // enter
                        R.anim.slide_out,  // exit
                        R.anim.slide_in,   // popEnter
                        R.anim.fade_out  // popExit
                ).replace(loginFragment.getId(), secondFragment, "LOGIN_FRAGMENT").addToBackStack(null).commit();
            }
        };

        secondFragment.continueButtonListener = new TextViewFragment.continueButtonListener() {
            @Override
            public void onContinueButtonClick() {
                getSupportFragmentManager().beginTransaction().setCustomAnimations(
                        R.anim.fade_in,  // enter
                        R.anim.slide_out,  // exit
                        R.anim.slide_in,   // popEnter
                        R.anim.fade_out  // popExit
                )
                        .replace(loginFragment.getId(), infoFragment).addToBackStack(null).commit();
            }
        };

        secondFragment.continueButtonListener = new TextViewFragment.continueButtonListener() {
            @Override
            public void onContinueButtonClick() {
                getSupportFragmentManager().beginTransaction().setCustomAnimations(
                        R.anim.fade_in,  // enter
                        R.anim.slide_out,  // exit
                        R.anim.slide_in,   // popEnter
                        R.anim.fade_out  // popExit
                )
                        .replace(loginFragment.getId(), infoFragment).addToBackStack(null).commit();
            }
        };

        registerFragment.continueButtonListener = new TextViewFragment.continueButtonListener() {
            @Override
            public void onContinueButtonClick() {
                getSupportFragmentManager().beginTransaction().setCustomAnimations(
                        R.anim.fade_in,  // enter
                        R.anim.slide_out,  // exit
                        R.anim.slide_in,   // popEnter
                        R.anim.fade_out  // popExit
                )
                        .replace(loginFragment.getId(), infoFragment, "REGISTER_FRAGMENT").addToBackStack(null).commit();
            }
        };

        infoFragment.continueListener = new InfoFragment.continueButtonClickListener() {
            @Override
            public void continueBtnClicked() {

                getSupportFragmentManager().beginTransaction().setCustomAnimations(
                        R.anim.fade_in,  // enter
                        R.anim.slide_out,  // exit
                        R.anim.slide_in,   // popEnter
                        R.anim.fade_out  // popExit
                )
                        .replace(loginFragment.getId(), coursesFragment).addToBackStack(null).commit();
            }
        };

        coursesFragment.endRegistrationBtnListener = new CoursesFragment.endRegistrationButtonClickListener() {
            @Override
            public void onEndRegistrationBtnClicked() {
                Intent editIntent = new Intent(MainActivity.this, MainScreenActivity.class);
                startActivity(editIntent);
            }
        };


        coursesFragment.onCheckBoxClickListener = new CoursesAdapter.OnCheckBoxClickListener() {
            @Override
            public void onCheckBoxClicked(View v, Course item) {
            }
        };

        coursesFragment.onItemClickListener = new CoursesAdapter.OnItemClickListener() {
            @Override
            public void onClick(Course item) {
                getSupportFragmentManager().beginTransaction().setCustomAnimations(
                        R.anim.fade_in,  // enter
                        R.anim.slide_out,  // exit
                        R.anim.slide_in,   // popEnter
                        R.anim.fade_out  // popExit
                )
                        .replace(loginFragment.getId(), courseInfoFragment, "SELECT_COURSE_ITEM_FRAGMENT").addToBackStack(null).commit();
            }
        };

        changeLanguageTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeLanguageDialog();
            }
        });
    }

    private void goToUrl(String s) {
        Uri url = Uri.parse(s);
        startActivity(new Intent(Intent.ACTION_VIEW, url));
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
            FirstFragment myFragment = (FirstFragment) getSupportFragmentManager().findFragmentByTag("FIRST_FRAGMENT");
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
            } else {
                super.onBackPressed();
            }
        }
    }

    private void showChangeLanguageDialog() {
        final String[] listItems = {"English", "עברית"};
        AlertDialog.Builder mbuilder = new AlertDialog.Builder(MainActivity.this);
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

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
        dataBase.saveLocale(lang);
        Intent refresh = new Intent(this, MainActivity.class);
        startActivity(refresh);
        finish();
    }
}
