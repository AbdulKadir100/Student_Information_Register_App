package com.example.student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ShowProfile extends AppCompatActivity {

    TextView nameTv,IDTv,branchTv,cityTv,contactTv;
    ImageView imageView;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_profile);

        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        imageView = findViewById(R.id.image_sp);
        nameTv = findViewById(R.id.name_tv_sp);
        IDTv = findViewById(R.id.age_tv_sp);
        branchTv = findViewById(R.id.branch_tv_sp);
        cityTv = findViewById(R.id.city_tv_sp);
        contactTv = findViewById(R.id.contact_tv_sp);

        documentReference = db.collection("student").document("data");
        storageReference = FirebaseStorage.getInstance().getReference("images");
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {

            documentReference.get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.getResult().exists()) {
                                String name_res = task.getResult().getString("name");
                                String age_res = task.getResult().getString("ID");
                                String bio_res = task.getResult().getString("branch");
                                String email_res = task.getResult().getString("city");
                                String web_res = task.getResult().getString("contact");
                                String Url = task.getResult().getString("url");

                                Picasso.get().load(Url).into(imageView);

                                nameTv.setText(name_res);
                                IDTv.setText(age_res);
                                branchTv.setText(bio_res);
                                cityTv.setText(email_res);
                                contactTv.setText(web_res);

                            } else {
                                Toast.makeText(ShowProfile.this, "No Profile Exist", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ShowProfile.this,ShowProfile.class);
                                startActivity(intent);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}