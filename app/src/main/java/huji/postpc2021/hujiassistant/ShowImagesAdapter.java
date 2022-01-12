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

import com.bumptech.glide.Glide;

import huji.postpc2021.hujiassistant.Activities.ImageActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ShowImagesAdapter extends RecyclerView.Adapter<ShowImagesAdapter.MyViewHolder> {

    private ArrayList<Model> mModels;
    private Context context;
    private int stage;
    private String savedCourse;
    private String savedDate;
    private DatabaseReference root;
    private List<Model> dataImages;
    private static final int PDF_TYPE = 2;
    private final String IMAGES_COLLECTION_NAME = "Image";
    private final int COURSES_PREVIEW = 0;
    private final int YEAR_PREVIEW = 1;
    private final int IMAGES_PREVIEW = 2;


    public ShowImagesAdapter(Context context, ArrayList<Model> mModels, int stage, String savedCourse) {
        this.context = context;
        this.mModels = mModels;
        this.stage = stage;
        this.savedCourse = savedCourse;
        this.savedDate = "";
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;

        if (stage == IMAGES_PREVIEW) {
            v = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        } else {
            v = LayoutInflater.from(context).inflate(R.layout.model_pdf, parent, false);
        }
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        dataImages = new ArrayList<>();

        if (stage == IMAGES_PREVIEW) {
            Glide.with(context).load(mModels.get(position).getImageUrl()).into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.ic_baseline_folder_256);
        }
        holder.imageTitle.setText(mModels.get(position).getName());

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (stage == COURSES_PREVIEW) {
                    savedCourse = holder.imageTitle.getText().toString();
                    String path = IMAGES_COLLECTION_NAME + "/" + savedCourse;
                    root = FirebaseDatabase.getInstance().getReference(path);

                    root.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                String course = dataSnapshot.getKey();
                                dataImages.add(new Model("", course, 0));
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
                    String path = IMAGES_COLLECTION_NAME + "/" + savedCourse + "/" + savedDate;
                    root = FirebaseDatabase.getInstance().getReference(path);

                    root.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Model model = dataSnapshot.getValue(Model.class);
                                assert model != null;
                                if (model.getType() != PDF_TYPE) {
                                    model.setId(dataSnapshot.getKey());
                                    dataImages.add(model);
                                }
                            }
                            swapImages(dataImages);
                            stage = IMAGES_PREVIEW;
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else if (stage == IMAGES_PREVIEW) {
                    String path = IMAGES_COLLECTION_NAME + "/" + savedCourse + "/" + savedDate;

                    root = FirebaseDatabase.getInstance().getReference(path);

                    root.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                if (dataSnapshot.getKey().equals(mModels.get(position).getId())) {
                                    Model model = dataSnapshot.getValue(Model.class);
                                    assert model != null;
                                    if (model.getType() != PDF_TYPE) {
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
        return mModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView imageTitle;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.m_image);
            imageTitle = itemView.findViewById(R.id.info_text);
            imageTitle.setTypeface(null, Typeface.BOLD_ITALIC);

        }
    }

    public void swapImages(List<Model> datas) {
        mModels.clear();
        mModels.addAll(datas);
        notifyDataSetChanged();
    }

    public int getStage() {
        return stage;
    }

    public String getSavedCourse() {
        return savedCourse;
    }

    private void openPDFView(String path) {
        Intent i = new Intent(context, ImageActivity.class);
        i.putExtra("PATH", path);
        context.startActivity(i);
    }
}
