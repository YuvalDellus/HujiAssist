package huji.postpc2021.hujiassistant;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class ScheduleItemHolder extends RecyclerView.ViewHolder {

    LocalDataBase dataBase;
    TextView type;
    TextView day;
    TextView group;
    TextView teacher;
    TextView location;
    TextView textView;
    TextView time;
    CardView cardView;
    CheckBox checkBox;

    public ScheduleItemHolder(@NonNull View itemView) {
        super(itemView);
        dataBase = HujiAssistentApplication.getInstance().getDataBase();
        type = itemView.findViewById(R.id.scheduleTypeHolder);
        day = itemView.findViewById(R.id.scheduleDayHolder);
        group = itemView.findViewById(R.id.scheduleGroup);
        teacher = itemView.findViewById(R.id.scheduleTeacherHolder);
        location = itemView.findViewById(R.id.scheduleLocationHolder);
        time = itemView.findViewById(R.id.scheduleHoursHolder);
        cardView = itemView.findViewById(R.id.scheduleitemcard);
    }
}