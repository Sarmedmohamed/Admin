package com.sarmed.my_admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sarmed.my_admin.Models.Admin;
import com.sarmed.my_admin.Models.Doctor;
import com.sarmed.my_admin.Models.Exercises;
import com.sarmed.my_admin.databinding.ActivityAdminDoctorAddBinding;
import com.sarmed.my_admin.databinding.ActivityAdminExercisesAddBinding;

import java.io.IOError;
import java.io.IOException;
import java.util.UUID;

public class Admin_exercises_add extends AppCompatActivity {
    ActivityAdminExercisesAddBinding binding;

    // Uri indicates, where the image will be picked from
    String downloadUrl;
    // Uri indicates, where the image will be picked from
    Uri imageUri;
    // request code
    private final int PICK_IMAGE_REQUEST = 22;
    Exercises exercises ;

    // instance for firebase storage and StorageReference
    FirebaseStorage storage ;
    StorageReference storageReference;

    // creating a variable
    // for firebasefirestore.
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminExercisesAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // getting our instance
        // from Firebase Firestore.
        db = FirebaseFirestore.getInstance();

        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        binding.AdminExercisesImGifImagesGif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
            }
        });
        binding.AdminExercisesBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Admin_exercises_add.this, "Save", Toast.LENGTH_SHORT).show();
                uploadImage();
            }
        });

        binding.AdminExercisesBtnCencel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    // Select Image method
    private void SelectImage()
    {

        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }

    // Override onActivityResult method
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data)
    {

        super.onActivityResult(requestCode,
                resultCode,
                data);

        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the URI of the selected image from the intent data
            imageUri = data.getData();
            try {

                // Setting image on image view using Bitmap

                Glide.with(getBaseContext())
                        .asGif()
                        .load(imageUri)
                        .into(binding.AdminExercisesImGifImagesGif);
            } catch (IOError e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }


    //رفع على الفاير بيز
    private void addDataToFirestore(Exercises exercises) {
        // creating a collection reference
        // for our Firebase Firestore database.
        CollectionReference dbAdmin = db.collection("exercises");
        exercises.setId(dbAdmin.document().getId());
        // below method is use to add data to Firebase Firestore.
        dbAdmin.add(exercises).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                // after the data addition is successful
                // we are displaying a success toast message.
                Toast.makeText(Admin_exercises_add.this, "Success", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // this method is called when the data addition process is failed.
                // displaying a toast message when data addition is failed.
            }
        });

    }

    // UploadImage method
    private void uploadImage() {
        if (imageUri != null) {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            // Defining the child of storageReference
            StorageReference ref
                    = storageReference
                    .child(
                            "exercises/"
                                    + UUID.randomUUID().toString());

            // adding listeners on upload
            // or failure of image

            // Upload the image to Firebase Storage and add a success listener to get the download URL
            UploadTask uploadTask = ref.putFile(imageUri);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                ref.getDownloadUrl().addOnSuccessListener(uri -> {

                    // Image uploaded successfully
                    // Dismiss dialog
                    progressDialog.dismiss();
                    Toast
                            .makeText(Admin_exercises_add.this,
                                    "Image Uploaded!!",
                                    Toast.LENGTH_SHORT)
                            .show();


                    downloadUrl = uri.toString();
                    Toast.makeText(this, "Uri", Toast.LENGTH_SHORT).show();
                   Exercises exercises = new Exercises(
                          downloadUrl ,
                          binding.AdminExercisesTvExerciseName.getText().toString(),
                          binding.AdminExercisesTvExerciseDuration.getText().toString(),
                          binding.AdminExercisesTvDescriptionMeal.getText().toString()
                   );
                    addDataToFirestore(exercises);
                    // Use the download URL to display the image or save it to a database
                    // ...
                });
            }).addOnFailureListener(exception -> {
                // Handle any errors
                // ...
                // Error, Image not uploaded
                progressDialog.dismiss();
                Toast
                        .makeText(Admin_exercises_add.this,
                                "Failed " + exception.getMessage(),
                                Toast.LENGTH_SHORT)
                        .show();
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    // Progress Listener for loading
                    // percentage on the dialog box

                    double progress = (100.0 * snapshot.getBytesTransferred()
                            / snapshot.getTotalByteCount());
                    progressDialog.setMessage(
                            "Uploaded "
                                    + (int)progress + "%");
                }
            });
        }
    }
}