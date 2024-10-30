package br.com.ifrs.meuifpoa.ui.fragment;

import static br.com.ifrs.meuifpoa.utils.Constants.BASE_URL_NOTICIA;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import br.com.ifrs.meuifpoa.R;
import br.com.ifrs.meuifpoa.adapter.recycler.LinhaEditaisAdapter;
import br.com.ifrs.meuifpoa.adapter.recycler.LinhaNoticiasAdapter;
import br.com.ifrs.meuifpoa.databinding.FragmentNoticiasBinding;
import br.com.ifrs.meuifpoa.model.Edital;
import br.com.ifrs.meuifpoa.model.Noticia;
import br.com.ifrs.meuifpoa.retrofit.EditaisRetrofit;
import br.com.ifrs.meuifpoa.retrofit.NoticiasRetrofit;
import br.com.ifrs.meuifpoa.retrofit.service.EditaisService;
import br.com.ifrs.meuifpoa.retrofit.service.NoticiasService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Fragmento que exibe notícias e editais em um RecyclerView.
 */
public class NoticiasFragment extends Fragment implements LinhaNoticiasAdapter.OnClickListener, LinhaEditaisAdapter.OnClickListener {

    /**
     * Tempo de atraso para a busca de notícias após o usuário digitar.
     */
    private static final long SEARCH_DELAY_MS = 400;
    /**
     * Adaptador para exibir notícias em um RecyclerView.
     */
    private LinhaNoticiasAdapter noticiasAdapter;
    /**
     * Adaptador para exibir editais em um RecyclerView.
     */
    private LinhaEditaisAdapter editaisAdapter;
    /**
     * Handler para atrasar a busca de notícias após o usuário digitar.
     */
    private Handler searchHandler;
    /**
     * Limite de notícias a serem exibidas.
     */
    private int limiteNoticias = 50;
    /**
     * Filtro de busca atual.
     */
    private String currentQuery = "";
    /**
     * Indica se é "Notícias" ou "Editais".
     */
    private boolean isNoticiasSelected = true; // Indica se é "Notícias" ou "Editais"
    /**
     * Binding do fragmento.
     */
    private FragmentNoticiasBinding binding;
    /**
     * Chamada para obter notícias.
     */
    private Call<List<Noticia>> callNoticias;
    /**
     * Chamada para obter editais.
     */
    private Call<List<Edital>> callEditais;

    /**
     * Indica se está carregando notícias ou editais.
     */
    private boolean isLoading = false;

    /**
     * Cria a view do fragmento com o ViewBinding.
     *
     * @param inflater O LayoutInflater.
     * @param container O ViewGroup.
     * @param savedInstanceState O Bundle com o estado salvo.
     * @return A view criada.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNoticiasBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Método chamado quando a view é criada.
     *
     * @param view A View.
     * @param savedInstanceState O estado salvo.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (binding != null) {
            searchHandler = new Handler(Looper.getMainLooper());

            setupRecyclerView();
            setupSearchView();
            setupSpinner();
            setupButtons(); // Configura os botões de alternância
            loadInitialNews(); // Carrega as notícias iniciais
        }
    }

    /**
     * Configura o RecyclerView para exibir notícias e editais.
     */
    private void setupRecyclerView() {
        if (binding != null) {
            binding.listViewNoticias.setHasFixedSize(true);
            binding.listViewNoticias.setLayoutManager(new LinearLayoutManager(requireContext()));
        }
    }

    /**
     * Configura a SearchView para capturar buscas do usuário.
     */
    private void setupSearchView() {
        if (binding != null) {
            binding.searchViewNoticias.setIconified(false);
            binding.searchViewNoticias.setQueryHint("Buscar");
            binding.searchViewNoticias.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    currentQuery = query;
                    if (isNoticiasSelected) {
                        fetchNews(currentQuery);
                    } else {
                        fetchEditais(currentQuery);
                    }
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    currentQuery = newText;
                    debounceSearch(); // Atrasa a busca enquanto o usuário digita
                    return true;
                }
            });
        }
    }

    /**
     * Atrasa a busca de notícias ou editais para evitar requisições repetidas enquanto o usuário digita.
     */
    private void debounceSearch() {
        if (binding != null) {
            searchHandler.removeCallbacksAndMessages(null);
            searchHandler.postDelayed(() -> {
                if (isNoticiasSelected) {
                    fetchNews(currentQuery);
                } else {
                    fetchEditais(currentQuery);
                }
            }, SEARCH_DELAY_MS);
        }
    }

    /**
     * Configura os botões para alternar entre "Notícias" e "Editais".
     */
    private void setupButtons() {
        if (binding != null) {
            binding.btnNoticias.setOnClickListener(v -> {
                if (!isNoticiasSelected && !isLoading) {
                    isNoticiasSelected = true;
                    fetchNews(currentQuery);
                    updateButtonStates();
                }
            });

            binding.btnEditais.setOnClickListener(v -> {
                if (isNoticiasSelected && !isLoading) {
                    isNoticiasSelected = false;
                    fetchEditais(currentQuery);
                    updateButtonStates();
                }
            });

            updateButtonStates(); // Define o estado inicial dos botões
        }
    }

    /**
     * Atualiza o estado visual dos botões de alternância.
     */
    private void updateButtonStates() {
        if (binding != null) {
            if (isNoticiasSelected) {
                // Botão "Notícias" selecionado
                binding.btnNoticias.setTextColor(getResources().getColor(R.color.colorPrimary)); // Texto verde
                binding.underlineNoticias.setVisibility(View.VISIBLE); // Mostrar sublinhado

                binding.btnEditais.setTextColor(getResources().getColor(R.color.gray)); // Texto cinza
                binding.underlineEditais.setVisibility(View.INVISIBLE); // Ocultar sublinhado
            } else {
                // Botão "Editais" selecionado
                binding.btnEditais.setTextColor(getResources().getColor(R.color.colorPrimary)); // Texto verde
                binding.underlineEditais.setVisibility(View.VISIBLE); // Mostrar sublinhado

                binding.btnNoticias.setTextColor(getResources().getColor(R.color.gray)); // Texto cinza
                binding.underlineNoticias.setVisibility(View.INVISIBLE); // Ocultar sublinhado
            }
        }
    }

    /**
     * Carrega as notícias iniciais ao iniciar o fragmento.
     */
    private void loadInitialNews() {
        fetchNews(null);
    }

    /**
     * Faz a chamada para buscar notícias com base no filtro.
     *
     * @param filter O filtro de busca.
     */
    private void fetchNews(String filter) {
        cancelarChamadaAnterior();

        isLoading = true; // Inicia o carregamento
        if (binding != null) {
            binding.containerProgressBar.setVisibility(View.VISIBLE);
            binding.listViewNoticias.setVisibility(View.GONE);
            binding.txtNaoTemNoticias.setVisibility(View.GONE);
        }

        NoticiasService service = new NoticiasRetrofit().getNoticiasService();
        callNoticias = service.listarNoticias(filter, limiteNoticias);

        callNoticias.enqueue(new Callback<List<Noticia>>() {
            @Override
            public void onResponse(Call<List<Noticia>> call, Response<List<Noticia>> response) {
                if (binding != null) {
                    binding.containerProgressBar.setVisibility(View.GONE);
                    if (response.isSuccessful() && response.body() != null) {
                        updateRecyclerViewNoticias(response.body());
                    } else {
                        mostrarMensagemErro(getString(R.string.erro_obter_dados_noticias));
                    }
                }
                isLoading = false; // Fim do carregamento
            }

            @Override
            public void onFailure(Call<List<Noticia>> call, Throwable t) {
                if (binding != null && !call.isCanceled()) {
                    mostrarMensagemErro(getString(R.string.erro_obter_dados_noticias));
                    binding.containerProgressBar.setVisibility(View.GONE);
                    binding.txtNaoTemNoticias.setVisibility(View.VISIBLE);
                }
                isLoading = false; // Fim do carregamento
            }
        });
    }

    /**
     * Faz a chamada para buscar editais com base no filtro.
     *
     * @param filter O filtro de busca.
     */
    private void fetchEditais(String filter) {
        cancelarChamadaAnterior();

        isLoading = true; // Inicia o carregamento
        if (binding != null) {
            binding.containerProgressBar.setVisibility(View.VISIBLE);
            binding.listViewNoticias.setVisibility(View.GONE);
            binding.txtNaoTemNoticias.setVisibility(View.GONE);
        }

        EditaisService service = new EditaisRetrofit().getEditaisService();
        callEditais = service.listarEditais(filter, limiteNoticias);

        callEditais.enqueue(new Callback<List<Edital>>() {
            @Override
            public void onResponse(Call<List<Edital>> call, Response<List<Edital>> response) {
                if (binding != null) {
                    binding.containerProgressBar.setVisibility(View.GONE);
                    if (response.isSuccessful() && response.body() != null) {
                        updateRecyclerViewEditais(response.body());
                    } else {
                        mostrarMensagemErro(getString(R.string.erro_obter_dados_editais));
                    }
                }
                isLoading = false; // Fim do carregamento
            }

            @Override
            public void onFailure(Call<List<Edital>> call, Throwable t) {
                if (binding != null && !call.isCanceled()) {
                    mostrarMensagemErro(getString(R.string.erro_obter_dados_editais));
                    binding.containerProgressBar.setVisibility(View.GONE);
                    binding.txtNaoTemNoticias.setVisibility(View.VISIBLE);
                }
                isLoading = false; // Fim do carregamento
            }
        });
    }

    /**
     * Cancela as chamadas anteriores de API, se ainda estiverem ativas.
     */
    private void cancelarChamadaAnterior() {
        if (callNoticias != null && !callNoticias.isCanceled()) {
            callNoticias.cancel();
        }
        if (callEditais != null && !callEditais.isCanceled()) {
            callEditais.cancel();
        }
    }

    /**
     * Atualiza o RecyclerView com as notícias recebidas.
     *
     * @param noticias A lista de notícias.
     */
    private void updateRecyclerViewNoticias(List<Noticia> noticias) {
        if (binding != null) {
            if (noticias.isEmpty()) {
                binding.txtNaoTemNoticias.setVisibility(View.VISIBLE);
                binding.listViewNoticias.setVisibility(View.GONE);
            } else {
                binding.txtNaoTemNoticias.setVisibility(View.GONE);
                binding.listViewNoticias.setVisibility(View.VISIBLE);

                noticiasAdapter = new LinhaNoticiasAdapter(noticias);
                noticiasAdapter.setOnClickListener(this);
                binding.listViewNoticias.setAdapter(noticiasAdapter);
            }
            binding.containerProgressBar.setVisibility(View.GONE);
        }
    }

    /**
     * Atualiza o RecyclerView com os editais recebidos.
     *
     * @param editais A lista de editais.
     */
    private void updateRecyclerViewEditais(List<Edital> editais) {
        if (binding != null) {
            if (editais.isEmpty()) {
                binding.txtNaoTemNoticias.setVisibility(View.VISIBLE);
                binding.listViewNoticias.setVisibility(View.GONE);
            } else {
                binding.txtNaoTemNoticias.setVisibility(View.GONE);
                binding.listViewNoticias.setVisibility(View.VISIBLE);

                editaisAdapter = new LinhaEditaisAdapter(editais);
                editaisAdapter.setOnClickListener(this);
                binding.listViewNoticias.setAdapter(editaisAdapter);
            }
            binding.containerProgressBar.setVisibility(View.GONE);
        }
    }

    /**
     * Abre a URL da notícia no navegador.
     *
     * @param position A posição da notícia no adaptador.
     * @param noticia A notícia selecionada.
     */
    @Override
    public void onClick(int position, Noticia noticia) {
        String url = noticia.getLink();
        if (url != null && !url.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(BASE_URL_NOTICIA + url));
            startActivity(intent);
        } else {
            mostrarMensagemErro("URL da notícia não disponível");
        }
    }

    /**
     * Abre a URL do edital no navegador.
     *
     * @param position A posição do edital no adaptador.
     * @param edital O edital selecionado.
     */
    @Override
    public void onClick(int position, Edital edital) {
        String url = edital.getLink();
        if (url != null && !url.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(BASE_URL_NOTICIA + url));
            startActivity(intent);
        } else {
            mostrarMensagemErro("URL do edital não disponível");
        }
    }

    /**
     * Mostra uma mensagem de erro na tela.
     *
     * @param message A mensagem de erro a ser exibida.
     */
    private void mostrarMensagemErro(String message) {
        if (binding != null) {
            Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Configura o Spinner para seleção de limite de notícias ou editais.
     */
    private void setupSpinner() {
        if (binding != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_spinner_item,
                    getResources().getStringArray(R.array.limites_noticias)) {

                @Override
                public boolean isEnabled(int position) {
                    return position != 0;
                }

                @Override
                public View getDropDownView(int position, View convertView, ViewGroup parent) {
                    View view = super.getDropDownView(position, convertView, parent);
                    TextView textView = (TextView) view;
                    if (position == 0) {
                        textView.setTextColor(Color.GRAY);
                    } else {
                        textView.setTextColor(Color.BLACK);
                    }
                    return view;
                }
            };
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binding.spinnerLimite.setAdapter(adapter);
            binding.spinnerLimite.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position != 0) {
                        limiteNoticias = Integer.parseInt(parent.getItemAtPosition(position).toString());
                        if (isNoticiasSelected) {
                            fetchNews(currentQuery);
                        } else {
                            fetchEditais(currentQuery);
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // Nenhuma ação necessária
                }
            });
        }
    }

    /**
     * Limpa a referência do binding e cancela as chamadas de API ao destruir a view.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cancelarChamadaAnterior();
        binding = null;
    }
}

