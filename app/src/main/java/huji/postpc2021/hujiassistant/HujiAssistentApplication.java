package huji.postpc2021.hujiassistant;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

import huji.postpc2021.hujiassistant.Fragments.InfoFragment;

public class HujiAssistentApplication extends Application {

    private UUID id;
    private LocalDataBase dataBase;
    private SharedPreferences sp;
    private static HujiAssistentApplication instance = null;
    private RequestQueue mRequestQueue;
    private InfoFragment infoFragment = null;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        instance = this;
        dataBase = new LocalDataBase(this); // pass the current context to allow broadcasts
        sp = this.getSharedPreferences("local_work_sp", Context.MODE_PRIVATE);
        // singleton of work manager
        // androidx.work.WorkManager workManager = androidx.work.WorkManager.getInstance(this);
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    String TAG = "TAG";

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public LocalDataBase getDataBase() {
        return dataBase;
    }

    public static HujiAssistentApplication getInstance() {
        return instance;
    }

    UUID getWorkerId() {
        return id;
    }

    public SharedPreferences getWorkSp() {
        return sp;
    }

    public InfoFragment getInfoFragment() {
        return infoFragment;
    }

    public void setInfoFragment(InfoFragment infoFragment) {
        this.infoFragment = infoFragment;
    }
}
