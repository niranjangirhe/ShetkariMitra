package com.ngsolutions.myapplication.HelperClasses.HomeAddapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ngsolutions.myapplication.Model.MessageModel;
import com.ngsolutions.myapplication.Model.MyQueryModel;
import com.ngsolutions.myapplication.R;
import com.ngsolutions.myapplication.ReplyActivity;

import java.util.ArrayList;

public class MyQueryAdapter extends  RecyclerView.Adapter{

    ArrayList<MyQueryModel> myQueryModels;
    Context context;
    String userID;
    FirebaseAuth Auth;
    FirebaseFirestore fstore;

    public MyQueryAdapter(ArrayList<MyQueryModel> myQueryModels, Context context, String userID) {
        this.myQueryModels = myQueryModels;
        this.context = context;
        this.userID = userID;
        Auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.my_queries,parent,false);
        return  new MyQueryAdapter.MyQueries(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyQueryModel myQueryModel = myQueryModels.get(position);
        ((MyQueries) holder).MyQueryCropName.setText(myQueryModel.getCropName());
        ((MyQueries) holder).MyQueryMessage.setText(myQueryModel.getMessage());
        ((MyQueries) holder).MyQueriesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ReplyActivity.class);
                intent.putExtra("cropCode", myQueryModel.getReplyForum());
                intent.putExtra("chatTitle", context.getString(R.string.your));
                intent.putExtra("userID", userID);
                intent.putExtra("message", myQueryModel.getMessage());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return myQueryModels.size();
    }

    public class MyQueries extends RecyclerView.ViewHolder {
        TextView MyQueryCropName, MyQueryMessage;
        ImageView MyQueriesBtn;
        public MyQueries(@NonNull View itemView) {
            super(itemView);
            MyQueryCropName = itemView.findViewById(R.id.MyQueryCropName);
            MyQueryMessage = itemView.findViewById(R.id.MyQueryMessage);
            MyQueriesBtn = itemView.findViewById(R.id.MyqueriesBtn);

        }
    }
}
