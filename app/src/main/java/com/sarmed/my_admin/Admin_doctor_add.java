package com.sarmed.my_admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

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
import com.sarmed.my_admin.databinding.ActivityAdminAddBinding;
import com.sarmed.my_admin.databinding.ActivityAdminDoctorAddBinding;

import java.io.IOException;
import java.util.UUID;

public class Admin_doctor_add extends AppCompatActivity {
ActivityAdminDoctorAddBinding binding;

Doctor doctor ;

    // Uri indicates, where the image will be picked from
    String downloadUrl;
    // Uri indicates, where the image will be picked from
    Uri imageUri;
    // request code
    private final int PICK_IMAGE_REQUEST = 22;

    // instance for firebase storage and StorageReference
    FirebaseStorage storage ;
    StorageReference storageReference;

    // creating a variable
    // for firebasefirestore.
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminDoctorAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // getting our instance
        // from Firebase Firestore.
        db = FirebaseFirestore.getInstance();

        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        binding.doctorAddIvImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //الحصول على صورة وتخزينها
                SelectImage();
            }
        });

        //زر Save
        binding.doctorAddBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(Admin_doctor_add.this, "جاري رفع البيانات", Toast.LENGTH_SHORT).show();

                // validating the text fields if empty or not.
                if(TextUtils.isEmpty(binding.doctorAddTvName.getText())){
                    binding.doctorAddTvName.setError("Please enter Name");
                } else if (TextUtils.isEmpty(binding.doctorAddTvPhone.getText())) {
                    binding.doctorAddTvPhone.setError("Please enter Phone");
                } else if (TextUtils.isEmpty(binding.doctorAddTvEmailAddress.getText())) {
                    binding.doctorAddTvEmailAddress.setError("Please enter Email");
                } else if (TextUtils.isEmpty(binding.doctorAddTvAddress.getText())){
                    binding.doctorAddTvAddress.setError("Please enter Address");
                } else if (TextUtils.isEmpty( binding.doctorAddTvADescription.getText())){
                    binding.doctorAddTvADescription.setError("Please enter Description");
                }
                else {
                    // calling method to add data to Firebase Firestore.
                    uploadImage();
                }
            }
        });
        binding.doctorAddBtnCencel.setOnClickListener(new View.OnClickListener() {
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
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getContentResolver(),
                                imageUri);
                binding.doctorAddIvImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }

    //رفع على الفاير بيز
    private void addDataToFirestore(Doctor doctor) {
        // creating a collection reference
        // for our Firebase Firestore database.
        CollectionReference dbAdmin = db.collection("Doctor");
        doctor.setId(dbAdmin.document().getId());
        // below method is use to add data to Firebase Firestore.
        dbAdmin.add(doctor).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                // after the data addition is successful
                // we are displaying a success toast message.
                Toast.makeText(Admin_doctor_add.this, "Success", Toast.LENGTH_SHORT).show();
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
                            .makeText(Admin_doctor_add.this,
                                    "Image Uploaded!!",
                                    Toast.LENGTH_SHORT)
                            .show();


                    downloadUrl = uri.toString();
                    Toast.makeText(this, "Uri", Toast.LENGTH_SHORT).show();
                    doctor = new Doctor(
                            downloadUrl,
                            binding.doctorAddTvName.getText().toString(),
                            binding.doctorAddTvPhone.getText().toString(),
                            binding.doctorAddTvEmailAddress.getText().toString(),
                            binding.doctorAddTvAddress.getText().toString(),
                            binding.doctorAddTvADescription.getText().toString()
                    );
                    addDataToFirestore(doctor);
                    // Use the download URL to display the image or save it to a database
                    // ...
                });
            }).addOnFailureListener(exception -> {
                // Handle any errors
                // ...
                // Error, Image not uploaded
                progressDialog.dismiss();
                Toast
                        .makeText(Admin_doctor_add.this,
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