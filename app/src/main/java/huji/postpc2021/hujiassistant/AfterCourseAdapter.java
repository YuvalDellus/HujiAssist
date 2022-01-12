package huji.postpc2021.hujiassistant;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AfterCourseAdapter extends RecyclerView.Adapter<AfterCourseItemHolder> {

    private ArrayList<KdamOrAfterCourse> list;
    private Context mContext;

    public AfterCourseAdapter(Context context) {
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
    public AfterCourseItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.kdamcourseitem, parent, false);
        return new AfterCourseItemHolder(view);
    }

    public huji.postpc2021.hujiassistant.CoursesAdapter.DeleteClickListener deleteListener;
    public huji.postpc2021.hujiassistant.CoursesAdapter.CancelClickListener cancelListener;
    public huji.postpc2021.hujiassistant.CoursesAdapter.OnItemClickListener itemClickListener;
    public huji.postpc2021.hujiassistant.CoursesAdapter.OnCheckBoxClickListener checkBoxClickListener;


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

    public void setItemClickListener(huji.postpc2021.hujiassistant.CoursesAdapter.OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public void setItemCheckBoxListener(huji.postpc2021.hujiassistant.CoursesAdapter.OnCheckBoxClickListener listener) {
        this.checkBoxClickListener = listener;
    }

    public void setDeleteListener(huji.postpc2021.hujiassistant.CoursesAdapter.DeleteClickListener listener) {
        this.deleteListener = listener;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull AfterCourseItemHolder holder, int position) {
        KdamOrAfterCourse courseItem = this.list.get(position);
        holder.name.setText(courseItem.getName());
        holder.number.setText(courseItem.getNumber());
        String semesterText = " סמסטר " + courseItem.getSemester();
        holder.semester.setText(semesterText);
        String text = courseItem.getPoints() + " נ''ז ";
        holder.points.setText(text);
    }


    public int getItemCount() {
        return this.list.size();
    }

    public ArrayList<KdamOrAfterCourse> getItems() {
        return list;
    }

}
