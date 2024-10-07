package br.com.ifrs.meuifpoa.ui.custom;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import br.com.ifrs.meuifpoa.R;
import br.com.ifrs.meuifpoa.databinding.ActivityProgressButtonBinding;

/**
 * The type Progress button activity.
 */
public class ProgressButtonActivity extends AppCompatActivity implements View.OnClickListener {
    // Fazer binding
    private ActivityProgressButtonBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProgressButtonBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Configurar o OnClickListener
        binding.progressButtonLayout.setOnClickListener(this);
    }

    /**
     * Show loading.
     */
    public void showLoading() {
        binding.progressBarButton.setVisibility(View.VISIBLE);
        binding.txtBtnProgress.setText("Carregando...");
    }

    /**
     * Hide loading.
     */
    public void hideLoading() {
        binding.progressBarButton.setVisibility(View.GONE);
        binding.txtBtnProgress.setText("Clique aqui");
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.progressButtonLayout) {
            showLoading();

            // Simular uma tarefa longa com um Handler
            new android.os.Handler().postDelayed(() -> {
                hideLoading();
                Toast.makeText(ProgressButtonActivity.this, "Operação concluída", Toast.LENGTH_SHORT).show();
            }, 2000); // Simula uma tarefa de 2 segundos
        }
    }
}
