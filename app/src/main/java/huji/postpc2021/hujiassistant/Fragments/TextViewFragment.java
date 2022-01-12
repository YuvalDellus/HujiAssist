package huji.postpc2021.hujiassistant.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
//import android.util.Log;
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
import huji.postpc2021.hujiassistant.Activities.MainScreenActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/*
    This fragment is a part of the registration process. This fragemnt is opened when choosing
    the existing user option, and allows an existing user to sign in.
 */
public class TextViewFragment extends Fragment {
    private LocalDataBase db;
    private ViewModelApp viewModelApp;
    public interface continueButtonListener{
        public void onContinueButtonClick();
    }

    public TextViewFragment(){
        super(R.layout.loginfragment);
    }
    public TextViewFragment.continueButtonListener continueButtonListener = null;
    boolean isEmailValid = false;
    boolean isPasswordValid = false;
    boolean isPersonalNameValid = false;
    boolean isFamilyNameValid = false;
    TextView emailValidationView;
    TextView passwordValidationView;
    TextView personalNameValidationView;
    TextView familyNameValidationView;
    int PASSWORD_LENGTH = 8;
    EditText email;
    EditText password;
    EditText personalName;
    EditText familyName;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            email.setText(savedInstanceState.getString("email", ""));
            password.setText(savedInstanceState.getString("password", ""));
        }
        this.db = HujiAssistentApplication.getInstance().getDataBase();
        viewModelApp = new ViewModelProvider(requireActivity()).get(ViewModelApp.class);

        TextView textView = requireActivity().findViewById(R.id.change_language_textView);
        textView.setVisibility(View.INVISIBLE);

        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        Button forgotPassword = view.findViewById(R.id.forgotPassword);

        emailValidationView = view.findViewById(R.id.emailValidation);
        passwordValidationView = view.findViewById(R.id.passwordValidation);

        // Set view model - singleton
        ViewModelApp vm = new ViewModelProvider(requireActivity()).get(ViewModelApp.class);

        // Gets current data from live data to show at the screen
        vm.studentInfoMutableLiveData.observe(getViewLifecycleOwner(), new Observer<StudentInfo>() {
            @Override
            public void onChanged(StudentInfo studentInfo) {
                // when observe occurs
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText resetMail = new EditText(view.getContext());
                final AlertDialog.Builder passwordDialog = new AlertDialog.Builder(view.getContext());
                passwordDialog.setTitle(R.string.reset_password);
                passwordDialog.setMessage(R.string.enter_email_for_reset_password);
                passwordDialog.setView(resetMail);
                passwordDialog.setPositiveButton(R.string.positive_answer, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String resetEmail = resetMail.getText().toString();
                        FirebaseAuth auth = db.getUsersAuthenticator();
                        auth.sendPasswordResetEmail(resetEmail)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(getActivity(), R.string.check_your_mail_for_reset_link_message, Toast.LENGTH_LONG).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), R.string.try_again_later_message, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
                passwordDialog.setNegativeButton(R.string.negative_answer, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                passwordDialog.create().show();
            }
        });

        Button continueBtn = view.findViewById(R.id.continueBtn);
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailValidationView.setVisibility(View.INVISIBLE);
                passwordValidationView.setVisibility(View.INVISIBLE);
                checkValidation(email.getText().toString(), password.getText().toString());

                // Checks if the email and password are valid
                if (isEmailValid && isPasswordValid){
                    if (continueButtonListener != null) {
                        FirebaseAuth auth = db.getUsersAuthenticator();
                        auth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
//                                            Log.d("LoginActivity", "signInWithEmail:success");
                                            FirebaseUser user = auth.getCurrentUser();
                                            db.setCurrentUser(user);
                                            StudentInfo newStudent = new StudentInfo(email.getText().toString(), password.getText().toString(),
                                                    "", "");
                                            viewModelApp.setStudent(newStudent);
                                            startActivity(new Intent(getActivity(), MainScreenActivity.class));
                                        }else{
//                                            Log.w("LoginActivity", "signInWithEmail:failure", task.getException());
                                            Toast.makeText(getActivity(), R.string.incorrect_username, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });
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

    public void checkValidation(String email, String password){
        if (email.isEmpty()) {
            Toast.makeText(getActivity(), getResources().getString(R.string.please_enter_email_msg), Toast.LENGTH_LONG).show();
            isEmailValid = false;
        }
        else if (email.length() > 254 ||
                !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
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
            }
        }

        if (isEmailValid && isPasswordValid) {
            // the values are valid
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("email", email.getText().toString());
        outState.putString("password", password.getText().toString());
    }
}
