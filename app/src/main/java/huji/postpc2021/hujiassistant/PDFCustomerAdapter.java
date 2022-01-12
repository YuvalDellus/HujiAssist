package huji.postpc2021.hujiassistant;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import huji.postpc2021.hujiassistant.Activities.PDFActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PDFCustomerAdapter extends RecyclerView.Adapter<PDFCustomerAdapter.MyViewHolder> {

    private ArrayList<PDFDoc> pdfDocs;
    private Context context;
    private int stage;
    private String savedCourse;
    private String savedDate;
    private DatabaseReference root;
    private List<PDFDoc> dataImages;
    private static final int PDF_TYPE = 2;
    private final String DOCUMENTS_COLLECTION_NAME = "Documents";
    private final int COURSES_PREVIEW = 0;
    private final int YEAR_PREVIEW = 1;
    private final int STUDENTS_PREVIEW = 2;


    public PDFCustomerAdapter(Context context, ArrayList<PDFDoc> pdfDocs, int stage, String savedCourse) {
        this.context = context;
        this.pdfDocs = pdfDocs;
        this.stage = stage;
        this.savedCourse = savedCourse;
        this.savedDate = "";
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.model_pdf, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        dataImages = new ArrayList<>();

        if (stage == STUDENTS_PREVIEW) {
            holder.imageView.setImageResource(R.drawable.ic_pdf_icon);
            holder.imageTitle.setText(pdfDocs.get(position).getName());
        } else {
            holder.imageView.setImageResource(R.drawable.ic_baseline_folder_256);
            holder.imageTitle.setText(pdfDocs.get(position).getName());
        }

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (stage == COURSES_PREVIEW) {
                    savedCourse = holder.imageTitle.getText().toString();
                    String path = DOCUMENTS_COLLECTION_NAME + "/" + savedCourse;
                    root = FirebaseDatabase.getInstance().getReference(path);

                    root.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                String course = dataSnapshot.getKey();
                                PDFDoc pdfDoc = new PDFDoc();
                                pdfDoc.setName(course);
                                dataImages.add(pdfDoc);
                            }
                            swapImages(dataImages);
                            stage = YEAR_PREVIEW;
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else if (stage == YEAR_PREVIEW) {
                    savedDate = holder.imageTitle.getText().toString();
                    String path = DOCUMENTS_COLLECTION_NAME + "/" + savedCourse + "/" + savedDate;
                    root = FirebaseDatabase.getInstance().getReference(path);

                    root.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Model model = dataSnapshot.getValue(Model.class);
                                assert model != null;
                                String course = model.getName();
                                PDFDoc pdfDoc = new PDFDoc();
                                pdfDoc.setName(course);
                                pdfDoc.setId(dataSnapshot.getKey());
                                dataImages.add(pdfDoc);
                            }
                            swapImages(dataImages);
                            stage = STUDENTS_PREVIEW;
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else if (stage == STUDENTS_PREVIEW) {
                    String path = DOCUMENTS_COLLECTION_NAME + "/" + savedCourse + "/" + savedDate;

                    root = FirebaseDatabase.getInstance().getReference(path);

                    root.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                if (dataSnapshot.getKey().equals(pdfDocs.get(position).getId())) {
                                    Model model = dataSnapshot.getValue(Model.class);
                                    assert model != null;
                                    if (model.getType() == PDF_TYPE) {
                                        PDFDoc pdfDoc = new PDFDoc();
                                        pdfDoc.setName(model.getName());
                                        pdfDoc.setPath(model.getImageUrl());
                                        openPDFView(pdfDoc.getPath());
                                        break;
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return pdfDocs.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView imageTitle;
        String id;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.m_image);
            imageTitle = itemView.findViewById(R.id.info_text);
            id = "";
            imageTitle.setTypeface(null, Typeface.BOLD_ITALIC);

        }
    }

    public void swapImages(List<PDFDoc> datas) {
        pdfDocs.clear();
        pdfDocs.addAll(datas);
        notifyDataSetChanged();
    }

    public int getStage() {
        return stage;
    }

    public String getSavedCourse() {
        return savedCourse;
    }

    private void openPDFView(String path) {
        Intent i = new Intent(context, PDFActivity.class);
        i.putExtra("PATH", path);
        context.startActivity(i);
    }
}
