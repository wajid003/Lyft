package com.example.wajid.lyft;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.HashMap;
import java.util.Map;

import static android.R.attr.type;
import static android.R.id.message;


public class ChatWindow extends AppCompatActivity {

    LinearLayout layout;
    ImageView sendButton;
    EditText messageArea;
    ScrollView scrollView;
    Firebase reference1, reference2;

    String Driver;
    String Rider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);


        if(getIntent()!=null)
        {
            Driver = getIntent().getStringExtra("Driver");
            Rider = getIntent().getStringExtra("Rider");
        }

        layout = (LinearLayout)findViewById(R.id.layout1);
        sendButton = (ImageView)findViewById(R.id.sendButton);
        messageArea = (EditText)findViewById(R.id.messageArea);
        scrollView = (ScrollView)findViewById(R.id.scrollView);

        Firebase.setAndroidContext(this);
        reference1 = new Firebase("https://lift-ef7da.firebaseio.com/messages/" + Rider + "_" + Driver );
        reference2 = new Firebase("https://lift-ef7da.firebaseio.com/messages/" + Driver + "_" + Rider);

       sendButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               String messageText = messageArea.getText().toString();
               messageArea.setText("");

               if(!messageText.equals("")){
                   Map<String, String> map = new HashMap<String, String>();
                   map.put("message", messageText);
                   map.put("user",Rider);

                   reference1.push().setValue(map);
                   reference2.push().setValue(map);
               }
           }
       });

        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                String message = map.get("message").toString();
                String userName = map.get("user").toString();

                if(userName.equals(Rider)){
                    addMessageBox("You:-\n" + message, 1);
                }
                else{
                    addMessageBox("Friend:-\n" + message, 2);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });



            }

    private void addMessageBox(String s, int i) {

        TextView textView = new TextView(ChatWindow.this);
        textView.setText(s);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 10);
        textView.setLayoutParams(lp);

        if(i == 1) {
            textView.setBackgroundResource(R.drawable.rounded_corner1);
        }
        else{
            textView.setBackgroundResource(R.drawable.rounded_corner2);
        }

        layout.addView(textView);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }

}
