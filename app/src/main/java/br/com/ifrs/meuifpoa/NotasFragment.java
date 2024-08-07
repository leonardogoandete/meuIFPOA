package br.com.ifrs.meuifpoa;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.List;

import br.com.ifrs.meuifpoa.adapter.recycler.LinhaNotasAdapter;
import br.com.ifrs.meuifpoa.model.Nota;
import br.com.ifrs.meuifpoa.retrofit.SyncRetrofit;
import br.com.ifrs.meuifpoa.retrofit.service.SyncService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotasFragment extends Fragment {

    RecyclerView recyclerViewListNotas;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notas, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerViewListNotas = view.findViewById(R.id.listViewNotas);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        // Usando SyncManager para verificar e requisitar a senha
        SyncManager syncManager = new SyncManager();
        syncManager.verificarERequisitarSenha(getContext(), this::obterNotasDoFirestore);

        obterNotasDoFirestore();
        recyclerViewListNotas.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerViewListNotas.setLayoutManager(layoutManager);
    }

    private void obterNotasDoFirestore() {
        if (mAuth.getUid() == null) {
            Snackbar.make(getView(), R.string.msg_titulo_deve_estar_logado, Snackbar.LENGTH_SHORT).show();
            return;
        }

        db.collection("notas")
                .document(mAuth.getUid())
                .collection("disciplinas")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Nota> notasServidor = task.getResult().toObjects(Nota.class);
                        if (!notasServidor.isEmpty()) {
                            LinhaNotasAdapter notasAdapter = new LinhaNotasAdapter(notasServidor);
                            recyclerViewListNotas.setAdapter(notasAdapter);
                        } else {
                            Toast.makeText(getContext(), "Sem notas disponíveis", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Erro ao obter notas do servidor", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Falha na conexão", Toast.LENGTH_SHORT).show();
                });
    }
}
