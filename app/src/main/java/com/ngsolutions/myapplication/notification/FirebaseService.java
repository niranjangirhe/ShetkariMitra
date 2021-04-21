package com.ngsolutions.myapplication.notification;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdReceiver;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.HashMap;
import java.util.Map;

public class FirebaseService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String tokenRefresh = FirebaseInstanceId.getInstance().getToken();
        if(user != null)
        {
            updateToken(tokenRefresh, user.getUid());
        }
    }

    private void updateToken(String tokenRefresh, String uid) {
        FirebaseFirestore fstore;
        fstore = FirebaseFirestore.getInstance();
        DocumentReference tokenUpdater = fstore.collection("users").document(uid);
        Map<String,Object> tk = new HashMap<>();
        tk.put("token",FirebaseInstanceId.getInstance().getToken());
        tokenUpdater.update(tk).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //Toast.makeText(HomePage.this, "Token Updated", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
