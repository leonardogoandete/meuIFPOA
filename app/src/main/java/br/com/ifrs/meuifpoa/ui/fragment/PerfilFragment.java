package br.com.ifrs.meuifpoa.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;



import br.com.ifrs.meuifpoa.R;
import br.com.ifrs.meuifpoa.databinding.FragmentPerfilBinding;
import br.com.ifrs.meuifpoa.model.Perfil;
import br.com.ifrs.meuifpoa.utils.GerenciadorSinc;

/**
 * Fragmento responsável por exibir e gerenciar o perfil do usuário.
 */
public class PerfilFragment extends Fragment {

    private static final String TAG = "PerfilFragment";
    private FragmentPerfilBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;


    /**
     * Método chamado para inflar o layout do fragmento.
     *
     * @param inflater           O LayoutInflater usado para inflar o layout.
     * @param container          O ViewGroup pai ao qual o layout será anexado.
     * @param savedInstanceState O estado salvo anteriormente do fragmento.
     * @return A View raiz do layout inflado.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Método chamado após a criação da view do fragmento.
     *
     * @param view               A View criada.
     * @param savedInstanceState O estado salvo anteriormente do fragmento.
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inicializarFirebase();

        // Primeiramente, exibe os dados do cache
        obterDadosDoCache();

        FirebaseUser usuario = mAuth.getCurrentUser();

        if (usuario == null) {
            //exibirErro("Usuário não autenticado.");
            return;
        }

        usuario.getIdToken(true).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult().getToken();
                if (token != null) {
                    GerenciadorSinc GerenciadorSinc = new GerenciadorSinc();
                    GerenciadorSinc.verificarERequisitarSenha(getContext(), this::sincronizarComServidor);
                    Log.d(TAG, "Token de autenticação obtido com sucesso.");
                } else {
                    Log.e(TAG, "Token de autenticação nulo.");
                }
            } else {
                Log.e(TAG, "Erro ao obter token de autenticação.", task.getException());
            }
        });

        binding.btnSairPerfil.setOnClickListener(v -> {
            mAuth.signOut();
            limparDadosPerfil();
            GerenciadorSinc.limpar(getContext());

            Navigation.findNavController(view).navigate(R.id.noticiasFragment);
        });
    }

    /**
     * Método chamado quando a view do fragmento é destruída.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Inicializa as instâncias do Firebase Authentication e Firestore.
     */
    private void inicializarFirebase() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Obtém os dados do perfil do cache local.
     */
    private void obterDadosDoCache() {
        FirebaseUser usuarioAtual = mAuth.getCurrentUser();
        if (usuarioAtual == null || binding == null) {
            return;
        }

        // Primeira tentativa: obter dados do cache local
        db.collection("usuarios")
                .document(usuarioAtual.getUid())
                .get(Source.CACHE)  // Tenta obter do cache primeiro
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && binding != null) {
                        DocumentSnapshot documento = task.getResult();
                        if (documento.exists()) {
                            Perfil perfil = documento.toObject(Perfil.class);
                            if (perfil != null) {
                                configurarPerfil(perfil);
                                carregarFotoPerfil(perfil);  // Tenta carregar a foto do cache local também
                            } else {
                                mostrarErro(getString(R.string.perfil_nao_disponivel_cache));
                            }
                        } else {
                            mostrarErro(getString(R.string.documento_nao_encontrado_cache));
                        }
                    } else {
                        mostrarErro(getString(R.string.erro_obter_perfil_cache));
                    }
                });
    }


    /**
     * Sincroniza os dados do perfil com o servidor.
     */
    private void sincronizarComServidor() {
        FirebaseUser usuarioAtual = mAuth.getCurrentUser();
        if (usuarioAtual == null || binding == null) {
            return;
        }

        // Sincronizar dados do servidor
        db.collection("usuarios")
                .document(usuarioAtual.getUid())
                .get(Source.SERVER)  // Busca os dados do servidor para sincronizar
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && binding != null) {
                        DocumentSnapshot documento = task.getResult();
                        if (documento.exists()) {
                            Perfil perfil = documento.toObject(Perfil.class);
                            if (perfil != null) {
                                configurarPerfil(perfil);  // Atualiza os dados com os do servidor
                                carregarFotoPerfil(perfil);
                            } else {
                                mostrarErro(getString(R.string.perfil_nao_disponivel_servidor));
                            }
                        } else {
                            mostrarErro(getString(R.string.documento_nao_encontrado_servidor));
                        }
                    } else {
                        mostrarErro(getString(R.string.erro_obter_perfil_servidor));
                    }
                })
                .addOnFailureListener(e -> {
                    if (binding != null) {
                        mostrarErro(getString(R.string.erro_sync_conexao) + e.getMessage());
                    }
                });
    }


    /**
     * Configura os dados do perfil na interface do usuário.
     *
     * @param perfil O objeto Perfil contendo os dados do usuário.
     */
    private void configurarPerfil(Perfil perfil) {
        if (binding == null) {
            return;
        }
        binding.txtViewValorNome.setText(perfil.getNomeDocente() != null ? perfil.getNomeDocente() : getString(R.string.dado_nao_disponivel));
        binding.txtViewValorMatricula.setText(perfil.getMatricula() != null ? perfil.getMatricula() : getString(R.string.dado_nao_disponivel));
        binding.txtViewValorCurso.setText(perfil.getCurso() != null ? perfil.getCurso() : getString(R.string.dado_nao_disponivel));
        binding.txtViewValorNivel.setText(perfil.getNivel() != null ? perfil.getNivel() : getString(R.string.dado_nao_disponivel));
        binding.txtViewValorSituacao.setText(perfil.getStatus() != null ? perfil.getStatus() : getString(R.string.dado_nao_disponivel));
        binding.txtViewValorIngresso.setText(perfil.getAnoIngresso() != null ? perfil.getAnoIngresso() : getString(R.string.dado_nao_disponivel));
        binding.txtViewValorEmail.setText(perfil.getEmail() != null ? perfil.getEmail() : getString(R.string.dado_nao_disponivel));
        exibirElementosPerfil(true);
    }


    /**
     * Exibe uma mensagem de erro na interface do usuário.
     *
     * @param mensagem A mensagem de erro a ser exibida.
     */
    private void mostrarErro(String mensagem) {
        Toast.makeText(getContext(), mensagem, Toast.LENGTH_SHORT).show();
        Log.e(TAG, mensagem);
    }

    /**
     * Limpa os dados do perfil exibidos na interface do usuário.
     */
    private void limparDadosPerfil() {
        if (binding == null) {
            return;
        }
        binding.txtViewValorNome.setText("");
        binding.txtViewValorMatricula.setText("");
        binding.txtViewValorCurso.setText("");
        binding.txtViewValorNivel.setText("");
        binding.txtViewValorSituacao.setText("");
        binding.txtViewValorIngresso.setText("");
        binding.txtViewValorEmail.setText("");
        binding.imgPerfil.setImageResource(R.drawable.ifrs_poa_logo);
        exibirElementosPerfil(false);
    }

    /**
     * Carrega a foto do perfil a partir de uma string Base64.
     *
     * @param perfil O objeto Perfil contendo a string Base64 da imagem.
     */
    private void carregarFotoPerfil(Perfil perfil) {
        if (perfil == null || binding == null) {
            return;
        }

        String base64Imagem = perfil.getImgPerfil();  // Obtém a imagem em Base64
        if (base64Imagem != null && !base64Imagem.isEmpty()) {
            try {
                // Decodifica a string Base64 para um array de bytes
                byte[] decodedString = Base64.decode(base64Imagem, Base64.DEFAULT);

                // Converte o array de bytes em um Bitmap
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                // Define o Bitmap no ImageView
                binding.imgPerfil.setImageBitmap(bitmap);

                Log.d(TAG, "Imagem de perfil carregada a partir do Base64");
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "Erro ao decodificar Base64", e);
                binding.imgPerfil.setImageResource(R.drawable.ifrs_poa_logo);  // Define uma imagem padrão em caso de erro
            }
        } else {
            Log.d(TAG, "Nenhuma imagem de perfil disponível, carregando padrão");
            binding.imgPerfil.setImageResource(R.drawable.ifrs_poa_logo);  // Define a imagem padrão
        }
    }

    /**
     * Exibe ou oculta os elementos do perfil na interface do usuário.
     *
     * @param exibir Se true, exibe os elementos; caso contrário, oculta-os.
     */
        private void exibirElementosPerfil(boolean exibir) {
            if (binding == null) {
                return;
            }
            int visibilidade = exibir ? View.VISIBLE : View.GONE;
            binding.imgPerfil.setVisibility(visibilidade);
            binding.containerPerfil.setVisibility(visibilidade);
            binding.btnSairPerfil.setVisibility(visibilidade);
        }

}
