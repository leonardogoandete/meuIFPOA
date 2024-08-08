package br.com.ifrs.meuifpoa;

import android.content.Intent;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

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

    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.listViewNoticias);
        searchView = view.findViewById(R.id.searchViewNoticias);
        spinnerLimite = view.findViewById(R.id.spinnerLimite);
        searchHandler = new Handler(Looper.getMainLooper());
    }

    private void setupRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentQuery = query;
                fetchNews(currentQuery);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                currentQuery = newText;
                searchHandler.removeCallbacksAndMessages(null);
                searchHandler.postDelayed(() -> fetchNews(currentQuery), SEARCH_DELAY_MS);
                return true;
            }
        });
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.limites_noticias, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLimite.setAdapter(adapter);
        spinnerLimite.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                limiteNoticias = Integer.parseInt(parent.getItemAtPosition(position).toString());
                fetchNews(currentQuery);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action needed
            }
        });
    }

    private void loadInitialNews() {
        fetchNews(null);
    }

    private void fetchNews(String filter) {
        NoticiasService service = new NoticiasRetrofit().getNoticiasService();
        Call<List<Noticia>> call = service.listarNoticias(filter, limiteNoticias);
        call.enqueue(new Callback<List<Noticia>>() {
            @Override
            public void onResponse(Call<List<Noticia>> call, Response<List<Noticia>> response) {
                if (response.isSuccessful()) {
                    List<Noticia> noticias = response.body();
                    if (noticias != null && !noticias.isEmpty()) {
                        updateRecyclerView(noticias);
                    } else {
                        showSnackbar("Não há notícias disponíveis");
                    }
                } else {
                    showSnackbar("Erro ao obter notícias!");
                }
            }

            @Override
            public void onFailure(Call<List<Noticia>> call, Throwable t) {
                showSnackbar("Erro ao obter notícias: " + t.getMessage());
            }
        });
    }

    private void updateRecyclerView(List<Noticia> noticias) {
        if (noticiasAdapter == null) {
            noticiasAdapter = new LinhaNoticiasAdapter(noticias);
            noticiasAdapter.setOnClickListener(NoticiasFragment.this);
            recyclerView.setAdapter(noticiasAdapter);
        } else {
            noticiasAdapter.updateNoticias(noticias);
        }
    }

    private void showSnackbar(String message) {
        View rootView = getView();
        if (rootView != null) {
            Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(int position, Noticia noticia) {
        String url = noticia.getLink();
        if (url != null && !url.isEmpty()) {
            String baseUrl = "https://poa.ifrs.edu.br";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(baseUrl + url));
            startActivity(intent);
        } else {
            showSnackbar("URL da notícia não disponível");
        }
    }
}
