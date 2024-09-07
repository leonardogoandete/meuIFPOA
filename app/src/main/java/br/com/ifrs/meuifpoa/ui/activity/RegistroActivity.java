package br.com.ifrs.meuifpoa.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import br.com.caelum.stella.validation.CPFValidator;
import br.com.ifrs.meuifpoa.R;
import br.com.ifrs.meuifpoa.model.Registro;

public class RegistroActivity extends AppCompatActivity {
    private EditText etNome;
    private EditText etCpf;
    private EditText etEmail;
    private EditText etSenha;
    private ProgressBar progressBar; // Declaração da ProgressBar

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CPFValidator cpfValidator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        setContentView(R.layout.activity_registro);
        etNome = findViewById(R.id.etNome);
        etCpf = findViewById(R.id.etCpf);
        etEmail = findViewById(R.id.etEmail);
        etSenha = findViewById(R.id.etSenha);
        progressBar = findViewById(R.id.progressBar); // Inicializa a ProgressBar

        Button btnRegistrar = findViewById(R.id.btnRegistrar);
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrar(v);
            }
        });
    }

    private void registrar(View v) {
        String nome = etNome.getText().toString();
        String cpf = etCpf.getText().toString();
        String email = etEmail.getText().toString();
        String senha = etSenha.getText().toString();

        if(nome.isEmpty()){
            etNome.setError("Campo obrigatório");
            etNome.requestFocus();
            return;
        }
        if(cpf.isEmpty()){
            etCpf.setError("Campo obrigatório");
            etCpf.requestFocus();
            return;
        }

        if(email.isEmpty()){
            etEmail.setError("Campo obrigatório");
            etEmail.requestFocus();
            return;
        }
        if(senha.isEmpty()){
            etSenha.setError("Campo obrigatório");
            etSenha.requestFocus();
            return;
        }

        // Exibe a ProgressBar antes de iniciar o registro
        progressBar.setVisibility(View.VISIBLE);



        // Consulta ao Firestore para verificar se o CPF já existe
        Query query = db.collection("usuarios").whereEqualTo("cpf", cpf);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    // CPF já existe
                    Snackbar.make(v, "Erro ao cadastrar, usuário existente", Snackbar.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE); // Oculta a ProgressBar
                } else {
                    // CPF não existe, prossiga com o registro
                    mAuth.createUserWithEmailAndPassword(email, senha)
                            .addOnCompleteListener(RegistroActivity.this, task1 -> {
                                if (task1.isSuccessful()) {
                                    String uid = mAuth.getUid();
                                    // Registro bem-sucedido, adicione os dados ao Firestore
                                    Registro registro = new Registro(uid, nome, cpf, email);
                                    db.collection("usuarios").document(uid).set(registro)
                                            .addOnSuccessListener(aVoid -> {
                                                Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
                                                startActivity(intent);
                                                finish();
                                            })
                                            .addOnFailureListener(e -> {
                                                Snackbar.make(v, "Erro ao registrar dados no Firestore", Snackbar.LENGTH_SHORT).show();
                                                progressBar.setVisibility(View.GONE); // Oculta a ProgressBar
                                            });
                                } else {
                                    // Erro ao criar usuário, exiba uma mensagem de erro
                                    Snackbar.make(v, "Erro ao criar usuário", Snackbar.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE); // Oculta a ProgressBar
                                }
                            });
                }
            } else {
                Log.e("FirestoreError", "Erro ao acessar o Firestore: " + task.getException().getMessage());
                Snackbar.make(v, "Erro ao acessar o Firestore: " + task.getException().getMessage(), Snackbar.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE); // Oculta a ProgressBar
            }
        });
    }


}
