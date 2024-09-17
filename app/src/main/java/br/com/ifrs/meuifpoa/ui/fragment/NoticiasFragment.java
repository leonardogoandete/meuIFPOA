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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import br.com.ifrs.meuifpoa.R;
import br.com.ifrs.meuifpoa.adapter.recycler.LinhaNoticiasAdapter;
import br.com.ifrs.meuifpoa.model.Noticia;
import br.com.ifrs.meuifpoa.retrofit.NoticiasRetrofit;
import br.com.ifrs.meuifpoa.retrofit.service.NoticiasService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoticiasFragment extends Fragment implements LinhaNoticiasAdapter.OnClickListener {

    private static final long SEARCH_DELAY_MS = 400;
    private RecyclerView recyclerView;
    private LinhaNoticiasAdapter noticiasAdapter;
    private TextView txtNaoTemNoticias;
    private SearchView searchView;
    private Spinner spinnerLimite;
    private Handler searchHandler;
    private int limiteNoticias = 50;
    private String currentQuery = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_noticias, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
        setupRecyclerView();
        setupSearchView();
        setupSpinner();
        loadInitialNews();
    }

    // Inicializa todas as views e objetos que serão utilizados
    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.listViewNoticias);
        searchView = view.findViewById(R.id.searchViewNoticias);
        spinnerLimite = view.findViewById(R.id.spinnerLimite);
        txtNaoTemNoticias = view.findViewById(R.id.txtNaoTemNoticias);
        searchHandler = new Handler(Looper.getMainLooper());
    }

    // Configura o RecyclerView para exibir as notícias
    private void setupRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    // Configura a SearchView para capturar as pesquisas com um delay de 400ms
    private void setupSearchView() {
        searchView.setIconified(false);
        searchView.setQueryHint("Buscar noticias");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentQuery = query;
                fetchNews(currentQuery);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Atualiza a variável currentQuery com o novo texto
                currentQuery = newText;

                if (newText.isEmpty()) {
                    // Quando o texto é vazio (X foi clicado), recarregar as notícias completas
                    fetchNews(null);
                } else {
                    // Caso contrário, faz a busca com debounce
                    debounceSearch();
                }
                return true;
            }
        });
    }


    // Método para lidar com debounce da pesquisa
    private void debounceSearch() {
        searchHandler.removeCallbacksAndMessages(null);
        searchHandler.postDelayed(() -> fetchNews(currentQuery), SEARCH_DELAY_MS);
    }

    // Configura o Spinner para permitir seleção de limite de notícias com item de dica "Selecione o limite"
    private void setupSpinner() {
        // Adiciona um item "Selecione o limite" como primeiro item
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(requireContext(),
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.limites_noticias)) {

            @Override
            public boolean isEnabled(int position) {
                // Desabilita o primeiro item ("Selecione o limite")
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                if (position == 0) {
                    // Customiza o primeiro item ("Selecione o limite")
                    ((TextView) view).setTextColor(Color.GRAY);
                } else {
                    ((TextView) view).setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLimite.setAdapter(adapter);
        spinnerLimite.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Somente atualiza o limite se o item selecionado não for o primeiro
                if (position != 0) {
                    limiteNoticias = Integer.parseInt(parent.getItemAtPosition(position).toString());
                    fetchNews(currentQuery);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Nenhuma ação necessária
            }
        });
    }

    // Carrega as notícias iniciais sem filtro
    private void loadInitialNews() {
        fetchNews(null);
    }

    // Realiza a chamada à API para buscar notícias com o filtro e limite especificados
    private void fetchNews(String filter) {
        NoticiasService service = new NoticiasRetrofit().getNoticiasService();
        Call<List<Noticia>> call = service.listarNoticias(filter, limiteNoticias);

        call.enqueue(new Callback<List<Noticia>>() {
            @Override
            public void onResponse(Call<List<Noticia>> call, Response<List<Noticia>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Noticia> noticias = response.body();
                    if (!noticias.isEmpty()) {
                        updateRecyclerView(noticias);
                        txtNaoTemNoticias.setVisibility(View.GONE); // Esconde a mensagem de "sem notícias"
                        recyclerView.setVisibility(View.VISIBLE); // Mostra o RecyclerView
                    } else {
                        showMessage("Não há notícias disponíveis");
                        txtNaoTemNoticias.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE); // Esconde o RecyclerView quando não há notícias
                    }
                } else {
                    showMessage("Erro ao obter notícias!");
                }
            }

            @Override
            public void onFailure(Call<List<Noticia>> call, Throwable t) {
                showMessage("Erro ao obter notícias!");
            }
        });
    }

    // Atualiza o RecyclerView com as notícias recebidas
    private void updateRecyclerView(List<Noticia> noticias) {
        if (noticiasAdapter == null) {
            noticiasAdapter = new LinhaNoticiasAdapter(noticias);
            noticiasAdapter.setOnClickListener(NoticiasFragment.this);
            recyclerView.setAdapter(noticiasAdapter);
        } else {
            noticiasAdapter.updateNoticias(noticias);
        }
        recyclerView.setVisibility(View.VISIBLE); // Garante que o RecyclerView seja visível quando houver notícias
    }

    // Exibe uma mensagem usando Snackbar ou Toast
    private void showMessage(String message) {
        View rootView = getView();
        if (rootView != null) {
            Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    // Abre a URL da notícia quando o item é clicado
    @Override
    public void onClick(int position, Noticia noticia) {
        String url = noticia.getLink();
        if (url != null && !url.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(BASE_URL_NOTICIA + url));
            startActivity(intent);
        } else {
            showMessage("URL da notícia não disponível");
        }
    }
}
