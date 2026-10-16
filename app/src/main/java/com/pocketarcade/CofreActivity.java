package com.pocketarcade;

import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import com.pocketarcade.databinding.ActivityCofreBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

/** Tela 5 */
public class CofreActivity extends BaseActivity {

    private static final int TAMANHO_CODIGO = 4;
    private static final int MAX_TENTATIVAS = 8;

    private ActivityCofreBinding binding;
    private final Random random = new Random();

    private String codigo;
    private int tentativas;
    private boolean partidaEncerrada;
    private long inicioMillis;

    private MediaPlayer somVitoria;
    private MediaPlayer somDerrota;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCofreBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        somVitoria = MediaPlayer.create(this, R.raw.sfx_vitoria);
        somDerrota = MediaPlayer.create(this, R.raw.sfx_derrota);

        binding.tvJogador.setText(getString(R.string.jogador_label, lerApelido()));

        binding.btnTentar.setOnClickListener(v -> tentar());
        binding.btnNovaPartida.setOnClickListener(v -> novaPartida());
        binding.btnVoltar.setOnClickListener(v -> finish());
        binding.btnEncerrar.setOnClickListener(v -> encerrarSessao(getString(R.string.modulo_cofre)));

        novaPartida();
    }

    private void novaPartida() {
        codigo = gerarCodigo();
        tentativas = 0;
        partidaEncerrada = false;
        inicioMillis = System.currentTimeMillis();

        binding.llHistorico.removeAllViews();
        binding.llFim.setVisibility(View.GONE);
        binding.etCodigo.setText("");
        binding.etCodigo.setError(null);
        binding.etCodigo.setEnabled(true);
        binding.btnTentar.setEnabled(true);
        atualizarTentativas();
    }

    private String gerarCodigo() {
        List<Integer> digitos = new ArrayList<>();
        for (int i = 0; i <= 9; i++) {
            digitos.add(i);
        }
        Collections.shuffle(digitos, random);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < TAMANHO_CODIGO; i++) {
            sb.append(digitos.get(i));
        }
        return sb.toString();
    }

    private void tentar() {
        if (partidaEncerrada) {
            return;
        }
        String palpite = binding.etCodigo.getText().toString().trim();

        if (palpite.isEmpty()) {
            mostrarErro(R.string.erro_codigo_vazio);
            return;
        }
        if (!palpite.matches("\\d{" + TAMANHO_CODIGO + "}")) {
            mostrarErro(R.string.erro_codigo_tamanho);
            return;
        }
        if (temDigitoRepetido(palpite)) {
            mostrarErro(R.string.erro_codigo_repetido);
            return;
        }

        binding.etCodigo.setError(null);
        tentativas++;

        int[] resumo = new int[2]; // [0] = certos, [1] = fora de lugar
        binding.llHistorico.addView(criarLinhaHistorico(palpite, resumo), 0);
        binding.etCodigo.setText("");
        atualizarTentativas();

        if (resumo[0] == TAMANHO_CODIGO) {
            encerrar(true);
        } else if (tentativas >= MAX_TENTATIVAS) {
            encerrar(false);
        }
    }

    private boolean temDigitoRepetido(String texto) {
        Set<Character> vistos = new HashSet<>();
        for (char c : texto.toCharArray()) {
            if (!vistos.add(c)) {
                return true;
            }
        }
        return false;
    }

    private TextView criarLinhaHistorico(String palpite, int[] resumo) {
        int verde = ContextCompat.getColor(this, R.color.pista_verde);
        int amarelo = ContextCompat.getColor(this, R.color.pista_amarelo);
        int cinza = ContextCompat.getColor(this, R.color.pista_cinza);

        SpannableStringBuilder texto = new SpannableStringBuilder(
                String.format(Locale.getDefault(), "#%d  ", tentativas));

        for (int i = 0; i < palpite.length(); i++) {
            char digito = palpite.charAt(i);
            int corFundo;
            int corTexto = Color.BLACK;
            if (digito == codigo.charAt(i)) {
                corFundo = verde;
                resumo[0]++;
            } else if (codigo.indexOf(digito) >= 0) {
                corFundo = amarelo;
                resumo[1]++;
            } else {
                corFundo = cinza;
                corTexto = Color.WHITE;
            }
            int inicio = texto.length();
            texto.append(' ').append(digito).append(' ');
            texto.setSpan(new BackgroundColorSpan(corFundo), inicio, texto.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            texto.setSpan(new ForegroundColorSpan(corTexto), inicio, texto.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            texto.append(' ');
        }
        texto.append(getString(R.string.pista_resumo, resumo[0], resumo[1]));

        TextView linha = new TextView(this);
        linha.setText(texto);
        linha.setTypeface(Typeface.MONOSPACE);
        linha.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        linha.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        linha.setPadding(0, 8, 0, 8);
        return linha;
    }

    private void encerrar(boolean vitoria) {
        partidaEncerrada = true;
        long decorridoMillis = System.currentTimeMillis() - inicioMillis;
        String tempo = getString(R.string.tempo_segundos, decorridoMillis / 1000.0);

        binding.etCodigo.setEnabled(false);
        binding.btnTentar.setEnabled(false);

        binding.tvFimTitulo.setText(getString(
                vitoria ? R.string.fim_vitoria : R.string.fim_derrota, codigo));
        binding.tvFimTitulo.setTextColor(ContextCompat.getColor(this,
                vitoria ? R.color.pista_verde : R.color.erro));
        binding.tvFimDetalhes.setText(getString(R.string.fim_detalhes, tentativas, tempo));
        binding.llFim.setVisibility(View.VISIBLE);

        if (vitoria) {
            binding.llFim.startAnimation(AnimationUtils.loadAnimation(this, R.anim.pop));
            binding.tvFimTitulo.setScaleX(0.8f);
            binding.tvFimTitulo.setScaleY(0.8f);
            binding.tvFimTitulo.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(500)
                    .setInterpolator(new OvershootInterpolator())
                    .start();
            tocar(somVitoria);
        } else {
            binding.llFim.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
            tocar(somDerrota);
        }
    }

    private void atualizarTentativas() {
        binding.tvTentativas.setText(getString(R.string.tentativas_status, tentativas, MAX_TENTATIVAS));
    }

    private void mostrarErro(@StringRes int mensagem) {
        binding.etCodigo.setError(getString(mensagem));
        binding.etCodigo.requestFocus();
        binding.etCodigo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
    }

    @Override
    protected void onStop() {
        super.onStop();
        pausar(somVitoria, somDerrota);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        liberar(somVitoria, somDerrota);
        somVitoria = null;
        somDerrota = null;
    }
}
