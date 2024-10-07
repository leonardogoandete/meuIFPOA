/**
 * Classe de atividade para registro de novos usuários.
 */
package br.com.ifrs.meuifpoa.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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

/**
 * Classe responsável pela atividade de registro de novos usuários.
 */
public class RegistroActivity extends AppCompatActivity {

    private ActivityRegistroBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    /**
     * Método chamado quando a atividade é criada.
     *
     * @param savedInstanceState Estado salvo da instância anterior da atividade.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializar o ViewBinding
        binding = ActivityRegistroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Configurar botão de registro
        binding.btnRegistrar.setOnClickListener(this::registrar);
    }

    /**
     * Método chamado quando a atividade é destruída.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null; // Limpar o binding para evitar vazamento de memória
    }

    /**
     * Método chamado ao clicar no botão de registrar.
     *
     * @param v A view que foi clicada.
     */
    private void registrar(View v) {
        String nome = binding.etNome.getText().toString().trim();
        String cpf = binding.etCpf.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String senha = binding.etSenha.getText().toString().trim();

        if (!validarEntradas(nome, cpf, email, senha)) {
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnRegistrar.setEnabled(false); // Desativa o botão enquanto processa

        verificarUsuarioExistente(cpf, email, senha, v);
    }

    /**
     * Valida as entradas do formulário de registro.
     *
     * @param nome  Nome do usuário.
     * @param cpf   CPF do usuário.
     * @param email E-mail do usuário.
     * @param senha Senha do usuário.
     * @return true se todas as entradas forem válidas, false caso contrário.
     */
    private boolean validarEntradas(String nome, String cpf, String email, String senha) {
        if (nome.isEmpty()) {
            binding.etNome.setError("Nome obrigatório");
            binding.etNome.requestFocus();
            return false;
        }

        if (cpf.isEmpty() || !validarCpf(cpf)) {
            binding.etCpf.setError(cpf.isEmpty() ? "CPF obrigatório" : "CPF inválido");
            binding.etCpf.requestFocus();
            return false;
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.setError(email.isEmpty() ? "E-mail obrigatório" : "E-mail inválido");
            binding.etEmail.requestFocus();
            return false;
        }

        if (senha.isEmpty() || senha.length() < 6) {
            binding.etSenha.setError(senha.isEmpty() ? "Senha obrigatória" : "Senha deve ter no mínimo 6 caracteres");
            binding.etSenha.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Verifica se o usuário já existe no Firestore.
     *
     * @param cpf   CPF do usuário.
     * @param email E-mail do usuário.
     * @param senha Senha do usuário.
     * @param v     A view que foi clicada.
     */
    private void verificarUsuarioExistente(String cpf, String email, String senha, View v) {
        Query query = db.collection("usuarios").whereEqualTo("cpf", cpf);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    mostrarErro("Usuário já existe", v);
                } else {
                    criarNovoUsuario(email, senha, cpf, v);
                }
            } else {
                mostrarErro("Erro ao verificar usuário: " + task.getException().getMessage(), v);
            }
        });
    }

    /**
     * Cria um novo usuário no Firebase Authentication.
     *
     * @param email E-mail do usuário.
     * @param senha Senha do usuário.
     * @param cpf   CPF do usuário.
     * @param v     A view que foi clicada.
     */
    private void criarNovoUsuario(String email, String senha, String cpf, View v) {
        mAuth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String uid = mAuth.getUid();
                        if (uid != null) {
                            salvarDadosNoFirestore(uid, binding.etNome.getText().toString().trim(), cpf, email, v);
                        } else {
                            mostrarErro("Erro ao obter ID do usuário", v);
                        }
                    } else {
                        mostrarErro("Erro ao criar usuário: " + task.getException().getMessage(), v);
                    }
                });
    }

    /**
     * Salva os dados do usuário no Firestore.
     *
     * @param uid   ID do usuário.
     * @param nome  Nome do usuário.
     * @param cpf   CPF do usuário.
     * @param email E-mail do usuário.
     * @param v     A view que foi clicada.
     */
    private void salvarDadosNoFirestore(String uid, String nome, String cpf, String email, View v) {
        Registro registro = new Registro(uid, nome, cpf, email);
        db.collection("usuarios").document(uid).set(registro)
                .addOnSuccessListener(aVoid -> {
                    navegarParaLogin();
                })
                .addOnFailureListener(e -> {
                    mostrarErro("Erro ao salvar dados: " + e.getMessage(), v);
                });
    }

    /**
     * Navega para a LoginActivity.
     */
    private void navegarParaLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Valida o CPF do usuário.
     *
     * @param cpf CPF do usuário.
     * @return true se o CPF for válido, false caso contrário.
     */
    private boolean validarCpf(String cpf) {
        cpf = cpf.replaceAll("[^\\d]", ""); // Remove formatação do CPF
        CPFValidator cpfValidator = new CPFValidator();
        try {
            cpfValidator.assertValid(cpf);
            return true;
        } catch (InvalidStateException e) {
            Log.e("CPFValidation", "Erro ao validar CPF: " + e.getInvalidMessages());
            return false;
        }
    }

    /**
     * Mostra uma mensagem de erro usando Snackbar.
     *
     * @param mensagem Mensagem de erro.
     * @param v        A view que foi clicada.
     */
    private void mostrarErro(String mensagem, View v) {
        Snackbar.make(v, mensagem, Snackbar.LENGTH_LONG).show();
        binding.progressBar.setVisibility(View.GONE);
        binding.btnRegistrar.setEnabled(true);
    }
}
