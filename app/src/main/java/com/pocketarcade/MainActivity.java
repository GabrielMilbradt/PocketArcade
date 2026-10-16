package com.pocketarcade;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pocketarcade.databinding.ActivityMainBinding;

/** Tela 1 */
public class MainActivity extends BaseActivity {

    private static final int TAMANHO_MINIMO_APELIDO = 3;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnEntrar.setOnClickListener(v -> tentarEntrar());
    }

    private void tentarEntrar() {
        String apelido = binding.etApelido.getText().toString().trim();

        if (apelido.length() < TAMANHO_MINIMO_APELIDO) {
            binding.etApelido.setError(getString(R.string.erro_apelido));
            binding.etApelido.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
            return;
        }

        binding.etApelido.setError(null);
        animarBotaoEEntrar(apelido);
    }

    private void animarBotaoEEntrar(String apelido) {
        binding.btnEntrar.setEnabled(false);
        binding.btnEntrar.animate()
                .scaleX(0.9f)
                .scaleY(0.9f)
                .setDuration(150)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(@NonNull Animator animation) {
                        binding.btnEntrar.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(150)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(@NonNull Animator animation) {
                                        binding.btnEntrar.animate().setListener(null);
                                        binding.btnEntrar.setEnabled(true);
                                        abrirCentral(apelido);
                                    }
                                })
                                .start();
                    }
                })
                .start();
    }

    private void abrirCentral(String apelido) {
        Intent intent = new Intent(this, HubActivity.class);
        intent.putExtra(Extras.APELIDO, apelido);
        startActivity(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        String modulo = intent.getStringExtra(Extras.ULTIMO_MODULO);
        if (modulo != null && !modulo.trim().isEmpty()) {
            binding.tvUltimoModulo.setText(getString(R.string.ultimo_modulo, modulo));
        } else {
            binding.tvUltimoModulo.setText(R.string.sessao_encerrada);
        }
        binding.tvUltimoModulo.setVisibility(View.VISIBLE);
        binding.etApelido.setText("");
        binding.etApelido.setError(null);
    }
}
