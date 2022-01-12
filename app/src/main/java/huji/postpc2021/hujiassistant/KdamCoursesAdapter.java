package huji.postpc2021.hujiassistant;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class KdamCoursesAdapter extends RecyclerView.Adapter<KdamCourseItemHolder> {

    private ArrayList<KdamOrAfterCourse> list;
    private Context mContext;

    public KdamCoursesAdapter(Context context) {
        this.list = new ArrayList<>();
        this.mContext = context;
    }

    public void addKdamCoursesListToAdapter(ArrayList<KdamOrAfterCourse> newList) {
        this.list.clear();
        this.list.addAll(newList);
        notifyDataSetChanged();
    }

    public void removeCourseFromAdapter(KdamOrAfterCourse course) {
        this.list.remove(course);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public KdamCourseItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.kdamcourseitem, parent, false);
        return new KdamCourseItemHolder(view);
    }

    public DeleteClickListener deleteListener;
    public CoursesAdapter.CancelClickListener cancelListener;
    public OnItemClickListener itemClickListener;
    public OnCheckBoxClickListener checkBoxClickListener;

    // Create an interface
    public interface DeleteClickListener {
        void onDeleteClick(View v, Course item);
    }

    // Create an interface
    public interface CancelClickListener {
        void onCancelClick(Course item);
    }

    public interface OnCheckBoxClickListener {
        void onCheckBoxClicked(View v, Course item);
    }

    public interface OnItemClickListener {
        public void onClick(Course item);
    }

    public void setItemCheckBoxListener(OnCheckBoxClickListener listener) {
        this.checkBoxClickListener = listener;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull KdamCourseItemHolder holder, int position) {
        KdamOrAfterCourse courseItem = this.list.get(position);
        holder.name.setText(courseItem.getName());
        holder.number.setText(courseItem.getNumber());
        String semesterText = " סמסטר " + courseItem.getSemester();
        holder.semester.setText(semesterText);
        String text = courseItem.getPoints() + " נ''ז ";
        holder.points.setText(text);

        // courses the student has to complete will be colored red
        if (!HujiAssistentApplication.getInstance().getDataBase().getCurrentStudent().getCourses().contains(courseItem.getNumber())) {
            holder.name.setTextColor(Color.RED);
            holder.number.setTextColor(Color.RED);
            holder.semester.setTextColor(Color.RED);
            holder.points.setTextColor(Color.RED);
        } else {
            holder.name.setTextColor(Color.BLACK);
            holder.number.setTextColor(Color.BLACK);
            holder.semester.setTextColor(Color.BLACK);
            holder.points.setTextColor(Color.BLACK);
        }
    }

    public int getItemCount() {
        return this.list.size();
    }

    public ArrayList<KdamOrAfterCourse> getItems() {
        return list;
    }
}
