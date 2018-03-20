package com.example.wajid.lyft.Service;

import com.example.wajid.lyft.Common.Common;
import com.example.wajid.lyft.Model.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by wajid on 13-Mar-18.
 */

public class MyFirebaseIdService extends FirebaseInstanceIdService {

    public MyFirebaseIdService() {
    }

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        updateTokenServer(refreshedToken); // update when token is refreshed
    }

    private void updateTokenServer(String refreshedToken) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference(Common.token_tbl);

        Token token = new Token(refreshedToken);
        if(FirebaseAuth.getInstance().getCurrentUser()!=null) // if already login , then update token
             tokens.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
              .setValue(token);
    }
}
