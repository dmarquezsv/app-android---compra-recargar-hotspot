package beenet.sv.splynx_tas.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

import beenet.sv.splynx_tas.R;

public class SplashScreen extends AppCompatActivity {

    ImageView loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        loader = (ImageView) findViewById(R.id.loaderStart);
        rotarImagen(loader);
        //******************************************************************************************
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                //Un intent sirve para invocar componentes en este caso a la vista.
                Intent intent = new Intent(SplashScreen.this,Login.class);
                startActivity(intent);//se utiliza para iniciar una nueva actividad
                finish(); //Finaliza vista
            }
        };

        Timer time = new Timer(); //Controlar el tiempo
        time.schedule(task,5000); //Tiempo de espera para abrir la vista
        //******
    }

    //Funcionalida para pantalla del movil
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    //Adaptar la pantalla de los telefonos y tabletas
    //Tambien ocultar la barra de estado y los botones del celular
    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        );
    }

    private void rotarImagen(View view){
        RotateAnimation animation = new RotateAnimation(0, 360,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(2000);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.REVERSE);
        view.startAnimation(animation);
    }
}