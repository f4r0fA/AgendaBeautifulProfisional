package com.digtech.agendaprofisional;

import android.app.AlertDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import com.digtech.agendaprofisional.Adapter.MyAdapterSaloes;
import com.digtech.agendaprofisional.Common.Common;
import com.digtech.agendaprofisional.Common.SpacesItemDecoration;
import com.digtech.agendaprofisional.Interface.IOnLoadCountSalon;
import com.digtech.agendaprofisional.Interface.ISaloesLoadListener;
import com.digtech.agendaprofisional.Model.Saloes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;

public class SalonListActivity extends AppCompatActivity implements IOnLoadCountSalon, ISaloesLoadListener {

    @BindView(R.id.txt_salon_count)
    TextView txt_salon_count;

    @BindView(R.id.recycler_salon)
    RecyclerView recycler_salon;

    IOnLoadCountSalon iOnLoadCountSalon;

    ISaloesLoadListener iSaloesLoadListener;

    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salon_list);

        ButterKnife.bind(this);

        initView();
        init();
        loadSalonBaseOnCity(Common.state_name);
    }

    private void loadSalonBaseOnCity(String name) {
        dialog.show();
        FirebaseFirestore.getInstance().collection("AllSalon")
                .document(name)
                .collection("Saloes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<Saloes> saloes = new ArrayList<>();
                       iOnLoadCountSalon.onLoadCountSalonSuccess(task.getResult().size());
                       for (DocumentSnapshot salonSnapShot:task.getResult()){
                           Saloes salon = salonSnapShot.toObject(Saloes.class);
                           salon.setSalomId(salonSnapShot.getId());
                           saloes.add(salon);
                       }
                       iSaloesLoadListener.onSaloesLoadSucess(saloes);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iSaloesLoadListener.onSaloesLoadFailed(e.getMessage());
            }
        });
    }

    private void init() {
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).setMessage("Carregando").build();

        iOnLoadCountSalon = this;
        iSaloesLoadListener = this;
    }

    private void initView() {
        recycler_salon.setHasFixedSize(true);
        recycler_salon.setLayoutManager(new GridLayoutManager(this, 1));
        recycler_salon.addItemDecoration(new SpacesItemDecoration(8));
    }

    @Override
    public void onLoadCountSalonSuccess(int count) {
        txt_salon_count.setText(new StringBuilder("Salões Disponíveis (")
        .append(count)
        .append(")"));
    }

    @Override
    public void onSaloesLoadSucess(List<Saloes> saloesList) {
        MyAdapterSaloes salonAdapter = new MyAdapterSaloes(this, saloesList);
        recycler_salon.setAdapter(salonAdapter);

        dialog.dismiss();
    }

    @Override
    public void onSaloesLoadFailed(String message) {
        Toast.makeText(this, "Verifique sua Conexao", Toast.LENGTH_SHORT).show();
        dialog.dismiss();
    }
}
