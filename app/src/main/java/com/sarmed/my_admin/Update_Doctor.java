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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sarmed.my_admin.Models.Doctor;
import com.sarmed.my_admin.Models.Exercises;
import com.sarmed.my_admin.databinding.ActivityUpdateDoctorBinding;

import java.io.FileNotFoundException;
import java.io.IOError;
import java.io.IOException;
import java.util.UUID;

public class Update_Doctor extends AppCompatActivity {

    ActivityUpdateDoctorBinding binding;

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
    Doctor doctor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateDoctorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // get the Firebase  Firestore reference
        db = FirebaseFirestore.getInstance();
        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        Intent intent = getIntent();
        doctor = (Doctor) intent.getSerializableExtra("doctor");
        Glide.with(this)
                .load(doctor.getImage())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.doctorUpdateIvImage);
        binding.doctorUpdateTvName.setText(doctor.getName());
        binding.doctorUpdateTvPhone.setText(doctor.getNumber());
        binding.doctorUpdateTvEmailAddress.setText(doctor.getEmail());
        binding.doctorUpdateTvAddress.setText(doctor.getAddress());
        binding.doctorUpdateTvADescription.setText(doctor.getDescription());

        //Image button
        binding.doctorUpdateIvImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
            }
        });
        //Update button
        binding.doctorUpdateBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri == null){
                    updateDoctor(new Doctor(
                            doctor.getId(),
                            doctor.getImage(),
                            binding.doctorUpdateTvName.getText().toString(),
                            binding.doctorUpdateTvPhone.getText().toString(),
                            binding.doctorUpdateTvEmailAddress.getText().toString(),
                            binding.doctorUpdateTvAddress.getText().toString(),
                            binding.doctorUpdateTvADescription.getText().toString()
                    ));

                }else uploadImage();

                finish();
            }
        });

    //Cencel button
      binding.doctorUpdateBtnCencel.setOnClickListener(new View.OnClickListener() {
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
        imageUri = data.getData();
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the URI of the selected image from the intent data
            imageUri = data.getData();
            try {

                // Setting image on image view using Bitmap

                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getContentResolver(),
                                imageUri);
                binding.doctorUpdateIvImage.setImageBitmap(bitmap);
            }  catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }


    private void updateDoctor(Doctor updateDoctor) {
        // inside this method we are passing our updated values
        // inside our object class and later on we
        // will pass our whole object to firebase Firestore.

        // after passing data to object class we are
        // sending it to firebase with specific document id.
        // below line is use to get the collection of our Firebase Firestore.
        db.collection("Doctor").
                // below line is use toset the id of
                // document where we have to perform
                // update operation.
                        document(doctor.getId()).

                // after setting our document id we are
                // passing our whole object class to it.
                        set(updateDoctor).

                // after passing our object class we are
                // calling a method for on success listener.
                        addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // on successful completion of this process
                        // we are displaying the toast message.
                        Toast.makeText(Update_Doctor.this, "Course has been updated.."+updateDoctor.getId(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            // inside on failure method we are
            // displaying a failure message.
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Update_Doctor.this, "Fail to update the data..", Toast.LENGTH_SHORT).show();
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
                            "doctor/"
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
                            .makeText(Update_Doctor.this,
                                    "Image Uploaded!!",
                                    Toast.LENGTH_SHORT)
                            .show();


                    downloadUrl = uri.toString();
                    Toast.makeText(this, "Uri", Toast.LENGTH_SHORT).show();

                    updateDoctor(new Doctor(
                            doctor.getId(),
                            downloadUrl,
                            binding.doctorUpdateTvName.getText().toString(),
                            binding.doctorUpdateTvPhone.getText().toString(),
                            binding.doctorUpdateTvEmailAddress.getText().toString(),
                            binding.doctorUpdateTvAddress.getText().toString(),
                            binding.doctorUpdateTvADescription.getText().toString()
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
                        .makeText(Update_Doctor.this,
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