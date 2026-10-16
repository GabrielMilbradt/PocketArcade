package com.pocketarcade;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.pocketarcade.databinding.ActivityRandomizerBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Tela 6 */
public class RandomizerActivity extends BaseActivity {

    private static final int COLUNAS_GRADE = 2;

    private ActivityRandomizerBinding binding;
    private final List<Palavra> itens = new ArrayList<>();
    private final Random random = new Random();
    private PalavraAdapter adapter;
    private boolean modoGrade;

    private MediaPlayer somSorteio;
    private MediaPlayer somAdicionar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRandomizerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        somSorteio = MediaPlayer.create(this, R.raw.sfx_sorteio);
        somAdicionar = MediaPlayer.create(this, R.raw.sfx_clique);

        binding.tvJogador.setText(getString(R.string.jogador_label, lerApelido()));

        ArrayAdapter<CharSequence> categorias = ArrayAdapter.createFromResource(
                this, R.array.categorias, android.R.layout.simple_spinner_item);
        categorias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spCategoria.setAdapter(categorias);

        carregarItensIniciais();

        adapter = new PalavraAdapter(itens, (posicao, palavra) -> remover(posicao));
        binding.rvItens.setLayoutManager(new LinearLayoutManager(this));
        binding.rvItens.setAdapter(adapter);

        binding.btnAdicionar.setOnClickListener(v -> adicionar());
        binding.btnSortear.setOnClickListener(v -> sortear());
        binding.btnAlternar.setOnClickListener(v -> alternarLayout());
        binding.btnVoltar.setOnClickListener(v -> finish());
        binding.btnEncerrar.setOnClickListener(v -> encerrarSessao(getString(R.string.modulo_randomizador)));

        atualizarContador();
    }

    private void carregarItensIniciais() {
        itens.add(new Palavra("Matrix", "Filmes"));
        itens.add(new Palavra("Interestelar", "Filmes"));
        itens.add(new Palavra("Pizza", "Comidas"));
        itens.add(new Palavra("Pão de queijo", "Comidas"));
        itens.add(new Palavra("Parque da cidade", "Lugares"));
        itens.add(new Palavra("Cinema", "Lugares"));
        itens.add(new Palavra("Fazer 10 flexões", "Desafios"));
        itens.add(new Palavra("Ficar 1 hora sem celular", "Desafios"));
    }

    private void adicionar() {
        String valor = binding.etValor.getText().toString().trim();
        if (valor.isEmpty()) {
            binding.etValor.setError(getString(R.string.erro_valor_vazio));
            binding.etValor.requestFocus();
            binding.etValor.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
            return;
        }
        binding.etValor.setError(null);

        String categoria = String.valueOf(binding.spCategoria.getSelectedItem());
        itens.add(new Palavra(valor, categoria));
        int posicao = itens.size() - 1;

        adapter.notifyItemInserted(posicao);
        binding.rvItens.scrollToPosition(posicao);

        binding.etValor.setText("");
        atualizarContador();
        tocar(somAdicionar);
    }

    private void remover(int posicao) {
        Palavra removida = itens.remove(posicao);
        adapter.notifyItemRemoved(posicao);
        adapter.setDestaque(null);
        atualizarContador();
        Toast.makeText(this, getString(R.string.item_removido, removida.getTexto()),
                Toast.LENGTH_SHORT).show();
    }

    private void sortear() {
        String categoria = String.valueOf(binding.spCategoria.getSelectedItem());

        List<Palavra> candidatos = new ArrayList<>();
        for (Palavra palavra : itens) {
            if (palavra.getCategoria().equals(categoria)) {
                candidatos.add(palavra);
            }
        }

        if (candidatos.isEmpty()) {
            binding.tvSorteado.setText(getString(R.string.rand_sem_itens, categoria));
            binding.tvSorteado.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
            adapter.setDestaque(null);
            return;
        }

        Palavra escolhida = candidatos.get(random.nextInt(candidatos.size()));
        binding.tvSorteado.setText(getString(R.string.rand_sorteado, escolhida.getTexto()));
        animarSorteio();
        tocar(somSorteio);

        adapter.setDestaque(escolhida);
        binding.rvItens.smoothScrollToPosition(itens.indexOf(escolhida));
    }

    private void animarSorteio() {
        binding.tvSorteado.setScaleX(0.4f);
        binding.tvSorteado.setScaleY(0.4f);
        binding.tvSorteado.setAlpha(0f);
        binding.tvSorteado.animate()
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .setDuration(500)
                .setInterpolator(new OvershootInterpolator())
                .start();
    }

    private void alternarLayout() {
        modoGrade = !modoGrade;
        if (modoGrade) {
            binding.rvItens.setLayoutManager(new GridLayoutManager(this, COLUNAS_GRADE));
            binding.btnAlternar.setText(R.string.ver_lista);
        } else {
            binding.rvItens.setLayoutManager(new LinearLayoutManager(this));
            binding.btnAlternar.setText(R.string.ver_grade);
        }
    }

    private void atualizarContador() {
        binding.tvContador.setText(getString(R.string.rand_contador, itens.size()));
    }

    @Override
    protected void onStop() {
        super.onStop();
        pausar(somSorteio, somAdicionar);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        liberar(somSorteio, somAdicionar);
        somSorteio = null;
        somAdicionar = null;
    }
}
