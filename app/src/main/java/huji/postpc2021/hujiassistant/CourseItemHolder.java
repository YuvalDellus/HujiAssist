package huji.postpc2021.hujiassistant;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CourseItemHolder extends RecyclerView.ViewHolder {

    protected ArrayList<Course> coursesList;

    LocalDataBase dataBase;
    TextView name;
    TextView number;
    TextView type;
    TextView points;
    CardView cardView;
    CheckBox checkBox;
    TextView textView;
    TextView grade;
    TextView gradeAddBtn;
    TextView deleteButton;
    TextView gradeDescription;
    TextView saveTextiew;

    public CourseItemHolder(View view) {
        super(view);
        this.coursesList = new ArrayList<>();
        dataBase = HujiAssistentApplication.getInstance().getDataBase();
        name = view.findViewById(R.id.courseNameHolder);
        number = view.findViewById(R.id.courseNumberHolder);
        type = view.findViewById(R.id.courseTypeHolder);
        points = view.findViewById(R.id.coursepoints);
        cardView = view.findViewById(R.id.courseitemcard);
        checkBox = view.findViewById(R.id.checkBoxCourse);
        textView = view.findViewById(R.id.textViewParent);
        grade = view.findViewById(R.id.coursegradeHolder);
        deleteButton = view.findViewById(R.id.deleteButton);
        gradeAddBtn = view.findViewById(R.id.textViewAddGrade);
        gradeDescription = view.findViewById(R.id.textviewgrade);
        saveTextiew = view.findViewById(R.id.saveTextView);
    }

    public ArrayList<Course> getCurrentItems() {
        return this.coursesList;
    }

}
