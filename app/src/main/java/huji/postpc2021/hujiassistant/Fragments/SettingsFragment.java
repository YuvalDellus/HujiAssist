package huji.postpc2021.hujiassistant.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import huji.postpc2021.hujiassistant.Activities.MainActivity;
import huji.postpc2021.hujiassistant.HujiAssistentApplication;
import huji.postpc2021.hujiassistant.LocalDataBase;
import huji.postpc2021.hujiassistant.R;
import huji.postpc2021.hujiassistant.StudentInfo;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;


public class SettingsFragment extends Fragment {

    private LocalDataBase db = null;
    FirebaseFirestore firebaseInstancedb = FirebaseFirestore.getInstance();
    FirebaseFirestoreSettings settings;
    public SettingsFragment(){
        super(R.layout.fragment_settings);
    }
    public SettingsFragment.sendEmailBtnListener sendEmailBtnListener = null;
    TextView currentUserTxt;
    TextView inviteFriendsBtn;
    ImageView logoutImageView;
    TextView changeLanguageTxt;
    EditText entermailedittext;
    Button sendmailbtn;
    StudentInfo currentUser;
    boolean isEmailValid = false;

    public SettingsFragment.changeLanguageBtnListener changeLanguageBtnListener = null;
    public interface sendEmailBtnListener{
        public void onSendEmailBtnClicked(String email);
    }

    public interface changeLanguageBtnListener{
        public void onClickChangeLanguage();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (db == null) {
            db = HujiAssistentApplication.getInstance().getDataBase();
        }
        settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firebaseInstancedb.setFirestoreSettings(settings);
        currentUserTxt = view.findViewById(R.id.nameOfCurrentUser);
        logoutImageView = view.findViewById(R.id.logoutImageView);
        inviteFriendsBtn = view.findViewById(R.id.inviteFriendsBtn);
        entermailedittext = view.findViewById(R.id.inviteFriendsEditMail);
        sendmailbtn = view.findViewById(R.id.sendemailbtn);
        changeLanguageTxt = view.findViewById(R.id.change_language_textView2);

        currentUser = db.getCurrentUser();
        String email = currentUser.getEmail();
        String text = currentUserTxt.getText() + " " + email;
        currentUserTxt.setText(text);

        inviteFriendsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                entermailedittext.setVisibility(View.VISIBLE);
                sendmailbtn.setVisibility(View.VISIBLE);
            }
        });

        changeLanguageTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (changeLanguageBtnListener != null){
                    changeLanguageBtnListener.onClickChangeLanguage();
                }
            }
        });

        sendmailbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emails = entermailedittext.getText().toString();
                String[] split = emails.split(",");

                for (int i = 0; i < split.length; i++){
                    validateEmail(split[i]);
                }

                if (isEmailValid){
                    if (sendEmailBtnListener != null){
                        sendEmailBtnListener.onSendEmailBtnClicked(emails);
                    }
                }
            }
        });


        logoutImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE: {
                            db.logoutUser();
                            startActivity(new Intent(requireActivity(), MainActivity.class));
                            requireActivity().finish();
                            break;
                        }
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                builder.setMessage(R.string.logout).setPositiveButton(R.string.positive_answer, dialogClickListener)
                        .setNegativeButton(R.string.negative_answer, dialogClickListener).show();
            }
        });

    }

    // private function to validate email
    private void validateEmail(String email){
        if (email == null || email.isEmpty()){
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
        }
    }

}
