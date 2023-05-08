package com.sarmed.my_admin.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sarmed.my_admin.Models.Admin;
import com.sarmed.my_admin.Models.Doctor;
import com.sarmed.my_admin.R;

import java.util.ArrayList;

public class AdminRVAdapter extends RecyclerView.Adapter<AdminRVAdapter.ViewHolder> {
    FirebaseFirestore db;
    // creating variables for our ArrayList and context
    private ArrayList<Admin> adminsArrayList;
    private Context context;
    OnEditListners onEditListners;

    public AdminRVAdapter(ArrayList<Admin> adminsArrayList, Context context, OnEditListners onEditListners) {
        this.adminsArrayList = adminsArrayList;
        this.context = context;
        this.onEditListners = onEditListners;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.temp_admin,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // setting data to our text views from our modal class.
        Admin admin = adminsArrayList.get(position);
        // loading the images from the position

        Glide.with(holder.itemView.getContext())
                .load(admin.getFilePath())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imageView);
        holder.name.setText(admin.getDepartmentName());
        holder.nameMeal.setText(admin.getNameMeal());
        holder.calories.setText(admin.getCaloriesMeal());
        holder.desc.setText(admin.getDescriptionMeal());

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEditListners.onEditClick(adminsArrayList.get(position));
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAdmin(adminsArrayList.get(position));
                adminsArrayList.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return adminsArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView, edit , delete;
        TextView name , nameMeal , calories, desc;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.temp_imageView);
            name = itemView.findViewById(R.id.temp_name);
            nameMeal = itemView.findViewById(R.id.temp_name_meal);
            calories = itemView.findViewById(R.id.temp_many_calories);
            desc = itemView.findViewById(R.id.temp_desc);
            edit = itemView.findViewById(R.id.tempAdmin_iv_edit);
            delete = itemView.findViewById(R.id.tempAdmin_iv_delete);
        }
    }

    public  interface OnEditListners{
        void  onEditClick(Admin admin);
    }

    private void deleteAdmin(Admin admin) {
        // below line is for getting the collection
        // where we are storing our courses.
        db = FirebaseFirestore.getInstance();
        db.collection("Admin").

                // after that we are getting the document
                // which we have to delete.
                        document(admin.getId()).

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
