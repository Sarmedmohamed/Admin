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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sarmed.my_admin.Models.Admin;
import com.sarmed.my_admin.Models.Doctor;
import com.sarmed.my_admin.databinding.ActivityUpdateAdminBinding;

import java.io.IOException;
import java.util.UUID;

public class Update_Admin extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    ActivityUpdateAdminBinding binding;
    int idSpn;
    String nameSp;
    String[] courses = {"C", "Data structures",
            "Interview prep", "Algorithms",
            "DSA with java", "OS"};
    FirebaseFirestore db;
    // Uri indicates, where the image will be picked from
    String downloadUrl;
    // Uri indicates, where the image will be picked from
    Uri imageUri;
    // request code
    private final int PICK_IMAGE_REQUEST = 22;
    // instance for firebase storage and StorageReference
    FirebaseStorage storage;
    StorageReference storageReference;
    Admin admin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // get the Firebase  Firestore reference
        db = FirebaseFirestore.getInstance();
        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        binding.AdminUpdateSpDepartmentName.setOnItemSelectedListener(Update_Admin.this);
        ArrayAdapter ad
                = new ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                courses);
        // set simple layout resource file
        // for each item of spinner
        ad.setDropDownViewResource(
                android.R.layout
                        .simple_spinner_dropdown_item);

        // Set the ArrayAdapter (ad) data on the
        // Spinner which binds data to spinner
        binding.AdminUpdateSpDepartmentName.setAdapter(ad);

        Intent intent = getIntent();
        admin = (Admin) intent.getSerializableExtra("admin");
        Glide.with(this)
                .load(admin.getFilePath())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.AdminUpdateImImages);

        binding.AdminUpdateSpDepartmentName.setSelection(admin.getIdSpinner());
        binding.AdminUpdateTvNameMeal.setText(admin.getNameMeal());
        binding.AdminUpdateTvCaloriesMeal.setText(admin.getCaloriesMeal());
        binding.AdminUpdateTvDescriptionMeal.setText(admin.getDescriptionMeal());


        binding.AdminUpdateImImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
            }
        });

        binding.AdminUpdateBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (imageUri == null) {
                    updateAdmin(new Admin(
                            idSpn,
                            admin.getFilePath(),
                            nameSp,
                            binding.AdminUpdateTvNameMeal.getText().toString(),
                            binding.AdminUpdateTvCaloriesMeal.getText().toString(),
                            binding.AdminUpdateTvDescriptionMeal.getText().toString()
                    ));
                } else uploadImage();

            }
        });
        binding.AdminUpdateBtnCencel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Update_Admin.this, ""+admin.getDepartmentName().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        nameSp = courses[position];
        idSpn = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    // Select Image method
    private void SelectImage() {

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
                binding.AdminUpdateImImages.setImageBitmap(bitmap);
            }  catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }

    private void updateAdmin(Admin updateAdmin) {
        // inside this method we are passing our updated values
        // inside our object class and later on we
        // will pass our whole object to firebase Firestore.

        // after passing data to object class we are
        // sending it to firebase with specific document id.
        // below line is use to get the collection of our Firebase Firestore.
        CollectionReference dbAdmin = db.collection("Admin");
        updateAdmin.setId(dbAdmin.document().getId());
        db.collection("Admin").
                // below line is use toset the id of
                // document where we have to perform
                // update operation.
                        document(admin.getId()).

                // after setting our document id we are
                // passing our whole object class to it.
                        set(updateAdmin).

                // after passing our object class we are
                // calling a method for on success listener.
                        addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // on successful completion of this process
                        // we are displaying the toast message.
                        Toast.makeText(Update_Admin.this, "Course has been updated.." + updateAdmin.getId(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            // inside on failure method we are
            // displaying a failure message.
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Update_Admin.this, "Fail to update the data..", Toast.LENGTH_SHORT).show();
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
                            "images/"
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
                            .makeText(Update_Admin.this,
                                    "Image Uploaded!!",
                                    Toast.LENGTH_SHORT)
                            .show();


                    downloadUrl = uri.toString();
                    Toast.makeText(this, "Uri", Toast.LENGTH_SHORT).show();

                    updateAdmin(new Admin(
                            idSpn,
                            downloadUrl,
                            nameSp,
                            binding.AdminUpdateTvNameMeal.getText().toString(),
                            binding.AdminUpdateTvCaloriesMeal.getText().toString(),
                            binding.AdminUpdateTvDescriptionMeal.getText().toString()
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
                        .makeText(Update_Admin.this,
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
//    // UploadImage method
//    private void uploadImage() {
//        if (imageUri != null) {
//            ProgressDialog progressDialog = new ProgressDialog(this);
//            progressDialog.setTitle("Uploading...");
//            progressDialog.show();
//            // Defining the child of storageReference
//            StorageReference ref
//                    = storageReference
//                    .child(
//                            "images/"
//                                    + UUID.randomUUID().toString());
//
//            // adding listeners on upload
//            // or failure of image
//
//            // Upload the image to Firebase Storage and add a success listener to get the download URL
//            UploadTask uploadTask = ref.putFile(imageUri);
//            uploadTask.addOnSuccessListener(taskSnapshot -> {
//                ref.getDownloadUrl().addOnSuccessListener(uri -> {
//
//                    // Image uploaded successfully
//                    // Dismiss dialog
//                    progressDialog.dismiss();
//                    Toast
//                            .makeText(Update_Admin.this,
//                                    "Image Uploaded!!",
//                                    Toast.LENGTH_SHORT)
//                            .show();
//
//
//                    downloadUrl = uri.toString();
//                    Toast.makeText(this, "Uri", Toast.LENGTH_SHORT).show();
//
//                    updateAdmin(new Admin(
//                            downloadUrl,
//                            nameSp,
//                            binding.AdminUpdateTvNameMeal.getText().toString(),
//                            binding.AdminUpdateTvCaloriesMeal.getText().toString(),
//                            binding.AdminUpdateTvDescriptionMeal.getText().toString()
//                    ));
//                    // Use the download URL to display the image or save it to a database
//                    // ...
//                });
//            }).addOnFailureListener(exception -> {
//                // Handle any errors
//                // ...
//                // Error, Image not uploaded
//                progressDialog.dismiss();
//                Toast
//                        .makeText(Update_Admin.this,
//                                "Failed " + exception.getMessage(),
//                                Toast.LENGTH_SHORT)
//                        .show();
//            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
//                    // Progress Listener for loading
//                    // percentage on the dialog box
//
//                    double progress = (100.0 * snapshot.getBytesTransferred()
//                            / snapshot.getTotalByteCount());
//                    progressDialog.setMessage(
//                            "Uploaded "
//                                    + (int) progress + "%");
//                }
//            });
//        }
//    }
