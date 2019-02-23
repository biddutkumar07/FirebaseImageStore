package com.example.biddutkumar.firebaseimagestore;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private Button saveBtn,displayBtn,chooseImageBtn;
    private EditText enterImagenameET;
    private ImageView imageView;
    private ProgressBar progressBar;
    private Uri imageUri;

    DatabaseReference databaseReference;
    StorageReference storageReference;
    StorageTask uploadTask;

    private static final int IMAGE_REQUEST=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseReference= FirebaseDatabase.getInstance().getReference("Upload");
        storageReference= FirebaseStorage.getInstance().getReference("Upload");

        saveBtn=findViewById(R.id.saveBtnId);
        displayBtn=findViewById(R.id.displayBtnId);
        chooseImageBtn=findViewById(R.id.chooseImageBtnId);

        enterImagenameET=findViewById(R.id.enterImagenameETId);
        imageView=findViewById(R.id.imageViewId);
        progressBar=findViewById(R.id.progressBarId);

        saveBtn.setOnClickListener(this);
        displayBtn.setOnClickListener(this);
        chooseImageBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id;
        id=v.getId();
        switch (id)
        {
            case R.id.chooseImageBtnId:
                openFileChooser();
                break;

            case R.id.saveBtnId:
                if(uploadTask!=null && uploadTask.isInProgress())
                {
                    Toast.makeText(getApplicationContext(), "Uploading in progress...", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    saveData();
                }

                break;

            case R.id.displayBtnId:
                Intent intent=new Intent(MainActivity.this, ImageShowActivity.class);
                startActivity(intent);
                break;

        }
    }

    private void openFileChooser() {

        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==IMAGE_REQUEST && resultCode==RESULT_OK && data!=null && data.getData()!=null)
        {
            imageUri=data.getData();
            Picasso.with(this).load(imageUri).into(imageView);
        }
    }

    // getting the file extension of the image
    public String getFileExtension(Uri imageUri)
    {
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));
    }

    private void saveData() {

        final String imageName=enterImagenameET.getText().toString().trim();

        if(imageName.isEmpty())
        {
            enterImagenameET.setError("Enter the image name ");
            enterImagenameET.requestFocus();
            return;
        }

        StorageReference ref=storageReference.child(System.currentTimeMillis()+"."+getFileExtension(imageUri));
        progressBar.setVisibility(View.VISIBLE);

        ref.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressBar.setVisibility(View.GONE);
                        // Get a URL to the uploaded content

                        Toast.makeText(getApplicationContext(), "Image is stored successfully ", Toast.LENGTH_SHORT).show();

                        Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());

                        Uri downloadUri= uriTask.getResult();

                        Upload upload=new Upload(imageName,downloadUri.toString());
                        String uploadId=databaseReference.push().getKey();
                        databaseReference.child(uploadId).setValue(upload);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Toast.makeText(getApplicationContext(), "Image is not stored successfully ", Toast.LENGTH_SHORT).show();
                    }
                });

    }

}
