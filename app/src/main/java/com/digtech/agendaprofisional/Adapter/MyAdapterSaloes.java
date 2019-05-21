package com.digtech.agendaprofisional.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.digtech.agendaprofisional.Common.Common;
import com.digtech.agendaprofisional.Common.CustomLoginDialog;
import com.digtech.agendaprofisional.Interface.IDialogClickListener;
import com.digtech.agendaprofisional.Interface.IRecyclerItemSelectedListener;
import com.digtech.agendaprofisional.Model.Saloes;
import com.digtech.agendaprofisional.R;
import com.digtech.agendaprofisional.StaffHomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class MyAdapterSaloes extends RecyclerView.Adapter<MyAdapterSaloes.MyViewHolder> implements IDialogClickListener {
    Context context;
    List<Saloes> saloesList;
    List<CardView> cardViewList;
    LocalBroadcastManager localBroadcastManager;

    public MyAdapterSaloes(Context context, List<Saloes> saloesList) {
        this.context = context;
        this.saloesList = saloesList;
        cardViewList = new ArrayList<>();
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_saloes,viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int i) {
        myViewHolder.txt_salon_name.setText(saloesList.get(i).getName());
        myViewHolder.txt_salon_address.setText(saloesList.get(i).getAddress());

        if (!cardViewList.contains(myViewHolder.card_saloes))
            cardViewList.add(myViewHolder.card_saloes);

        myViewHolder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
            @Override
            public void onItemSelected(View view, int Position) {
                Common.selectedSalon = saloesList.get(Position);

                showLoginDialog();

            }
        });
    }

    private void showLoginDialog() {
        CustomLoginDialog.getInstance()
                .showLoginDialog("AGENDA PRO LOGIN",
                        "LOGIN",
                        "CANCELAR",
                        context, this);
    }

    @Override
    public int getItemCount() {
        return saloesList.size();
    }

    @Override
    public void onClickPositionButton(final DialogInterface dialogInterface, String userName, String password) {
        final AlertDialog loading = new SpotsDialog.Builder().setCancelable(false).setMessage("Logando, aguarde...").setContext(context).build();

        loading.show();
        FirebaseFirestore.getInstance()
                .collection("AllSalon")
                .document(Common.state_name)
                .collection("Saloes")
                .document(Common.selectedSalon.getSalomId())
                .collection("Cabeleleiro")
                .whereEqualTo("username", userName)
                .whereEqualTo("password", password)
                .limit(1)
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().size() > 0){
                        dialogInterface.dismiss();
                        loading.dismiss();
                        Intent staffHome = new Intent(context, StaffHomeActivity.class);
                        staffHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        staffHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(staffHome);

                    }else{
                        loading.dismiss();
                        Toast.makeText(context, "Usuario ou senha incorretos", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public void onClickNegativeButton(DialogInterface dialogInterface) {
        dialogInterface.dismiss();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_salon_name, txt_salon_address;
        CardView card_saloes;

        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;

        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            card_saloes = (CardView)itemView.findViewById(R.id.card_saloes);
            txt_salon_address = (TextView)itemView.findViewById(R.id.txt_salon_address);
            txt_salon_name = (TextView)itemView.findViewById(R.id.txt_salon_name);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            iRecyclerItemSelectedListener.onItemSelected(view, getAdapterPosition());
        }
    }
}
