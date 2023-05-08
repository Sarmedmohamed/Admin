package com.sarmed.my_admin.Adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import com.sarmed.my_admin.Admin_exercises_add;
import com.sarmed.my_admin.Models.Doctor;
import com.sarmed.my_admin.Models.Exercises;
import com.sarmed.my_admin.R;
import com.sarmed.my_admin.details_exercises;

import java.util.ArrayList;

public class ExercisesRVAdapter extends RecyclerView.Adapter<ExercisesRVAdapter.ViewHolder> {
    FirebaseFirestore db;

    // creating variables for our ArrayList and context
    private ArrayList<Exercises> exercisesArrayList;
    private Context context;
    OnEditListners onEditListners;

    public ExercisesRVAdapter(ArrayList<Exercises> exercisesArrayList, Context context, OnEditListners onEditListners) {
        this.exercisesArrayList = exercisesArrayList;
        this.context = context;
        this.onEditListners = onEditListners;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.temp_exercises,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // setting data to our text views from our modal class.
        Exercises exercises = exercisesArrayList.get(position);
        // loading the images from the position
        //Glide.with(holder.itemView.getContext()).load(imageList.get(position)).into(holder.imageView);
        Glide.with(holder.itemView.getContext())
                .load(exercises.getImGif())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imageView);
        holder.name.setText(exercises.getExerciseName());
        holder.duration.setText(exercises.getExerciseDuration());
        holder.description.setText(exercises.getDescriptionMeal());
//        holder.edit.setOnClickListener(v -> {
//            onEditListners.onEditClick(exercisesArrayList.get(position));
//        });

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEditListners.onEditClick(exercisesArrayList.get(position));
            }
        });


        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "delete", Toast.LENGTH_SHORT).show();
                deleteCourse(exercisesArrayList.get(position));
                exercisesArrayList.remove(exercisesArrayList.get(position));
                notifyDataSetChanged();

            }
        });
    }
        
    @Override
    public int getItemCount() {
        return exercisesArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView name , duration , description;
        ImageView edit,delete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.tempExercises_ImGif_imagesGif);
            name = itemView.findViewById(R.id.tempExercises_tv_ExerciseName);
            duration = itemView.findViewById(R.id.tempExercises_tv_exerciseDuration);
            description = itemView.findViewById(R.id.tempExercises_tv_descriptionMeal);
            edit = itemView.findViewById(R.id.tempExercises_iv_edit);
            delete = itemView.findViewById(R.id.tempExercises_iv_delete);
        }
    }

    public interface OnEditListners{
        void onEditClick(Exercises exercises);
    }

    private void deleteCourse(Exercises exercises) {
        // below line is for getting the collection
        // where we are storing our courses.
        db = FirebaseFirestore.getInstance();
        db.collection("exercises").

                // after that we are getting the document
                // which we have to delete.
                        document(exercises.getId()).

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

