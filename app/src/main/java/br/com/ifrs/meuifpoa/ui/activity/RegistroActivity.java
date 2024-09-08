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
import br.com.caelum.stella.validation.InvalidStateException;
import br.com.ifrs.meuifpoa.R;
import br.com.ifrs.meuifpoa.model.Registro;

public class RegistroActivity extends AppCompatActivity {
    private EditText etNome;
    private EditText etCpf;
    private EditText etEmail;
    private EditText etSenha;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

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
        progressBar = findViewById(R.id.progressBar);

        Button btnRegistrar = findViewById(R.id.btnRegistrar);
        btnRegistrar.setOnClickListener(this::registrar);
    }

    private void registrar(View v) {
        String nome = etNome.getText().toString().trim();
        String cpf = etCpf.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String senha = etSenha.getText().toString().trim();

        if (nome.isEmpty()) {
            etNome.setError("Nome obrigatório");
            etNome.requestFocus();
            return;
        }

        if (cpf.isEmpty()) {
            etCpf.setError("CPF obrigatório");
            etCpf.requestFocus();
            return;
        }

        if (!validarCpf(cpf)) {
            etCpf.setError("CPF inválido");
            etCpf.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            etEmail.setError("E-mail obrigatório");
            etEmail.requestFocus();
            return;
        }

        if (senha.isEmpty()) {
            etSenha.setError("Senha obrigatório");
            etSenha.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        Query query = db.collection("usuarios").whereEqualTo("cpf", cpf);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    Snackbar.make(v, "Erro ao cadastrar, usuário existente", Snackbar.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                } else {
                    mAuth.createUserWithEmailAndPassword(email, senha)
                            .addOnCompleteListener(RegistroActivity.this, task1 -> {
                                if (task1.isSuccessful()) {
                                    String uid = mAuth.getUid();
                                    Registro registro = new Registro(uid, nome, cpf, email);
                                    db.collection("usuarios").document(uid).set(registro)
                                            .addOnSuccessListener(aVoid -> {
                                                Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
                                                startActivity(intent);
                                                finish();
                                            })
                                            .addOnFailureListener(e -> {
                                                Snackbar.make(v, "Erro ao registrar dados no Firestore", Snackbar.LENGTH_SHORT).show();
                                                progressBar.setVisibility(View.GONE);
                                            });
                                } else {
                                    Snackbar.make(v, "Erro ao criar usuário", Snackbar.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                }
            } else {
                Log.e("FirestoreError", "Erro ao acessar o Firestore: " + task.getException().getMessage());
                Snackbar.make(v, "Erro ao acessar o Firestore", Snackbar.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private boolean validarCpf(String cpf) {
        CPFValidator cpfValidator = new CPFValidator();
        try {
            cpfValidator.assertValid(cpf);
            return true;
        } catch (InvalidStateException e) {
            Log.e("CPFValidation", "Erro ao validar CPF: " + e.getInvalidMessages());
            return false;
        }
    }
}
