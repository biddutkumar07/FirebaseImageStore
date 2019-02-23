package com.example.biddutkumar.firebaseimagestore;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ImageShowActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private List<Upload> uploadList;
    DatabaseReference databaseReference;
    private ProgressBar progressBar;
    private FirebaseStorage firebaseStorage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_show);

        recyclerView=findViewById(R.id.RecyclerViewRVId);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressBar=findViewById(R.id.progressBarRecyclerId);
        firebaseStorage=FirebaseStorage.getInstance();

        uploadList=new ArrayList<>();

        databaseReference=FirebaseDatabase.getInstance().getReference("Upload");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                uploadList.clear();

                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
                    Upload upload=dataSnapshot1.getValue(Upload.class);
                    upload.setKey(dataSnapshot1.getKey());
                    uploadList.add(upload);
                }
                imageAdapter=new ImageAdapter(ImageShowActivity.this,uploadList);
                recyclerView.setAdapter(imageAdapter);
                imageAdapter.setOnItemClickListener(new ImageAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {

                        String text=uploadList.get(position).getImageName();
                        Toast.makeText(getApplicationContext(), text+" is selected "+position, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDoAnyTask(int position) {

                        Toast.makeText(getApplicationContext(), "onDoAnyTask is selected ", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onDelete(int position) {

                       // Toast.makeText(getApplicationContext(), "onDelete is selected ", Toast.LENGTH_SHORT).show();

                        Upload selectedItem=uploadList.get(position);
                        final String key=selectedItem.getKey();

                        StorageReference storageReference=firebaseStorage.getReferenceFromUrl(selectedItem.getImageUri());
                        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                databaseReference.child(key).removeValue();
                                Toast.makeText(getApplicationContext(), "Item is deleted ", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                });

                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(ImageShowActivity.this, "Error"+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }
}
