package br.com.ifrs.meuifpoa.ui.fragment;

import static br.com.ifrs.meuifpoa.utils.Constants.DOC_ATESTADO_MATRICULA;
import static br.com.ifrs.meuifpoa.utils.Constants.DOC_DECLARACAO_VINCULO;
import static br.com.ifrs.meuifpoa.utils.Constants.DOC_HISTORICO;
import static br.com.ifrs.meuifpoa.utils.Constants.DOC_HISTORICO_EMENTAS;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
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
        // Inicializar o View Binding
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
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
        SharedPreferences preferencias = getContext().getSharedPreferences("loginSigaa", Context.MODE_PRIVATE);
        String token = preferencias.getString("token", "");
        setupSemiCircularChart(0);
        binding.btnEmitirHistorico.txtBtnProgress.setText(R.string.msgBtnEmitirHistorico);
        binding.btnEmitirHistorico.progressButtonLayout.setOnClickListener(v -> {
            solicitarSenha(() -> {
                binding.btnEmitirHistorico.progressBarButton.setVisibility(View.VISIBLE);
                binding.btnEmitirHistorico.txtBtnProgress.setText(R.string.msgCarregando);
                DocumentoRequest documentoRequest = new DocumentoRequest(DOC_HISTORICO, minhaSenha);
                Call<DocumentoResponse> call = documentoService.obterDocumento(token, documentoRequest);
                call.enqueue(new Callback<DocumentoResponse>() {
                    @Override
                    public void onResponse(Call<DocumentoResponse> call, Response<DocumentoResponse> response) {
                        if (response.isSuccessful()) {
                            DocumentoResponse documentoResponse = response.body();
                            if (documentoResponse != null && documentoResponse.getPdfbase64() != null) {
                                String base64Documento = documentoResponse.getPdfbase64();
                                salvarEPDFVisualizar(DOC_HISTORICO, base64Documento);
                                binding.btnEmitirHistorico.progressBarButton.setVisibility(View.GONE);
                                binding.btnEmitirHistorico.txtBtnProgress.setText(R.string.msgBtnEmitirHistorico);
                            } else {
                                Log.e("API Error", "Documento está vazio.");
                                Toast.makeText(getContext(), "Erro: Resposta do documento está vazia.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e("API Error", "Falha ao obter o documento. Código de resposta: " + response.code());
                            Toast.makeText(getContext(), "Falha ao obter o documento.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<DocumentoResponse> call, Throwable t) {
                        Log.e("API Error", "Falha ao obter o documento.", t);
                        Toast.makeText(getContext(), "Falha ao obter o documento.", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });

        binding.btnEmitirHistoricoEmentas.txtBtnProgress.setText(R.string.msgBtnEmitirHistoricoEmentas);
        binding.btnEmitirHistoricoEmentas.progressButtonLayout.setOnClickListener(v -> {
            solicitarSenha(() -> {
                binding.btnEmitirHistoricoEmentas.progressBarButton.setVisibility(View.VISIBLE);
                binding.btnEmitirHistoricoEmentas.txtBtnProgress.setText(R.string.msgCarregando);
                DocumentoRequest documentoRequest = new DocumentoRequest(DOC_HISTORICO_EMENTAS, minhaSenha);
                Call<DocumentoResponse> call = documentoService.obterDocumento(token, documentoRequest);
                call.enqueue(new Callback<DocumentoResponse>() {
                    @Override
                    public void onResponse(Call<DocumentoResponse> call, Response<DocumentoResponse> response) {
                        if (response.isSuccessful()) {
                            DocumentoResponse documentoResponse = response.body();
                            if (documentoResponse != null && documentoResponse.getPdfbase64() != null) {
                                String base64Documento = documentoResponse.getPdfbase64();
                                salvarEPDFVisualizar(DOC_HISTORICO_EMENTAS,base64Documento);
                                binding.btnEmitirHistoricoEmentas.progressBarButton.setVisibility(View.GONE);
                                binding.btnEmitirHistoricoEmentas.txtBtnProgress.setText(R.string.msgBtnEmitirHistoricoEmentas);
                            } else {
                                Log.e("API Error", "Documento está vazio.");
                                Toast.makeText(getContext(),"Erro: Resposta do documento está vazia.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e("API Error", "Falha ao obter o documento. Código de resposta: " + response.code());
                            Toast.makeText(getContext(),"Falha ao obter o documento.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<DocumentoResponse> call, Throwable t) {
                        Log.e("API Error", "Falha ao obter o documento.", t);
                        Toast.makeText(getContext(),"Falha ao obter o documento.", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });

        binding.btnEmitirDeclaracaoVinculo.txtBtnProgress.setText(R.string.msgBtnEmitirDeclaracaoVinculo);
        binding.btnEmitirDeclaracaoVinculo.progressButtonLayout.setOnClickListener(v -> {
            solicitarSenha(() -> {
                binding.btnEmitirDeclaracaoVinculo.progressBarButton.setVisibility(View.VISIBLE);
                binding.btnEmitirDeclaracaoVinculo.txtBtnProgress.setText(R.string.msgCarregando);
                DocumentoRequest documentoRequest = new DocumentoRequest(DOC_DECLARACAO_VINCULO, minhaSenha);
                Call<DocumentoResponse> call = documentoService.obterDocumento(token, documentoRequest);
                call.enqueue(new Callback<DocumentoResponse>() {
                    @Override
                    public void onResponse(Call<DocumentoResponse> call, Response<DocumentoResponse> response) {
                        if (response.isSuccessful()) {
                            DocumentoResponse documentoResponse = response.body();
                            if (documentoResponse != null && documentoResponse.getPdfbase64() != null) {
                                String base64Documento = documentoResponse.getPdfbase64();
                                salvarEPDFVisualizar(DOC_DECLARACAO_VINCULO,base64Documento);
                                binding.btnEmitirDeclaracaoVinculo.progressBarButton.setVisibility(View.GONE);
                                binding.btnEmitirDeclaracaoVinculo.txtBtnProgress.setText(R.string.msgBtnEmitirDeclaracaoVinculo);
                            } else {
                                Log.e("API Error", "Documento está vazio.");
                                Toast.makeText(getContext(),"Erro: Resposta do documento está vazia.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e("API Error", "Falha ao obter o documento. Código de resposta: " + response.code());
                            Toast.makeText(getContext(),"Falha ao obter o documento.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<DocumentoResponse> call, Throwable t) {
                        Log.e("API Error", "Falha ao obter o documento.", t);
                        Toast.makeText(getContext(),"Falha ao obter o documento.", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });

        binding.btnEmitirAtestadoMatricula.txtBtnProgress.setText(R.string.msgBtnEmitirAtestadoMatricula);
        binding.btnEmitirAtestadoMatricula.progressButtonLayout.setOnClickListener(v -> {
            solicitarSenha(() -> {
                binding.btnEmitirAtestadoMatricula.progressBarButton.setVisibility(View.VISIBLE);
                binding.btnEmitirAtestadoMatricula.txtBtnProgress.setText(R.string.msgCarregando);
                DocumentoRequest documentoRequest = new DocumentoRequest(DOC_ATESTADO_MATRICULA, minhaSenha);
                Call<DocumentoResponse> call = documentoService.obterDocumento(token, documentoRequest);
                call.enqueue(new Callback<DocumentoResponse>() {
                    @Override
                    public void onResponse(Call<DocumentoResponse> call, Response<DocumentoResponse> response) {
                        if (response.isSuccessful()) {
                            DocumentoResponse documentoResponse = response.body();
                            if (documentoResponse != null && documentoResponse.getPdfbase64() != null) {
                                String base64Documento = documentoResponse.getPdfbase64();
                                salvarEPDFVisualizar(DOC_ATESTADO_MATRICULA,base64Documento);
                                binding.btnEmitirAtestadoMatricula.progressBarButton.setVisibility(View.GONE);
                                binding.btnEmitirAtestadoMatricula.txtBtnProgress.setText(R.string.msgBtnEmitirAtestadoMatricula);
                            } else {
                                Log.e("API Error", "Documento está vazio.");
                                Toast.makeText(getContext(),"Erro: Resposta do documento está vazia.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e("API Error", "Falha ao obter o documento. Código de resposta: " + response.code());
                            Toast.makeText(getContext(),"Falha ao obter o documento.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<DocumentoResponse> call, Throwable t) {
                        Log.e("API Error", "Falha ao obter o documento.", t);
                        Toast.makeText(getContext(),"Falha ao obter o documento.", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });
    }

    private void checkUserAuthentication() {
        if (mAuth.getCurrentUser() != null) {
            // Verifique se o binding ainda está ativo
            if (binding == null) {
                return;  // Saia do método se o binding for nulo
            }

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

                                    // Verifique novamente se o binding é nulo antes de acessá-lo
                                    if (binding != null) {
                                        binding.txtBemVindo.setText(mensagem);
                                        binding.containerIntegralizacoes.setVisibility(View.VISIBLE);

                                        binding.txtChObrigatoria.setText("CH Obrigatória Pendente: " + perfil.getChObrigatoriaPendente());
                                        binding.txtChOptativa.setText("CH Optativa Pendente: " + perfil.getChOptativaPendente());
                                        binding.txtChTotalCurriculo.setText("CH Total do Currículo: " + perfil.getChTotalCurriculo());
                                        binding.txtChComplementar.setText("CH Complementar Pendente: " + perfil.getChComplementarPendente());
                                        percentualIntegralizado = perfil.getIntegralizado();
                                        try {
                                            percentualIntegralizado = perfil.getIntegralizado();

                                            if (percentualIntegralizado != null && !percentualIntegralizado.isEmpty()) {
                                                setupSemiCircularChart(Integer.parseInt(percentualIntegralizado));
                                            } else {
                                                setupSemiCircularChart(0); // Valor padrão caso esteja vazio
                                            }
                                        } catch (NumberFormatException e) {
                                            Log.e("Error", "Falha ao converter percentual integralizado", e);
                                            setupSemiCircularChart(0); // Valor padrão em caso de falha
                                        }
                                    }
                                }
                            } else {
                                if (binding != null) {
                                    binding.containerIntegralizacoes.setVisibility(View.GONE);
                                }
                            }
                        } else {
                            if (binding != null) {
                                binding.containerIntegralizacoes.setVisibility(View.GONE);
                            }
                        }
                    });
        } else {
            if (binding != null) {
                binding.containerIntegralizacoes.setVisibility(View.GONE);
            }
        }
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
        PasswordDialog passwordDialog = new PasswordDialog(requireContext(), senha -> {
            minhaSenha = senha;
            onSuccess.run();
        });
        passwordDialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Evitar vazamento de memória
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
        colors.add(rgb("#FF0000")); // Vermelho para 9%

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



}