package br.com.ifrs.meuifpoa.ui.custom;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import br.com.ifrs.meuifpoa.R;
import br.com.ifrs.meuifpoa.databinding.ActivityProgressButtonBinding;

/**
 * \@author Seu Nome
 * \@version 1.0
 *
 * Classe que representa uma atividade com um botão de progresso.
 */
public class ProgressButtonActivity extends AppCompatActivity implements View.OnClickListener {
    // Fazer binding
    private ActivityProgressButtonBinding binding;

    /**
     * Método chamado quando a atividade é criada.
     * <p>
     * \@param savedInstanceState Estado salvo da instância anterior da atividade.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProgressButtonBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Configurar o OnClickListener
        binding.progressButtonLayout.setOnClickListener(this);
    }

    /**
     * Exibe a barra de progresso e altera o texto do botão.
     */
    public void showLoading() {
        binding.progressBarButton.setVisibility(View.VISIBLE);
        binding.txtBtnProgress.setText("Carregando...");
    }

    /**
     * Oculta a barra de progresso e restaura o texto do botão.
     */
    public void hideLoading() {
        binding.progressBarButton.setVisibility(View.GONE);
        binding.txtBtnProgress.setText("Clique aqui");
    }

    /**
     * Método chamado quando o botão é clicado.
     * <p>
     * \@param view A view que foi clicada.
     */
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.progressButtonLayout) {
            showLoading();

            // Simular uma tarefa longa com um Handler
            // Simula uma tarefa longa com um Handler
            hideLoading();
            Toast.makeText(ProgressButtonActivity.this, "Operação concluída", Toast.LENGTH_SHORT).show();

        }
    }
}

