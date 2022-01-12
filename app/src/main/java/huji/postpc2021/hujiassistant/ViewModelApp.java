package huji.postpc2021.hujiassistant;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


public class ViewModelApp extends ViewModel {

    public MutableLiveData<StudentInfo> studentInfoMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<Course> courseMutableLiveData = new MutableLiveData<>();

    public ViewModelApp(FragmentActivity fragmentActivity) {
    }

    public ViewModelApp() {

    }

    public void setStudent(StudentInfo student) {
        studentInfoMutableLiveData.setValue(student);
    }

    public void setCourse(Course course) {
        courseMutableLiveData.setValue(course);
    }

    public MutableLiveData<StudentInfo> getStudent() {
        if (studentInfoMutableLiveData == null) {
            studentInfoMutableLiveData = new MutableLiveData<>();
        }
        return studentInfoMutableLiveData;
    }

    public MutableLiveData<Course> getCourse() {
        if (courseMutableLiveData == null) {
            courseMutableLiveData = new MutableLiveData<>();
        }
        return courseMutableLiveData;
    }

}
