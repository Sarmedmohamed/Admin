package com.sarmed.my_admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sarmed.my_admin.Adapters.AdminRVAdapter;
import com.sarmed.my_admin.Adapters.DoctorRVAdapter;
import com.sarmed.my_admin.Models.Admin;
import com.sarmed.my_admin.Models.Doctor;
import com.sarmed.my_admin.databinding.ActivityDetailsDoctorBinding;
import com.sarmed.my_admin.databinding.ActivityDetailsFoodBinding;

import java.util.ArrayList;
import java.util.List;

public class details_doctor extends AppCompatActivity implements DoctorRVAdapter.OnEditListners{
    ActivityDetailsDoctorBinding binding;
    ArrayList<Doctor> doctors;
    DoctorRVAdapter adapter;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailsDoctorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // initializing our variable for firebase
        // firestore and getting its instance.
        db = FirebaseFirestore.getInstance();


        doctors=new ArrayList<>();
        adapter = new DoctorRVAdapter(doctors , getBaseContext(), this::onEditClick);
        binding.idRVCoursesDoctor.setHasFixedSize(true);
        binding.idRVCoursesDoctor.setLayoutManager(new LinearLayoutManager(null));
        binding.idRVCoursesDoctor.setAdapter(adapter);
        binding.idProgressBarDoctor.setVisibility(View.VISIBLE);

        db.collection("Doctor").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // after getting the data we are calling on success method
                        // and inside this method we are checking if the received
                        // query snapshot is empty or not.
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // if the snapshot is not empty we are
                            // hiding our progress bar and adding
                            // our data in a list.
                            binding.idProgressBarDoctor.setVisibility(View.GONE);
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                // after getting this list we are passing
                                // that list to our object class.
                                Doctor c = d.toObject(Doctor.class);

                                // below is the updated line of code which we have to
                                // add to pass the document id inside our modal class.
                                // we are setting our document id with d.getId() method
                                c.setId(d.getId());

                                // and we will pass this object class
                                // inside our arraylist which we have
                                // created for recycler view.
                                doctors.add(c);
                            }
                            // after adding the data to recycler view.
                            // we are calling recycler view notifyDataSetChanged
                            // method to notify that data has been changed in recycler view.
                            adapter.notifyDataSetChanged();
                        } else {
                            // if the snapshot is empty we are displaying a toast message.
                            Toast.makeText(details_doctor.this, "No data found in Database", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // if we do not get any data or any error we are displaying
                        // a toast message that we do not get any data
                        Toast.makeText(details_doctor.this, "Fail to get the data.", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public void onEditClick(Doctor doctor) {
        Intent intent = new Intent(getBaseContext() , Update_Doctor.class);
        intent.putExtra("doctor" , doctor);
        startActivity(intent);
    }
}