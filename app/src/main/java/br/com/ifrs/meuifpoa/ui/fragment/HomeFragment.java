package br.com.ifrs.meuifpoa.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Source;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import br.com.ifrs.meuifpoa.R;
import br.com.ifrs.meuifpoa.model.DocumentoResponse;
import br.com.ifrs.meuifpoa.model.Perfil;
import br.com.ifrs.meuifpoa.retrofit.DocumentoRetrofit;
import br.com.ifrs.meuifpoa.retrofit.service.DocumentoService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView txtBemVindo;
    private ProgressBar probarChObrigatoria;
    private View containerIntegralizacoes;

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

                                    // Fazer a chamada para obter o documento
                                    DocumentoService service = new DocumentoRetrofit().getDocumentoService();
                                    Call<DocumentoResponse> call = service.obterDocumento();
                                    call.enqueue(new Callback<DocumentoResponse>() {
                                        @Override
                                        public void onResponse(Call<DocumentoResponse> call, Response<DocumentoResponse> response) {
                                            if (response.isSuccessful()) {
                                                Log.d("API Response", "Response Body: " + response.body());
                                                DocumentoResponse documentoResponse = response.body();
                                                if (documentoResponse != null && documentoResponse.getPdfbase64() != null) {
                                                    String base64Documento = documentoResponse.getPdfbase64();
                                                    salvarEPDFVisualizar(base64Documento);
                                                } else {
                                                    Log.e("API Error", "Documento está vazio.");
                                                    txtBemVindo.setText("Erro: Resposta do documento está vazia.");
                                                }
                                            } else {
                                                Log.e("API Error", "Falha ao obter o documento. Código de resposta: " + response.code());
                                                txtBemVindo.setText("Falha ao obter o documento.");
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<DocumentoResponse> call, Throwable t) {
                                            txtBemVindo.setText("Falha ao obter o documento.");
                                        }
                                    });
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

    private void salvarEPDFVisualizar(String base64Data) {
        try {
            // Converter a string base64 em bytes
            byte[] pdfAsBytes = Base64.decode(base64Data, Base64.DEFAULT);

            // Salvar o PDF em um arquivo temporário no armazenamento interno
            File pdfFile = new File(requireContext().getCacheDir(), "documento.pdf");
            try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
                fos.write(pdfAsBytes);
            }

            // Agora que o arquivo foi salvo, exiba-o e ofereça a opção de compartilhamento
            visualizarPDF(pdfFile);
            compartilharPDF(pdfFile);

        } catch (IOException e) {
            e.printStackTrace();
            txtBemVindo.setText("Erro ao salvar o documento.");
        }
    }

    private void visualizarPDF(File pdfFile) {
        // Verificar se o arquivo existe
        if (pdfFile.exists()) {
            // Obter o URI do arquivo
            Uri pdfUri = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".fileprovider", pdfFile);

            // Criar um intent para abrir o PDF
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(pdfUri, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            // Verificar se o Google PDF Viewer está disponível
            Intent googlePDFViewerIntent = new Intent(Intent.ACTION_VIEW);
            googlePDFViewerIntent.setDataAndType(pdfUri, "application/pdf");
            googlePDFViewerIntent.setPackage("com.google.android.apps.pdfviewer"); // Google PDF Viewer package name

            // Verificar se existe um aplicativo de visualização de PDF
            if (googlePDFViewerIntent.resolveActivity(requireContext().getPackageManager()) != null) {
                startActivity(googlePDFViewerIntent);
            } else {
                // Se o Google PDF Viewer não estiver disponível, use o intent padrão
                Intent chooser = Intent.createChooser(intent, "Abrir com");
                if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
                    startActivity(chooser);
                } else {
                    txtBemVindo.setText("Nenhum aplicativo de visualização de PDF encontrado.");
                }
            }
        } else {
            txtBemVindo.setText("Arquivo PDF não encontrado.");
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
                txtBemVindo.setText("Nenhum aplicativo disponível para compartilhar PDF.");
            }
        }
    }
}
