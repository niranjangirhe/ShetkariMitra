package com.ngsolutions.myapplication.HelperClasses.HomeAddapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
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

import com.google.android.gms.tasks.OnSuccessListener;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ReplyAdapter extends  RecyclerView.Adapter{

    ArrayList<MessageModel> messageModels;
    Context context;
    FirebaseAuth Auth;
    FirebaseFirestore fstore;
    String cropCode,userID;

    int SENDER_VIEW = 1;
    int RECEIVER_VIEW = 2;
    int QUERY_VIEW = 3;
    int SENDER_VIEW_IMAGE = 4;
    int RECEIVER_VIEW_IMAGE = 5;

    public ReplyAdapter(ArrayList<MessageModel> messageModels, Context context,String cropCode, String userID) {
        this.messageModels = messageModels;
        this.context = context;
        this.cropCode = cropCode;
        this.userID = userID;
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
            Auth = FirebaseAuth.getInstance();
            fstore = FirebaseFirestore.getInstance();
            ((SenderViewHolder) holder).time.setText(getDate(messageModel.getTime()));
            if(messageModel.getMessage().equals("message is deleted"))
            {
                ((SenderViewHolder) holder).senderMessage.setTextColor(context.getResources().getColor(R.color.white40));
                ((SenderViewHolder) holder).senderMessage.setText(R.string.message_delete);
                ((SenderViewHolder) holder).delete.setVisibility(View.GONE);
            }
            else {
                ((SenderViewHolder) holder).senderMessage.setTextColor(context.getResources().getColor(R.color.white));
                ((SenderViewHolder) holder).senderMessage.setText(messageModel.getMessage());
                ((SenderViewHolder) holder).delete.setVisibility(View.VISIBLE);
                ((SenderViewHolder) holder).delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        messageModel.setMessage("message is deleted");
                        Map<String, Object> user = new HashMap<>();
                        user.put(messageModel.getKey(), userID + "~" + messageModel.getTime() + "||message is deleted");
                        DocumentReference addForum = fstore.collection("forums").document(cropCode);
                        addForum.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                ((SenderViewHolder) holder).senderMessage.setTextColor(context.getResources().getColor(R.color.white40));
                                ((SenderViewHolder) holder).senderMessage.setText(R.string.message_delete);
                                ((SenderViewHolder) holder).delete.setVisibility(View.GONE);
                                Toast.makeText(context, R.string.message_delete, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }
        else if(holder.getClass()== ReceiverViewHolder.class)
        {
            ((ReceiverViewHolder) holder).time.setText(getDate(messageModel.getTime()));
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
            if(messageModel.getMessage().equals("message is deleted"))
            {
                ((ReceiverViewHolder) holder).recieverMessage.setTextColor(context.getResources().getColor(R.color.white40));
                ((ReceiverViewHolder) holder).recieverMessage.setText(R.string.message_delete);
            }
            else {
                ((ReceiverViewHolder) holder).recieverMessage.setTextColor(context.getResources().getColor(R.color.white));
                ((ReceiverViewHolder) holder).recieverMessage.setText(messageModel.getMessage());
            }
        }
        else if(holder.getClass()== SenderImageViewHolder.class)
        {
            ((SenderImageViewHolder) holder).time.setText(getDate(messageModel.getTime()));
            Auth = FirebaseAuth.getInstance();
            fstore = FirebaseFirestore.getInstance();
            if(messageModel.getMessage().equals("message is deleted"))
            {
                ((SenderImageViewHolder) holder).senderMessage.setTextColor(context.getResources().getColor(R.color.white40));
                ((SenderImageViewHolder) holder).senderMessage.setText(R.string.message_delete);
                ((SenderImageViewHolder) holder).delete.setVisibility(View.GONE);
                ((SenderImageViewHolder) holder).senderImageView.setVisibility(View.GONE);
            }
            else {
                ((SenderImageViewHolder) holder).delete.setVisibility(View.VISIBLE);
                ((SenderImageViewHolder) holder).senderImageView.setVisibility(View.VISIBLE);
                ((SenderImageViewHolder) holder).senderMessage.setTextColor(context.getResources().getColor(R.color.white));
                ((SenderImageViewHolder) holder).senderMessage.setText(messageModel.getMessage());
                ImageView imageview = ((SenderImageViewHolder) holder).senderImageView;
                Picasso.with(context).load(messageModel.getUri()).fit().centerCrop().into(imageview);
                ((SenderImageViewHolder) holder).delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        messageModel.setMessage("message is deleted");
                        Map<String, Object> user = new HashMap<>();
                        user.put(messageModel.getKey(), userID + "~" + messageModel.getTime() + "||message is deleted");
                        DocumentReference addForum = fstore.collection("forums").document(cropCode);
                        addForum.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                ((SenderImageViewHolder) holder).senderMessage.setTextColor(context.getResources().getColor(R.color.white40));
                                ((SenderImageViewHolder) holder).senderMessage.setText(R.string.message_delete);
                                ((SenderImageViewHolder) holder).delete.setVisibility(View.GONE);
                                ((SenderImageViewHolder) holder).senderImageView.setVisibility(View.GONE);
                                Toast.makeText(context, R.string.message_delete, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }
        else if(holder.getClass()== ReceiverImageViewHolder.class)
        {
            Auth = FirebaseAuth.getInstance();
            fstore = FirebaseFirestore.getInstance();
            ((ReceiverImageViewHolder) holder).time.setText(getDate(messageModel.getTime()));
            DocumentReference documentReference = fstore.collection("users").document(messageModel.getName());
            documentReference.addSnapshotListener((Activity) context, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(value.exists()) {
                        ((ReceiverImageViewHolder) holder).reciverName.setText(value.get("userName").toString());
                    }
                }
            });
            if(messageModel.getMessage().equals("message is deleted"))
            {
                ((ReceiverImageViewHolder) holder).recieverMessage.setText(R.string.message_delete);
                ((ReceiverImageViewHolder) holder).recieverMessage.setTextColor(context.getResources().getColor(R.color.white40));
                ((ReceiverImageViewHolder) holder).receiverImageView.setVisibility(View.GONE);
            }
            else {
                ((ReceiverImageViewHolder) holder).recieverMessage.setTextColor(context.getResources().getColor(R.color.white));
                ((ReceiverImageViewHolder) holder).recieverMessage.setText(messageModel.getMessage());
                ImageView imageview = ((ReceiverImageViewHolder) holder).receiverImageView;
                ((ReceiverImageViewHolder) holder).receiverImageView.setVisibility(View.VISIBLE);
                Picasso.with(context).load(messageModel.getUri()).fit().centerCrop().into(imageview);
            }
        }
    }

    private String getDate(String dateinmill)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
        Date resultdate = new Date(Long.parseLong(dateinmill));
        return DateFormat.format("HH:mm  dd/MM/yy", resultdate).toString();

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
        ImageButton delete;
        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMessage = itemView.findViewById(R.id.SenderReplyText);
            time = itemView.findViewById(R.id.Time_ssr);
            delete = itemView.findViewById(R.id.SenderReplyTextDelete);
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
        ImageButton delete;
        public SenderImageViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMessage = itemView.findViewById(R.id.SenderImageTextReply);
            senderImageView = itemView.findViewById(R.id.senderImageViewReply);
            time = itemView.findViewById(R.id.Time_rsi);
            delete = itemView.findViewById(R.id.senderImageViewReplydelete);
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
