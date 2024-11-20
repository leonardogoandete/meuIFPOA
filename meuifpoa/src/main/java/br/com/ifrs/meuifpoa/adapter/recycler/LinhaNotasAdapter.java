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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.ifrs.meuifpoa.R;
import br.com.ifrs.meuifpoa.model.Nota;

/**
 * Adaptador para a lista de notas do usuário.
 */
public class LinhaNotasAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TIPO_SEMESTRE = 0;
    private static final int TIPO_NOTA = 1;

    private final List<Object> itens;
    private final Map<String, Boolean> estadoSemestres; // Controla o estado de expansão de cada semestre
    private final Map<String, List<Nota>> notasPorSemestre; // Notas por semestre

    /**
     * Construtor do adaptador LinhaNotasAdapter.
     *
     * @param itens Lista contendo objetos do tipo String (títulos de semestre).
     * @param notasPorSemestre Map contendo as notas organizadas por semestre.
     */
    public LinhaNotasAdapter(List<Object> itens, Map<String, List<Nota>> notasPorSemestre) {
        this.itens = new ArrayList<>(itens); // Cópia mutável da lista original
        this.estadoSemestres = new HashMap<>();
        this.notasPorSemestre = notasPorSemestre;

        // Inicializa todos os semestres como contraídos
        for (Object item : itens) {
            if (item instanceof String) {
                estadoSemestres.put((String) item, false);
            }
        }
    }

    /**
     * Retorna o tipo de visualização de um item na posição especificada.
     */
    @Override
    public int getItemViewType(int position) {
        if (itens.get(position) instanceof String) {
            return TIPO_SEMESTRE;
        } else {
            return TIPO_NOTA;
        }
    }

    /**
     * Cria um novo ViewHolder para um item na posição especificada.
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TIPO_SEMESTRE) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_semestre_row, parent, false);
            return new SemestreViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_notas_row, parent, false);
            return new NotasViewHolder(view);
        }
    }


    /**
     * Atualiza o conteúdo de um ViewHolder na posição especificada.
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SemestreViewHolder) {
            String tituloSemestre = (String) itens.get(position);
            SemestreViewHolder semestreViewHolder = (SemestreViewHolder) holder;
            semestreViewHolder.textTituloSemestre.setText(tituloSemestre);

            boolean isExpandido = estadoSemestres.getOrDefault(tituloSemestre, false);
            atualizarIconeExpandido(semestreViewHolder, isExpandido);

            semestreViewHolder.itemView.setOnClickListener(v -> {
                boolean novoEstado = !estadoSemestres.getOrDefault(tituloSemestre, false);
                estadoSemestres.put(tituloSemestre, novoEstado);
                if (novoEstado) {
                    expandirSemestre(tituloSemestre, position);
                } else {
                    contrairSemestre(tituloSemestre, position);
                }
                atualizarIconeExpandido(semestreViewHolder, novoEstado); // Atualiza o ícone após alterar o estado
            });
        } else if (holder instanceof NotasViewHolder) {
            Nota nota = (Nota) itens.get(position);
            NotasViewHolder notasViewHolder = (NotasViewHolder) holder;
            notasViewHolder.textCodDisciplinaListNotas.setText(nota.getCodigoDisciplina());
            notasViewHolder.textNomeDisciplina.setText(nota.getNomeDisciplina());
            notasViewHolder.textNota1.setText(nota.getPrimeiraUnidade().isEmpty() ? "--" : nota.getPrimeiraUnidade());
            notasViewHolder.textNota2.setText(nota.getSegundaUnidade().isEmpty() ? "--" : nota.getSegundaUnidade());
            notasViewHolder.textRecuperacao.setText(nota.getNotaRecuperacao().isEmpty() ? "--" : nota.getNotaRecuperacao());
            notasViewHolder.textResultado.setText(nota.getNotaFinal().isEmpty() ? "--" : nota.getNotaFinal());
            notasViewHolder.textFaltasValor.setText(String.valueOf(nota.getNumeroFaltas()));
            notasViewHolder.textSituacaoValor.setText(nota.getSituacao());

            if (nota.getSituacao().equalsIgnoreCase("aprovado")) {
                notasViewHolder.linearLayoutResultado.setBackground(
                        ContextCompat.getDrawable(notasViewHolder.linearLayoutResultado.getContext(), R.drawable.border_shape_inner_aprovado)
                );
            } else if (nota.getSituacao().equalsIgnoreCase("--")) {
                notasViewHolder.linearLayoutResultado.setBackground(
                        ContextCompat.getDrawable(notasViewHolder.linearLayoutResultado.getContext(), R.drawable.border_shape_inner_default)
                );
            } else {
                notasViewHolder.linearLayoutResultado.setBackground(
                        ContextCompat.getDrawable(notasViewHolder.linearLayoutResultado.getContext(), R.drawable.border_shape_inner_reprovado)
                );
            }
        }
    }

    /**
     * Atualiza o ícone de expansão/contração do título do semestre.
     */
    private void atualizarIconeExpandido(SemestreViewHolder holder, boolean isExpandido) {
        int iconResId = isExpandido ? R.drawable.ic_expand_less : R.drawable.ic_expand_more;
        holder.textTituloSemestre.setCompoundDrawablesWithIntrinsicBounds(0, 0, iconResId, 0);
    }

    /**
     * Retorna o número total de itens na lista.
     */
    @Override
    public int getItemCount() {
        return itens.size();
    }

    /**
     * Expande um semestre na lista.
     */
    private void expandirSemestre(String semestre, int position) {
        List<Nota> notasParaAdicionar = notasPorSemestre.get(semestre);
        if (notasParaAdicionar != null) {
            itens.addAll(position + 1, notasParaAdicionar);
            notifyItemRangeInserted(position + 1, notasParaAdicionar.size());
        }
    }

    /**
     * Contrai um semestre na lista.
     */
    private void contrairSemestre(String semestre, int position) {
        List<Nota> notasParaRemover = notasPorSemestre.get(semestre);
        if (notasParaRemover != null) {
            for (int i = 0; i < notasParaRemover.size(); i++) {
                itens.remove(position + 1);
            }
            notifyItemRangeRemoved(position + 1, notasParaRemover.size());
        }
    }

    /**
     * ViewHolder para os itens de título de semestre.
     */
    public static class SemestreViewHolder extends RecyclerView.ViewHolder {
        TextView textTituloSemestre;

        public SemestreViewHolder(@NonNull View itemView) {
            super(itemView);
            textTituloSemestre = itemView.findViewById(R.id.textTituloSemestre);
        }
    }

    /**
     * ViewHolder para os itens da lista de notas.
     */
    public static class NotasViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayoutResultado;
        TextView textCodDisciplinaListNotas;
        TextView textNomeDisciplina;
        TextView textNota1;
        TextView textNota2;
        TextView textRecuperacao;
        TextView textResultado;
        TextView textFaltasValor;
        TextView textSituacaoValor;

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
