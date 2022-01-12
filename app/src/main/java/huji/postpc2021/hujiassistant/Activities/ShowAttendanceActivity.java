package huji.postpc2021.hujiassistant.Activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import huji.postpc2021.hujiassistant.R;
import huji.postpc2021.hujiassistant.ShowAttendanceAdapter;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ShowAttendanceActivity extends AppCompatActivity {

    private final String ATTENDANCE_COLLECTION_NAME = "attendance";
    private final int COURSES_PREVIEW = 0;
    private final int YEAR_PREVIEW = 1;
    private final int IMAGES_PREVIEW = 2;
    private RecyclerView recyclerView;
    private ArrayList<String> courses;

    public ShowAttendanceAdapter adapter;
    FirebaseFirestore firebaseInstancedb = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_attendance);

        // Set layout in 'rtl' direction
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        courses = new ArrayList<>();
        // initializes adapter
        adapter = new ShowAttendanceAdapter(this, courses, COURSES_PREVIEW, "");
        recyclerView.setAdapter(adapter);

        // Sets the courses preview
        firebaseInstancedb.collection(ATTENDANCE_COLLECTION_NAME).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        courses.add(document.getId());
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        int stage = adapter.getStage();
        String savedCourseDocument = adapter.getSavedCourseDocument();

        if (stage == COURSES_PREVIEW) {
            startActivity(new Intent(ShowAttendanceActivity.this, MainScreenActivity.class));
            finish();
        }
        // Go back to courses preview
        else if (stage == YEAR_PREVIEW) {
            adapter = new ShowAttendanceAdapter(this, courses, COURSES_PREVIEW, "");
            recyclerView.setAdapter(adapter);
            courses = new ArrayList<>();
            firebaseInstancedb.collection(ATTENDANCE_COLLECTION_NAME)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            courses.add(document.getId());
                        }
                        adapter.swap(courses);
                    }
                }
            });
        }
        // Go back to years preview
        else if (stage == IMAGES_PREVIEW) {
            String year = new SimpleDateFormat("yyyy").format(new Date());
            adapter = new ShowAttendanceAdapter(this, courses, YEAR_PREVIEW, savedCourseDocument);
            recyclerView.setAdapter(adapter);
            courses = new ArrayList<>();

            firebaseInstancedb.collection(ATTENDANCE_COLLECTION_NAME).document(savedCourseDocument)
                    .collection(year).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    courses.add(document.getId());
                                }
                                adapter.swap(courses);
                            }
                        }
                    });
        }
    }
}
