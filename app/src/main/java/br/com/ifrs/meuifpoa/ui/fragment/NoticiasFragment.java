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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import br.com.ifrs.meuifpoa.R;
import br.com.ifrs.meuifpoa.adapter.recycler.LinhaNoticiasAdapter;
import br.com.ifrs.meuifpoa.databinding.FragmentNoticiasBinding;
import br.com.ifrs.meuifpoa.model.Noticia;
import br.com.ifrs.meuifpoa.retrofit.NoticiasRetrofit;
import br.com.ifrs.meuifpoa.retrofit.service.NoticiasService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoticiasFragment extends Fragment implements LinhaNoticiasAdapter.OnClickListener {

    private static final long SEARCH_DELAY_MS = 400;
    private LinhaNoticiasAdapter noticiasAdapter;
    private Handler searchHandler;
    private int limiteNoticias = 50;
    private String currentQuery = "";
    private FragmentNoticiasBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inicializa o ViewBinding
        binding = FragmentNoticiasBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchHandler = new Handler(Looper.getMainLooper());

        setupRecyclerView();
        setupSearchView();
        setupSpinner();
        loadInitialNews();
    }

    // Configura o RecyclerView para exibir as notícias
    private void setupRecyclerView() {
        binding.listViewNoticias.setHasFixedSize(true);
        binding.listViewNoticias.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    // Configura a SearchView para capturar as pesquisas com um delay de 400ms
    private void setupSearchView() {
        binding.searchViewNoticias.setIconified(false);
        binding.searchViewNoticias.setQueryHint("Buscar noticias");
        binding.searchViewNoticias.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentQuery = query;
                fetchNews(currentQuery);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                currentQuery = newText;
                if (newText.isEmpty()) {
                    fetchNews(null);
                } else {
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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(requireContext(),
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
                } else if (position == 1) {
                    textView.setText("Todos");
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
        binding.containerProgressBar.setVisibility(View.VISIBLE);
        binding.listViewNoticias.setVisibility(View.GONE);
        binding.txtNaoTemNoticias.setVisibility(View.GONE);

        NoticiasService service = new NoticiasRetrofit().getNoticiasService();
        Call<List<Noticia>> call = service.listarNoticias(filter, limiteNoticias);

        call.enqueue(new Callback<List<Noticia>>() {
            @Override
            public void onResponse(Call<List<Noticia>> call, Response<List<Noticia>> response) {
                binding.containerProgressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<Noticia> noticias = response.body();
                    if (!noticias.isEmpty()) {
                        updateRecyclerView(noticias);
                        binding.txtNaoTemNoticias.setVisibility(View.GONE);
                        binding.listViewNoticias.setVisibility(View.VISIBLE);
                    } else {
                        binding.txtNaoTemNoticias.setVisibility(View.VISIBLE);
                        binding.listViewNoticias.setVisibility(View.GONE);
                    }
                } else {
                    binding.txtNaoTemNoticias.setText("Erro ao obter notícias!");
                }
            }

            @Override
            public void onFailure(Call<List<Noticia>> call, Throwable t) {
                binding.containerProgressBar.setVisibility(View.GONE);
                binding.txtNaoTemNoticias.setVisibility(View.VISIBLE);
                binding.txtNaoTemNoticias.setText("Erro ao obter notícias!");
            }
        });
    }

    // Atualiza o RecyclerView com as notícias recebidas
    private void updateRecyclerView(List<Noticia> noticias) {
        if (noticiasAdapter == null) {
            noticiasAdapter = new LinhaNoticiasAdapter(noticias);
            noticiasAdapter.setOnClickListener(NoticiasFragment.this);
            binding.listViewNoticias.setAdapter(noticiasAdapter);
        } else {
            noticiasAdapter.updateNoticias(noticias);
        }
        binding.listViewNoticias.setVisibility(View.VISIBLE);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Evita vazamento de memória
    }
}
