package com.ngsolutions.myapplication.notification;

import retrofit2.Call;

import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;


public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAaMOt8SA:APA91bHM2RUUr0a8jJNWzwiQCOy5rE4z6j6cNTrZ_ScBNmfKUHtXAB51dVgYjahKN4biGNc8U_-QRPhdUAgBobLUsWJVqedB84d6Vd59OYlrfh0weOl3nx_h_6WwYA2IbVvQ01aAGoFO"
    })
    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);
}
