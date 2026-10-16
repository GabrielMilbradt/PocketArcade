package com.pocketarcade;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.pocketarcade.databinding.ActivityArenaBinding;

import java.util.Random;

/** Tela 4 */
public class ArenaActivity extends BaseActivity {

    private static final int PEDRA = 0;
    private static final int PAPEL = 1;
    private static final int TESOURA = 2;
    private static final int VITORIAS_PARA_GANHAR = 2;

    @DrawableRes
    private static final int[] ICONES = {R.drawable.ic_pedra, R.drawable.ic_papel, R.drawable.ic_tesoura};
    @StringRes
    private static final int[] NOMES = {R.string.pedra, R.string.papel, R.string.tesoura};

    private ActivityArenaBinding binding;
    private final Random random = new Random();

    private int placarJogador;
    private int placarDispositivo;
    private boolean partidaEncerrada;

    private MediaPlayer somVitoria;
    private MediaPlayer somDerrota;
    private MediaPlayer somEmpate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityArenaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        somVitoria = MediaPlayer.create(this, R.raw.sfx_vitoria);
        somDerrota = MediaPlayer.create(this, R.raw.sfx_derrota);
        somEmpate = MediaPlayer.create(this, R.raw.sfx_empate);

        binding.tvJogador.setText(getString(R.string.jogador_label, lerApelido()));

        binding.btnPedra.setOnClickListener(v -> jogar(PEDRA));
        binding.btnPapel.setOnClickListener(v -> jogar(PAPEL));
        binding.btnTesoura.setOnClickListener(v -> jogar(TESOURA));
        binding.btnNovaPartida.setOnClickListener(v -> novaPartida());
        binding.btnVoltar.setOnClickListener(v -> finish());
        binding.btnEncerrar.setOnClickListener(v -> encerrarSessao(getString(R.string.modulo_arena)));

        atualizarPlacar();
    }

    private void jogar(int jogador) {
        if (partidaEncerrada) {
            return;
        }
        int dispositivo = random.nextInt(3);

        binding.ivJogador.setImageResource(ICONES[jogador]);
        binding.ivJogador.setContentDescription(getString(NOMES[jogador]));
        binding.ivDispositivo.setImageResource(ICONES[dispositivo]);
        binding.ivDispositivo.setContentDescription(getString(NOMES[dispositivo]));

        int resultado = (jogador - dispositivo + 3) % 3;
        @StringRes int mensagem;
        MediaPlayer som;

        if (resultado == 0) {
            mensagem = R.string.arena_empate;
            som = somEmpate;
        } else if (resultado == 1) {
            placarJogador++;
            mensagem = R.string.arena_ganhou_rodada;
            som = somVitoria;
        } else {
            placarDispositivo++;
            mensagem = R.string.arena_perdeu_rodada;
            som = somDerrota;
        }

        if (placarJogador == VITORIAS_PARA_GANHAR) {
            partidaEncerrada = true;
            mensagem = R.string.arena_vitoria_final;
        } else if (placarDispositivo == VITORIAS_PARA_GANHAR) {
            partidaEncerrada = true;
            mensagem = R.string.arena_derrota_final;
        }

        atualizarPlacar();
        binding.tvResultado.setText(mensagem);
        animarRodada();
        tocar(som);

        if (partidaEncerrada) {
            habilitarJogadas(false);
        }
    }

    private void animarRodada() {
        binding.ivJogador.setScaleX(0.2f);
        binding.ivJogador.setScaleY(0.2f);
        binding.ivJogador.setAlpha(0f);
        binding.ivJogador.setRotation(-40f);
        binding.ivJogador.animate()
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .rotation(0f)
                .setStartDelay(0)
                .setDuration(500)
                .setInterpolator(new OvershootInterpolator())
                .start();

        binding.ivDispositivo.setTranslationY(120f);
        binding.ivDispositivo.setAlpha(0f);
        binding.ivDispositivo.setRotation(40f);
        binding.ivDispositivo.animate()
                .translationY(0f)
                .alpha(1f)
                .rotation(0f)
                .setStartDelay(150)
                .setDuration(500)
                .setInterpolator(new OvershootInterpolator())
                .start();

        binding.tvResultado.setAlpha(0f);
        binding.tvResultado.setScaleX(0.7f);
        binding.tvResultado.setScaleY(0.7f);
        binding.tvResultado.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setStartDelay(300)
                .setDuration(400)
                .start();
    }

    private void novaPartida() {
        placarJogador = 0;
        placarDispositivo = 0;
        partidaEncerrada = false;

        binding.ivJogador.setImageResource(R.drawable.ic_interrogacao);
        binding.ivJogador.setContentDescription(getString(R.string.cd_escolha_jogador));
        binding.ivDispositivo.setImageResource(R.drawable.ic_interrogacao);
        binding.ivDispositivo.setContentDescription(getString(R.string.cd_escolha_dispositivo));
        binding.tvResultado.setText(R.string.arena_inicial);

        habilitarJogadas(true);
        atualizarPlacar();
    }

    private void atualizarPlacar() {
        binding.tvPlacar.setText(getString(R.string.placar, placarJogador, placarDispositivo));
    }

    private void habilitarJogadas(boolean habilitar) {
        float alpha = habilitar ? 1f : 0.4f;
        View[] botoes = {binding.btnPedra, binding.btnPapel, binding.btnTesoura};
        for (View botao : botoes) {
            botao.setEnabled(habilitar);
            botao.setAlpha(alpha);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        pausar(somVitoria, somDerrota, somEmpate);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        liberar(somVitoria, somDerrota, somEmpate);
        somVitoria = null;
        somDerrota = null;
        somEmpate = null;
    }
}
