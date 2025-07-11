package com.example.chatbox;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    Dialog loading2;

    public Profile() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == getActivity().RESULT_OK){
                        Intent data = result.getData();
                        if(data!=null && data.getData()!=null){
                            selectedImageUri = data.getData();
                            AndroidUtils.setProfilePic(getContext(), selectedImageUri, profilePic);
                        }
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        profilePic = view.findViewById(R.id.profile_Pic);
        usernameInput = view.findViewById(R.id.username);
        phoneInput = view.findViewById(R.id.phone);
        updateProfileBtn = view.findViewById(R.id.updateProfile);
        logoutBtn = view.findViewById(R.id.logout);
        loading2 = new Dialog(requireContext());
        loading2.setContentView(R.layout.loading2);
        loading2.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        loading2.getWindow().setBackgroundDrawable(requireContext().getDrawable(R.drawable.roundcorners));
        loading2.setCancelable(false);

        getUserData();

        updateProfileBtn.setOnClickListener((v -> updateBtnClick()));

        logoutBtn.setOnClickListener((v) -> {
            loading2.show();  // Show loading dialog when logout is initiated
            FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(task -> {
                loading2.dismiss();  // Dismiss loading dialog regardless of task success or failure

                if (task.isSuccessful()) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(getContext(), Splash_Screen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    // Handle the case where deleting the token is not successful
                    Log.e("Profile", "Error deleting token", task.getException());
                    Toast.makeText(getActivity(), "Failed to logout", Toast.LENGTH_SHORT).show();
                }
            });
        });


        profilePic.setOnClickListener((v) -> ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512, 512)
                .createIntent(intent -> {
                    imagePickLauncher.launch(intent);
                    return null;
                }));

        return view;
    }

    void updateBtnClick() {
        String newUsername = usernameInput.getText().toString();
        if (newUsername.isEmpty() || newUsername.length() < 3) {
            usernameInput.setError("Username length should be at least 3 chars");
            return;
        }
        currentUserModel.setUsername(newUsername);

        // Show loading dialog before updating
        loading2.show();

        if (selectedImageUri != null) {
            FirebaseUtils.getCurrentProfilePicStorageRef().putFile(selectedImageUri)
                    .addOnCompleteListener(task -> {
                        // Dismiss loading dialog when the task is complete
                        loading2.dismiss();
                        updateToFirestore();
                    });
        } else {
            // Dismiss loading dialog when there is no image to upload
            loading2.dismiss();
            updateToFirestore();
        }
    }

    void updateToFirestore() {
        if (getContext() == null || getActivity() == null) {
            // Log an error or handle it appropriately
            return;
        }

        // Show loading dialog before updating
        loading2.show();

        FirebaseUtils.currentUserDetails().set(currentUserModel)
                .addOnCompleteListener(task -> {
                    getActivity().runOnUiThread(() -> {
                        // Dismiss loading dialog when the task is complete
                        loading2.dismiss();

                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
    }

    void getUserData() {
        // Show loading dialog before fetching user data
        loading2.show();

        FirebaseUtils.getCurrentProfilePicStorageRef().getDownloadUrl()
                .addOnCompleteListener(task -> {
                    // Dismiss loading dialog when the task is complete
                    loading2.dismiss();

                    if (task.isSuccessful()) {
                        Uri uri = task.getResult();
                        AndroidUtils.setProfilePic(getContext(), uri, profilePic);
                    }
                });

        FirebaseUtils.currentUserDetails().get().addOnCompleteListener(task -> {
            // Dismiss loading dialog when the task is complete
            loading2.dismiss();

            if (task.isSuccessful()) {
                currentUserModel = task.getResult().toObject(UserModel.class);

                if (currentUserModel != null) {
                    usernameInput.setText(currentUserModel.getUsername());
                    phoneInput.setText(currentUserModel.getPhone());
                } else {
                    // Handle the case where currentUserModel is null
                    Log.e("Profile", "UserModel is null");
                }
            } else {
                Log.e("Profile", "Error getting user details", task.getException());
            }
        });
    }
}
