package com.gostar.driverversion;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.util.Map;

import io.reactivex.rxjava3.subjects.PublishSubject;

public class FirebaseService extends FirebaseMessagingService {

    public static PublishSubject<Map<String,String>> orderPublishSubject = PublishSubject.create();
    private Map<String, String> data;

    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String, String> data2 = remoteMessage.getData();
        this.data = data2;
        getData(data2.get("title"));
    }

    private void getData(String title) {
        if (((title.hashCode() == 324686403 && title.equals("userOrder")) ? (char) 0 : 65535) == 0) {
            getOrder();
        }
    }

    private void getOrder() {
        orderPublishSubject.onNext(this.data);
    }
}
