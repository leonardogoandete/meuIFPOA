package br.com.ifrs.meuifpoa.adapter.recycler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.com.ifrs.meuifpoa.R;
import br.com.ifrs.meuifpoa.model.Nota;

public class LinhaNotasAdapter extends RecyclerView.Adapter<LinhaNotasAdapter.NotasViewHolder> {
    List<Nota> notas = new ArrayList<>();

    /**
     * Construtor do adaptador LinhaNotasAdapter.
     *
     * @param notas a lista de notas a ser exibida
     */
    public LinhaNotasAdapter(List<Nota> notas) {
        this.notas = notas;
    }


    /**
     * Cria um novo ViewHolder para a linha de notas.
     *
     * @param parent o ViewGroup pai
     * @param position a posição do item
     * @return um novo NotasViewHolder
     */
    @NonNull
    @Override
    public NotasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View viewLinhasNotas = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_notas_row, parent, false);
        return new NotasViewHolder(viewLinhasNotas);
    }

    /**
     * Vincula os dados da nota ao ViewHolder.
     *
     * @param holder o ViewHolder
     * @param position a posição do item
     */
    @Override
    public void onBindViewHolder(@NonNull NotasViewHolder holder, int position) {
        Nota nota = notas.get(position);
        holder.textCodDisciplinaListNotas.setText(nota.getCodigoDisciplina());
        holder.textNomeDisciplina.setText(nota.getNomeDisciplina());
        holder.textNota1.setText(nota.getPrimeiraUnidade().isEmpty() ? "--" : notas.get(position).getPrimeiraUnidade());
        holder.textNota2.setText(nota.getSegundaUnidade().isEmpty() ? "--" : notas.get(position).getSegundaUnidade());
        holder.textRecuperacao.setText(nota.getNotaRecuperacao().isEmpty() ? "--" : notas.get(position).getNotaRecuperacao());
        holder.textResultado.setText(nota.getNotaFinal().isEmpty() ? "--" : notas.get(position).getNotaFinal());
        holder.textFaltasValor.setText(String.valueOf(nota.getNumeroFaltas()));
        holder.textSituacaoValor.setText(nota.getSituacao());

        // Define o background com base na situação da nota
        if (nota.getSituacao().equalsIgnoreCase("aprovado")) {
            holder.linearLayoutResultado.setBackground(ContextCompat.getDrawable(holder.linearLayoutResultado.getContext(), R.drawable.border_shape_inner_aprovado));
        } else if (nota.getSituacao().equalsIgnoreCase("--")) {
            holder.linearLayoutResultado.setBackground(ContextCompat.getDrawable(holder.linearLayoutResultado.getContext(), R.drawable.border_shape_inner_default));
        } else {
            holder.linearLayoutResultado.setBackground(ContextCompat.getDrawable(holder.linearLayoutResultado.getContext(), R.drawable.border_shape_inner_reprovado));
        }
    }

    /**
     * Retorna o número de itens na lista de notas.
     *
     * @return o número de itens
     */
    @Override
    public int getItemCount() {
        return notas.size();
    }


    /**
     * ViewHolder para os itens da lista de notas.
     */
    public class NotasViewHolder extends RecyclerView.ViewHolder {

        LinearLayout linearLayoutResultado;
        TextView textCodDisciplinaListNotas;
        TextView textNomeDisciplina;
        TextView textNota1;
        TextView textNota2;
        TextView textRecuperacao;
        TextView textResultado;
        TextView textFaltasValor;
        TextView textSituacaoValor;

        /**
         * Construtor do ViewHolder.
         *
         * @param itemView a view do item
         */
        public NotasViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayoutResultado = itemView.findViewById(R.id.resultadoContainer);
            textCodDisciplinaListNotas = itemView.findViewById(R.id.textCodDisciplinaListNotas);
            textNomeDisciplina = itemView.findViewById(R.id.textNomeDisciplinaListNotas);
            textNota1 = itemView.findViewById(R.id.textNota1);
            textNota2 = itemView.findViewById(R.id.textNota2);
            textRecuperacao = itemView.findViewById(R.id.textRecuperacao);
            textResultado = itemView.findViewById(R.id.textResultado);
            textFaltasValor = itemView.findViewById(R.id.textFaltasValor);
            textSituacaoValor = itemView.findViewById(R.id.textSituacaoValor);
        }
    }
}

