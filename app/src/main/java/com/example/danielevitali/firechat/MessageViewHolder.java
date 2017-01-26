package com.example.danielevitali.firechat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 *
 */
public class MessageViewHolder extends RecyclerView.ViewHolder {

    private TextView nameTextView, messageTextView;

    public MessageViewHolder(View itemView) {
        super(itemView);
        nameTextView = (TextView) itemView.findViewById(R.id.name);
        messageTextView = (TextView) itemView.findViewById(R.id.message);
    }

    public void onBindViewHodler(Message message){
        nameTextView.setText(message.getName());
        messageTextView.setText(message.getText());
    }

}
