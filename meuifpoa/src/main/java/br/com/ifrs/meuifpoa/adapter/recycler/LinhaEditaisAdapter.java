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
import br.com.ifrs.meuifpoa.model.Edital;
import br.com.ifrs.meuifpoa.model.Noticia;

/**
 * Adaptador para exibir uma lista de notícias em um RecyclerView.
 */
public class LinhaEditaisAdapter extends RecyclerView.Adapter<LinhaEditaisAdapter.EditaisViewHolder> implements Filterable {

    /** Lista de editais. */
    private List<Edital> editais;
    /** Lista de editais filtrados. */
    private List<Edital> editaisFiltrados;
    /** Listener de clique nos itens da lista. */
    private OnClickListener onItemClickListener;

    /**
     * Construtor do adaptador LinhaNoticiasAdapter.
     *
     * @param editais a lista de editais a ser exibida
     */
    public LinhaEditaisAdapter(List<Edital> editais) {
        this.editais = editais;
        this.editaisFiltrados = new ArrayList<>(editais);
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
    public EditaisViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewLinhasEditais = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listar_editais_row, parent, false);
        return new EditaisViewHolder(viewLinhasEditais);
    }

    /**
     * Vincula os dados da notícia ao ViewHolder.
     *
     * @param holder o ViewHolder
     * @param position a posição do item
     */
    @Override
    public void onBindViewHolder(@NonNull EditaisViewHolder holder, int position) {
        Edital edital = editaisFiltrados.get(position);
        holder.textTituloNoticia.setText(edital.getTitulo());
        holder.textDataPublicacao.setText(edital.getDataPublicacao());

        // Define o clique no item da notícia
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onClick(position, edital);
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
        return editaisFiltrados.size();
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
                List<Edital> filteredList = new ArrayList<>();

                if (query.isEmpty()) {
                    filteredList.addAll(editais);
                } else {
                    for (Edital edital : editais) {
                        if (edital.getTitulo().toLowerCase().contains(query)) {
                            filteredList.add(edital);
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
                editaisFiltrados.clear();
                editaisFiltrados.addAll((List<Edital>) results.values);
                notifyDataSetChanged();
            }
        };
    }

    /**
     * Interface para o listener de clique nos itens da lista.
     */
    public interface OnClickListener {
        void onClick(int position, Edital edital);
    }

    /**
     * ViewHolder para os itens da lista de editais.
     */
    public class EditaisViewHolder extends RecyclerView.ViewHolder {

        TextView textTituloNoticia;
        TextView textDataPublicacao;

        /**
         * Construtor do ViewHolder.
         *
         * @param itemView a view do item
         */
        public EditaisViewHolder(@NonNull View itemView) {
            super(itemView);
            textTituloNoticia = itemView.findViewById(R.id.textTituloEdital);
            textDataPublicacao = itemView.findViewById(R.id.textDataPublicacaoEdital);
        }
    }

    /**
     * Atualiza a lista de editais exibida no RecyclerView.
     *
     * @param novosEditais a nova lista de editais
     */
    public void updateEditais(List<Edital> novosEditais) {
        this.editais = novosEditais;
        this.editaisFiltrados.clear();
        this.editaisFiltrados.addAll(novosEditais);
        notifyDataSetChanged();
    }
}