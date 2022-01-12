
package huji.postpc2021.hujiassistant.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import huji.postpc2021.hujiassistant.HujiAssistentApplication;
import huji.postpc2021.hujiassistant.LocalDataBase;
import huji.postpc2021.hujiassistant.Model;
import huji.postpc2021.hujiassistant.R;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CaptureImageActivity extends AppCompatActivity {

    private Button uploadBtn;
    private TextView pdfName;
    private ImageView imageView, cameraImageUpload, pdfImageUpload, imageShow;
    private int uploadChoose;
    private String fName, messagePushId;
    private ProgressBar progressBar;
    private DatabaseReference root;
    private DatabaseReference rootForDocument;
    private StorageReference reference;
    private Uri imageUri, classContentUri;
    private LocalDataBase dataBase = null;
    Spinner dropdown;
    EditText imageTitle;
    String currentPhotoPath;
    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int DOCUMENTS_REQUEST_CODE = 104;
    public static final int GALLERY_REQUEST_CODE = 105;
    private static final int GALLERY_TYPE = 0;
    private static final int CAMERA_TYPE = 1;
    private static final int PDF_TYPE = 2;
    private ActivityResultLauncher<Intent> cameraUploadActivityResultLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture2);

        // Set layout in 'rtl' direction
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        uploadBtn = findViewById(R.id.upload_btn);
        uploadBtn.setEnabled(false);
        progressBar = findViewById(R.id.progressBar2);
        imageView = findViewById(R.id.uploadFromGallery);
        String IMAGES_COLLECTION_NAME = "Image";
        root = FirebaseDatabase.getInstance().getReference(IMAGES_COLLECTION_NAME);
        String DOCUMENTS_COLLECTION_NAME = "Documents";
        rootForDocument = FirebaseDatabase.getInstance().getReference(DOCUMENTS_COLLECTION_NAME);
        reference = FirebaseStorage.getInstance().getReference();
        progressBar.setVisibility(View.INVISIBLE);
        cameraImageUpload = findViewById(R.id.cameraImageUpload);
        pdfName = findViewById(R.id.PDFName);
        pdfImageUpload = findViewById(R.id.pdfImageUpload);
        imageShow = findViewById(R.id.imageShow);
        imageTitle = findViewById(R.id.imageTitle);
        imageTitle.setText("");
        imageShow.setVisibility(View.INVISIBLE);
        uploadChoose = -1;
        fName = "";
        pdfName.setText("");
        pdfName.setVisibility(View.INVISIBLE);

        if (dataBase == null) {
            dataBase = HujiAssistentApplication.getInstance().getDataBase();
        }

        ArrayList<String> courses = dataBase.getCurrentStudent().getCourses();
        courses.add(0, getString(R.string.choose_a_course));
        courses.add(getString(R.string.other));

        dropdown = findViewById(R.id.spinner1);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, courses);
        dropdown.setAdapter(adapter);

        // Upload image
        cameraImageUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askCameraPermissions();
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageTitle.getText().toString().isEmpty()) {
                    Toast.makeText(CaptureImageActivity.this, R.string.image_description_is_empty, Toast.LENGTH_SHORT).show();
                } else {
                    if (checkCourseNumberValidation()) {
                        if (uploadChoose == GALLERY_TYPE) {
                            uploadToFirebase(imageUri, GALLERY_REQUEST_CODE, imageTitle.getText().toString());
                        } else if (uploadChoose == CAMERA_TYPE) {
                            uploadToFirebase(classContentUri, CAMERA_REQUEST_CODE, fName);
                        } else if (uploadChoose == PDF_TYPE) {
                            uploadToFirebase(imageUri, DOCUMENTS_REQUEST_CODE, messagePushId);
                        }
                    } else {
                        Toast.makeText(CaptureImageActivity.this, R.string.course_number_is_not_valid, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        cameraUploadActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent data = result.getData();
                            File f = new File(currentPhotoPath);

                            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            Uri contentUri = Uri.fromFile(f);
                            mediaScanIntent.setData(contentUri);
                            sendBroadcast(mediaScanIntent);
                            hideButtonsAfterChooseImage();
                            imageShow.setImageURI(contentUri);
                            uploadChoose = CAMERA_TYPE;
                            classContentUri = contentUri;
                            fName = f.getName();
                            uploadBtn.setEnabled(true);
                        }
                    }
                });

        ActivityResultLauncher<Intent> GalleryUploadActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent data = result.getData();
                            imageUri = data.getData();
                            hideButtonsAfterChooseImage();
                            imageShow.setImageURI(imageUri);
                            uploadBtn.setEnabled(true);
                            uploadChoose = GALLERY_TYPE;
                        }
                    }
                });

        ActivityResultLauncher<Intent> pdfImageUploadActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent data = result.getData();
                            assert data != null;
                            imageUri = data.getData();
                            final String timestamp = "" + System.currentTimeMillis();
                            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                            final String messagePushID = timestamp + "." + "pdf";
                            hideButtonsAfterChooseImage();
                            imageShow.setImageResource(R.drawable.ic_pdf_icon);
                            uploadChoose = PDF_TYPE;
                            messagePushId = messagePushID;
                            uploadBtn.setEnabled(true);
                            pdfName.setVisibility(View.VISIBLE);
                            pdfName.setText(messagePushID);
                        }
                    }
                });

        pdfImageUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                // We will be redirected to choose pdf
                galleryIntent.setType("application/pdf");
                pdfImageUploadActivityResultLauncher.launch(galleryIntent);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                GalleryUploadActivityResultLauncher.launch(galleryIntent);
            }
        });
    }

    // Check if the user chose a course
    private boolean checkCourseNumberValidation() {
        return !dropdown.getSelectedItem().toString().equals(getString(R.string.choose_a_course));
    }

    private void uploadToFirebase(Uri uri, int source, String name) {
        name = new SimpleDateFormat("dd-MM-yyyy").format(new Date()) +
                "_" + dataBase.getCurrentStudent().getPersonalName() + "_" +
                dataBase.getCurrentStudent().getFamilyName();
        String course = dropdown.getSelectedItem().toString();
        String year = new SimpleDateFormat("yyyy").format(new Date());

        if (!imageTitle.getText().toString().equals("")) {
            name = imageTitle.getText().toString() + "_" + name;
        }
        StorageReference fileRef;
        int type;
        String nameToUpload = "";
        if (source == GALLERY_REQUEST_CODE) {
            fileRef = reference.child(course).child(year).child(name + "." + getFileExtension(uri));
            nameToUpload = name + "." + getFileExtension(uri);
            type = GALLERY_TYPE;
        } else if (source == CAMERA_REQUEST_CODE) {
            fileRef = reference.child("Camera_images/" + name + ".png");
            nameToUpload = name + ".png";
            type = CAMERA_TYPE;
        } else {
            fileRef = reference.child("Documents/" + name + ".pdf");
            nameToUpload = name + ".pdf";
            type = PDF_TYPE;
        }
        StorageReference finalFileRef = fileRef;
        String finalNameToUpload = nameToUpload;
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                finalFileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Model model = new Model(uri.toString(), finalNameToUpload, type);
                        progressBar.setVisibility(View.INVISIBLE);
                        if (type == GALLERY_TYPE || type == CAMERA_TYPE) {
                            root.child(course).child(year).push().setValue(model);
                        } else {
                            rootForDocument.child(course).child(year).push().setValue(model);
                        }
                        imageTitle.setText("");
                        dropdown.setSelection(0);
                        uploadBtn.setEnabled(false);
                        showButtonsAfterChooseImage();
                        Toast.makeText(CaptureImageActivity.this, R.string.upload_Successfully_message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                progressBar.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(CaptureImageActivity.this, R.string.upload_failed_message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getFileExtension(Uri mUri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));
    }

    private void askCameraPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        } else {
            dispatchTakePictureIntent();
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "huji.postpc2021.hujiassistant.Activities.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                cameraUploadActivityResultLauncher.launch(takePictureIntent);
            }
        }
    }

    private File createImageFile() throws IOException {
//     Create an image file name
        String imageFileName = "";
        String timeStamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
        if (!imageTitle.getText().toString().equals("")) {
            imageFileName = imageTitle.getText().toString() + "_";
        } else {
            imageFileName = timeStamp + "_";
        }
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("imageTitle", imageTitle.getText().toString());
        outState.putParcelable("imageUri", imageUri);
        outState.putInt("progressBarVisibility", progressBar.getVisibility());
        outState.putInt("cameraUploadVisibility", cameraImageUpload.getVisibility());
        outState.putInt("pdfImageVisibility", pdfImageUpload.getVisibility());
        outState.putInt("imageShowVisibility", imageShow.getVisibility());
        outState.putString("pdfNameText", pdfName.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        imageTitle.setText(savedInstanceState.getString("imageTitle"));
        imageUri = savedInstanceState.getParcelable("imageUri");
        imageShow.setImageURI(imageUri);
        progressBar.setVisibility(savedInstanceState.getInt("progressBarVisibility"));
        cameraImageUpload.setVisibility(savedInstanceState.getInt("cameraUploadVisibility"));
        pdfImageUpload.setVisibility(savedInstanceState.getInt("pdfImageVisibility"));
        imageShow.setVisibility(savedInstanceState.getInt("imageShowVisibility"));
        pdfName.setText(savedInstanceState.getString("pdfNameText"));
    }

    private void hideButtonsAfterChooseImage() {
        imageView.setVisibility(View.INVISIBLE);
        cameraImageUpload.setVisibility(View.INVISIBLE);
        pdfImageUpload.setVisibility(View.INVISIBLE);
        imageShow.setVisibility(View.VISIBLE);
    }

    private void showButtonsAfterChooseImage() {
        imageView.setVisibility(View.VISIBLE);
        cameraImageUpload.setVisibility(View.VISIBLE);
        pdfImageUpload.setVisibility(View.VISIBLE);
        imageShow.setVisibility(View.INVISIBLE);
        imageTitle.setText("");
        pdfName.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (imageShow.getVisibility() == View.VISIBLE) {
            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE: {
                        showButtonsAfterChooseImage();
                        break;
                    }
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.reset_image).setPositiveButton(R.string.positive_answer, dialogClickListener)
                    .setNegativeButton(R.string.negative_answer, dialogClickListener).show();
        } else {
            startActivity(new Intent(this, MainScreenActivity.class));
            finish();
        }
    }
}
