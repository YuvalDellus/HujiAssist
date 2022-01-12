
package huji.postpc2021.hujiassistant.Fragments;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import huji.postpc2021.hujiassistant.Course;
import huji.postpc2021.hujiassistant.HujiAssistentApplication;
import huji.postpc2021.hujiassistant.LocalDataBase;
import huji.postpc2021.hujiassistant.R;
import huji.postpc2021.hujiassistant.ViewModelApp;

import java.util.ArrayList;

public class AddCourseFragment extends Fragment {

    private ViewModelApp viewModelApp;
    ArrayList list;
    public LocalDataBase dataBase = null;

    public AddCourseFragment(){
        super(R.layout.addcourse_fragment);
    }
    public AddCourseFragment.addCourseToListButtonClickListener addCourseToListButtonClickListener = null;
    public interface addCourseToListButtonClickListener{
        public void addCourseToListBtnClicked(String id);
    }
    private boolean isCourseIdValid = false;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewModelApp viewModelApp = new ViewModelProvider(requireActivity()).get(ViewModelApp.class);
        Button addCourseBtn = view.findViewById(R.id.addCourseToListBtn);
        TextView courseIdTextView = view.findViewById(R.id.insertCourseNumber);

        if (dataBase == null){
            dataBase = HujiAssistentApplication.getInstance().getDataBase();
        }
        ArrayList<Course> courseItems = dataBase.getMyCoursesList();

        addCourseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String courseIdToAdd = courseIdTextView.getText().toString();

                checkIfValidCourseNumber(courseIdToAdd);

                if (isCourseIdValid) {
                    String text = getResources().getString(R.string.numberofcoursetoast) + " " +  courseIdToAdd +" "+getResources().getString(R.string.addingcoursemsgsuccess);
                    Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
                    dataBase.addCourseId(courseIdToAdd);
                    courseIdTextView.setText("");
                }
            }
        });

        list = new ArrayList<>(courseItems);
    }

    // Checks if the number of course to add is correct
    private void checkIfValidCourseNumber(String courseToAddId){
        // Gets the list of courses of current student
        ArrayList<String> courses = dataBase.getCurrentStudent().getCourses();
        ArrayList<Course> coursesFromFireBase = dataBase.getCoursesFromFireBase();

        // go to firebase to check if course exists
        boolean isExists = checkIfExists(courseToAddId);

        if (!isExists){
            String text = getResources().getString(R.string.courseDoesntExistsInFireBase);
            Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
            isCourseIdValid = false;
        }
        else {
            if (courses.contains(courseToAddId)) {
                String text = getResources().getString(R.string.courseExistsInMyCoursesList);
                Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
                isCourseIdValid = false;
            } else {
                isCourseIdValid = true;
            }
        }
    }

    private boolean checkIfExists(String courseId){

        ArrayList<Course> coursesFromFireBase = dataBase.getCoursesFromFireBase();
        for (Course course : coursesFromFireBase) {
            if (course.getNumber().equals(courseId)){
                return true;
            }
        }
        return false;
    }
}
