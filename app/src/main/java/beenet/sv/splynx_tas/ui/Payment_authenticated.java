package beenet.sv.splynx_tas.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;

import beenet.sv.splynx_tas.R;
import beenet.sv.splynx_tas.model.CustomerResponse;
import beenet.sv.splynx_tas.model.ServiceList;
import beenet.sv.splynx_tas.model.TariffResponse;
import beenet.sv.splynx_tas.service.RestApiService;
import beenet.sv.splynx_tas.service.RetrofitInstance;
import beenet.sv.splynx_tas.utils.Queries;
import beenet.sv.splynx_tas.utils.Tools;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Payment_authenticated extends AppCompatActivity {

    /******************************************
     * Variables de la vistas XML
     * ****************************************/

    /**CAMPOS DEL SERVICIO DE INTERNET*/
    EditText  title,price, card_number ,card_month , card_year , card_cvc;

    /** BOTON PARA REALIZAR EL PROCESO DE SPLYNX Y BANCO */
    Button btnPayment;

    /**VARIABLES DE COMENTARIO PARA SER UTILIZADO EN LA RESPUESTA DEL PROGRAMADOR*/
    private String ServerResponse = "RESPUESTA DEL SERVIDOR => ";
    private String ServerResponse2 = "RESPUESTA DEL TRY catch =>";
    private String Comment  = "Vuelva intentar o más tarde";

    /***************************************************
     *INSTANCIAS DE CLASES
     *************************************************/
    Tools tools = new Tools();// DIFRENTES METODOS PARA SER EJECUTADO EN EL SISTEMA
    Queries queries = new Queries();//CONSULTAS DE SPLYNX
    Date date = new Date(); //OBTENER FECHA
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd"); // FORMATO DE LA FECHA
    SweetAlertDialog preloader;//ALERTAS PERSONALIZADAS


    /****************************************************
     * VARIABLES GLOBALES
     **************************************************/
    //VARIABLES DEL CLIENTE
    String  id_customer  , status_Customer;
    int id_user_customer ; //Guardar el id del usuario de tipo entero para ser ocupado planes de internet y pago

    //VARIABLES DEL SERVICIO
    int selected_pack_id;
    ServiceList itemPackage; // Guardar el valor proveniente de Home
    String Current_date , ActiveServiceId;

    /**VARIABLES DEL TOKEN DEL ADMINISTRADOR*/
    String token;
    String refresh_token;
    String access_token_expiration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_authenticated);

        /**OCULTAR EL MENU*/
        getSupportActionBar().hide();

        /**Recuperando valor enviado proveniente de InternetPackages*/
        itemPackage = (ServiceList) getIntent().getSerializableExtra("itemPackage");

        /**Referenciando variables con el EditText*/
        title = findViewById(R.id.txt_name_package2);
        price = findViewById(R.id.txt_price_internet2);

        /**Mostramos la informacion en los campos  de EditText*/
        title.setText(itemPackage.getService_Title());
        price.setText(itemPackage.getService_price());

        /**Recuperando valor enviado proveniente de home del usuario autenticado*/

        //OBTENEMOS EL TOKEN DE ADMIN
        token = getIntent().getStringExtra("tokenAdmin");
        refresh_token = getIntent().getStringExtra("refresh_token");
        access_token_expiration = getIntent().getStringExtra("access_token_expiration");
        Log.d("TAG1", "TOKEN RECIBIDO ADMIN=> "+ token);
        Log.d("TAG1","REFRESH TOKEN RECIBIODO =>" + refresh_token);
        Log.d("TAG1" , "ACCESS TOKEN EXPIRATION =>" + access_token_expiration);

        //OBTENEMOS EL ID DEL CLIENTE
        id_customer =  getIntent().getStringExtra("id");
        id_user_customer = Integer.parseInt(id_customer);
        Log.d("TAG1", "ID CUSTOMER PAYMENT=> " + id_customer);
        //OBTENEMOS EL STATUS DEL CLIENTE
        status_Customer = getIntent().getStringExtra("statusCustomer");
        Log.d("TAG1", "STATUS CUSTOMER=> " + status_Customer);

        //OBTENEMOS EL ID DEL SERVICIO SELECCIONADO POR EL USUARIO
        selected_pack_id = Integer.parseInt(itemPackage.getService_Id());
        Log.d("TAG1", "SERVICIO INTERNET SELECIONADO=> "+ selected_pack_id);

        //OBTENEMOS EL ID DEL SERVICIO ACTIVO
        ActiveServiceId =  getIntent().getStringExtra("idService");
        Log.d("TAG1" , "ID DEL SERVICIO ACTIVO =>" + ActiveServiceId);

        //Referenciando variables con el Formulario
        //Campos de la tarjeta.
        card_number = findViewById(R.id.txt_card_number);
        card_month = findViewById(R.id.txt_card_month);
        card_year = findViewById(R.id.txt_card_year);
        card_cvc = findViewById(R.id.txt_card_cvc);

        //Variable obtener la fecha actual
        Current_date  = df.format(date.getTime());

        //Boton de pago
        btnPayment = findViewById(R.id.btnPayment);
        //Evento ONCLICK DEL BOTON
        btnPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               validations_inputs();
            }
        });

    }

     //VALIDAR LOS CAMPOS DEL FORMULARIO DE PAGO
      void validations_inputs() {
        if (!tools.ValidateFields(card_number) && !tools.ValidateFields(card_month) && !tools.ValidateFields(card_year) && !tools.ValidateFields(card_cvc)) {
            if (!tools.ValidateCharacters(card_number, 16) && !tools.ValidateCharacters(card_month, 2) && !tools.ValidateCharacters(card_year, 2) && !tools.ValidateCharacters(card_cvc, 3)) {
                if (!tools.ValidateCharacters2(card_number, 16) && !tools.ValidateCharacters2(card_month, 2) && !tools.ValidateCharacters2(card_year, 2) && !tools.ValidateCharacters2(card_cvc, 3)) {

                    //Mensaje de confirmacion
                    preloader = new SweetAlertDialog(Payment_authenticated.this, SweetAlertDialog.WARNING_TYPE);
                    preloader.setTitleText("¿Realizar pago?");
                    preloader.setContentText("Esta seguro de realizar la compra");
                    preloader.setConfirmText("Si");
                    preloader.setCancelable(false);
                    preloader.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {

                            /**OCULTAR LA ALERTA DE CONFIRMACION*/
                            sDialog.dismissWithAnimation();

                            //Si la transaccion fue exitosa realizara los procceso correspodiente de forma asíncrona
                            //Caso lo contrario se le notificara con la respuesta del banco o falla del servidor
                            queries.payment_banco_agricola(new Queries.Result() {
                                @Override
                                public void onSuccess(String value) {

                                    if(value == "Transacción exitosa"){

                                        /**Si el estado del cliente esta bloqueado se cambiar el estado a activo*/
                                        if(status_Customer.equals("blocked")){ UpdateStatusCustomer(); }

                                        /**Realizar el pago en el SPLYNX*/
                                        queries.payment_splynx(new Queries.Result() {
                                            @Override
                                            public void onSuccess(String value) {
                                                ChangeTariffCustomer(); // Agregar el servicio al cliente
                                            }
                                        }, Payment_authenticated.this, token, id_user_customer, Current_date, itemPackage.getService_price());

                                    }
                                }
                            },Payment_authenticated.this,card_number.getText().toString().trim(),card_cvc.getText().toString().trim(), card_year.getText().toString().trim(),card_month.getText().toString().trim(), itemPackage.getService_price().trim());

                        }
                    }).setCancelButton("No", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation(); //OCULTAR LA ALERTA
                        }
                    }).show();

                }
            }
        }
    }


    void refreshToken(){

            queries.refresh_token_admin(new Queries.resultToken() {
                @Override
                public void onSuccess(String token_api, String refresh_token_api, String access_token_expiration_api, String status) {
                    token = token_api;
                    refresh_token = refresh_token_api;
                    access_token_expiration = access_token_expiration_api;
                    Log.d("TAG1" , "TOKEN RENOVADO");
                }
            } , Payment_authenticated.this, refresh_token , Integer.parseInt(access_token_expiration));

    }

    /**
     * Cambiar el estado del cliente
     * */
    void UpdateStatusCustomer(){
           try {

               //Importamos la clases de la api de splynx
               RestApiService apiService = RetrofitInstance.getApiService();

               //Enviamos los datos a la api de tipo token admin y el estado en ACTIVO
               Call<CustomerResponse> call = apiService.UpdateStatusCustomer(token,id_customer,"active");

               call.enqueue(new Callback<CustomerResponse>() {
                   @Override
                   public void onResponse(Call<CustomerResponse> call, Response<CustomerResponse> response) {
                    Log.d("TAG1" , "ESTADO CAMBIADO A => ACTIVO");
                   }

                   @Override
                   public void onFailure(Call<CustomerResponse> call, Throwable t) {
                       Log.d("TAG1", ServerResponse + " => UpdateStatusCustomer =>" + t.getMessage());
                       queries.Loader(Payment_authenticated.this , "Error", Comment);
                   }
               });

           }catch (Exception e) {
               Log.d("TAG1", ServerResponse2 + " => UpdateStatusCustomer =>" + e.getMessage());
               queries.Loader(Payment_authenticated.this , "Error", Comment);
           }
    }

    void ChangeTariffCustomer(){

        try {

            //Importamos la clases de la api de splynx
            RestApiService apiService = RetrofitInstance.getApiService();

            //Enviamos los datos a la api de tipo token admin y el estado en ACTIVO
            Call<TariffResponse> call = apiService.ChangeTariff(token,ActiveServiceId,selected_pack_id,Current_date);

            call.enqueue(new Callback<TariffResponse>() {
                @Override
                public void onResponse(Call<TariffResponse> call, Response<TariffResponse> response) {
                    Log.d("TAG1" , "TARIFA AGREGADA");
                    alert();
                }

                @Override
                public void onFailure(Call<TariffResponse> call, Throwable t) {
                    Log.d("TAG1", ServerResponse + " => ChangeTariffCustomer =>" + t.getMessage());
                    queries.Loader(Payment_authenticated.this , "Error", Comment);
                }
            });

        }catch (Exception e) {
            Log.d("TAG1", ServerResponse2 + " => ChangeTariffCustomer =>" + e.getMessage());
            queries.Loader(Payment_authenticated.this , "Error", Comment);
        }
    }



     void alert(){

         runOnUiThread(new Runnable() {
             @Override
             public void run() {

                 SweetAlertDialog alert = new SweetAlertDialog(Payment_authenticated.this, SweetAlertDialog.PROGRESS_TYPE);
                 alert.dismiss(); //DETENER LOADER

                 SweetAlertDialog pDialog =  new SweetAlertDialog(Payment_authenticated.this, SweetAlertDialog.SUCCESS_TYPE);
                 pDialog.setTitleText("Transacción exitosa");
                 pDialog.setContentText("Su pago se ha realizado correctamente");
                 pDialog .setConfirmText("FINALIZAR");
                 pDialog.setCancelable(false);
                 pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                     @Override
                     public void onClick(SweetAlertDialog sDialog) {

                         SweetAlertDialog pDialog =  new SweetAlertDialog(Payment_authenticated.this, SweetAlertDialog.SUCCESS_TYPE);
                         pDialog.setTitleText("Servicio Activado");
                         pDialog.setContentText(itemPackage.getService_Title());
                         pDialog .setConfirmText("OK");
                         pDialog.setCancelable(false);
                         pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                             @Override
                             public void onClick(SweetAlertDialog sDialog) {
                                 finish();
                             }
                         }).show();

                     }
                 }).show();
             }
         });

     }

}