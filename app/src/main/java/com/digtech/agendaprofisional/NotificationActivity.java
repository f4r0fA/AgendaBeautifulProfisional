package com.digtech.agendaprofisional;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.digtech.agendaprofisional.Adapter.MyNotificationAdapter;
import com.digtech.agendaprofisional.Common.Common;
import com.digtech.agendaprofisional.Interface.INotificationLoadListener;
import com.digtech.agendaprofisional.Model.MyNotification;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationActivity extends AppCompatActivity implements INotificationLoadListener {

    @BindView(R.id.recycler_notification)
    RecyclerView recycler_notification;

    CollectionReference notificationCollection;

    INotificationLoadListener iNotificationLoadListener;

    int total_item = 0, last_visible_item;
    boolean isLoading = false, isMaxData= false;
    DocumentSnapshot finalDoc;
    MyNotificationAdapter adapter;
    List<MyNotification> firstList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        ButterKnife.bind(this);

        init();
        initView();

        loadNotification(null);
    }

    private void initView() {
        recycler_notification.setHasFixedSize(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycler_notification.setLayoutManager(layoutManager);
        recycler_notification.addItemDecoration(new DividerItemDecoration(this,layoutManager.getOrientation()));

        recycler_notification.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                total_item = layoutManager.findLastVisibleItemPosition();
                last_visible_item = layoutManager.findLastVisibleItemPosition();

                if (!isLoading && total_item <= (last_visible_item) + Common.MAX_NOTIFICATION_PER_LOAD){
                    loadNotification(finalDoc);
                    isLoading = true;
                }
            }
        });
    }

    private void loadNotification(DocumentSnapshot lastDoc) {
            notificationCollection = FirebaseFirestore.getInstance()
                    .collection("AllSalon")
                    .document(Common.state_name)
                    .collection("Saloes")
                    .document(Common.selected_salon.getSalomId())
                    .collection("Cabeleleiro")
                    .document(Common.currentCabeleleiro.getCabeleleiroId())
                    .collection("Notifications");

            if (lastDoc == null){
                notificationCollection.orderBy("serverTimestamp", Query.Direction.DESCENDING)
                        .limit(Common.MAX_NOTIFICATION_PER_LOAD)
                        .get()
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                iNotificationLoadListener.onNotificationLoadFailed(e.getMessage());
                            }
                        }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            List<MyNotification> myNotifications = new ArrayList<>();
                            DocumentSnapshot finalDoc = null;
                           for (DocumentSnapshot notiSnapShot:task.getResult()){
                               MyNotification myNotification = notiSnapShot.toObject(MyNotification.class);
                               myNotifications.add(myNotification);
                               finalDoc = notiSnapShot;
                           }
                           iNotificationLoadListener.onNotificationLoadSuccess(myNotifications,finalDoc);
                        }
                    }
                });
            }else{
                if (!isMaxData){
                    notificationCollection.orderBy("serverTimestamp", Query.Direction.DESCENDING)
                            .startAfter(lastDoc)
                            .limit(Common.MAX_NOTIFICATION_PER_LOAD)
                            .get()
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    iNotificationLoadListener.onNotificationLoadFailed(e.getMessage());
                                }
                            }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                List<MyNotification> myNotifications = new ArrayList<>();
                                DocumentSnapshot finalDoc = null;
                                for (DocumentSnapshot notiSnapShot:task.getResult()){
                                    MyNotification myNotification = notiSnapShot.toObject(MyNotification.class);
                                    myNotifications.add(myNotification);
                                    finalDoc = notiSnapShot;
                                }
                                iNotificationLoadListener.onNotificationLoadSuccess(myNotifications,finalDoc);
                            }
                        }
                    });
                }
            }
    }

    private void init(){
        iNotificationLoadListener = this;
    }

    @Override
    public void onNotificationLoadSuccess(List<MyNotification> myNotificationList, DocumentSnapshot lastDocument) {
        if (lastDocument != null){
            if (lastDocument.equals(finalDoc))
                isMaxData = false;
        }
        if (adapter == null && firstList.size() == 0){
            adapter = new MyNotificationAdapter(this,myNotificationList);
            firstList = myNotificationList;
        }else{
            if (!myNotificationList.equals(firstList))
                adapter.updateList(myNotificationList);
        }
        recycler_notification.setAdapter(adapter);
    }

    @Override
    public void onNotificationLoadFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
