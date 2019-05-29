package com.digtech.agendaprofisional;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.digtech.agendaprofisional.Adapter.MyTimeSlotAdapter;
import com.digtech.agendaprofisional.Common.Common;
import com.digtech.agendaprofisional.Common.SpacesItemDecoration;
import com.digtech.agendaprofisional.Interface.INotificationCountListener;
import com.digtech.agendaprofisional.Interface.ITimeSlotLoadListener;
import com.digtech.agendaprofisional.Model.TimeSlot;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

import static com.digtech.agendaprofisional.Common.Common.currentCabeleleiro;
import static com.digtech.agendaprofisional.Common.Common.selected_salon;
import static com.digtech.agendaprofisional.Common.Common.simpleDateFormat;

public class StaffHomeActivity extends AppCompatActivity implements ITimeSlotLoadListener, INotificationCountListener {

    @BindView(R.id.activity_main)
    DrawerLayout drawerLayout;

    @BindView(R.id.navigation_view)
    NavigationView navigationView;

    @BindView(R.id.recycler_time_slot)
    RecyclerView recycler_time_slot;

    @BindView(R.id.calendarView)
    HorizontalCalendarView calendarView;

    ActionBarDrawerToggle actionBarDrawerToggle;

    ITimeSlotLoadListener iTimeSlotLoadListener;

    DocumentReference cabeleleiroDoc;

    android.app.AlertDialog alertDialog;

    TextView txt_notification_badge;
    CollectionReference notificationCollection;
    CollectionReference currentBookDateCollection;

    EventListener<QuerySnapshot> notificationEvent;
    EventListener<QuerySnapshot> bookingEvent;

    ListenerRegistration notificationListener;
    ListenerRegistration bookingRealtimeListener;

    INotificationCountListener iNotificationCountListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_home);

        ButterKnife.bind(this);

        init();
        InitView();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item))
            return true;
        if (item.getItemId() == R.id.action_new_noitification){
            startActivity(new Intent(StaffHomeActivity.this, NotificationActivity.class));
            txt_notification_badge.setText("");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void InitView() {
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,
                R.string.open,
                R.string.close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.menu_exit)
                    logOut();
                return true;
            }
        });

        alertDialog = new SpotsDialog.Builder().setCancelable(false).setContext(this).setMessage("Carregando").build();

        Calendar date = Calendar.getInstance();
        date.add(Calendar.DATE,0);
        loadAvailableTimeSlotOfCabeleleiro(Common.currentCabeleleiro.getCabeleleiroId(),Common.simpleDateFormat.format(date.getTime()));

        recycler_time_slot.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recycler_time_slot.setLayoutManager(layoutManager);
        recycler_time_slot.addItemDecoration(new SpacesItemDecoration(8));

        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.DATE, 0);

        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.DATE, 2);

        HorizontalCalendar horizontalCalendar = new HorizontalCalendar.Builder(this,R.id.calendarView)
                .range(startDate,endDate).datesNumberOnScreen(1)
                .mode(HorizontalCalendar.Mode.DAYS).defaultSelectedDate(startDate).configure().end()
                .build();

        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                if (Common.bookingDate.getTimeInMillis() != date.getTimeInMillis())
                {
                    Common.bookingDate = date;
                    loadAvailableTimeSlotOfCabeleleiro(currentCabeleleiro.getCabeleleiroId(),simpleDateFormat.format(date.getTime()));
                }
            }
        });

    }

    private void logOut() {
        Paper.init(this);
        Paper.book().delete(Common.SALON_KEY);
        Paper.book().delete(Common.CABELELEIRO_KEY);
        Paper.book().delete(Common.STATE_KEY);
        Paper.book().delete(Common.LOGGED_KEY);

        new AlertDialog.Builder(this)
                .setMessage("Voce realmente quer sair ?")
                .setCancelable(false)
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        Intent mainActivity = new Intent(StaffHomeActivity.this, MainActivity.class);
                        mainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(mainActivity);
                        finish();
                    }
                }).setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }

    private void loadAvailableTimeSlotOfCabeleleiro(final String cabeleleiroId, final String bookDate) {
        alertDialog.show();


        cabeleleiroDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    final DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        CollectionReference date = FirebaseFirestore.getInstance()
                                .collection("AllSalon")
                                .document(Common.state_name)
                                .collection("Saloes")
                                .document(selected_salon.getSalomId())
                                .collection("Cabeleleiro")
                                .document(cabeleleiroId)
                                .collection(bookDate);
                        date.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()){
                                    QuerySnapshot querySnapshot = task.getResult();
                                    if (querySnapshot.isEmpty())
                                        iTimeSlotLoadListener.onTimeSlotLoadEmpty();
                                    else{
                                        List<TimeSlot> timeSlots = new ArrayList<>();
                                        for (QueryDocumentSnapshot document:task.getResult())
                                            timeSlots.add(document.toObject(TimeSlot.class));
                                        iTimeSlotLoadListener.onTimeSlotLoadSuccess(timeSlots);
                                    }
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                iTimeSlotLoadListener.onTimeSlotLoadFailed(e.getMessage());
                            }
                        });

                    }
                }
            }
        });
    }

    private void init() {
        initNotificationRealtimeUpdate();
        iNotificationCountListener = this;
        iTimeSlotLoadListener = this ;
        InitBookingRealtimeUpdate();
    }

    private void InitBookingRealtimeUpdate() {
        cabeleleiroDoc = FirebaseFirestore.getInstance()
                .collection("AllSalon")
                .document(Common.state_name)
                .collection("Saloes")
                .document(Common.selected_salon.getSalomId())
                .collection("Cabeleleiro")
                .document(Common.currentCabeleleiro.getCabeleleiroId());

        final Calendar date = Calendar.getInstance();
        date.add(Calendar.DATE, 0);
        bookingEvent = new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                loadAvailableTimeSlotOfCabeleleiro(currentCabeleleiro.getCabeleleiroId(),
                        simpleDateFormat.format(date.getTime()));
            }
        };
        currentBookDateCollection = cabeleleiroDoc.collection(Common.simpleDateFormat.format(date.getTime()));
        bookingRealtimeListener = currentBookDateCollection.addSnapshotListener(bookingEvent);
    }

    private void initNotificationRealtimeUpdate() {
        notificationCollection = FirebaseFirestore.getInstance()
                .collection("AllSalon")
                .document(Common.state_name)
                .collection("Saloes")
                .document(Common.selected_salon.getSalomId())
                .collection("Cabeleleiro")
                .document(Common.currentCabeleleiro.getCabeleleiroId())
                .collection("Notifications");

        notificationEvent = new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots.size() > 0)
                    loadNotification();
            }
        };
       notificationListener = notificationCollection.whereEqualTo("read", false).addSnapshotListener(notificationEvent);

    }

    @Override
    public void onBackPressed(){
        new AlertDialog.Builder(this)
                .setMessage("Voce realmente quer sair ?")
                .setCancelable(false)
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        Toast.makeText(StaffHomeActivity.this, "Funcao desativada", Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }

    @Override
    public void onTimeSlotLoadSuccess(List<TimeSlot> timeSlot) {
        MyTimeSlotAdapter adapter = new MyTimeSlotAdapter(this,timeSlot);
        recycler_time_slot.setAdapter(adapter);
        alertDialog.dismiss();

    }

    @Override
    public void onTimeSlotLoadFailed(String message) {
         Toast.makeText(this, ""+message, Toast.LENGTH_SHORT).show();
         alertDialog.dismiss();
    }

    @Override
    public void onTimeSlotLoadEmpty() {
        MyTimeSlotAdapter adapter = new MyTimeSlotAdapter(this);
        recycler_time_slot.setAdapter(adapter);
        alertDialog.dismiss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.staff_home_menu,menu);
        final MenuItem menuItem = menu.findItem(R.id.action_new_noitification);
        txt_notification_badge = (TextView)menuItem.getActionView()
                .findViewById(R.id.notification_badge);

        loadNotification();
        menuItem.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              onOptionsItemSelected(menuItem);
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void loadNotification() {
        notificationCollection.whereEqualTo("read", false)
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(StaffHomeActivity.this, "Verifique sua conexao, para checar as notificações", Toast.LENGTH_SHORT).show();
                    }
                }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    iNotificationCountListener.onNotificationCountSuccess(task.getResult().size());
                }
            }
        });
    }

    @Override
    public void onNotificationCountSuccess(int count) {
        if (count == 0)
            txt_notification_badge.setVisibility(View.INVISIBLE);
        else{
            txt_notification_badge.setVisibility(View.VISIBLE);
            if (count <= 9)
                txt_notification_badge.setText(String.valueOf(count));
            else
                txt_notification_badge.setText("+9");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initNotificationRealtimeUpdate();
        InitBookingRealtimeUpdate();
    }

    @Override
    protected void onStop() {
        if (notificationListener != null)
            notificationListener.remove();
        if (bookingRealtimeListener != null)
            bookingRealtimeListener.remove();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (notificationListener != null)
            notificationListener.remove();
        if (bookingRealtimeListener != null)
            bookingRealtimeListener.remove();
        super.onDestroy();
    }
}
