package com.sarmed.my_admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sarmed.my_admin.Adapters.ExercisesRVAdapter;
import com.sarmed.my_admin.Models.Exercises;
import com.sarmed.my_admin.databinding.ActivityUpdateExercisesBinding;

import java.io.IOError;
import java.util.UUID;

public class Update_exercises extends AppCompatActivity {
    ActivityUpdateExercisesBinding binding;

    Exercises exercisesIntent;
    FirebaseFirestore db;
    // Uri indicates, where the image will be picked from
    String downloadUrl;
    // Uri indicates, where the image will be picked from
    Uri imageUri;
    // request code
    private final int PICK_IMAGE_REQUEST = 22;
    // instance for firebase storage and StorageReference
    FirebaseStorage storage ;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateExercisesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // get the Firebase  Firestore reference
        db = FirebaseFirestore.getInstance();
        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        Intent intent = getIntent();
         exercisesIntent = (Exercises) intent.getSerializableExtra("exercises");

        Glide.with(this)
                        .load(exercisesIntent.getImGif())
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(binding.UpdateExercisesImGifImagesGif);

        binding.UpdateExercisesTvExerciseName.setText(exercisesIntent.getExerciseName());
        binding.UpdateExercisesTvExerciseDuration.setText(exercisesIntent.getExerciseDuration());
        binding.UpdateExercisesTvDescriptionMeal.setText(exercisesIntent.getDescriptionMeal());

        binding.UpdateExercisesImGifImagesGif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            SelectImage();
            }
        });
        binding.UpdateExercisesBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri == null){
                updateExercises(new Exercises(
                        exercisesIntent.getId(),
                        exercisesIntent.getImGif(),
                        binding.UpdateExercisesTvExerciseName.getText().toString() ,
                        binding.UpdateExercisesTvExerciseDuration.getText().toString(),
                        binding.UpdateExercisesTvDescriptionMeal.getText().toString()
                ));
            }else uploadImage();

            }
        });
    }

    private void updateExercises(Exercises Updateexercises) {
        // inside this method we are passing our updated values
        // inside our object class and later on we
        // will pass our whole object to firebase Firestore.

        // after passing data to object class we are
        // sending it to firebase with specific document id.
        // below line is use to get the collection of our Firebase Firestore.
        db.collection("exercises").
                // below line is use toset the id of
                // document where we have to perform
                // update operation.
                        document(exercisesIntent.getId()).

                // after setting our document id we are
                // passing our whole object class to it.
                        set(Updateexercises).

                // after passing our object class we are
                // calling a method for on success listener.
                        addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // on successful completion of this process
                        // we are displaying the toast message.
                        Toast.makeText(Update_exercises.this, "Course has been updated.."+Updateexercises.getId(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            // inside on failure method we are
            // displaying a failure message.
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Update_exercises.this, "Fail to update the data..", Toast.LENGTH_SHORT).show();
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
                        .into(binding.UpdateExercisesImGifImagesGif);
            } catch (IOError e) {
                // Log the exception
                e.printStackTrace();
            }
        }
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
                            .makeText(Update_exercises.this,
                                    "Image Uploaded!!",
                                    Toast.LENGTH_SHORT)
                            .show();


                    downloadUrl = uri.toString();
                    Toast.makeText(this, "Uri", Toast.LENGTH_SHORT).show();
                    updateExercises(new Exercises(
                            exercisesIntent.getId(),
                            downloadUrl,
                            binding.UpdateExercisesTvExerciseName.getText().toString(),
                            binding.UpdateExercisesTvExerciseDuration.getText().toString(),
                            binding.UpdateExercisesTvDescriptionMeal.getText().toString()
                    ));

                    // Use the download URL to display the image or save it to a database
                    // ...
                });
            }).addOnFailureListener(exception -> {
                // Handle any errors
                // ...
                // Error, Image not uploaded
                progressDialog.dismiss();
                Toast
                        .makeText(Update_exercises.this,
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
                                    + (int) progress + "%");
                }
            });
        }
    }

}