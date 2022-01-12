package huji.postpc2021.hujiassistant;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
//import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PlanCoursesAdapter extends RecyclerView.Adapter<PlanCourseItemHolder> {
    private ArrayList<Course> list;

    class CustomFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint != null && constraint.length() > 0) {

                constraint = constraint.toString().toUpperCase();
                ArrayList<String> filters = new ArrayList<>();

                for (int i = 0; i < filterList.size(); i++) {
                    if (filterList.get(i).getName().toUpperCase().contains(constraint)) {
                        filters.add(filterList.get(i).getName());
                    }
                }

                results.count = filters.size();
                results.values = filters;
            } else {
                results.count = filterList.size();
                results.values = filterList;
            }
            return results;
        }

        protected void publishResults(CharSequence constraint, FilterResults results) {
            list = (ArrayList<Course>) results.values;
            notifyDataSetChanged();
        }
    }

    private Context mContext;
    ArrayList<Course> filterList;
    CustomFilter filter;

    public Filter getFilter() {
        if (filter == null) {
            filter = new CustomFilter();
        }
        return filter;
    }

    public PlanCoursesAdapter(Context context) {
        this.list = new ArrayList<>();
        this.filterList = list;
        this.mContext = context;
    }

    public void addCoursesListToAdapter(ArrayList<Course> newList) {
        this.list.clear();
        this.list.addAll(newList);
        notifyDataSetChanged();
    }

    public void removeCourseFromAdapter(Course course) {
        this.list.remove(course);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlanCourseItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.plancourseitemholder, parent, false);
        return new PlanCourseItemHolder(view);
    }

    public CancelClickListener cancelListener;
    public OnItemClickListener itemClickListener;
    public OnCheckBoxClickListener checkBoxClickListener;

    // Create an interface
    public interface CancelClickListener {
        void onCancelClick(Course item);
    }

    public interface OnCheckBoxClickListener {
        void onCheckBoxClicked(View v, Course item);
    }

    public interface OnItemClickListener {
        //  public void onClick(View view, int position);
        void onClick(Course item);
    }

    public void setItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public void setItemCheckBoxListener(OnCheckBoxClickListener listener) {
        this.checkBoxClickListener = listener;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull PlanCourseItemHolder holder, int position) {
        LocalDataBase db = HujiAssistentApplication.getInstance().getDataBase();
        FirebaseFirestore firebaseInstancedb = db.getFirestoreDB();
        Course courseItem = this.list.get(position);

        if (courseItem.getName().startsWith("שנה ")) {
            holder.name.setText(courseItem.getName());
            holder.name.setTextColor(Color.BLACK);
            holder.checkBox.setVisibility(View.INVISIBLE);
            holder.number.setVisibility(View.INVISIBLE);
            holder.type.setVisibility(View.INVISIBLE);
            holder.grade.setVisibility(View.INVISIBLE);
            holder.points.setVisibility(View.INVISIBLE);
            holder.cardView.setCardBackgroundColor(Color.WHITE);
            holder.itemView.setOnClickListener(v -> {
            });

            return;
        } else {

            holder.checkBox.setVisibility(View.VISIBLE);
            holder.number.setVisibility(View.VISIBLE);
            holder.type.setVisibility(View.VISIBLE);
            holder.points.setVisibility(View.VISIBLE);

            holder.checkBox.setChecked(courseItem.getPlannedChecked());
            holder.name.setText(courseItem.getName());
            holder.number.setText(courseItem.getNumber());
            holder.type.setText(courseItem.getType());
            holder.grade.setVisibility(View.INVISIBLE);
            String text = courseItem.getPoints() + " נ''ז ";
            holder.points.setText(text);

            try {
                // Yuval: this method find the kdam courses that missing to the student and coloring it red,
                //  however, sometimes need only one from each group, ex need infi or infi for math, but not both,
                //  so if one is done they both don't need to be red.
                // this need to be more group oriented, need only on course of group ** and one of ** ** and so on, need to be fixed.
                Task<QuerySnapshot> document = firebaseInstancedb.collection("coursesTestOnlyCs").document(db.getCurrentStudent().getChugId())
                        .collection("maslulimInChug").document(db.getCurrentStudent().getMaslulId()).collection("coursesInMaslul")
                        .document(courseItem.getNumber())
                        .collection("kdamCourses")
                        .get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                List<DocumentSnapshot> documents = task.getResult().getDocuments();
                                for (DocumentSnapshot document1 : documents) {
                                    KdamOrAfterCourse course = document1.toObject(KdamOrAfterCourse.class);
                                    if (!HujiAssistentApplication.getInstance().getDataBase().getCurrentStudent().getCourses().contains(course.getNumber())) {
                                        holder.name.setTextColor(Color.RED);
                                        break; // one course missing is enough for now
                                    }
                                }
                            }
                        });
            } catch (Exception e) {
//                Log.i("ERROR", "failed to get kdam courses");
            }

            holder.checkBox.setOnClickListener(v -> {
                if (holder.checkBox.isChecked()) {
                    courseItem.setPlannedChecked(true);
                    courseItem.setPlanned(true);
                    courseItem.setChecked(true);
                    if (holder.name.getCurrentTextColor() == Color.RED) { // missing some kdam Courses
                        Toast.makeText(mContext, "Notice: you got some Course kdam missing", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    courseItem.setPlanned(false);
                    courseItem.setChecked(false);
                    courseItem.setPlannedChecked(false);
                }
                checkBoxClickListener.onCheckBoxClicked(v, courseItem);
            });

            holder.itemView.setOnClickListener(v -> {
                itemClickListener.onClick(courseItem);
            });

            switch (courseItem.getType()) {
                case "לימודי חובה":
                    holder.cardView.setCardBackgroundColor(this.mContext.getResources().getColor(R.color.lightblue1));
                    break;
                case "או": // plaster
                case "וגם": // plaster
                    holder.cardView.setCardBackgroundColor(this.mContext.getResources().getColor(R.color.lightblue1));
                    holder.type.setText("לימודי חובה");
                    courseItem.setType("לימודי חובה");
                    break;
                case "לימודי חובת בחירה":
                    holder.cardView.setCardBackgroundColor(this.mContext.getResources().getColor(R.color.lightblue2));
                    break;
                case "קורסי בחירה":
                    holder.cardView.setCardBackgroundColor(this.mContext.getResources().getColor(R.color.lightblue3));
                    break;
                case "משלימים":
                    holder.cardView.setCardBackgroundColor(this.mContext.getResources().getColor(R.color.lightblue4));
                    break;
                case "אבני פינה":
                    holder.cardView.setCardBackgroundColor(this.mContext.getResources().getColor(R.color.lightblue5));
                    break;
                default:
                    break;
            }

        }
    }

    public int getItemCount() {
        return this.list.size();
    }

    public ArrayList<Course> getItems() {
        return list;
    }
}

