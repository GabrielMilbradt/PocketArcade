package com.pocketarcade;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.pocketarcade.databinding.ItemPalavraBinding;

import java.util.List;


public class PalavraAdapter extends RecyclerView.Adapter<PalavraAdapter.PalavraViewHolder> {

    public interface OnItemLongClickListener {
        void onItemLongClick(int posicao, Palavra palavra);
    }

    private final List<Palavra> itens;
    private final OnItemLongClickListener listener;
    @Nullable
    private Palavra destaque;

    public PalavraAdapter(List<Palavra> itens, OnItemLongClickListener listener) {
        this.itens = itens;
        this.listener = listener;
    }

    public void setDestaque(@Nullable Palavra novoDestaque) {
        Palavra anterior = destaque;
        destaque = novoDestaque;
        if (anterior != null) {
            int posAnterior = itens.indexOf(anterior);
            if (posAnterior >= 0) {
                notifyItemChanged(posAnterior);
            }
        }
        if (novoDestaque != null) {
            int posNovo = itens.indexOf(novoDestaque);
            if (posNovo >= 0) {
                notifyItemChanged(posNovo);
            }
        }
    }

    @NonNull
    @Override
    public PalavraViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPalavraBinding binding = ItemPalavraBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new PalavraViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PalavraViewHolder holder, int position) {
        Palavra palavra = itens.get(position);
        holder.binding.tvPalavra.setText(palavra.getTexto());
        holder.binding.tvCategoria.setText(palavra.getCategoria());
        holder.binding.getRoot().setActivated(palavra == destaque);
    }

    @Override
    public int getItemCount() {
        return itens.size();
    }

    class PalavraViewHolder extends RecyclerView.ViewHolder {

        private final ItemPalavraBinding binding;

        PalavraViewHolder(@NonNull ItemPalavraBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnLongClickListener(v -> {
                int posicao = getBindingAdapterPosition();
                if (posicao != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemLongClick(posicao, itens.get(posicao));
                }
                return true;
            });
        }
    }
}
