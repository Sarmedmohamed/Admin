package com.sarmed.my_admin.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sarmed.my_admin.Models.Admin;
import com.sarmed.my_admin.Models.Doctor;
import com.sarmed.my_admin.Models.Exercises;
import com.sarmed.my_admin.R;

import java.util.ArrayList;



public class DoctorRVAdapter extends RecyclerView.Adapter<DoctorRVAdapter.ViewHolder> {
    //fierbases fierStor
    FirebaseFirestore db;
    // creating variables for our ArrayList and context
    private ArrayList<Doctor> doctorsArrayList;
    private Context context;
    OnEditListners onEditListners;

    public DoctorRVAdapter(ArrayList<Doctor> doctorsArrayList, Context context, OnEditListners onEditListners) {
        this.doctorsArrayList = doctorsArrayList;
        this.context = context;
        this.onEditListners = onEditListners;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.temp_doctor,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // setting data to our text views from our modal class.
        Doctor doctor = doctorsArrayList.get(position);
        // loading the images from the position
        //Glide.with(holder.itemView.getContext()).load(imageList.get(position)).into(holder.imageView);
        Glide.with(holder.itemView.getContext())
                .load(doctor.getImage())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imageView);
                holder.name.setText(doctor.getName());
                holder.number.setText(doctor.getNumber());
                holder.email.setText(doctor.getEmail());
                holder.address.setText(doctor.getAddress());
                holder.description.setText(doctor.getDescription());

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEditListners.onEditClick(doctorsArrayList.get(position));
            }
        });
        
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "delete", Toast.LENGTH_SHORT).show();
                deleteCourse(doctorsArrayList.get(position));
                doctorsArrayList.remove(position);
                notifyDataSetChanged();
            }
        }); 
    }

    @Override
    public int getItemCount() {
        return doctorsArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
                        ImageView imageView , edit , delete;
                TextView name , number , email , address , description;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.doctor_iv_image);
            name = itemView.findViewById(R.id.doctor_tv_name);
            number = itemView.findViewById(R.id.doctor_tv_number);
            email = itemView.findViewById(R.id.doctor_tv_email);
            address = itemView.findViewById(R.id.doctor_tv_address);
            description = itemView.findViewById(R.id.doctor_tv_desc);
            edit = itemView.findViewById(R.id.tempDoctor_iv_edit);
            delete = itemView.findViewById(R.id.tempDoctor_iv_delete);
    }
}

   public interface OnEditListners{
        void  onEditClick(Doctor doctor);
   }

    private void deleteCourse(Doctor doctor) {
        // below line is for getting the collection
        // where we are storing our courses.
        db = FirebaseFirestore.getInstance();
        db.collection("Doctor").

                // after that we are getting the document
                // which we have to delete.
                        document(doctor.getId()).

                // after passing the document id we are calling
                // delete method to delete this document.
                        delete().
                // after deleting call on complete listener
                // method to delete this data.
                        addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // inside on complete method we are checking
                        // if the task is success or not.
                        if (task.isSuccessful()) {
                            // this method is called when the task is success
                            // after deleting we are starting our MainActivity.
//                            Toast.makeText(UpdateCourse.this, "Course has been deleted from Database.", Toast.LENGTH_SHORT).show();
//                            Intent i = new Intent(UpdateCourse.this, CoursesfierStore.class);
//                            startActivity(i);


                        } else {
                            // if the delete operation is failed
                            // we are displaying a toast message.
                            //      Toast.makeText(UpdateCourse.this, "Fail to delete the course. ", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }
}
