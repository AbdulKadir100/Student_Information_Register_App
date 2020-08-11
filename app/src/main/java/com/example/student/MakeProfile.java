package com.example.student;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class MakeProfile extends AppCompatActivity {
    EditText ed_name, ed_ID, ed_branch, ed_city, ed_contact;
    ImageView imageView;
    ProgressBar progressBar;
    private Uri imageUri;
    private static final int PICK_IMAGE = 1;
    UploadTask uploadTask;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_profile);

        getSupportActionBar().setTitle("New Student");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ed_name = findViewById(R.id.name_et);
        ed_ID = findViewById(R.id.id_et);
        ed_branch = findViewById(R.id.branch_et);
        ed_city = findViewById(R.id.city_et);
        ed_contact = findViewById(R.id.contact_et);
        imageView = findViewById(R.id.image_choose);
        Button btn = findViewById(R.id.save_profile_btn);
        progressBar = findViewById(R.id.progressbar);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadData();
            }
        });


        documentReference = db.collection("student").document("data");
        storageReference = FirebaseStorage.getInstance().getReference("images");
    }

    private void UploadData() {

        final String name = ed_name.getText().toString();
        final String ID = ed_ID.getText().toString();
        final String branch = ed_branch.getText().toString();
        final String city = ed_city.getText().toString();
        final String contact = ed_contact.getText().toString();
        try {


            if (!TextUtils.isEmpty(name) || !TextUtils.isEmpty(ID) || !TextUtils.isEmpty(branch)
                    || !TextUtils.isEmpty(city) || !TextUtils.isEmpty(contact) || imageUri != null) {
                progressBar.setVisibility(View.VISIBLE);
                final StorageReference reference = storageReference.child(System.currentTimeMillis() + "," + getFileExt(imageUri));
                uploadTask = reference.putFile(imageUri);

                Task<Uri> task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return reference.getDownloadUrl();
                    }
                })
                        .addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Uri downloadUri = task.getResult();
                                    final Map<String, String> profile = new HashMap<>();
                                    profile.put("name", name);
                                    profile.put("ID", ID);
                                    profile.put("branch", branch);
                                    profile.put("city", city);
                                    profile.put("contact", contact);
                                    profile.put("url", downloadUri.toString());

                                    documentReference.set(profile)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    Toast.makeText(MakeProfile.this, "Profile Created", Toast.LENGTH_SHORT).show();

                                                Intent intent = new Intent(MakeProfile.this, ShowProfile.class);
                                                startActivity(intent);

                                                }

                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(MakeProfile.this, "Failed", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });

            } else {
                Toast.makeText(this, "All Fields are required", Toast.LENGTH_SHORT).show();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void ChooseImage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE || resultCode == RESULT_OK || data != null || data.getData() != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(imageView);


        }
    }

    private String getFileExt(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}