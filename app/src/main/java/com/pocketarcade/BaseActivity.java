package com.pocketarcade;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    protected static final String LOG_TAG = "PocketArcade";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, getClass().getSimpleName() + " -> onCreate");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, getClass().getSimpleName() + " -> onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(LOG_TAG, getClass().getSimpleName() + " -> onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, getClass().getSimpleName() + " -> onDestroy");
    }

    protected String lerApelido() {
        String apelido = getIntent().getStringExtra(Extras.APELIDO);
        if (apelido == null || apelido.trim().isEmpty()) {
            return getString(R.string.apelido_padrao);
        }
        return apelido.trim();
    }

    protected void encerrarSessao(@Nullable String ultimoModulo) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (ultimoModulo != null) {
            intent.putExtra(Extras.ULTIMO_MODULO, ultimoModulo);
        }
        startActivity(intent);
    }

    protected void tocar(@Nullable MediaPlayer player) {
        if (player == null) {
            return;
        }
        player.seekTo(0);
        player.start();
    }

    protected void pausar(MediaPlayer... players) {
        for (MediaPlayer player : players) {
            if (player != null && player.isPlaying()) {
                player.pause();
            }
        }
    }

    protected void liberar(MediaPlayer... players) {
        for (MediaPlayer player : players) {
            if (player != null) {
                player.release();
            }
        }
    }
}
