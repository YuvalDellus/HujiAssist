package huji.postpc2021.hujiassistant;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleItemHolder> {
    private ArrayList<CourseScheduleEntry> list = new ArrayList<>();
    private LocalDataBase dataBase;
    public OnCheckBoxClickListener checkBoxClickListener;

    @NonNull
    @Override
    public ScheduleItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.scheduleitem, parent, false);
        return new ScheduleItemHolder(view);
    }

    public interface OnCheckBoxClickListener {
        void onCheckBoxClicked(View v, CourseScheduleEntry item);
    }

    public void setItemCheckBoxListener(ScheduleAdapter.OnCheckBoxClickListener listener) {
        this.checkBoxClickListener = listener;
    }

    public void addScheduleListToAdapter(ArrayList<CourseScheduleEntry> schedules) {
        this.list.clear();
        this.list.addAll(schedules);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleItemHolder holder, int position) {
        CourseScheduleEntry scheduleEntry = this.list.get(position);
        dataBase = HujiAssistentApplication.getInstance().getDataBase();

        String hours = scheduleEntry.getStarting() + " - " + scheduleEntry.getEnding();
        String dayText = " יום " + scheduleEntry.getDay();
        String groupText = " קבוצה " + scheduleEntry.getGroup();

        holder.day.setText(dayText);
        holder.group.setText(groupText);
        holder.location.setText(scheduleEntry.getLocation());
        holder.teacher.setText(scheduleEntry.getTeacher());
        holder.type.setText(scheduleEntry.getType());
        holder.time.setText(hours);


        //TODO: no chceck box in this version, maybe in next one
//        holder.checkBox.setOnClickListener(v -> {
//            System.out.println("check box clicked");
//            if (holder.checkBox.isChecked()){
//                list.add(scheduleEntry);
//            }
//            else{
//                list.remove(scheduleEntry);
//            }
//            dataBase.getCurrentStudent().setSchedulePlannedByStudent(list);
//            checkBoxClickListener.onCheckBoxClicked(v, scheduleEntry);
//        });

    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }
}