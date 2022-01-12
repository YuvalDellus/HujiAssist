package huji.postpc2021.hujiassistant.Fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import huji.postpc2021.hujiassistant.HujiAssistentApplication;
import huji.postpc2021.hujiassistant.LocalDataBase;
import huji.postpc2021.hujiassistant.R;
import huji.postpc2021.hujiassistant.StudentInfo;
import huji.postpc2021.hujiassistant.ViewModelApp;
import huji.postpc2021.hujiassistant.Course;
import huji.postpc2021.hujiassistant.HujiAssistentApplication;
import huji.postpc2021.hujiassistant.LocalDataBase;
import huji.postpc2021.hujiassistant.PlanCoursesAdapter;
import huji.postpc2021.hujiassistant.R;
import huji.postpc2021.hujiassistant.StudentInfo;
import huji.postpc2021.hujiassistant.ViewModelAppMainScreen;
import huji.postpc2021.hujiassistant.databinding.FragmentPlancoursesBinding;
import huji.postpc2021.hujiassistant.CourseScheduleEntry;
import huji.postpc2021.hujiassistant.HujiAssistentApplication;
import huji.postpc2021.hujiassistant.KdamCoursesAdapter;
import huji.postpc2021.hujiassistant.KdamOrAfterCourse;
import huji.postpc2021.hujiassistant.LocalDataBase;
import huji.postpc2021.hujiassistant.R;
import huji.postpc2021.hujiassistant.ScheduleAdapter;
import huji.postpc2021.hujiassistant.ViewModelAppMainScreen;
import huji.postpc2021.hujiassistant.Maslul;


public class TopFragment extends Fragment {

    public TopFragment(){
        super(R.layout.topfragment);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView text = view.findViewById(R.id.screennametextview);
        text.setText("מסך רישום משתמש חדש");
        text.setVisibility(View.VISIBLE);

    }
}

