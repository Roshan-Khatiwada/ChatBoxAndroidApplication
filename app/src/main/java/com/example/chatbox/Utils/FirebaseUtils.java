package com.example.chatbox.Utils;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.List;

public class FirebaseUtils {
    public static CollectionReference allUserCollectionReference(){
        return FirebaseFirestore.getInstance().collection("Registration");
    }

    public static String currentUserId(){
        return FirebaseAuth.getInstance().getUid();
    }

    public static DocumentReference currentUserDetails(){
        return FirebaseFirestore.getInstance().collection("Registration").document(currentUserId());
    }

    public static DocumentReference getChatRoomReference(String ChatroomId){
        return FirebaseFirestore.getInstance().collection("Chatroom").document(ChatroomId);
    }

    public static CollectionReference getChatroomMessageReference(String chatroomId){
        return getChatRoomReference(chatroomId).collection("chats");
    }
    public static CollectionReference allChatroomCollectionsReference(){
        return FirebaseFirestore.getInstance().collection("Chatroom");
    }
    public static DocumentReference getOtheruserFormChatroom(List<String> userIds){
        if(userIds.get(0).equals(FirebaseUtils.currentUserId())){
            return allUserCollectionReference().document(userIds .get(1));
        }
        else{
            return allUserCollectionReference().document(userIds.get(0));
        }
    }

    public static String timestampToString(Timestamp timestamp){
        return new SimpleDateFormat("HH:mm").format(timestamp.toDate());
    }

    public static StorageReference profilePicStorageRef(){
        return FirebaseStorage.getInstance().getReference().child("Profile_Pic")
                .child(FirebaseUtils.currentUserId());
    }
    public static StorageReference  getCurrentProfilePicStorageRef(){
        return FirebaseStorage.getInstance().getReference().child("profile_pic")
                .child(FirebaseUtils.currentUserId());
    }

    public static StorageReference  getOtherProfilePicStorageRef(String otherUserId){
        return FirebaseStorage.getInstance().getReference().child("profile_pic")
                .child(otherUserId);
    }
    public static boolean isLoggedIn(){
        if(currentUserId()!=null){
            return true;
        }
        return false;
    }


}
