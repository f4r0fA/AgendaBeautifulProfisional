package com.digtech.agendaprofisional;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.digtech.agendaprofisional.Adapter.MyStateAdapter;
import com.digtech.agendaprofisional.Common.Common;
import com.digtech.agendaprofisional.Common.SpacesItemDecoration;
import com.digtech.agendaprofisional.Interface.IOnAllStateLoadListener;
import com.digtech.agendaprofisional.Model.Cabeleleiro;
import com.digtech.agendaprofisional.Model.City;
import com.digtech.agendaprofisional.Model.Saloes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity implements IOnAllStateLoadListener {

    @BindView(R.id.recycler_state)
    RecyclerView recycler_state;

    CollectionReference allSalonCollection;

    IOnAllStateLoadListener iOnAllStateLoadListener;

    MyStateAdapter adapter;
    AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseInstanceId.getInstance()
                .getInstanceId()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (task.isSuccessful()){
                    Common.updateToken(MainActivity.this,task.getResult().getToken());
                    Log.d("DIGTOKEN", task.getResult().getToken());
                }
            }
        });

        Paper.init(this);
        String user = Paper.book().read(Common.LOGGED_KEY);
        if (TextUtils.isEmpty(user)){
            ButterKnife.bind(this);

            initView();
            init();

            loadAllStateFromFireStore();
        }else{
            Gson gson = new Gson();
            Common.state_name = Paper.book().read(Common.STATE_KEY);
            Common.selected_salon = gson.fromJson(Paper.book().read(Common.SALON_KEY, ""),
                    new TypeToken<Saloes>(){}.getType());
            Common.currentCabeleleiro = gson.fromJson(Paper.book().read(Common.CABELELEIRO_KEY, ""),
                    new TypeToken<Cabeleleiro>(){}.getType());

            Intent intent = new Intent(this,StaffHomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void loadAllStateFromFireStore() {
    dialog.show();
    allSalonCollection.get()
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    iOnAllStateLoadListener.onAllStateLoadFailed(e.getMessage());
                }
            }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
        @Override
        public void onComplete(@NonNull Task<QuerySnapshot> task) {
            if (task.isSuccessful()){
                List<City> cities = new ArrayList<>();
                for (DocumentSnapshot citySnapShot:task.getResult()){
                    City city = citySnapShot.toObject(City.class);
                    cities.add(city);
                }
                iOnAllStateLoadListener.onAllStateLoadSuccess(cities);
            }
        }
    });
    }

    private void init() {
        allSalonCollection = FirebaseFirestore.getInstance()
                .collection("AllSalon");
        iOnAllStateLoadListener = this;
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).setMessage("Carregando").build();
    }

    private void initView() {
        recycler_state.setHasFixedSize(true);
        recycler_state.setLayoutManager(new GridLayoutManager(this,2));
        recycler_state.addItemDecoration(new SpacesItemDecoration(8));
    }

    @Override
    public void onAllStateLoadSuccess(List<City> cityList) {
        adapter = new MyStateAdapter(this,cityList);
        recycler_state.setAdapter(adapter);

        dialog.dismiss();

    }

    @Override
    public void onAllStateLoadFailed(String message) {
        Toast.makeText(this, "Algo deu errado =(", Toast.LENGTH_SHORT).show();
        dialog.dismiss();
    }
}
