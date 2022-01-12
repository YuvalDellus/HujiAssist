package huji.postpc2021.hujiassistant;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ViewModelAppMainScreen extends ViewModel {

    public MutableLiveData<Course> courseMutableLiveData = new MutableLiveData<>();

    public ViewModelAppMainScreen(FragmentActivity fragmentActivity) {
    }

    public ViewModelAppMainScreen() {
    }

    public void set(Course course) {
        courseMutableLiveData.setValue(course);
    }

    public MutableLiveData<Course> get() {
        if (courseMutableLiveData == null) {
            courseMutableLiveData = new MutableLiveData<>();
        }
        return courseMutableLiveData;
    }
}
