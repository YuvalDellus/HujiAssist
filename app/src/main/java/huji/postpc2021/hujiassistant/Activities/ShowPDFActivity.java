package huji.postpc2021.hujiassistant.Activities;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import huji.postpc2021.hujiassistant.PDFCustomerAdapter;
import huji.postpc2021.hujiassistant.PDFDoc;
import huji.postpc2021.hujiassistant.R;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShowPDFActivity extends AppCompatActivity {

    private final String DOCUMENTS_COLLECTION_NAME = "Documents";
    private final int COURSES_PREVIEW = 0;
    private final int YEAR_PREVIEW = 1;
    private final int DOCUMENTS_PREVIEW = 2;
    private RecyclerView recyclerView;
    private ArrayList<PDFDoc> PDFList;
    private PDFCustomerAdapter adapter;
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
        root = FirebaseDatabase.getInstance().getReference(DOCUMENTS_COLLECTION_NAME);

        PDFList = new ArrayList<>();
        // initializes adapter
        adapter = new PDFCustomerAdapter(this, PDFList, COURSES_PREVIEW, "");
        recyclerView.setAdapter(adapter);

        // Sets the courses preview
        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String course = dataSnapshot.getKey();
                    assert course != null;
                    PDFDoc pdfDoc = new PDFDoc();
                    pdfDoc.setName(course);
                    PDFList.add(pdfDoc);
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
            adapter = new PDFCustomerAdapter(this, PDFList, COURSES_PREVIEW, "");
            recyclerView.setAdapter(adapter);
            PDFList = new ArrayList<>();

            root = FirebaseDatabase.getInstance().getReference(DOCUMENTS_COLLECTION_NAME);
            root.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String course = dataSnapshot.getKey();
                        assert course != null;
                        PDFDoc pdfDoc = new PDFDoc();
                        pdfDoc.setName(course);
                        PDFList.add(pdfDoc);
                    }
                    adapter.swapImages(PDFList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        // Go back to years preview
        else if (stage == DOCUMENTS_PREVIEW) {
            adapter = new PDFCustomerAdapter(this, PDFList, YEAR_PREVIEW, savedCourse);
            recyclerView.setAdapter(adapter);
            PDFList = new ArrayList<>();

            String path = DOCUMENTS_COLLECTION_NAME + "/" + savedCourse;
            root = FirebaseDatabase.getInstance().getReference(path);

            root.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String course = dataSnapshot.getKey();
                        PDFDoc pdfDoc = new PDFDoc();
                        pdfDoc.setName(course);
                        PDFList.add(pdfDoc);
                    }
                    adapter.swapImages(PDFList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}
