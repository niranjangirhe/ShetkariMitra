package com.ngsolutions.myapplication.HelperClasses.HomeAddapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.ngsolutions.myapplication.Model.MessageModel;
import com.ngsolutions.myapplication.R;
import com.ngsolutions.myapplication.ReplyActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ReplyAdapter extends  RecyclerView.Adapter{

    ArrayList<MessageModel> messageModels;
    Context context;
    FirebaseAuth Auth;
    FirebaseFirestore fstore;

    int SENDER_VIEW = 1;
    int RECEIVER_VIEW = 2;
    int QUERY_VIEW = 3;
    int SENDER_VIEW_IMAGE = 4;
    int RECEIVER_VIEW_IMAGE = 5;

    public ReplyAdapter(ArrayList<MessageModel> messageModels, Context context) {
        this.messageModels = messageModels;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if(position==0)
        {
            return QUERY_VIEW;
        }
        else if(messageModels.get(position).getHaveImage() && messageModels.get(position).getSorR())
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
            View view = LayoutInflater.from(context).inflate(R.layout.sample_sender_reply,parent,false);
            return  new SenderViewHolder(view);
        }
        else if(viewType == RECEIVER_VIEW)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_receiver_reply,parent,false);
            return  new ReceiverViewHolder(view);
        }
        else if(viewType == QUERY_VIEW)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_sender_querry,parent,false);
            return  new QuerryViewHolder(view);
        }
        else if(viewType == SENDER_VIEW_IMAGE)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.reply_sender_image,parent,false);
            return  new SenderImageViewHolder(view);
        }
        else if(viewType == RECEIVER_VIEW_IMAGE)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.reply_receiver_image,parent,false);
            return  new ReceiverImageViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModel messageModel = messageModels.get(position);
        if(position==0)
        {
            ((QuerryViewHolder) holder).QueryMessage.setText(messageModel.getMessage());
        }
        else if(holder.getClass()== SenderViewHolder.class)
        {
            ((SenderViewHolder) holder).senderMessage.setText(messageModel.getMessage());
            ((SenderViewHolder) holder).time.setText(messageModel.getTime());
        }
        else if(holder.getClass()== ReceiverViewHolder.class)
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
            ((ReceiverViewHolder) holder).recieverMessage.setText(messageModel.getMessage());
            ((ReceiverViewHolder) holder).time.setText(messageModel.getTime());
        }
        else if(holder.getClass()== SenderImageViewHolder.class)
        {
            ((SenderImageViewHolder) holder).senderMessage.setText(messageModel.getMessage());
            ImageView imageview = ((SenderImageViewHolder) holder).senderImageView;
            ((SenderImageViewHolder) holder).time.setText(messageModel.getTime());
            Picasso.with(context).load(messageModel.getUri()).fit().centerCrop().into(imageview);
        }
        else if(holder.getClass()== ReceiverImageViewHolder.class)
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
        }
    }

    @Override
    public int getItemCount() {
        return messageModels.size();
    }

    public class ReceiverViewHolder extends RecyclerView.ViewHolder {
        TextView recieverMessage, reciverName,time;
        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            recieverMessage = itemView.findViewById(R.id.RecieverText);
            reciverName = itemView.findViewById(R.id.ReceiverName);
            time = itemView.findViewById(R.id.Time_srr);
        }
    }
    public class SenderViewHolder extends RecyclerView.ViewHolder {
        TextView senderMessage,time;
        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMessage = itemView.findViewById(R.id.SenderText);
            time = itemView.findViewById(R.id.Time_ssr);
        }
    }
    public class QuerryViewHolder extends RecyclerView.ViewHolder {
        TextView QueryMessage;
        public QuerryViewHolder(@NonNull View itemView) {
            super(itemView);
            QueryMessage = itemView.findViewById(R.id.QueryText);
        }
    }
    public class SenderImageViewHolder extends RecyclerView.ViewHolder {
        TextView senderMessage,time;
        ImageView senderImageView;
        public SenderImageViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMessage = itemView.findViewById(R.id.SenderImageTextReply);
            senderImageView = itemView.findViewById(R.id.senderImageViewReply);
            time = itemView.findViewById(R.id.Time_rsi);
        }
    }
    public class ReceiverImageViewHolder extends RecyclerView.ViewHolder {
        TextView recieverMessage,reciverName,time;
        ImageView receiverImageView;
        public ReceiverImageViewHolder(@NonNull View itemView) {
            super(itemView);
            reciverName = itemView.findViewById(R.id.ReceiverImageNameReply);
            recieverMessage = itemView.findViewById(R.id.ReceiverImageTextReply);
            receiverImageView = itemView.findViewById(R.id.receiverImageViewReply);
            time = itemView.findViewById(R.id.Time_rri);
        }
    }
}
