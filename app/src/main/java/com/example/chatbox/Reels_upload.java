package com.example.chatbox;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.chatbox.Model.UserModel;
import com.example.chatbox.Model.VideoModel;
import com.example.chatbox.R;
import com.example.chatbox.Utils.FirebaseUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class Reels_upload extends AppCompatActivity {
    ImageView imageView;
    TextView username;
    VideoView videoView;
    ImageButton camera, upload, browse, back;
    Uri videouri;
    MediaController mediaController;
    EditText userinput;
    StorageReference storageReference;
    DatabaseReference databaseReference;

    private static final int REQUEST_VIDEO_CAPTURE = 102;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reels_upload);

        imageView = findViewById(R.id.profile_Pic);
        username = findViewById(R.id.username);
        back = findViewById(R.id.back);
        videoView = findViewById(R.id.videoView);
        upload = findViewById(R.id.upload);
        upload.setClickable(false);
        browse = findViewById(R.id.picked_video);
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("Videos");
        camera = findViewById(R.id.camera);
        userinput= findViewById(R.id.sendMsgEdittext);

        videoView.start();


        // Retrieve the user's profile information from the database
        FirebaseUtils.currentUserDetails().get().addOnSuccessListener(documentSnapshot -> {
            UserModel userModel = documentSnapshot.toObject(UserModel.class);
            if (userModel != null) {
                username.setText(userModel.getUsername());
                // You can load the profile picture using Glide or any other image loading library
                // Assuming you have a method in FirebaseUtils to get the download URL of the profile picture
                FirebaseUtils.getCurrentProfilePicStorageRef().getDownloadUrl().addOnSuccessListener(uri -> {
                    Glide.with(this).load(uri).circleCrop().into(imageView);
                });
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Reels_upload.this, ChatsListActivity.class);
                startActivity(intent);
                finish();
            }
        });
        browse.setOnClickListener(view -> {
            Dexter.withContext(getApplicationContext())
                    .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                            Intent intent = new Intent();
                            intent.setType("video/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(intent, 101);
                            upload.setClickable(true);
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                            // Handle the denial of permission
                            Toast.makeText(Reels_upload.this, "Please Allow The permission", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                            permissionToken.continuePermissionRequest();
                        }
                    }).check();
        });

        camera.setOnClickListener(view -> {
            Dexter.withContext(getApplicationContext())
                    .withPermission(Manifest.permission.CAMERA)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                            dispatchTakeVideoIntent();
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                            // Handle the denial of permission
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                            permissionToken.continuePermissionRequest();
                        }
                    }).check();
        });

        upload.setOnClickListener(view -> VideoUploading());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101 && resultCode == RESULT_OK) {
            videouri = data.getData();
            videoView.setVideoURI(videouri);
            upload.setClickable(true);
            // Start playing the video automatically
            videoView.setOnPreparedListener(mp -> {
                mp.start();
                mp.setLooping(true); // Auto-repeat the video
            });
        } else if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            videouri = data.getData();
            videoView.setVideoURI(videouri);
            upload.setClickable(true);
            // Start playing the video automatically
            videoView.setOnPreparedListener(mp -> {
                mp.start();
                mp.setLooping(true); // Auto-repeat the video
            });
        }
    }


    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    private String getExtension() {
        if (videouri != null) {
            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
            return mimeTypeMap.getExtensionFromMimeType(getContentResolver().getType(videouri));
        }
        return null;

    }


    private void VideoUploading() {
        if (videouri != null) {
            final ProgressDialog pd = new ProgressDialog(this);
            pd.setTitle("ChatBox");
            pd.show();
            pd.setCancelable(false);

            // Retrieve user details
            FirebaseUtils.currentUserDetails().get().addOnSuccessListener(documentSnapshot -> {
                UserModel userModel = documentSnapshot.toObject(UserModel.class);
                if (userModel != null) {
                    // Upload video with user details
                    final StorageReference uploader = storageReference.child("Videos/" + System.currentTimeMillis() + "." + getExtension());
                    uploader.putFile(videouri)
                            .addOnSuccessListener(taskSnapshot -> uploader.getDownloadUrl().addOnSuccessListener(uri -> {
                                DatabaseReference newVideoRef = databaseReference.push();
                                VideoModel obj = new VideoModel(
                                        new Timestamp(new Date()).toDate(),
                                        userinput.getText().toString(),
                                        userModel.getUsername(),
                                        uri.toString()


                                );
                                newVideoRef.setValue(obj)
                                        .addOnSuccessListener(unused -> {
                                            pd.dismiss();
                                            Toast.makeText(getApplicationContext(), "Successfully Uploaded", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            pd.dismiss();
                                            Toast.makeText(Reels_upload.this, "Failure", Toast.LENGTH_SHORT).show();
                                        });
                            }))
                            .addOnProgressListener(snapshot -> {
                                float per = (100 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                                pd.setMessage("Uploading " + (int) per + "%");
                            });
                }
            }).addOnFailureListener(e -> {
                pd.dismiss();
                Toast.makeText(Reels_upload.this, "Failed to retrieve user details", Toast.LENGTH_SHORT).show();
            });
        } else {
            // Handle the case where videouri is null
            Toast.makeText(Reels_upload.this, "Video not selected", Toast.LENGTH_SHORT).show();
        }
    }

}
