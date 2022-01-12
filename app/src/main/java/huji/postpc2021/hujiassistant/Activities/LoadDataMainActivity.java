package huji.postpc2021.hujiassistant.Activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import huji.postpc2021.hujiassistant.HujiAssistentApplication;
import huji.postpc2021.hujiassistant.LocalDataBase;
import huji.postpc2021.hujiassistant.R;

import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

public class LoadDataMainActivity extends AppCompatActivity {

    LocalDataBase db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wait_screen);

        db = HujiAssistentApplication.getInstance().getDataBase();

        FirebaseUser currentUser = db.getUsersAuthenticator().getCurrentUser();
        // Update current language
        // db.logoutUser();
        setLocale(db.loadLocale());

        // update UI when DB finish to load the initial data
        db.firstLoadFlagLiveData.observe(this, isLoad -> {
            if (isLoad) {
                updateUI(currentUser);
            }
        });
    }

    /**
     * if there is a logged in user -> navigate to the map screen, else navigate to the login screen
     *
     * @param firebaseUser - the currently logged in user, null if no user os logged in
     */
    private void updateUI(FirebaseUser firebaseUser) {

        if (firebaseUser != null) {
            LocalDataBase db = HujiAssistentApplication.getInstance().getDataBase();
            db.setCurrentUser(firebaseUser);
            db.currentUserLiveData.observe(this, user -> {
                // If the user is already logged in
                if (user != null) {
                    startActivity(new Intent(this, MainScreenActivity.class));
                    finish();
                }
            });
            // If the user needs to logged in
        } else {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    // Update the current language
    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
        db.saveLocale(lang);
    }
}
