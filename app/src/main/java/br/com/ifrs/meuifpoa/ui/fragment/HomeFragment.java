package br.com.ifrs.meuifpoa.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.ui.AppBarConfiguration;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Source;

import br.com.ifrs.meuifpoa.R;
import br.com.ifrs.meuifpoa.model.Perfil;

public class HomeFragment extends Fragment {
    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private NavController navController;
    private BottomNavigationView bottomNavigationView;
    private View containerIntegralizacoes;
    private TextView txtBemVindo;
    private ProgressBar probarChObrigatoria;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        containerIntegralizacoes = view.findViewById(R.id.containerIntegralizacoes);
        probarChObrigatoria = view.findViewById(R.id.progressChObrigatoria);
        txtBemVindo = view.findViewById(R.id.txtBemVindo);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        checkUserAuthentication();
    }

    private void checkUserAuthentication() {
        if (mAuth.getCurrentUser() != null) {
            // Usuário está logado, exibe a integralização
            String userId = mAuth.getCurrentUser().getUid();
            db.collection("usuarios").document(userId).get(Source.DEFAULT)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Perfil perfil = document.toObject(Perfil.class);
                                if (perfil != null) {
                                    String nomeCompleto = perfil.getNomeDocente();
                                    String primeiroNome = obterPrimeiroNome(nomeCompleto);
                                    String mensagem = "Bem vindo(a) " + primeiroNome;
                                    txtBemVindo.setText(mensagem);
                                    containerIntegralizacoes.setVisibility(View.VISIBLE);
                                    probarChObrigatoria.setMax(2216);
                                    probarChObrigatoria.setProgress(2150);
                                }
                            } else {
                                // Documento não existe, ocultar integralizações
                                containerIntegralizacoes.setVisibility(View.GONE);
                            }
                        } else {
                            // Falha ao buscar o documento
                            containerIntegralizacoes.setVisibility(View.GONE);
                        }
                    });
        } else {
            // Usuário não está logado, oculta a integralização
            containerIntegralizacoes.setVisibility(View.GONE);
        }
    }

    private String obterPrimeiroNome(String nomeCompleto) {
        if (nomeCompleto == null || nomeCompleto.isEmpty()) {
            return "";
        }
        // Divide o nome completo em partes usando espaços
        String[] partes = nomeCompleto.split(" ");
        if (partes.length > 0) {
            // Obtém o primeiro nome
            String primeiroNome = partes[0];
            // Converte a primeira letra para maiúscula e o restante para minúscula
            primeiroNome = primeiroNome.substring(0, 1).toUpperCase() + primeiroNome.substring(1).toLowerCase();
            return primeiroNome;
        }
        return "";
    }
}
