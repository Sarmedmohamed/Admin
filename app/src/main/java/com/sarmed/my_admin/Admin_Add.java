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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.sarmed.my_admin.databinding.ActivityAdminAddBinding;

import java.io.IOException;
import java.util.UUID;

public class Admin_Add extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    ActivityAdminAddBinding binding;
    // constant to compare
// the activity result code
    int SELECT_PICTURE = 200;
    String nametemp;
    int idSpinner;
    String[] courses = {"C", "Data structures",
            "Interview prep", "Algorithms",
            "DSA with java", "OS"};
    String downloadUrl;
    // Uri indicates, where the image will be picked from
    Uri imageUri;
    // request code
    private final int PICK_IMAGE_REQUEST = 22;

    // instance for firebase storage and StorageReference
    FirebaseStorage storage;
    StorageReference storageReference;

    // creating a variable
    // for firebasefirestore.
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // getting our instance
        // from Firebase Firestore.
        db = FirebaseFirestore.getInstance();

        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        binding.AdminAddSpDepartmentName.setOnItemSelectedListener(Admin_Add.this);
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
        binding.AdminAddSpDepartmentName.setAdapter(ad);

        // on pressing btnSelect SelectImage() is called
        binding.AdminAddImImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //الحصول على صورة وتخزينها
                SelectImage();
            }
        });

        //حفظ في الفاير بيز عن طريق ضفط عزر البتون
        binding.AdminAddBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //حفظ البيانات في أوبجكت وتخزينها بقاعدة بيانات
                Toast.makeText(Admin_Add.this, nametemp + "", Toast.LENGTH_SHORT).show();

                // getting data from edittext fields.
                if (TextUtils.isEmpty(binding.AdminAddTvNameMeal.getText())) {
                    binding.AdminAddTvNameMeal.setError("Please enter Name");
                } else if (TextUtils.isEmpty(binding.AdminAddTvCaloriesMeal.getText())) {
                    binding.AdminAddTvCaloriesMeal.setError("Please enter Calories ");
                } else if (TextUtils.isEmpty(binding.AdminAddTvDescriptionMeal.getText())) {
                    binding.AdminAddTvDescriptionMeal.setError("Please enter DescriptionMeal");
                } else {
                    // calling method to add data to Firebase Firestore.
                    uploadImage();
                }

            }
        });

        //ارجوع لصفحة رئيسية
        binding.AdminAddBtnCencel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ارجوع لصفحة رئيسية
                finish();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        nametemp = courses[position];
        idSpinner = position;
        Toast.makeText(getApplicationContext(),
                        courses[position],
                        Toast.LENGTH_LONG)
                .show();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
//رفع على الفاير ستور
    private void addDataToFirestore(Admin admin) {
        // creating a collection reference
        // for our Firebase Firestore database.
        CollectionReference dbAdmin = db.collection("Admin");
        admin.setId(dbAdmin.document().getId());
        // below method is use to add data to Firebase Firestore.
        dbAdmin.add(admin).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                // after the data addition is successful
                // we are displaying a success toast message.
                Toast.makeText(Admin_Add.this, "Success", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // this method is called when the data addition process is failed.
                // displaying a toast message when data addition is failed.
            }
        });

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
                                    Intent data) {

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
                binding.AdminAddImImages.setImageBitmap(bitmap);
            } catch (IOException e) {
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
                                            .makeText(Admin_Add.this,
                                                   "Image Uploaded!!",
                                                   Toast.LENGTH_SHORT)
                                            .show();


                    downloadUrl = uri.toString();
                    Toast.makeText(this, "Uri", Toast.LENGTH_SHORT).show();
                   Admin admines = new Admin(
                           idSpinner,
                            downloadUrl,
                            nametemp,
                            binding.AdminAddTvNameMeal.getText().toString(),
                            binding.AdminAddTvCaloriesMeal.getText().toString(),
                            binding.AdminAddTvDescriptionMeal.getText().toString());
                    addDataToFirestore(admines);
                    // Use the download URL to display the image or save it to a database
                    // ...
                });
            }).addOnFailureListener(exception -> {
                // Handle any errors
                // ...
                // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast
                                    .makeText(Admin_Add.this,
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

