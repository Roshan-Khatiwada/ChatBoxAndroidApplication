package com.example.chatbox.Utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

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

}
