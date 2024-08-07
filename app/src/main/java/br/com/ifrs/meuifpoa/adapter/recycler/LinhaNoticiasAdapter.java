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

public class LinhaNoticiasAdapter extends RecyclerView.Adapter<LinhaNoticiasAdapter.NoticiasViewHolder> implements Filterable {

    private List<Noticia> noticias;
    private List<Noticia> noticiasFiltradas;
    private OnClickListener onItemClickListener;

    public LinhaNoticiasAdapter(List<Noticia> noticias) {
        this.noticias = noticias;
        this.noticiasFiltradas = new ArrayList<>(noticias);
    }

    @NonNull
    @Override
    public NoticiasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewLinhasNoticias = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listar_noticias_row, parent, false);
        return new NoticiasViewHolder(viewLinhasNoticias);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticiasViewHolder holder, int position) {
        Noticia noticia = noticiasFiltradas.get(position);
        holder.textTituloNoticia.setText(noticia.getTitulo());
        holder.textDescricaoNoticia.setText(noticia.getResumo());
        holder.textDataNoticia.setText(noticia.getDataHoraPublicacao());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onClick(position, noticia);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return noticiasFiltradas.size();
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onItemClickListener = onClickListener;
    }

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

    public interface OnClickListener {
        void onClick(int position, Noticia noticia);
    }

    public class NoticiasViewHolder extends RecyclerView.ViewHolder {

        TextView textTituloNoticia;
        TextView textDescricaoNoticia;
        TextView textDataNoticia;

        public NoticiasViewHolder(@NonNull View itemView) {
            super(itemView);
            textTituloNoticia = itemView.findViewById(R.id.textTituloNoticia);
            textDescricaoNoticia = itemView.findViewById(R.id.textResumoNoticia);
            textDataNoticia = itemView.findViewById(R.id.textDataHoraNoticia);
        }
    }

    public void updateNoticias(List<Noticia> novasNoticias) {
        this.noticias = novasNoticias;
        this.noticiasFiltradas.clear();
        this.noticiasFiltradas.addAll(novasNoticias);
        notifyDataSetChanged();
    }
}
