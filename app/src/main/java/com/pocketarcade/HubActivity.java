package com.pocketarcade;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.pocketarcade.databinding.ActivityHubBinding;
import com.pocketarcade.databinding.ItemPanelBinding;

/** Tela 2 */
public class HubActivity extends BaseActivity {

    private ActivityHubBinding binding;
    private String apelido;
    @Nullable
    private String ultimoModulo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHubBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        apelido = lerApelido();
        binding.tvSaudacao.setText(getString(R.string.saudacao, apelido));

        configurarPainel(binding.painelScanner, R.drawable.ic_scanner,
                R.string.scanner_titulo, R.string.scanner_desc,
                R.string.modulo_scanner, ScannerActivity.class);
        configurarPainel(binding.painelArena, R.drawable.ic_arena,
                R.string.arena_titulo, R.string.arena_desc,
                R.string.modulo_arena, ArenaActivity.class);
        configurarPainel(binding.painelCofre, R.drawable.ic_cofre,
                R.string.cofre_titulo, R.string.cofre_desc,
                R.string.modulo_cofre, CofreActivity.class);
        configurarPainel(binding.painelRandomizador, R.drawable.ic_shuffle,
                R.string.randomizador_titulo, R.string.randomizador_desc,
                R.string.modulo_randomizador, RandomizerActivity.class);

        binding.btnEncerrar.setOnClickListener(v -> encerrarSessao(ultimoModulo));

        animarEntrada();
    }

    private void configurarPainel(ItemPanelBinding painel,
                                  @DrawableRes int icone,
                                  @StringRes int titulo,
                                  @StringRes int descricao,
                                  @StringRes int modulo,
                                  Class<?> destino) {
        painel.ivPainel.setImageResource(icone);
        painel.ivPainel.setContentDescription(getString(titulo));
        painel.tvPainelTitulo.setText(titulo);
        painel.tvPainelDescricao.setText(descricao);
        painel.btnAbrir.setOnClickListener(v -> abrirDesafio(destino, getString(modulo)));
    }

    private void abrirDesafio(Class<?> destino, String nomeModulo) {
        ultimoModulo = nomeModulo;
        Intent intent = new Intent(this, destino);
        intent.putExtra(Extras.APELIDO, apelido);
        startActivity(intent);
    }

    private void animarEntrada() {
        View[] vistas = {
                binding.tvSaudacao,
                binding.tvHubSubtitulo,
                binding.painelScanner.getRoot(),
                binding.painelArena.getRoot(),
                binding.painelCofre.getRoot(),
                binding.painelRandomizador.getRoot(),
                binding.cardRegras,
                binding.btnEncerrar
        };
        for (int i = 0; i < vistas.length; i++) {
            Animation animacao = AnimationUtils.loadAnimation(this, R.anim.slide_fade_in);
            animacao.setStartOffset(i * 100L);
            vistas[i].startAnimation(animacao);
        }
    }
}
