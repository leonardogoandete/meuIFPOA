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
 * Adaptador para exibir uma lista de notícias em um RecyclerView.
 */
public class LinhaNoticiasAdapter extends RecyclerView.Adapter<LinhaNoticiasAdapter.NoticiasViewHolder> implements Filterable {

    /** Lista de notícias. */
    private List<Noticia> noticias;
    /** Lista de notícias filtradas. */
    private List<Noticia> noticiasFiltradas;
    /** Listener de clique nos itens da lista. */
    private OnClickListener onItemClickListener;

    /**
     * Construtor do adaptador LinhaNoticiasAdapter.
     *
     * @param noticias a lista de notícias a ser exibida
     */
    public LinhaNoticiasAdapter(List<Noticia> noticias) {
        this.noticias = noticias;
        this.noticiasFiltradas = new ArrayList<>(noticias);
    }

    /**
     * Cria um novo ViewHolder para a linha de notícias.
     *
     * @param parent o ViewGroup pai
     * @param viewType o tipo de view
     * @return um novo NoticiasViewHolder
     */
    @NonNull
    @Override
    public NoticiasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewLinhasNoticias = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listar_noticias_row, parent, false);
        return new NoticiasViewHolder(viewLinhasNoticias);
    }

    /**
     * Vincula os dados da notícia ao ViewHolder.
     *
     * @param holder o ViewHolder
     * @param position a posição do item
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
     * Retorna o número de itens na lista de notícias.
     *
     * @return o número de itens
     */
    @Override
    public int getItemCount() {
        return noticiasFiltradas.size();
    }

    /**
     * Define o listener de clique para os itens da lista.
     *
     * @param onClickListener o listener de clique
     */
    public void setOnClickListener(OnClickListener onClickListener) {
        this.onItemClickListener = onClickListener;
    }

    /**
     * Retorna o filtro para a lista de notícias.
     *
     * @return o filtro
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
     * Interface para o listener de clique nos itens da lista.
     */
    public interface OnClickListener {
        void onClick(int position, Noticia noticia);
    }

    /**
     * ViewHolder para os itens da lista de notícias.
     */
    public class NoticiasViewHolder extends RecyclerView.ViewHolder {

        TextView textTituloNoticia;
        TextView textDescricaoNoticia;
        TextView textDataNoticia;

        /**
         * Construtor do ViewHolder.
         *
         * @param itemView a view do item
         */
        public NoticiasViewHolder(@NonNull View itemView) {
            super(itemView);
            textTituloNoticia = itemView.findViewById(R.id.textTituloNoticia);
            textDescricaoNoticia = itemView.findViewById(R.id.textResumoNoticia);
            textDataNoticia = itemView.findViewById(R.id.textDataHoraNoticia);
        }
    }

    /**
     * Atualiza a lista de notícias.
     *
     * @param novasNoticias a nova lista de notícias
     */
    public void updateNoticias(List<Noticia> novasNoticias) {
        this.noticias = novasNoticias;
        this.noticiasFiltradas.clear();
        this.noticiasFiltradas.addAll(novasNoticias);
        notifyDataSetChanged();
    }
}