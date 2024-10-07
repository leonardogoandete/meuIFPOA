package br.com.ifrs.meuifpoa.adapter.recycler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.com.ifrs.meuifpoa.R;
import br.com.ifrs.meuifpoa.model.Noticia;

/**
 * O adaptador `LinhaNoticiasAdapter` é responsável por adaptar os dados das notícias em um RecyclerView.
 * Ele fornece o layout e a lógica para exibir cada item de notícia na interface do usuário.
 * Ele também implementa a funcionalidade de filtro para que os usuários possam pesquisar notícias por título ou resumo.
 */
public class LinhaNoticiasAdapter extends RecyclerView.Adapter<LinhaNoticiasAdapter.NoticiasViewHolder> implements Filterable {

    private List<Noticia> noticias;
    private List<Noticia> noticiasFiltradas;
    private OnClickListener onItemClickListener;

    /**
     * Construtor para inicializar o adaptador com uma lista de notícias.
     *
     * @param noticias Lista de objetos {@link Noticia}.
     */
    public LinhaNoticiasAdapter(List<Noticia> noticias) {
        this.noticias = noticias;
        this.noticiasFiltradas = new ArrayList<>(noticias);
    }

    /**
     * Infla o layout para cada item de notícia no RecyclerView.
     *
     * @param parent   O ViewGroup ao qual a nova View será anexada.
     * @param viewType O tipo de visualização do novo item.
     * @return Um novo objeto {@link NoticiasViewHolder} que contém as Views para cada item de notícia.
     */
    @NonNull
    @Override
    public NoticiasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewLinhasNoticias = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listar_noticias_row, parent, false);
        return new NoticiasViewHolder(viewLinhasNoticias);
    }

    /**
     * Vincula os dados de uma notícia ao ViewHolder.
     *
     * @param holder   O ViewHolder que será atualizado com os dados da notícia.
     * @param position A posição do item no dataset.
     */
    @Override
    public void onBindViewHolder(@NonNull NoticiasViewHolder holder, int position) {
        Noticia noticia = noticiasFiltradas.get(position);
        holder.textTituloNoticia.setText(noticia.getTitulo());
        holder.textDescricaoNoticia.setText(noticia.getResumo());
        holder.textDataNoticia.setText(noticia.getDataHoraPublicacao());

        // Define o clique no item da notícia
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onClick(position, noticia);
            }
        });
    }

    /**
     * Retorna o número total de itens na lista filtrada de notícias.
     *
     * @return O número de itens na lista de notícias filtradas.
     */
    @Override
    public int getItemCount() {
        return noticiasFiltradas.size();
    }

    /**
     * Define o listener para cliques nos itens de notícias.
     *
     * @param onClickListener O listener que será chamado quando um item for clicado.
     */
    public void setOnClickListener(OnClickListener onClickListener) {
        this.onItemClickListener = onClickListener;
    }

    /**
     * Retorna o objeto de filtro que será usado para realizar a filtragem de notícias.
     *
     * @return Um filtro para realizar a pesquisa de notícias.
     */
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String query = constraint.toString().toLowerCase();
                List<Noticia> filteredList = new ArrayList<>();

                if (query.isEmpty()) {
                    filteredList.addAll(noticias);
                } else {
                    for (Noticia noticia : noticias) {
                        if (noticia.getTitulo().toLowerCase().contains(query) ||
                                noticia.getResumo().toLowerCase().contains(query)) {
                            filteredList.add(noticia);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            @SuppressWarnings("unchecked")
            protected void publishResults(CharSequence constraint, FilterResults results) {
                noticiasFiltradas.clear();
                noticiasFiltradas.addAll((List<Noticia>) results.values);
                notifyDataSetChanged();
            }
        };
    }

    /**
     * Interface para tratar os cliques nos itens do RecyclerView.
     */
    public interface OnClickListener {
        /**
         * Método chamado quando um item é clicado.
         *
         * @param position A posição do item que foi clicado.
         * @param noticia  A notícia correspondente ao item clicado.
         */
        void onClick(int position, Noticia noticia);
    }

    /**
     * Classe interna que representa o ViewHolder para as notícias.
     * Armazena as Views que exibem as informações de uma notícia.
     */
    public class NoticiasViewHolder extends RecyclerView.ViewHolder {

        TextView textTituloNoticia;
        TextView textDescricaoNoticia;
        TextView textDataNoticia;

        /**
         * Construtor da classe ViewHolder, que inicializa as Views para exibir os dados de uma notícia.
         *
         * @param itemView A View correspondente ao item da lista.
         */
        public NoticiasViewHolder(@NonNull View itemView) {
            super(itemView);
            textTituloNoticia = itemView.findViewById(R.id.textTituloNoticia);
            textDescricaoNoticia = itemView.findViewById(R.id.textResumoNoticia);
            textDataNoticia = itemView.findViewById(R.id.textDataHoraNoticia);
        }
    }

    /**
     * Atualiza a lista de notícias exibida no RecyclerView.
     *
     * @param novasNoticias A nova lista de objetos {@link Noticia} a ser exibida.
     */
    public void updateNoticias(List<Noticia> novasNoticias) {
        this.noticias = novasNoticias;
        this.noticiasFiltradas.clear();
        this.noticiasFiltradas.addAll(novasNoticias);
        notifyDataSetChanged();
    }
}
