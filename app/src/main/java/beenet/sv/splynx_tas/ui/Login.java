package beenet.sv.splynx_tas.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.muddzdev.styleabletoast.StyleableToast;

import java.util.List;

import beenet.sv.splynx_tas.R;
import beenet.sv.splynx_tas.model.CustomerResponse;
import beenet.sv.splynx_tas.model.TokenResponse;
import beenet.sv.splynx_tas.service.RestApiService;
import beenet.sv.splynx_tas.service.RetrofitInstance;
import beenet.sv.splynx_tas.utils.Tools;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {

    /**
     * Variables de la vistas XML
     * */
    TextView btnComprar; EditText user , passoword;  Button btnLogin;

    /**
     * Variables Globales
     * */
    //VARIABLES PARA EL CLIENTE
    String id, nameUser ,statusCustomer, header;

    /**
     * Instancia de clases
     */
    Gson gson = new Gson(); //Convertidor JSON
    Tools tools = new Tools(); //Herammientas para el desarrollo
    List<CustomerResponse> getCustomer; //Guardar en una lista la respuesta de la api

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        //Referenciando variables con el Formulario
        user = findViewById(R.id.txt_email_login);
        passoword = findViewById(R.id.txt_password_login);

        //Evento de onclick del boton del login
        btnLogin = findViewById(R.id.btnLogin2);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginResponse();
            }
        });

        //Evento de onclick para la vista de paquetes de internet
        btnComprar = findViewById(R.id.btn_ViewInternetPackages);
        btnComprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewInternetPackages();
            }
        });

    }

    /**
     * login
     * */
    public void loginResponse(){

        if(!tools.ValidateFields(user) && !tools.ValidateFields(passoword)){

            try {

                //Elemento de carga
                tools.showDialog(0,Login.this,"" , "");

                //Instanciamos de la clases RestApiSertvice y RetrofitInstance para la conexion de la api
                RestApiService apiService = RetrofitInstance.getApiService();
                //Consulta para validar inicio de sesion
                Call<TokenResponse> call = apiService.getToken("customer" , user.getText().toString(),passoword.getText().toString());

                call.enqueue(new Callback<TokenResponse>() {
                    @Override
                    public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {

                        if (response.isSuccessful() && response.code() == 201){
                            Log.d("TAG1", "ESTADO-> SESION INICIADA");
                            TokenResponse tokenResponse = response.body(); //Obtiene la respuesta
                            header = "Splynx-EA (access_token="+tokenResponse.getAccess_token()+")";//Formato del header para splnyx
                            getCustomer_ID(header, user.getText().toString());//Obtener el ID del Usuario
                        }
                        else{
                            Loader("Error" , "Credenciales invalidas");
                            Log.d("TAG1", "Credenciales invalidas");
                        }
                    }

                    @Override
                    public void onFailure(Call<TokenResponse> call, Throwable t) {
                        Loader("Error" , "Vuelva intentar o más tarde");
                        Log.d("TAG1", "Fallo DE LA RESPUESTA DEL SPLYNX LOGIN ->" + t.getMessage());
                    }
                });

            }
            catch (Exception e) {
                Loader("Error" , "Vuelva intentar o más tarde");
                Log.d("TAG1", "Fallo loginResponse->" + e.getMessage());
            }

        }
    }


    /**
     * Obtener el id del cliente y nombre
     * */
    public void getCustomer_ID(String header , String email){

        try {

            //Instanciamos de la clases RestApiSertvice y RetrofitInstance para la conexion de la api
            RestApiService apiService = RetrofitInstance.getApiService();
            //Consulta para extraer el id y nombre del cliente
            Call<List<CustomerResponse>> call = apiService.getCustomerID(header,email);

            call.enqueue(new Callback<List<CustomerResponse>>() {
                @Override
                public void onResponse(Call<List<CustomerResponse>> call, Response<List<CustomerResponse>> response) {

                    getCustomer = response.body(); //RESPUESTA DE LA API => INFORMACION DEL CLIENTE

                    for (int i = 0; i < getCustomer.size(); i++) {
                        id = gson.toJson(response.body().get(i).getId());
                        nameUser = gson.toJson(response.body().get(i).getName());
                        statusCustomer = gson.toJson(response.body().get(i).getStatus());
                    }

                    //Remplazamos las comillas
                    id= id.replace("\"", "");
                    nameUser= nameUser.replace("\"", "");
                    statusCustomer = statusCustomer.replace("\"", "");
                    Loader("IniciarSesion","");
                }

                @Override
                public void onFailure(Call<List<CustomerResponse>> call, Throwable t) {
                    Loader("Error" , "Vuelva intentar o más tarde");
                    Log.d("TAG1", "Fallo ->" + t.getMessage());
                }
            });

        }catch (Exception e){
            Loader("Error" , "Vuelva intentar o más tarde");
            Log.d("TAG1", "Fallo getCustomer_ID->" + e.getMessage());
        }


    }


    /**
     * VISTA DE LOS PAQUETES DE INTERNET
     * */
    public  void  ViewInternetPackages(){
        Intent intent = new Intent(this,InternetPackages.class);//Vista de los paquetes de internet
        startActivity(intent);//Inicia la activada de la vista
    }

    /**
     * Alerta inicio de sesion
     * */
    private void Loader(String title, String text){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                    //DETENER LOADER
                     tools.showDialog(1,Login.this,"" , "");
                    if(title=="IniciarSesion"){
                        //Mensaje de Exito de inicio de Sesion
                        new StyleableToast.Builder(getApplicationContext()).text("SESION INICIADA").textSize(16).textColor(Color.WHITE).iconStart(R.drawable.outline_verified_user_24).backgroundColor(Color.rgb(56,53,51)).show();
                        finish();
                        //Llamada siguiente ventana Home.class y envia los paramatros  a HomeActivity
                        Intent intent = new Intent (getApplicationContext(), Home.class);
                        Bundle myBundle = new Bundle();
                        myBundle.putString("tokenCustomer",header); //
                        myBundle.putString("statusCustomer",statusCustomer);
                        myBundle.putString("id",id); //IDCustomer
                        myBundle.putString("name",nameUser); //Nombre Usuario
                        myBundle.putString("email",user.getText().toString());
                        intent.putExtras(myBundle);//Guardamos los recuros
                        startActivityForResult(intent, 0);//Inicia la activada de la vista
                    }
                    else if(title=="Error") {
                        tools.showDialog(2,Login.this,title , text);  // CARGANDO MENSAJE DE ERROR
                    }
            }
        });
    }


    /**
     * EVENTO DE RETROCESO
     * */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        boolean handled = false;
        switch (keyCode) {
            //Evento cuando retrocede
            case KeyEvent.KEYCODE_BACK:
                handled = true;
                finish();
                break;
        }
        return handled || super.onKeyDown(keyCode, event);
    }


}