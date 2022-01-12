package huji.postpc2021.hujiassistant.Activities;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import huji.postpc2021.hujiassistant.Model;
import huji.postpc2021.hujiassistant.R;
import huji.postpc2021.hujiassistant.ShowImagesAdapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShowImagesActivity extends AppCompatActivity {

    private final String IMAGES_COLLECTION_NAME = "Image";
    private final int COURSES_PREVIEW = 0;
    private final int YEAR_PREVIEW = 1;
    private final int IMAGES_PREVIEW = 2;
    private RecyclerView recyclerView;
    private ArrayList<Model> list;
    private ShowImagesAdapter adapter;
    private DatabaseReference root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        // Set layout in 'rtl' direction
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        root = FirebaseDatabase.getInstance().getReference(IMAGES_COLLECTION_NAME);

        list = new ArrayList<>();
        // initializes adapter
        adapter = new ShowImagesAdapter(this, list, COURSES_PREVIEW, "");
        recyclerView.setAdapter(adapter);

        // Sets the courses preview
        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String course = dataSnapshot.getKey();
                    assert course != null;
                    list.add(new Model("", course, COURSES_PREVIEW));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        int stage = adapter.getStage();
        String savedCourse = adapter.getSavedCourse();

        if (stage == COURSES_PREVIEW) {
            super.onBackPressed();
        }
        // Go back to courses preview
        else if (stage == YEAR_PREVIEW) {
            adapter = new ShowImagesAdapter(this, list, COURSES_PREVIEW, "");
            recyclerView.setAdapter(adapter);
            list = new ArrayList<>();

            root = FirebaseDatabase.getInstance().getReference(IMAGES_COLLECTION_NAME);
            root.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String course = dataSnapshot.getKey();
                        assert course != null;
                        list.add(new Model("", course, COURSES_PREVIEW));
                    }
                    adapter.swapImages(list);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        // Go back to years preview
        else if (stage == IMAGES_PREVIEW) {
            adapter = new ShowImagesAdapter(this, list, 1, savedCourse);
            recyclerView.setAdapter(adapter);
            list = new ArrayList<>();

            String path = IMAGES_COLLECTION_NAME + "/" + savedCourse;
            root = FirebaseDatabase.getInstance().getReference(path);

            root.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String course = dataSnapshot.getKey();
                        list.add(new Model("", course, COURSES_PREVIEW));
                    }
                    adapter.swapImages(list);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}
