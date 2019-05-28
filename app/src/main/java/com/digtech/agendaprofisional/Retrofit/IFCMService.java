package com.digtech.agendaprofisional.Retrofit;

import com.digtech.agendaprofisional.Model.FCMResponse;
import com.digtech.agendaprofisional.Model.FCMSendData;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAKO3AzN8:APA91bFWeEZvEBlPC1gQz7i7xZwgPe6AFHLtB4o7B3eDe213XShx4d5eZxZy1Mxt63Nz3ah_b91I7QxsjBK9juyoQvDaROGnovpKnffXF2-teuzI98NqCTpMn2QFah6ebKy5kxA8xLwO"
    })
    @POST("fcm/send")
    Observable<FCMResponse> sendNotification(@Body FCMSendData body);
}
