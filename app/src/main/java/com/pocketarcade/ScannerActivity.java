package com.pocketarcade;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.pocketarcade.databinding.ActivityScannerBinding;

import java.text.Normalizer;
import java.util.Locale;

/** Tela 3 */
public class ScannerActivity extends BaseActivity {

    private static final int TAMANHO_MINIMO = 3;
    private static final String VOGAIS = "aeiou";

    private ActivityScannerBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScannerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.tvJogador.setText(getString(R.string.jogador_label, lerApelido()));

        binding.btnAnalisar.setOnClickListener(v -> analisar());
        binding.btnLimpar.setOnClickListener(v -> limpar());
        binding.btnVoltar.setOnClickListener(v -> finish());
        binding.btnEncerrar.setOnClickListener(v -> encerrarSessao(getString(R.string.modulo_scanner)));
    }

    private void analisar() {
        String texto = binding.etMensagem.getText().toString().trim();

        if (texto.isEmpty()) {
            mostrarErro(R.string.erro_mensagem_vazia);
            return;
        }
        if (texto.length() < TAMANHO_MINIMO) {
            mostrarErro(R.string.erro_mensagem_curta);
            return;
        }
        binding.etMensagem.setError(null);

        int caracteresSemEspacos = texto.replaceAll("\\s+", "").length();
        int palavras = texto.split("\\s+").length;
        int vogais = contarVogais(texto);
        String invertido = new StringBuilder(texto).reverse().toString();
        boolean palindromo = ehPalindromo(texto);

        binding.tvResCaracteres.setText(String.valueOf(caracteresSemEspacos));
        binding.tvResPalavras.setText(String.valueOf(palavras));
        binding.tvResVogais.setText(String.valueOf(vogais));
        binding.tvResInvertido.setText(invertido);
        binding.tvResPalindromo.setText(palindromo ? R.string.sim : R.string.nao);

        mostrarResultadoAnimado();
    }

    private int contarVogais(String texto) {
        String normalizado = normalizar(texto);
        int total = 0;
        for (int i = 0; i < normalizado.length(); i++) {
            if (VOGAIS.indexOf(normalizado.charAt(i)) >= 0) {
                total++;
            }
        }
        return total;
    }

    private boolean ehPalindromo(String texto) {
        String limpo = normalizar(texto).replaceAll("[^a-z0-9]", "");
        if (limpo.isEmpty()) {
            return false;
        }
        return new StringBuilder(limpo).reverse().toString().equals(limpo);
    }

    private String normalizar(String texto) {
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT);
    }

    private void mostrarErro(@StringRes int mensagem) {
        binding.llResultado.setVisibility(View.GONE);
        binding.etMensagem.setError(getString(mensagem));
        binding.etMensagem.requestFocus();
        binding.etMensagem.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
    }

    private void mostrarResultadoAnimado() {
        binding.llResultado.setAlpha(0f);
        binding.llResultado.setTranslationY(60f);
        binding.llResultado.setVisibility(View.VISIBLE);
        binding.llResultado.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(450)
                .start();
    }

    private void limpar() {
        binding.etMensagem.setText("");
        binding.etMensagem.setError(null);
        binding.llResultado.setVisibility(View.GONE);
    }
}
