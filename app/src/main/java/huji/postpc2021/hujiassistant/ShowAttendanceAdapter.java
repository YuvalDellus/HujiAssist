package huji.postpc2021.hujiassistant;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ShowAttendanceAdapter extends RecyclerView.Adapter<ShowAttendanceAdapter.MyViewHolder> {

    private ArrayList<String> mListCourses;
    private Context context;
    private String savedCourseDocument;
    private int stage;
    private String savedDateDocument;
    private List<String> data;
    private FirebaseFirestore firebaseInstancedb = FirebaseFirestore.getInstance();
    private final String ATTENDANCE_COLLECTION_NAME = "attendance";
    private final String DUMMY_COLLECTION = "dummy";
    private final int COURSES_PREVIEW = 0;
    private final int YEAR_PREVIEW = 1;
    private final int STUDENTS_PREVIEW = 2;

    public ShowAttendanceAdapter(Context context, ArrayList<String> mList, int stage, String savedCourseDocument) {
        this.context = context;
        this.mListCourses = mList;
        this.stage = stage;
        this.savedCourseDocument = savedCourseDocument;
        this.savedDateDocument = "";
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.attendance_course_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String year = new SimpleDateFormat("yyyy").format(new Date());
        data = new ArrayList<>();
        holder.imageTitle.setText(mListCourses.get(position));
        if (stage == STUDENTS_PREVIEW) {
            holder.imageView.setImageResource(R.drawable.ic_baseline_person_24);
        } else {
            holder.imageView.setImageResource(R.drawable.ic_baseline_folder_24);
        }

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (stage == COURSES_PREVIEW) {
                    savedCourseDocument = holder.imageTitle.getText().toString();
                    firebaseInstancedb.collection(ATTENDANCE_COLLECTION_NAME).document(holder.imageTitle.getText().toString()).collection(year).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    data.add(document.getId());
                                }
                                swap(data);
                            }
                        }
                    });
                    stage = YEAR_PREVIEW;
                } else if (stage == YEAR_PREVIEW) {
                    savedDateDocument = holder.imageTitle.getText().toString();
                    firebaseInstancedb.collection(ATTENDANCE_COLLECTION_NAME).document(savedCourseDocument).collection(year).document(holder.imageTitle.getText().toString()).collection(DUMMY_COLLECTION).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Map<String, Object> map = document.getData();
                                    data.addAll(map.keySet());
                                }
                                swap(data);
                            }
                        }
                    });
                    stage = STUDENTS_PREVIEW;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mListCourses.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView imageTitle;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.pdfImage);
            imageTitle = itemView.findViewById(R.id.nameTxt);
            imageTitle.setTypeface(null, Typeface.BOLD_ITALIC);
        }
    }

    public void swap(List<String> data) {
        mListCourses.clear();
        mListCourses.addAll(data);
        notifyDataSetChanged();
    }

    public int getStage() {
        return stage;
    }

    public String getSavedCourseDocument() {
        return this.savedCourseDocument;
    }
}
