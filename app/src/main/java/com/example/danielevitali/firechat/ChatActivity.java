package com.example.danielevitali.firechat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.Arrays;
import java.util.Collections;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText editText;
    private FirebaseRecyclerAdapter<Message, MessageViewHolder> adapter;
    private DatabaseReference ref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        recyclerView = (RecyclerView) findViewById(R.id.list);
        editText = (EditText) findViewById(R.id.message);
        findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(editText.getText().toString())) {
                    return;
                }

                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser == null) {
                    Intent intent = AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setProviders(Collections.singletonList(new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                            .setIsSmartLockEnabled(false)
                            .build();
                    startActivityForResult(intent, 0);
                    return;
                }

                ref.push().setValue(new Message(currentUser.getDisplayName(), editText.getText().toString()));
                Bundle bundle = new Bundle();
                bundle.putString("text", editText.getText().toString());
                FirebaseAnalytics.getInstance(ChatActivity.this).logEvent("newMessage", bundle);
                editText.setText("");
            }
        });

        FirebaseDatabase instance = FirebaseDatabase.getInstance();
        ref = instance.getReference();

        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        manager.setStackFromEnd(true);
        recyclerView.setLayoutManager(manager);
        adapter = new FirebaseRecyclerAdapter<Message, MessageViewHolder>(Message.class, R.layout.message_item, MessageViewHolder.class, ref) {
            @Override
            protected void populateViewHolder(MessageViewHolder viewHolder, Message model, final int position) {
                viewHolder.onBindViewHodler(model);
            }
        };
        recyclerView.setAdapter(adapter);

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                recyclerView.scrollToPosition(adapter.getItemCount() - 1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                throw new RuntimeException("Message has been changed!");
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        FirebaseRemoteConfig.getInstance().fetch(0).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    FirebaseRemoteConfig.getInstance().activateFetched();
                }
            }
        });
        String color = FirebaseRemoteConfig.getInstance().getString("bg_color");
        recyclerView.setBackgroundColor(Color.parseColor(color));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            ref.push().setValue(new Message(FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), editText.getText().toString()));
            editText.setText("");
        }
    }

    @Override
    protected void onDestroy() {
        adapter.cleanup();
        super.onDestroy();
    }
}
