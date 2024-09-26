package br.com.ifrs.meuifpoa.ui.fragment;

import static br.com.ifrs.meuifpoa.utils.Constants.DOC_ATESTADO_MATRICULA;
import static br.com.ifrs.meuifpoa.utils.Constants.DOC_DECLARACAO_VINCULO;
import static br.com.ifrs.meuifpoa.utils.Constants.DOC_HISTORICO_EMENTAS;
import static br.com.ifrs.meuifpoa.utils.Constants.DOC_HISTORICO;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Source;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.ifrs.meuifpoa.R;
import br.com.ifrs.meuifpoa.databinding.FragmentHomeBinding;
import br.com.ifrs.meuifpoa.model.Documento.DocumentoRequest;
import br.com.ifrs.meuifpoa.model.Documento.DocumentoResponse;
import br.com.ifrs.meuifpoa.model.Perfil;
import br.com.ifrs.meuifpoa.retrofit.DocumentoRetrofit;
import br.com.ifrs.meuifpoa.retrofit.service.DocumentoService;
import br.com.ifrs.meuifpoa.ui.dialog.PasswordDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private final DocumentoService documentoService = new DocumentoRetrofit().getDocumentoService();
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FragmentHomeBinding binding;
    private String minhaSenha;
    private String percentualIntegralizado;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Verifica se o usuário está autenticado
        if (mAuth.getCurrentUser() == null) {
            // Se o usuário não estiver autenticado, esconder os componentes
            esconderComponentesInterface();
            return; // Sai do método para evitar continuar carregando os dados
        }

        configurarFirestore();

        SharedPreferences preferencias = getContext().getSharedPreferences("loginSigaa", Context.MODE_PRIVATE);
        String token = preferencias.getString("token", "");


        if (mAuth.getUid() != null) {
            setupSemiCircularChart(0);
            binding.containerIntegralizacoes.setVisibility(View.VISIBLE);
            configurarBotoes(token);
            checkUserAuthentication();
        }

    }

    private void configurarFirestore() {
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
    }

    private void configurarBotoes(String token) {
        configurarBotaoHistorico(token);
        configurarBotaoHistoricoEmentas(token);
        configurarBotaoDeclaracaoVinculo(token);
        configurarBotaoAtestadoMatricula(token);
    }

    private void configurarBotaoHistorico(String token) {
        binding.btnEmitirHistorico.txtBtnProgress.setText(R.string.msgBtnEmitirHistorico);
        binding.btnEmitirHistorico.progressButtonLayout.setOnClickListener(v -> {
            solicitarSenha(() -> emitirDocumento(token, DOC_HISTORICO, binding.btnEmitirHistorico.progressButtonLayout));
        });
    }

    private void configurarBotaoHistoricoEmentas(String token) {
        binding.btnEmitirHistoricoEmentas.txtBtnProgress.setText(R.string.msgBtnEmitirHistoricoEmentas);
        binding.btnEmitirHistoricoEmentas.progressButtonLayout.setOnClickListener(v -> {
            solicitarSenha(() -> emitirDocumento(token, DOC_HISTORICO_EMENTAS, binding.btnEmitirHistoricoEmentas.progressButtonLayout));
        });
    }

    private void configurarBotaoDeclaracaoVinculo(String token) {
        binding.btnEmitirDeclaracaoVinculo.txtBtnProgress.setText(R.string.msgBtnEmitirDeclaracaoVinculo);
        binding.btnEmitirDeclaracaoVinculo.progressButtonLayout.setOnClickListener(v -> {
            solicitarSenha(() -> emitirDocumento(token, DOC_DECLARACAO_VINCULO, binding.btnEmitirDeclaracaoVinculo.progressButtonLayout));
        });
    }

    private void configurarBotaoAtestadoMatricula(String token) {
        binding.btnEmitirAtestadoMatricula.txtBtnProgress.setText(R.string.msgBtnEmitirAtestadoMatricula);
        binding.btnEmitirAtestadoMatricula.progressButtonLayout.setOnClickListener(v -> {
            solicitarSenha(() -> emitirDocumento(token, DOC_ATESTADO_MATRICULA, binding.btnEmitirAtestadoMatricula.progressButtonLayout));
        });
    }


    private void emitirDocumento(String token, String tipoDocumento, View buttonLayout) {
        // Exibir o ProgressBar do botão clicado
        buttonLayout.findViewById(R.id.progressBarButton).setVisibility(View.VISIBLE);

        DocumentoRequest documentoRequest = new DocumentoRequest(tipoDocumento, minhaSenha);

        documentoService.obterDocumento(token, documentoRequest).enqueue(new Callback<DocumentoResponse>() {
            @Override
            public void onResponse(Call<DocumentoResponse> call, Response<DocumentoResponse> response) {
                if (binding == null) return;

                // Ocultar o ProgressBar após a resposta
                buttonLayout.findViewById(R.id.progressBarButton).setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    DocumentoResponse documentoResponse = response.body();
                    if (documentoResponse != null && documentoResponse.getPdfbase64() != null) {
                        String base64Documento = documentoResponse.getPdfbase64();
                        salvarEPDFVisualizar(tipoDocumento, base64Documento);
                    } else {
                        exibirErro("Documento está vazio.");
                    }
                } else {
                    exibirErro("Erro ao obter documento. Código: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<DocumentoResponse> call, Throwable t) {
                if (binding == null) return;

                // Ocultar o ProgressBar em caso de falha
                buttonLayout.findViewById(R.id.progressBarButton).setVisibility(View.GONE);
                exibirErro("Erro na solicitação: " + t.getMessage());
            }
        });
        configuraDesabilitarBotao(true);
    }


    private void configuraDesabilitarBotao(boolean habilitar) {
        Log.d("HomeFragment", "configuraDesabilitarBotao: " + habilitar);
        binding.btnEmitirHistorico.progressButtonLayout.setEnabled(habilitar);
        binding.btnEmitirHistoricoEmentas.progressButtonLayout.setEnabled(habilitar);
        binding.btnEmitirAtestadoMatricula.progressButtonLayout.setEnabled(habilitar);
        binding.btnEmitirDeclaracaoVinculo.progressButtonLayout.setEnabled(habilitar);
    }

    private void checkUserAuthentication() {
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            db.collection("usuarios").document(userId).get(Source.DEFAULT).addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult().exists()) {
                    Perfil perfil = task.getResult().toObject(Perfil.class);
                    if (perfil != null && binding != null) {
                        atualizarInterfacePerfil(perfil);
                    }
                } else {
                    esconderComponentesInterface();
                }
            });
        } else {
            esconderComponentesInterface();
        }
    }

    private void atualizarInterfacePerfil(Perfil perfil) {
        String primeiroNome = obterPrimeiroNome(perfil.getNomeDocente());
        StringBuffer mensagem = new StringBuffer();
        mensagem.append(getString(R.string.msgBemVindo)).append(primeiroNome).append("!");
        binding.txtBemVindo.setText(mensagem);
        binding.containerIntegralizacoes.setVisibility(View.VISIBLE);
        configurarValoresPerfil(perfil);
    }

    private void configurarValoresPerfil(Perfil perfil) {
        binding.txtChObrigatoria.setText(perfil.getChObrigatoriaPendente());
        binding.txtChOptativa.setText(perfil.getChOptativaPendente());
        binding.txtChTotalCurriculo.setText(perfil.getChTotalCurriculo());
        binding.txtChComplementar.setText(perfil.getChComplementarPendente());

        try {
            percentualIntegralizado = perfil.getIntegralizado();
            setupSemiCircularChart(percentualIntegralizado != null && !percentualIntegralizado.isEmpty() ?
                    Integer.parseInt(percentualIntegralizado) : 0);
        } catch (NumberFormatException e) {
            Log.e("Error", "Falha ao converter percentual integralizado", e);
            setupSemiCircularChart(0);
        }
    }

    private void esconderComponentesInterface() {
        if (binding == null) return;
        binding.containerIntegralizacoes.setVisibility(View.GONE);
    }

    private void exibirErro(String mensagem) {
        Log.e("API Error", mensagem);
        Toast.makeText(getContext(), mensagem, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setupSemiCircularChart(int realizado) {
        // Cria os dados para o gráfico
        List<PieEntry> pieEntries = new ArrayList<>();
        int percentualRestante = 100-realizado;

        pieEntries.add(new PieEntry(realizado, "")); // 91% preenchido
        pieEntries.add(new PieEntry(percentualRestante, ""));  // 9% restante

        // Configura o DataSet e os dados do gráfico
        PieDataSet dataSet = new PieDataSet(pieEntries, "");

        // Define as cores: 91% verde e 9% vermelho
        List<Integer> colors = new ArrayList<>();
        colors.add(rgb("#2F9E41")); // Verde para 91%
        colors.add(rgb("#CF212D")); // Vermelho para 9%

        dataSet.setColors(colors); // Aplica as cores
        dataSet.setDrawValues(false); // Isso remove os percentuais exibidos nas fatias

        PieData data = new PieData(dataSet);

        binding.semiCircularChart.setData(data);

        // Configura a aparência do gráfico
        binding.semiCircularChart.setRotationAngle(135f); // Define o ângulo inicial
        binding.semiCircularChart.setMaxAngle(270f); // Define o ângulo total como 270 graus (semi-círculo)

        // Configura o furo central para cobrir quase toda a área interna
        binding.semiCircularChart.setHoleRadius(80f); // Deixa apenas 10% da área visível como borda externa

        // Desabilita o clique no gráfico
        binding.semiCircularChart.setTouchEnabled(false);

        // Remove a legenda do gráfico
        binding.semiCircularChart.getLegend().setEnabled(false);

        // Remove a descrição do gráfico
        binding.semiCircularChart.getDescription().setEnabled(false);

        // Remove os rótulos das entradas
        binding.semiCircularChart.setDrawEntryLabels(false);

        // Exibe o texto central como "%"
        binding.semiCircularChart.setCenterText("Integralizado\n"+realizado+"%"); // Exibe o texto central com o valor desejado
        binding.semiCircularChart.setCenterTextSize(20f); // Define o tamanho do texto central

        // Atualiza o gráfico
        binding.semiCircularChart.invalidate();
    }

    // Configura a cor
    public static int rgb(String hex) {
        int color = (int) Long.parseLong(hex.replace("#", ""), 16);
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = (color >> 0) & 0xFF;
        return Color.rgb(r, g, b);
    }

    private String obterPrimeiroNome(String nomeCompleto) {
        if (nomeCompleto == null || nomeCompleto.isEmpty()) {
            return "";
        }
        String[] partes = nomeCompleto.split(" ");
        if (partes.length > 0) {
            String primeiroNome = partes[0];
            primeiroNome = primeiroNome.substring(0, 1).toUpperCase() + primeiroNome.substring(1).toLowerCase();
            return primeiroNome;
        }
        return "";
    }

    private void salvarEPDFVisualizar(String nome, String base64Data) {
        try {
            byte[] pdfAsBytes = Base64.decode(base64Data, Base64.DEFAULT);

            File pdfFile = new File(requireContext().getCacheDir(), nome + ".pdf");
            try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
                fos.write(pdfAsBytes);
            }

            visualizarPDF(pdfFile);
            //compartilharPDF(pdfFile);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Erro ao salvar o documento.", Toast.LENGTH_SHORT).show();
        }
    }

    private void visualizarPDF(File pdfFile) {
        if (pdfFile.exists()) {
            Uri pdfUri = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".fileprovider", pdfFile);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(pdfUri, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            Intent googlePDFViewerIntent = new Intent(Intent.ACTION_VIEW);
            googlePDFViewerIntent.setDataAndType(pdfUri, "application/pdf");
            googlePDFViewerIntent.setPackage("com.google.android.apps.pdfviewer");

            if (googlePDFViewerIntent.resolveActivity(requireContext().getPackageManager()) != null) {
                startActivity(googlePDFViewerIntent);
            } else {
                Intent chooser = Intent.createChooser(intent, "Abrir com");
                if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
                    startActivity(chooser);
                } else {
                    compartilharPDF(pdfFile);
                    Toast.makeText(getContext(), "Nenhum aplicativo de visualização de PDF encontrado.", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(getContext(), "Arquivo PDF não encontrado.", Toast.LENGTH_SHORT).show();
        }
    }

    private void compartilharPDF(File pdfFile) {
        if (pdfFile.exists()) {
            Uri pdfUri = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".fileprovider", pdfFile);

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("application/pdf");
            shareIntent.putExtra(Intent.EXTRA_STREAM, pdfUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            Intent chooser = Intent.createChooser(shareIntent, "Compartilhar PDF com");
            if (shareIntent.resolveActivity(requireContext().getPackageManager()) != null) {
                startActivity(chooser);
            } else {
                Toast.makeText(getContext(), "Nenhum aplicativo de compartilhamento encontrado.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void solicitarSenha(Runnable onSuccess) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_sync_sigaa, null);

        // Referenciando o TextInputLayout para obter a senha digitada
        TextInputLayout senhaInput = dialogView.findViewById(R.id.textInputSenhaSyncSigaa);
        TextView titulo = dialogView.findViewById(R.id.textViewTitulo);
        titulo.setText("Senha SIGA");
        builder.setView(dialogView)
                .setPositiveButton("OK", null)  // Não fechar o diálogo automaticamente
                .setNegativeButton("Cancelar", (dialogInterface, which) -> dialogInterface.dismiss());

        AlertDialog dialog = builder.create();

        // Override do botão positivo para tratar a senha
        dialog.setOnShowListener(dialogInterface -> {
            Button okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            okButton.setOnClickListener(v -> {
                // Capturar a senha
                String senhaDigitada = senhaInput.getEditText().getText().toString().trim();

                if (!senhaDigitada.isEmpty()) {
                    // Armazena a senha e executa o callback
                    minhaSenha = senhaDigitada;
                    onSuccess.run();  // Executa a ação após inserir a senha
                    dialog.dismiss();  // Fecha o diálogo
                } else {
                    // Exibe um erro se o campo de senha estiver vazio
                    senhaInput.setError("Senha não pode ser vazia");
                }
            });
        });

        dialog.show();  // Exibe o diálogo
    }


}
