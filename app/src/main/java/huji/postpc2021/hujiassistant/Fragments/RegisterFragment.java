package huji.postpc2021.hujiassistant.Fragments;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import huji.postpc2021.hujiassistant.HujiAssistentApplication;
import huji.postpc2021.hujiassistant.LocalDataBase;
import huji.postpc2021.hujiassistant.R;
import huji.postpc2021.hujiassistant.StudentInfo;
import huji.postpc2021.hujiassistant.ViewModelApp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
    This fragment is a part of the registration process. Allows the user to create a new user
    in the db with an email, password and personal info.
 */
public class RegisterFragment extends Fragment {
    private LocalDataBase db;
    private ViewModelApp viewModelApp;
    private EditText email;
    private EditText password;
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public interface continueButtonListener{
        public void onContinueButtonClick();
    }
    public RegisterFragment(){
        super(R.layout.register_fragment);
    }
    public TextViewFragment.continueButtonListener continueButtonListener = null;
    boolean isEmailValid = false;
    boolean isPasswordValid = false;
    TextView emailValidationView;
    TextView passwordValidationView;
    TextView personalNameValidationView;
    TextView familyNameValidationView;
    int PASSWORD_LENGTH = 8;
    EditText personalName;
    EditText familyName;
    boolean isPersonalNameValid = false;
    boolean isFamilyNameValid = false;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.db = HujiAssistentApplication.getInstance().getDataBase();
        viewModelApp = new ViewModelProvider(requireActivity()).get(ViewModelApp.class);

        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);

        emailValidationView = view.findViewById(R.id.emailValidation);
        passwordValidationView = view.findViewById(R.id.passwordValidation);
        personalName = view.findViewById(R.id.editTextPersonalName);
        familyName = view.findViewById(R.id.editTextSecondName);

        TextView textView = requireActivity().findViewById(R.id.change_language_textView);
        textView.setVisibility(View.INVISIBLE);

        // Set view model - singleton
        ViewModelApp vm = new ViewModelProvider(requireActivity()).get(ViewModelApp.class);

        emailValidationView = view.findViewById(R.id.emailValidation);
        passwordValidationView = view.findViewById(R.id.passwordValidation);

        if (savedInstanceState != null) {
            email.setText(savedInstanceState.getString("email", ""));
            password.setText(savedInstanceState.getString("password", ""));
        }

        // Gets current data from live data to show at the screen
        vm.studentInfoMutableLiveData.observe(getViewLifecycleOwner(), new Observer<StudentInfo>() {
            @Override
            public void onChanged(StudentInfo studentInfo) {
                // when observe occurs
            }
        });

        Button continueBtn = view.findViewById(R.id.continueBtn);
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailValidationView.setVisibility(View.GONE);
                passwordValidationView.setVisibility(View.GONE);

                checkValidation(email.getText().toString(), password.getText().toString(), personalName.getText().toString(),
                        familyName.getText().toString());
                if (isEmailValid && isPasswordValid && isPersonalNameValid && isFamilyNameValid){
                    if (continueButtonListener != null && !db.emailUserExists(email.getText().toString())) {

                        StudentInfo newStudent = new StudentInfo(email.getText().toString(), password.getText().toString(),
                                personalName.getText().toString(), familyName.getText().toString());
                        viewModelApp.setStudent(newStudent);
                        continueButtonListener.onContinueButtonClick();
                    }else{
                        Toast.makeText(getActivity(), R.string.user_exist_message, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void setText(String text){
        View view = getView();
        if (view != null){
            TextView textView = view.findViewById(R.id.screennametextview);
            textView.setText(text);
        }
    }

    // Checks validation for the fields in the screen
    public void checkValidation(String email, String password, String personalName, String familyName){
        if (email.isEmpty()) {
            Toast.makeText(getActivity(), getResources().getString(R.string.please_enter_email_msg), Toast.LENGTH_LONG).show();

            isEmailValid = false;
        }
        else if (email.length() > 254 ||
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() || !validateEmail(email)){
            Toast.makeText(getActivity(), getResources().getString(R.string.email_not_valid_message), Toast.LENGTH_LONG).show();
            isEmailValid = false;
        }
        else {
            isEmailValid = true;

            // Check for a valid password
            if (password.isEmpty()) {
                Toast.makeText(getActivity(), getResources().getString(R.string.please_enter_password_msg), Toast.LENGTH_LONG).show();
                isPasswordValid = false;
            } else if (password.length() < PASSWORD_LENGTH) {
                Toast.makeText(getActivity(), getResources().getString(R.string.please_enter_password_msg), Toast.LENGTH_LONG).show();
                isPasswordValid = false;
            } else {
                isPasswordValid = true;

                if (personalName.isEmpty()) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.please_enter_personal_name_msg), Toast.LENGTH_LONG).show();
                    isPersonalNameValid = false;
                } else {
                    isPersonalNameValid = true;

                    if (familyName.isEmpty()) {
                        Toast.makeText(getActivity(), getResources().getString(R.string.please_enter_family_name_msg), Toast.LENGTH_LONG).show();
                        isFamilyNameValid = false;
                    } else {
                        isFamilyNameValid = true;
                    }
                }
            }
        }

        if (isEmailValid && isPasswordValid) {
            // the values are valid
        }
    }

    public static boolean validateEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }
}
