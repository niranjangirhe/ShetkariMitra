package com.ngsolutions.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.ngsolutions.myapplication.HelperClasses.HomeAddapter.ChatAdapter;
import com.ngsolutions.myapplication.HelperClasses.HomeAddapter.ReplyAdapter;
import com.ngsolutions.myapplication.Model.MessageModel;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ReplyActivity extends AppCompatActivity {


    ImageButton backButton;
    TextView chatTitleText;
    String cropCode;
    String chatTitle;
    FirebaseAuth Auth;
    FirebaseFirestore fstore;
    String userID;
    EditText messageText;
    ImageButton sendText,addImageButton;
    RecyclerView chatRecyclerView;
    int textnum=0;
    ArrayList<MessageModel> messageModels = new ArrayList<>();
    ReplyAdapter replyAdapter;
    String queeryMessage;
    ProgressBar progressBar;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri;
    private StorageReference mStoreRef;
    private StorageTask mUploadTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);
        loadLocale();
        chatTitleText = findViewById(R.id.ReplyTilteText);
        backButton = findViewById(R.id.BackBtnReply);
        messageText = findViewById(R.id.AddReplyText);
        sendText = findViewById(R.id.SendReplyText);
        chatRecyclerView = findViewById(R.id.ReplyRecylcer);
        addImageButton = findViewById(R.id.AddImageBtnReply);
        progressBar = findViewById(R.id.replyUploadBar);

        mStoreRef = FirebaseStorage.getInstance().getReference("uploads");

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //layoutManager.setReverseLayout(true);
        //layoutManager.setStackFromEnd(true);
        chatRecyclerView.setLayoutManager(layoutManager);

        Bundle b = getIntent().getExtras();
        if(b!=null) {
            cropCode = (String) b.get("cropCode");
            chatTitle = (String) b.get("chatTitle");
            Auth = FirebaseAuth.getInstance();
            fstore = FirebaseFirestore.getInstance();
            DocumentReference documentReference = fstore.collection("users").document(chatTitle);
            documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(value.exists()) {
                        chatTitleText.setText(value.get("userName").toString() + " "+getString(R.string.query));
                    }
                }
            });
            chatTitleText.setText(chatTitle + " "+getString(R.string.query));
            userID = (String) b.get("userID");
            queeryMessage = (String) b.get("message");
            checkIfPresent();
            replyAdapter = new ReplyAdapter(messageModels, ReplyActivity.this);
            chatRecyclerView.setAdapter(replyAdapter);
        }
        else {
            Toast.makeText(this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            finish();
        }
        DocumentReference documentReference = fstore.collection("forums").document(cropCode);
        documentReference.addSnapshotListener(ReplyActivity.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.exists()) {
                    if(Integer.parseInt(value.get("textNum").toString())>0) {
                        readMessage();
                        }

                }

            }
        });
        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
        sendText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( mUploadTask!=null && mUploadTask.isInProgress())
                {
                    Toast.makeText(ReplyActivity.this, getString(R.string.upload_in_progress), Toast.LENGTH_SHORT).show();
                }
                else if(!messageText.getText().toString().isEmpty() ){
                    sendMessage();
                }
                else
                {
                    Toast.makeText(ReplyActivity.this, getString(R.string.write_message), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data!=null && data.getData()!=null)
        {
            mImageUri = data.getData();
            Picasso.with(this).load( mImageUri ).fit().centerCrop().into(addImageButton);
        }
    }

    private void fillChats(ArrayList<MessageModel> messageModels) {
        chatRecyclerView.smoothScrollToPosition(messageModels.size());
        replyAdapter.notifyDataSetChanged();
    }

    private String getFileExt(Uri uri)
    {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }
    private void sendMessage() {
        try {
            String message = messageText.getText().toString();
            DocumentReference documentReference = fstore.collection("forums").document(cropCode);
            documentReference.addSnapshotListener(ReplyActivity.this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    int textNum = Integer.parseInt(value.get("textNum").toString());
                    if (!messageText.getText().toString().isEmpty()) {
                        messageText.setText("");
                        Map<String, Object> user = new HashMap<>();
                        String messageKey = "text" + textNum;

                        if (mImageUri != null) {
                            StorageReference fileReference = mStoreRef.child(System.currentTimeMillis() + "." + getFileExt(mImageUri));
                            mUploadTask = fileReference.putFile(mImageUri)
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    progressBar.setProgress(0);
                                                }
                                            }, 1000);
                                            taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(
                                                    new OnCompleteListener<Uri>() {

                                                        @Override
                                                        public void onComplete(@NonNull Task<Uri> task) {
                                                            String fileLink = task.getResult().toString();
                                                            //Toast.makeText(ChatActivity.this, fileLink, Toast.LENGTH_SHORT).show();
                                                            user.put(messageKey, userID + "~" + System.currentTimeMillis()+"||"+ message + "^^" + fileLink);
                                                            user.put("textNum", textNum + 1);
                                                            mImageUri = null;
                                                            addImageButton.setImageDrawable(getResources().getDrawable(R.drawable.addimage));
                                                            DocumentReference addForum = fstore.collection("forums").document(cropCode);
                                                            addForum.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    readMessage();
                                                                    //Toast.makeText(ChatActivity.this, R.string.Messagesent, Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                        }
                                                    });
                                            //Toast.makeText(ChatActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(ReplyActivity.this, getString(R.string.image_upload_failed), Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                            double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                                            progressBar.setProgress((int) progress);
                                        }
                                    });
                        } else {
                            user.put(messageKey, userID + "~" + System.currentTimeMillis()+"||"+ message);
                            user.put("textNum", textNum + 1);
                            DocumentReference addForum = fstore.collection("forums").document(cropCode);
                            addForum.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    readMessage();
                                    //Toast.makeText(ChatActivity.this, R.string.Messagesent, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }
            });
        }
        catch (Exception e)
        {
            Log.d("Error123",e.getMessage());
        }
    }
    private void readMessage(){
        DocumentReference documentReference = fstore.collection("forums").document(cropCode);
        documentReference.addSnapshotListener(ReplyActivity.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                int textNumtemp = Integer.parseInt(value.get("textNum").toString());
                if(textNumtemp==textnum)
                    return;
                for(int i = textnum; i<textNumtemp; i++) {
                    String Key1 = "text" + i;
                    String sender = value.get(Key1).toString();
                    int index = sender.indexOf("~");

                    if(index>0) {
                        int time = sender.indexOf("||");
                        int imageLink = sender.indexOf("^^");
                        String mtime = "";
                        mtime = getDate(sender.substring(index+1,time));
                        if (imageLink > 0) {
                            String message = sender.substring(time+ 2,imageLink);
                            String ImageUri = sender.substring(imageLink + 2);
                            sender = sender.substring(0, index);
                            Uri uri = Uri.parse(ImageUri);
                            if (sender.equals(userID)) {
                                MessageModel messageModel = new MessageModel("", message, true, uri, true,Integer.toString(i),mtime);
                                messageModels.add(messageModel);
                            } else {
                                MessageModel messageModel = new MessageModel(sender, message, false, uri, true,Integer.toString(i),mtime);
                                messageModels.add(messageModel);
                            }
                        } else {
                            String message = sender.substring(time + 2);
                            sender = sender.substring(0, index);
                            if (sender.equals(userID)) {
                                MessageModel messageModel = new MessageModel("", message, true,Integer.toString(i),mtime);
                                messageModels.add(messageModel);
                            } else {
                                MessageModel messageModel = new MessageModel(sender, message, false,Integer.toString(i),mtime);
                                messageModels.add(messageModel);
                            }
                        }
                    }
                    else
                    {
                        MessageModel messageModel1 = new MessageModel("a", queeryMessage, true,Integer.toString(i),"");
                        messageModels.add(messageModel1);
                    }
                }
                textnum=textNumtemp;
                fillChats(messageModels);
            }
        });
    }
    private void checkIfPresent() {
        Auth = FirebaseAuth.getInstance();
        fstore =FirebaseFirestore.getInstance();
        DocumentReference documentReference = fstore.collection("forums").document(cropCode);
        documentReference.addSnapshotListener(ReplyActivity.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(!value.exists()) {
                    Map<String, Object> user = new HashMap<>();
                    user.put("textNum", 1);
                    user.put("text0",queeryMessage);
                    DocumentReference addForum = fstore.collection("forums").document(cropCode);
                    addForum.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(ReplyActivity.this, getString(R.string.no_reply), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
    private String getDate(String dateinmill)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
        Date resultdate = new Date(Long.parseLong(dateinmill));
        return DateFormat.format("dd/MM/yy", resultdate).toString();

    }
    private void setLocale(String lang)
    {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences("Settings",MODE_PRIVATE).edit();
        editor.putString("My_lang",lang);
        editor.apply();
    }
    private void loadLocale()
    {
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String lang = prefs.getString("My_lang","en");
        setLocale(lang);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}