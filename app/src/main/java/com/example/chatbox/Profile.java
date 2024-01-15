package com.example.chatbox;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.chatbox.Model.UserModel;
import com.example.chatbox.Utils.AndroidUtils;
import com.example.chatbox.Utils.FirebaseUtils;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

public class Profile extends Fragment {
    ImageView profilePic;
    EditText usernameInput;
    EditText phoneInput;
    Button updateProfileBtn;
    TextView logoutBtn;

    UserModel currentUserModel;
    ActivityResultLauncher<Intent> imagePickLauncher;
    Uri selectedImageUri;

    public Profile() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        if(data!=null && data.getData()!=null){
                            selectedImageUri = data.getData();
                            AndroidUtils.setProfilePic(getContext(),selectedImageUri,profilePic);
                        }
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        profilePic = view.findViewById(R.id.profile_Pic);
        usernameInput = view.findViewById(R.id.username);
        phoneInput = view.findViewById(R.id.phone);
        updateProfileBtn = view.findViewById(R.id.updateProfile);
        logoutBtn = view.findViewById(R.id.logout);

        getUserData();

        updateProfileBtn.setOnClickListener((v -> updateBtnClick()));

        logoutBtn.setOnClickListener((v)-> FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(),Splash_Screen.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }));

        profilePic.setOnClickListener((v)-> ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512,512)
                .createIntent(intent -> {
                    imagePickLauncher.launch(intent);
                    return null;
                }));

        return view;
    }

    void updateBtnClick(){
        String newUsername = usernameInput.getText().toString();
        if(newUsername.isEmpty() || newUsername.length()<3){
            usernameInput.setError("Username length should be at least 3 chars");
            return;
        }
        currentUserModel.setUsername(newUsername);


        if(selectedImageUri!=null){
            FirebaseUtils.getCurrentProfilePicStorageRef().putFile(selectedImageUri)
                    .addOnCompleteListener(task -> updateToFirestore());
        }else{
            updateToFirestore();
        }





    }

    void updateToFirestore(){
        FirebaseUtils.currentUserDetails().set(currentUserModel)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(getContext(), "Successful", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getContext(),"Failed",Toast.LENGTH_SHORT).show();
                    }
                });
    }



    void getUserData(){
        FirebaseUtils.getCurrentProfilePicStorageRef().getDownloadUrl()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Uri uri  = task.getResult();
                        AndroidUtils.setProfilePic(getContext(),uri,profilePic);
                    }
                });

        FirebaseUtils.currentUserDetails().get().addOnCompleteListener(task -> {
            currentUserModel = task.getResult().toObject(UserModel.class);
            assert currentUserModel != null;
            usernameInput.setText(currentUserModel.getUsername());
            phoneInput.setText(currentUserModel.getPhone());
        });
    }

}