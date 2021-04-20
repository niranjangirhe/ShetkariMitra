package com.ngsolutions.myapplication.HelperClasses.HomeAddapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.ngsolutions.myapplication.ChatActivity;
import com.ngsolutions.myapplication.Model.MessageModel;
import com.ngsolutions.myapplication.R;
import com.ngsolutions.myapplication.ReplyActivity;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatAdapter extends  RecyclerView.Adapter{

    ArrayList<MessageModel> messageModels;
    Context context;
    String cropCode,userID;
    FirebaseAuth Auth;
    FirebaseFirestore fstore;

    int SENDER_VIEW = 1;
    int RECEIVER_VIEW = 2;
    int SENDER_VIEW_IMAGE = 3;
    int RECEIVER_VIEW_IMAGE = 4;



    public ChatAdapter(ArrayList<MessageModel> messageModels, Context context, String cropCode, String userID) {
        this.messageModels = messageModels;
        this.context = context;
        this.cropCode=cropCode;
        this.userID=userID;
    }

    @Override
    public int getItemViewType(int position) {
        if(messageModels.get(position).getHaveImage() && messageModels.get(position).getSorR())
        {
            return  SENDER_VIEW_IMAGE;
        }
        else if(messageModels.get(position).getHaveImage() && !messageModels.get(position).getSorR())
        {
            return  RECEIVER_VIEW_IMAGE;
        }
        else if(messageModels.get(position).getSorR() && !messageModels.get(position).getHaveImage())
        {
            return SENDER_VIEW;
        }
        else if(!messageModels.get(position).getSorR() && !messageModels.get(position).getHaveImage())
        {
            return  RECEIVER_VIEW;
        }
        return 0;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == SENDER_VIEW)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_sender,parent,false);
            return  new SenderViewHolder(view);
        }
        else if(viewType == RECEIVER_VIEW)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_receiver,parent,false);
            return  new ReceiverViewHolder(view);
        }
        else if(viewType == SENDER_VIEW_IMAGE)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_sender_image,parent,false);
            return  new SenderImageViewHolder(view);
        }
        else if(viewType == RECEIVER_VIEW_IMAGE)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_receiver_image,parent,false);
            return  new ReceiverImageViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModel messageModel = messageModels.get(position);
        if(holder.getClass()==SenderViewHolder.class)
        {
            ((SenderViewHolder) holder).senderMessage.setText(messageModel.getMessage());
            ((SenderViewHolder) holder).time.setText(messageModel.getTime());
            ((SenderViewHolder) holder).myreplyThread.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ReplyActivity.class);
                    intent.putExtra("cropCode","r"+cropCode+"~"+messageModel.getIndex());
                    intent.putExtra("chatTitle",context.getString(R.string.your));
                    intent.putExtra("userID",userID);
                    intent.putExtra("message",messageModel.getMessage());
                    context.startActivity(intent);
                }
            });
        }
        else if(holder.getClass()==ReceiverViewHolder.class)
        {
            Auth = FirebaseAuth.getInstance();
            fstore = FirebaseFirestore.getInstance();
            DocumentReference documentReference = fstore.collection("users").document(messageModel.getName());
            documentReference.addSnapshotListener((Activity) context, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(value.exists()) {
                        ((ReceiverViewHolder) holder).reciverName.setText(value.get("userName").toString());
                    }
                }
            });

            ((ReceiverViewHolder) holder).time.setText(messageModel.getTime());
            ((ReceiverViewHolder) holder).recieverMessage.setText(messageModel.getMessage());
            ((ReceiverViewHolder) holder).replyThread.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(v.getContext(), ReplyActivity.class);
                    intent.putExtra("cropCode","r"+cropCode+"~"+messageModel.getIndex());
                    intent.putExtra("chatTitle",messageModel.getName());
                    intent.putExtra("userID",userID);
                    intent.putExtra("message",messageModel.getMessage());
                    context.startActivity(intent);
                }
            });
        }
        else if(holder.getClass()==SenderImageViewHolder.class)
        {
            ((SenderImageViewHolder) holder).senderMessage.setText(messageModel.getMessage());
            ((SenderImageViewHolder) holder).time.setText(messageModel.getTime());
            ImageView imageview = ((SenderImageViewHolder) holder).senderImageView;
            Picasso.with(context).load(messageModel.getUri()).fit().centerCrop().into(imageview);
            ((SenderImageViewHolder) holder).myreplyThread.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(v.getContext(), ReplyActivity.class);
                    intent.putExtra("cropCode","r"+cropCode+"~"+messageModel.getIndex());
                    intent.putExtra("chatTitle",context.getString(R.string.your));
                    intent.putExtra("userID",userID);
                    intent.putExtra("message",messageModel.getMessage());
                    context.startActivity(intent);


                }
            });
        }
        else if(holder.getClass()==ReceiverImageViewHolder.class)
        {
            Auth = FirebaseAuth.getInstance();
            fstore = FirebaseFirestore.getInstance();
            DocumentReference documentReference = fstore.collection("users").document(messageModel.getName());
            documentReference.addSnapshotListener((Activity) context, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(value.exists()) {
                        ((ReceiverImageViewHolder) holder).reciverName.setText(value.get("userName").toString());
                    }
                }
            });
            ((ReceiverImageViewHolder) holder).time.setText(messageModel.getTime());
            ((ReceiverImageViewHolder) holder).recieverMessage.setText(messageModel.getMessage());
            ImageView imageview = ((ReceiverImageViewHolder) holder).receiverImageView;
            Picasso.with(context).load(messageModel.getUri()).fit().centerCrop().into(imageview);
            ((ReceiverImageViewHolder) holder).replyThread.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(v.getContext(), ReplyActivity.class);
                    intent.putExtra("cropCode","r"+cropCode+"~"+messageModel.getIndex());
                    intent.putExtra("chatTitle",messageModel.getName());
                    intent.putExtra("userID",userID);
                    intent.putExtra("message",messageModel.getMessage());
                    context.startActivity(intent);


                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return messageModels.size();
    }

    public class ReceiverViewHolder extends RecyclerView.ViewHolder {
        TextView recieverMessage, reciverName,time;
        ImageButton replyThread;
        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            recieverMessage = itemView.findViewById(R.id.RecieverText);
            reciverName = itemView.findViewById(R.id.ReceiverName);
            replyThread = itemView.findViewById(R.id.replyThread);
            time = itemView.findViewById(R.id.Time_sr);
        }
    }
    public class SenderViewHolder extends RecyclerView.ViewHolder {
        TextView senderMessage,time;
        ImageButton myreplyThread;
        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMessage = itemView.findViewById(R.id.SenderText);
            myreplyThread = itemView.findViewById(R.id.myreplythread);
            time = itemView.findViewById(R.id.Time_ss);
        }
    }
    public class SenderImageViewHolder extends RecyclerView.ViewHolder {
        TextView senderMessage,time;
        ImageButton myreplyThread;
        ImageView senderImageView;
        public SenderImageViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMessage = itemView.findViewById(R.id.SenderImageText);
            myreplyThread = itemView.findViewById(R.id.myreplyImagethread);
            senderImageView = itemView.findViewById(R.id.senderImageView);
            time = itemView.findViewById(R.id.Time_ssi);
        }
    }
    public class ReceiverImageViewHolder extends RecyclerView.ViewHolder {
        TextView recieverMessage,reciverName,time;
        ImageButton replyThread;
        ImageView receiverImageView;
        public ReceiverImageViewHolder(@NonNull View itemView) {
            super(itemView);
            reciverName = itemView.findViewById(R.id.ReceiverImageName);
            recieverMessage = itemView.findViewById(R.id.ReceiverImageText);
            replyThread = itemView.findViewById(R.id.ReceiverReplyImageThread);
            receiverImageView = itemView.findViewById(R.id.receiverImageView);
            time = itemView.findViewById(R.id.Time_sri);
        }
    }
}
