package br.com.ifrs.meuifpoa.ui.fragment;

import static br.com.ifrs.meuifpoa.utils.Constants.BASE_URL_NOTICIA;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
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

public class NoticiasFragment extends Fragment implements LinhaNoticiasAdapter.OnClickListener, LinhaEditaisAdapter.OnClickListener {

    private static final long SEARCH_DELAY_MS = 400;
    private LinhaNoticiasAdapter noticiasAdapter;
    private LinhaEditaisAdapter editaisAdapter;
    private Handler searchHandler;
    private int limiteNoticias = 50;
    private String currentQuery = "";
    private boolean isNoticiasSelected = true; // Indica se é "Notícias" ou "Editais"
    private FragmentNoticiasBinding binding;
    private Call<List<Noticia>> callNoticias;
    private Call<List<Edital>> callEditais;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNoticiasBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchHandler = new Handler(Looper.getMainLooper());

        setupRecyclerView();
        setupSearchView();
        setupButtons(); // Configura os botões de alternância
        loadInitialNews();
    }

    private void setupRecyclerView() {
        binding.listViewNoticias.setHasFixedSize(true);
        binding.listViewNoticias.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void setupSearchView() {
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
                debounceSearch();
                return true;
            }
        });
    }

    private void debounceSearch() {
        searchHandler.removeCallbacksAndMessages(null);
        searchHandler.postDelayed(() -> {
            if (isNoticiasSelected) {
                fetchNews(currentQuery);
            } else {
                fetchEditais(currentQuery);
            }
        }, SEARCH_DELAY_MS);
    }

    private void setupButtons() {
        binding.btnNoticias.setOnClickListener(v -> {
            if (!isNoticiasSelected) {
                isNoticiasSelected = true;
                fetchNews(currentQuery);
                updateButtonStates();
            }
        });

        binding.btnEditais.setOnClickListener(v -> {
            if (isNoticiasSelected) {
                isNoticiasSelected = false;
                fetchEditais(currentQuery);
                updateButtonStates();
            }
        });

        updateButtonStates(); // Define o estado inicial dos botões
    }

    private void updateButtonStates() {
        if (isNoticiasSelected) {
            binding.btnNoticias.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
            binding.btnNoticias.setTextColor(getResources().getColor(android.R.color.white));

            //binding.btnEditais.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccentLight));
            //binding.btnEditais.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        } else {
            //binding.btnEditais.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));
            binding.btnEditais.setTextColor(getResources().getColor(android.R.color.white));

            //binding.btnNoticias.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimaryLight));
            //binding.btnNoticias.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    private void loadInitialNews() {
        fetchNews(null);
    }

    private void fetchNews(String filter) {
        cancelarChamadaAnterior();

        binding.containerProgressBar.setVisibility(View.VISIBLE);
        binding.listViewNoticias.setVisibility(View.GONE);
        binding.txtNaoTemNoticias.setVisibility(View.GONE);

        NoticiasService service = new NoticiasRetrofit().getNoticiasService();
        callNoticias = service.listarNoticias(filter, limiteNoticias);

        callNoticias.enqueue(new Callback<List<Noticia>>() {
            @Override
            public void onResponse(Call<List<Noticia>> call, Response<List<Noticia>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Noticia> noticias = response.body();
                    updateRecyclerViewNoticias(noticias);
                } else {
                    mostrarMensagemErro("Erro ao obter notícias!");
                }
            }

            @Override
            public void onFailure(Call<List<Noticia>> call, Throwable t) {
                handleApiFailure();
            }
        });
    }

    private void fetchEditais(String filter) {
        cancelarChamadaAnterior();

        binding.containerProgressBar.setVisibility(View.VISIBLE);
        binding.listViewNoticias.setVisibility(View.GONE);
        binding.txtNaoTemNoticias.setVisibility(View.GONE);

        EditaisService service = new EditaisRetrofit().getEditaisService();
        callEditais = service.listarEditais(filter, limiteNoticias);

        callEditais.enqueue(new Callback<List<Edital>>() {
            @Override
            public void onResponse(Call<List<Edital>> call, Response<List<Edital>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Edital> editais = response.body();
                    updateRecyclerViewEditais(editais);
                } else {
                    mostrarMensagemErro("Erro ao obter editais!");
                }
            }

            @Override
            public void onFailure(Call<List<Edital>> call, Throwable t) {
                handleApiFailure();
            }
        });
    }

    private void cancelarChamadaAnterior() {
        if (callNoticias != null && !callNoticias.isCanceled()) {
            callNoticias.cancel();
        }
        if (callEditais != null && !callEditais.isCanceled()) {
            callEditais.cancel();
        }
    }

    private void handleApiFailure() {
        binding.containerProgressBar.setVisibility(View.GONE);
        binding.txtNaoTemNoticias.setVisibility(View.VISIBLE);
        binding.txtNaoTemNoticias.setText("Erro ao obter dados!");
    }

    private void updateRecyclerViewNoticias(List<Noticia> noticias) {
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

    private void updateRecyclerViewEditais(List<Edital> editais) {
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

    private void mostrarMensagemErro(String message) {
        View rootView = getView();
        if (rootView != null) {
            Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cancelarChamadaAnterior();
        binding = null;
    }
}
