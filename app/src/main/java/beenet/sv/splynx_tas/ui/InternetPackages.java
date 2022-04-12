package beenet.sv.splynx_tas.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import beenet.sv.splynx_tas.R;
import beenet.sv.splynx_tas.model.ServiceResponse;
import beenet.sv.splynx_tas.model.ServiceList;
import beenet.sv.splynx_tas.model.ServiceListAdapter;
import beenet.sv.splynx_tas.model.TokenResponse;
import beenet.sv.splynx_tas.service.RestApiService;
import beenet.sv.splynx_tas.service.RetrofitInstance;
import beenet.sv.splynx_tas.utils.Queries;
import beenet.sv.splynx_tas.utils.Tools;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InternetPackages extends AppCompatActivity {


    /**Instancia de clases*/
    Queries queries = new Queries();

    /**Variables para la creacion de los paquete de internet*/
    String header;//Guarda el token
    RecyclerView recyclerView;

    /**VARIABLES DEL TOKEN DEL ADMINISTRADOR*/
    String refresh_token;
    String access_token_expiration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internet_packages);

        getSupportActionBar().hide(); //ocultar el navbar

        /**Referenciando variables con el Formulario de recyclerView*/
        recyclerView = findViewById(R.id.listRecyclerView);//Le endicamos a la vista en donde se mostrar el resultado
        recyclerView.setHasFixedSize(true);//se utiliza para mantenga el mismo tamaño del recyclerView.
        recyclerView.setLayoutManager(new LinearLayoutManager(InternetPackages.this));//El efecto de esta configuración es que el componente baja verticalmente

        /**Funcion que envia datos a la API, retorna el valor del token , refresh_token , access_token_expiration*/
        queries.generate_token_admin(new Queries.resultToken() {
            @Override
            public void onSuccess(String token , String refresh_token_api, String access_token_expiration_api,String status) {

                /**Almacenar token del adminitrador y la renovacion del token*/
                header = token;
                refresh_token = refresh_token_api;
                access_token_expiration =  access_token_expiration_api;

                /**Funcion que muestra los servicios de internet atraves de la API, recibe como paramentro valor del token*/
                queries.GetAllServices(new Queries.ResultList() {
                    @Override
                    public void onSuccess(List<ServiceList> value) {
                        /**ServiceListAdapter es una clases donde se encarga mostrar la vista de los servicio y el manejo de evento del onclick*/
                        ServiceListAdapter serviceListAdapter = new ServiceListAdapter(value, InternetPackages.this, new ServiceListAdapter.OnItemClickListener() {
                            @Override
                            public Void onItemClick(ServiceList item) {
                                get_pack_info(item); /** Obtener el item seleccionado por el usuario  y envia los datos a la nueva vista.*/
                                return null;
                            }
                        });
                        recyclerView.setAdapter(serviceListAdapter); /** El contenido que trea la api se enviar a la vista recyclerview.*/
                    }
                },InternetPackages.this,header);
            }
        },InternetPackages.this);
    }

    /** Iniciarlizar la vista Payment y recibir el item para se enviado a la vista.*/
    public void get_pack_info(ServiceList item){
        // Los Intents permiten intercambiar datos entre aplicaciones o componentes de aplicaciones,
        Intent intent = new Intent(this,Payment.class);
        intent.putExtra("itemPackage", item);//ITEM DEL SERVICIO SELECCIONADO  POR EL USUARIO
        intent.putExtra("token",header);
        intent.putExtra("refreshToken" , refresh_token);
        intent.putExtra("tokenExpiration" , access_token_expiration);
        startActivity(intent);//Inicia la actividad de Payment
    }

    /** Evento al presionar el boton de retroceder de los telefonos o tabletas*/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        boolean handled = false;
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                handled = true;
                finish();
                break;
        }
        return handled || super.onKeyDown(keyCode, event);
    }

}