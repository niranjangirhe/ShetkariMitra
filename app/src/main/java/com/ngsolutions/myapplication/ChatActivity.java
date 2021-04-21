package com.ngsolutions.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.ngsolutions.myapplication.Model.MessageModel;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
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
    int textnum1=0;
    ArrayList<MessageModel> messageModels = new ArrayList<>();
    ChatAdapter chatAdapter;
    ProgressBar progressBar;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri;
    private StorageReference mStoreRef;
    private StorageTask mUploadTask;
    private Bitmap mbitmap;
    int totalChats = 15;
    int toatlcount;
    boolean isloading = false;
    boolean firstTime = true;
    String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        loadLocale();
        chatTitleText = findViewById(R.id.ChatTilteText);
        backButton = findViewById(R.id.BackBtnChat);
        messageText = findViewById(R.id.AddChatText);
        sendText = findViewById(R.id.SendChatText);
        addImageButton = findViewById(R.id.AddImageBtn);
        chatRecyclerView = findViewById(R.id.ChatRecylcer);
        progressBar = findViewById(R.id.chatUploadbar);

        mStoreRef = FirebaseStorage.getInstance().getReference("uploads");

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openHomeActivity = new Intent(ChatActivity.this, ChatMainScreenActivity.class);
                openHomeActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivityIfNeeded(openHomeActivity, 0);
                finish();
            }
        });

        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        chatRecyclerView.setLayoutManager(layoutManager);
        chatRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) chatRecyclerView.getLayoutManager();
                //messageText.setHint(Integer.toString(linearLayoutManager.findFirstCompletelyVisibleItemPosition()));
                if(linearLayoutManager.findFirstCompletelyVisibleItemPosition()==0)
                {
                    progressBar.setIndeterminate(true);
                    loadExtra(1);
                }
            }
        });
        Bundle b = getIntent().getExtras();
        if(b!=null) {
            cropCode = (String) b.get("cropCode");
            chatTitle = (String) b.get("chatTitle");
            if(cropCode.contains("0"))
                chatTitleText.setText(chatTitle + getString(R.string.Forum));
            else
                chatTitleText.setText(chatTitle);
            userID = (String) b.get("userID");
            checkIfPresent();
            chatAdapter = new ChatAdapter(messageModels, ChatActivity.this,cropCode,userID);
            chatRecyclerView.setAdapter(chatAdapter);
        }
        else {
            Toast.makeText(this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            finish();
        }
        DocumentReference documentReference = fstore.collection("forums").document(cropCode);
        documentReference.addSnapshotListener(ChatActivity.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.exists()) {
                    if(Integer.parseInt(value.get("textNum").toString())>0) {
                        readMessage();
                    }
                }
            }
        });
        sendText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( mUploadTask!=null && mUploadTask.isInProgress())
                {
                    Toast.makeText(ChatActivity.this, getString(R.string.upload_in_progress), Toast.LENGTH_SHORT).show();
                }
                else if(!messageText.getText().toString().isEmpty() ){
                    sendMessage();
                }
                else
                {
                    //loadExtra(2);
                    Toast.makeText(ChatActivity.this, getString(R.string.write_message), Toast.LENGTH_SHORT).show();
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
            BackgroundImageResize backgroundImageResize = new BackgroundImageResize();
            backgroundImageResize.execute(mImageUri);
        }
    }
    public class BackgroundImageResize extends AsyncTask<Uri,Integer,byte[]>
    {
        public BackgroundImageResize() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Toast.makeText(ChatActivity.this,getString(R.string.compressing_image), Toast.LENGTH_SHORT).show();
            progressBar.setIndeterminate(true);
        }

        @Override
        protected byte[] doInBackground(Uri... uris) {
            try {
                mbitmap = MediaStore.Images.Media.getBitmap(ChatActivity.this.getContentResolver(), uris[0]);
                mImageUri = getUriFromBitmap(mbitmap,10,ChatActivity.this);
            }
            catch (Exception e)
            {

            }
            return null;
        }
        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);
            progressBar.setIndeterminate(false);
            Picasso.with(ChatActivity.this).load( mImageUri ).fit().centerCrop().into(addImageButton);
        }

    }
    @Override
    protected void onResume() {
        super.onResume();
        DocumentReference documentReference = fstore.collection("forums").document(cropCode);
        documentReference.addSnapshotListener(ChatActivity.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.exists()) {
                    if(Integer.parseInt(value.get("textNum").toString())>0) {
                        readMessage();
                    }
                }
            }
        });
    }

    private static Uri getUriFromBitmap(Bitmap bitmap, int quality, Context context)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(),bitmap,"title", null);
        return Uri.parse(path);
    }

    private void fillChats(ArrayList<MessageModel> messageModels) {
        chatRecyclerView.smoothScrollToPosition(messageModels.size());
        chatAdapter.notifyDataSetChanged();
    }
    private String getFileExt(Uri uri)
    {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }
    private void loadExtra(int no)
    {
        //Toast.makeText(ChatActivity.this, "Loading Data ", Toast.LENGTH_SHORT).show();

        DocumentReference documentReference = fstore.collection("forums").document(cropCode);
        documentReference.addSnapshotListener(ChatActivity.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                for(int i = Math.max(0,toatlcount-totalChats-no); i<toatlcount-totalChats; i++) {
                    String Key1 = "text" + i;
                    String sender = value.get(Key1).toString();
                    int index = sender.indexOf("~");
                    int time = sender.indexOf("||");
                    int imageLink = sender.indexOf("^^");
                    String mtime = "";
                    mtime = getDate(sender.substring(index+1,time));
                    //Toast.makeText(ChatActivity.this, Integer.toString(imageLink), Toast.LENGTH_SHORT).show();
                    if(imageLink>0)
                    {
                        String message = sender.substring(time+ 2,imageLink);
                        String ImageUri = sender.substring(imageLink+2);
                        sender = sender.substring(0, index);
                        Uri uri = Uri.parse(ImageUri);
                        if (sender.equals(userID)) {
                            MessageModel messageModel = new MessageModel("", message, true,uri,true,Integer.toString(i),mtime);
                            messageModels.add(0,messageModel);
                        } else {
                            MessageModel messageModel = new MessageModel(sender, message, false,uri,true,Integer.toString(i),mtime);
                            messageModels.add(0,messageModel);
                        }
                    }
                    else {
                        String message = sender.substring(time + 2);
                        sender = sender.substring(0, index);
                        if (sender.equals(userID)) {
                            MessageModel messageModel = new MessageModel("", message, true,Integer.toString(i),mtime);
                            messageModels.add(0,messageModel);
                        } else {
                            MessageModel messageModel = new MessageModel(sender, message, false,Integer.toString(i),mtime);
                            messageModels.add(0,messageModel);
                        }
                    }
                    chatAdapter.notifyItemInserted(0);
                }
                totalChats=totalChats+no;
                isloading = false;
                progressBar.setIndeterminate(false);
            }
        });
    }
    private void sendMessage() {
        String message = messageText.getText().toString();
        DocumentReference documentReference = fstore.collection("forums").document(cropCode);
        documentReference.addSnapshotListener(ChatActivity.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                int textNum = Integer.parseInt(value.get("textNum").toString());
                if(!messageText.getText().toString().isEmpty()) {
                    messageText.setText("");
                    Map<String, Object> user = new HashMap<>();
                    String messageKey = "text" + textNum;
                    if (mImageUri != null) {
                        StorageReference fileReference = mStoreRef.child(System.currentTimeMillis() + "." + getFileExt(mImageUri));
                        mUploadTask = fileReference.putFile(mImageUri)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Handler handler =  new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressBar.setProgress(0);
                                            }
                                        },1000);
                                        File file = new File(mImageUri.getPath());
                                        file.delete();
                                        taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(
                                                new OnCompleteListener<Uri>() {

                                                    @Override
                                                    public void onComplete(@NonNull Task<Uri> task) {
                                                        String fileLink = task.getResult().toString();
                                                        //Toast.makeText(ChatActivity.this, fileLink, Toast.LENGTH_SHORT).show();
                                                        user.put(messageKey, userID + "~" + System.currentTimeMillis()+"||"+ message + "^^"+fileLink);
                                                        user.put("textNum", textNum + 1);
                                                        mImageUri=null;
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
                                        Toast.makeText(ChatActivity.this,getString(R.string.image_upload_failed), Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                        double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                                        progressBar.setProgress((int) progress);
                                    }
                                });
                    }
                    else {
                        user.put(messageKey, userID + "~"+ System.currentTimeMillis()+"||" + message);
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
    private void readMessage(){
        DocumentReference documentReference = fstore.collection("forums").document(cropCode);
        documentReference.addSnapshotListener(ChatActivity.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                int textNumtemp = Integer.parseInt(value.get("textNum").toString());
                if(textNumtemp==textnum1)
                    return;

                for(int i = textnum1; i<textNumtemp; i++) {
                    String Key1 = "text" + i;
                    String sender = value.get(Key1).toString();
                    int index = sender.indexOf("~");
                    int time = sender.indexOf("||");
                    int imageLink = sender.indexOf("^^");
                    String mtime = "";
                    Log.d("TimeNow",getDate(sender.substring(index+1,time)));
                    mtime = getDate(sender.substring(index+1,time));
                    //Toast.makeText(ChatActivity.this, Integer.toString(imageLink), Toast.LENGTH_SHORT).show();
                    if(imageLink>0)
                    {
                        String message = sender.substring(time+ 2,imageLink);
                        String ImageUri = sender.substring(imageLink+2);
                        sender = sender.substring(0, index);
                        Uri uri = Uri.parse(ImageUri);
                        if (sender.equals(userID)) {
                            MessageModel messageModel = new MessageModel("", message, true,uri,true,Integer.toString(i),mtime);
                            messageModels.add(messageModel);
                        } else {
                            MessageModel messageModel = new MessageModel(sender, message, false,uri,true,Integer.toString(i),mtime);
                            messageModels.add(messageModel);
                        }
                    }
                    else {
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
                textnum1=textNumtemp;
                fillChats(messageModels);
            }
        });
    }
    private String getDate(String dateinmill)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
        Date resultdate = new Date(Long.parseLong(dateinmill));
        return DateFormat.format("HH:mm  dd/MM/yy", resultdate).toString();

    }
    private void checkIfPresent() {
        Auth = FirebaseAuth.getInstance();
        fstore =FirebaseFirestore.getInstance();
        DocumentReference documentReference = fstore.collection("forums").document(cropCode);
        documentReference.addSnapshotListener(ChatActivity.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(!value.exists()) {
                    Map<String, Object> user = new HashMap<>();
                    user.put("textNum", 0);
                    DocumentReference addForum = fstore.collection("forums").document(cropCode);
                    addForum.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(ChatActivity.this, getString(R.string.new_forum), Toast.LENGTH_SHORT).show();
                            toatlcount = 0;
                            textnum1 = 0;
                        }
                    });
                }
                else if(firstTime)
                {
                    firstTime=false;
                    textnum1 = Math.max(0,Integer.parseInt(value.get("textNum").toString())-totalChats);
                    toatlcount = Integer.parseInt(value.get("textNum").toString());
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent openHomeActivity = new Intent(ChatActivity.this, ChatMainScreenActivity.class);
        openHomeActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivityIfNeeded(openHomeActivity, 0);
        finish();
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
}