package br.com.ifrs.meuifpoa.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import br.com.caelum.stella.validation.CPFValidator;
import br.com.caelum.stella.validation.InvalidStateException;
import br.com.ifrs.meuifpoa.databinding.ActivityRegistroBinding;
import br.com.ifrs.meuifpoa.model.Registro;

public class RegistroActivity extends AppCompatActivity {

    private ActivityRegistroBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializar o ViewBinding
        binding = ActivityRegistroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Configurar botão de registro com o método de click
        binding.btnRegistrar.setOnClickListener(this::registrar);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null; // Limpar o binding para evitar vazamento de memória
    }

    private void registrar(View v) {
        String nome = binding.etNome.getText().toString().trim();
        String cpf = binding.etCpf.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String senha = binding.etSenha.getText().toString().trim();

        if (nome.isEmpty()) {
            binding.etNome.setError("Nome obrigatório");
            binding.etNome.requestFocus();
            return;
        }

        if (cpf.isEmpty()) {
            binding.etCpf.setError("CPF obrigatório");
            binding.etCpf.requestFocus();
            return;
        }

        if (!validarCpf(cpf)) {
            binding.etCpf.setError("CPF inválido");
            binding.etCpf.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            binding.etEmail.setError("E-mail obrigatório");
            binding.etEmail.requestFocus();
            return;
        }

        if (senha.isEmpty()) {
            binding.etSenha.setError("Senha obrigatória");
            binding.etSenha.requestFocus();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);

        Query query = db.collection("usuarios").whereEqualTo("cpf", cpf);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    Snackbar.make(v, "Erro ao cadastrar, usuário existente", Snackbar.LENGTH_SHORT).show();
                    binding.progressBar.setVisibility(View.GONE);
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
                                                binding.progressBar.setVisibility(View.GONE);
                                            });
                                } else {
                                    Snackbar.make(v, "Erro ao criar usuário", Snackbar.LENGTH_SHORT).show();
                                    binding.progressBar.setVisibility(View.GONE);
                                }
                            });
                }
            } else {
                Log.e("FirestoreError", "Erro ao acessar o Firestore: " + task.getException().getMessage());
                Snackbar.make(v, "Erro ao acessar o Firestore", Snackbar.LENGTH_SHORT).show();
                binding.progressBar.setVisibility(View.GONE);
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
