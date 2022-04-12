package beenet.sv.splynx_tas.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;

import beenet.sv.splynx_tas.R;
import beenet.sv.splynx_tas.model.CustomerResponse;
import beenet.sv.splynx_tas.model.ServiceList;
import beenet.sv.splynx_tas.service.RestApiService;
import beenet.sv.splynx_tas.service.RetrofitInstance;
import beenet.sv.splynx_tas.utils.JavaMailAPI;
import beenet.sv.splynx_tas.utils.Queries;
import beenet.sv.splynx_tas.utils.Tools;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Payment extends AppCompatActivity {

    /******************************************
     * Variables de la vistas XML
     * ****************************************/

    /** CAMPOS DE LA TARJETA DE CREDITO O DEBITO */
    private EditText  title,price, card_number ,card_month , card_year , card_cvc;

    /** CAMPOS DEL CLIENTE */
    private EditText nameCustomer, customerEmail , customerTelephone, customerCountry, customerMunicipality;

    /** BOTON PARA REALIZAR EL PROCESO DE SPLYNX Y BANCO */
    private Button btnPayment;

    /**VARIABLES DE COMENTARIO PARA SER UTILIZADO EN LA RESPUESTA DEL PROGRAMADOR*/
    private String ServerResponse = "RESPUESTA DEL SERVIDOR => ";
    private String ServerResponse2 = "RESPUESTA DEL TRY catch =>";
    private String Comment  = "Vuelva intentar o más tarde";

    /***************************************************
     * INSTANCIAS DE CLASES
     * * ***********************************************/
    private Queries queries = new Queries(); // CONSULTAS DE SPLYNX
    private Gson gson = new Gson(); //CONVERTIDOR A JSON
    private Date date = new Date(); //OBTENER FECHA
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd"); // FORMATO DE LA FECHA
    private Tools tools = new Tools();// DIFRENTES METODOS PARA SER EJECUTADO EN EL SISTEMA
    private SweetAlertDialog preloader;//ALERTAS PERSONALIZADAS
    private ServiceList itemPackage; // Guardar el valor proveniente de InternetPackages

    /****************************************************
     * VARIABLES GLOBALES
     * * ************************************************/
     String token , id_customer ="633" ,  id_package ,Current_date;
     int id_user_customer =633; //Guardar el id del usuario de tipo entero para ser ocupado planes de internet y pago
     int ID_Servicio_Internet; //Guarda el id del servicio de internet de tipo entero ya que api lo recibe asi.

    /**VARIABLES DEL TOKEN DE TIPO DE ADMINISTRADOR*/
     String refresh_token;
     String access_token_expiration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        /**OCULTAR EL MENU*/
        getSupportActionBar().hide();

        /**Referenciando variables con el EditText*/

        //Campos de la tarjeta.
        card_number = findViewById(R.id.txt_card_number);
        card_month = findViewById(R.id.txt_card_month);
        card_year = findViewById(R.id.txt_card_year);
        card_cvc = findViewById(R.id.txt_card_cvc);

        //Campos del cliente
        nameCustomer = findViewById(R.id.txtName);
        customerEmail = findViewById(R.id.txt_Email);
        customerTelephone = findViewById(R.id.txt_telephone);
        customerCountry = findViewById(R.id.txt_city);
        customerMunicipality = findViewById(R.id.txt_municipality);

        /**Recuperando valor enviado proveniente de InternetPackages*/
        itemPackage = (ServiceList) getIntent().getSerializableExtra("itemPackage");
        //Referenciando variables con el EditText
        title = findViewById(R.id.txt_name_package);
        price = findViewById(R.id.txt_price_internet);

        //Mostramos la informacion en los EditText correspodiente
        title.setText(itemPackage.getService_Title());
        price.setText(itemPackage.getService_price());

        //Guardamos el ID del servicio de internet
        ID_Servicio_Internet = Integer.parseInt(itemPackage.getService_Id());
        Log.d("TAG1", "SERVICIO INTERNET SELECIONADO ->"+ ID_Servicio_Internet);

        //Fecha actual con el formato  yyyy-MM-dd
         Current_date  = df.format(date.getTime());

        /**Recuperando valor del token proveniente de InternetPackages*/
        token = getIntent().getStringExtra("token");
        refresh_token = getIntent().getStringExtra("refreshToken");
        access_token_expiration = getIntent().getStringExtra("tokenExpiration");
        Log.d("TAG1" ,"TOKEN RECIBIDO =>" + token);
        Log.d("TAG1","REFRESH TOKEN RECIBIODO =>" + refresh_token);
        Log.d("TAG1" , "ACCESS TOKEN EXPIRATION =>" + access_token_expiration);

        /**Referenciando variables con el button*/
        btnPayment = findViewById(R.id.btnPayment);

        /**Evento de OnClick par el boton de pago*/
        btnPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { validations_inputs();}
        });

    }

    /**VALIDAR LOS CAMPOS DEL FORMULARIO DEL CLIENTE*/
     void validations_inputs(){
        if(!tools.ValidateFields(nameCustomer) && !tools.ValidateFields(customerEmail) && !tools.ValidateFields(customerTelephone) && !tools.ValidateFields(customerCountry) && !tools.ValidateFields(customerMunicipality)){
            if(!tools.ValidateFields(card_number) && !tools.ValidateFields(card_month) && !tools.ValidateFields(card_year) && !tools.ValidateFields(card_cvc)){
                if(!tools.ValidateCharacters(card_number,16) && !tools.ValidateCharacters(card_month,2) && !tools.ValidateCharacters(card_year,2) && !tools.ValidateCharacters(card_cvc,3)){
                    if(!tools.ValidateCharacters2(card_number,16) && !tools.ValidateCharacters2(card_month,2) && !tools.ValidateCharacters2(card_year,2) && !tools.ValidateCharacters2(card_cvc,3)){

                        //MENSAJE DE CONFIRMACION
                        preloader =  new SweetAlertDialog(Payment.this, SweetAlertDialog.WARNING_TYPE);
                        preloader.setTitleText("¿Tienes una cuenta?");
                        preloader.setContentText("Si ya posee una cuenta, inicia sesión con tu correo electrónico y contraseña");
                        preloader.setConfirmText("No");
                        preloader.setCancelable(false);
                        preloader.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.dismissWithAnimation(); //OCULTAR LA ALERTA DE CONFIRMACION

                                //Si la transaccion fue exitosa realizara los procceso correspodiente de forma asíncrona
                                //Caso lo contrario se le notificara con la respuesta del banco o falla del servidor en este caso en Log de error
                                queries.payment_banco_agricola(new Queries.Result() {
                                    @Override
                                    public void onSuccess(String value) {
                                            if(value == "Transacción exitosa"){
                                                queries.refresh_token_admin(new Queries.resultToken() {
                                                    @Override
                                                    public void onSuccess(String token_api, String refresh_token_api, String access_token_expiration_api , String status) {
                                                        if(status == "Ronovar Token"){ token = token_api; Log.d("TAG1","TOKEN RENOVADO:" + token); }
                                                        create_customer();
                                                    }
                                                } , Payment.this, refresh_token,Integer.parseInt(access_token_expiration));
                                            }
                                        }
                                },Payment.this,card_number.getText().toString().trim(),card_cvc.getText().toString().trim(), card_year.getText().toString().trim(),card_month.getText().toString().trim(), itemPackage.getService_price().trim());
                            }
                        }).setCancelButton("Si", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                finish();
                                Intent intent = new Intent (getApplicationContext(), Login.class);
                                startActivityForResult(intent, 0);
                            }
                        }).show();
                    }
                }
            }
        }
    }


    /**
     * CREAR EL USUARIO EN SPLYNX
     * */
    private void create_customer(){
        Log.d("TAG1" , "METODO CREAR EL USUARIO");
        try {

            //Importamos la clases de la api de splynx
            RestApiService apiService = RetrofitInstance.getApiService();
            //Enviamos los datos a la api de tipo token admin y los datos de los usuarios
            Call<CustomerResponse> call = apiService.createUser(
                    token,
                    customerEmail.getText().toString(),
                    "new",
                    2,
                    1,
                    nameCustomer.getText().toString(),
                    customerEmail.getText().toString().trim(),
                    "",
                    customerTelephone.getText().toString().trim(),
                    "person",
                    customerMunicipality.getText().toString(),
                    "",
                    "",
                    customerCountry.getText().toString(),
                    "",
                    "prepaid_monthly"
            );

            call.enqueue(new Callback<CustomerResponse>() {
                @Override
                public void onResponse(Call<CustomerResponse> call, Response<CustomerResponse> response) {

                    if(response.isSuccessful() && response.code() == 201){

                        id_customer =  gson.toJson(response.body().getId());
                        id_customer = id_customer.replace("\"", "");
                        id_user_customer = Integer.parseInt(id_customer);
                        Log.d("TAG1", "USUARIO CREADO" + id_user_customer);

                        //CREAR EL PAGO EN SPLYNX
                        queries.payment_splynx(new Queries.Result() {
                            @Override
                            public void onSuccess(String value) {
                                //AGREGAR EL SERVICIO DEL CLIENTE
                                Add_Internet_Services();
                            }
                        },Payment.this, token, id_user_customer, Current_date, itemPackage.getService_price());

                    }else if(response.isSuccessful() == false && response.code() == 422){
                        Log.d("TAG1" , "EMAIL UTILIZADO");
                    }

                }

                @Override
                public void onFailure(Call<CustomerResponse> call, Throwable t) {
                    Log.d("TAG1", ServerResponse + " => create_customer =>" + t.getMessage());
                    queries.Loader(Payment.this , "Error", Comment);
                }
            });

        }
        catch (Exception e) {
            Log.d("TAG1", ServerResponse2 + " => create_customer =>" + e.getMessage());
            queries.Loader(Payment.this , "Error", Comment);
        }
    }


    /**
     * Agregar el servicio al cliente
     * */

    public void Add_Internet_Services(){

        try {
            Log.d("TAG1", "AGREGAR EL SERVICIO DE INTERNET AL CLIENTE" );
            Log.d("TAG1" , "FECHA SERVICIO CREADO-> "+ Current_date);
            Log.d("TAG1" , "ASIGANAR SERVICIO-> "+ ID_Servicio_Internet);

            //Importamos la clases de la api de splynx
            RestApiService apiService = RetrofitInstance.getApiService();

            Call<CustomerResponse> call = apiService.postCreateInternetServices(
                    token,
                    id_customer,
                    id_user_customer,
                    ID_Servicio_Internet,
                    "active",
                    itemPackage.getService_Title(),
                    1,
                    itemPackage.getService_price(),
                    Current_date,
                    "",
                    4,
                    customerEmail.getText().toString().trim(),
                    0,
                    "",
                    "",
                    0,
                    "",
                    0
            );

            call.enqueue(new Callback<CustomerResponse>() {
                @Override
                public void onResponse(Call<CustomerResponse> call, Response<CustomerResponse> response) {
                    Log.d("TAG1","SERVICIO CREADO" );
                    getPasswordCustomer();//OBTENER CONTRASEÑA DEL CLIENTE
                }

                @Override
                public void onFailure(Call<CustomerResponse> call, Throwable t) {
                    Log.d("TAG1", ServerResponse + " => Add_Internet_Services =>" + t.getMessage());
                    queries.Loader(Payment.this , "Error", Comment);
                }
            });

        }catch (Exception e) {
            Log.d("TAG1", ServerResponse2 + " => Add_Internet_Services =>" + e.getMessage());
            queries.Loader(Payment.this , "Error", Comment);
        }
    }


    /**
     * Obtener el  id y la contrasena del cliente
     * */
     private void getPasswordCustomer(){

         try {
             //Importamos la clases de la api de splynx
             RestApiService apiService = RetrofitInstance.getApiService();
             Call<CustomerResponse> call = apiService.getCustomerInfo(token, id_customer);
             call.enqueue(new Callback<CustomerResponse>() {
                 @Override
                 public void onResponse(Call<CustomerResponse> call, Response<CustomerResponse> response) {

                     String PasswordCustomer = gson.toJson(response.body().getPassword());
                     PasswordCustomer = PasswordCustomer.replace("\"", "");
                     Log.d("TAG1", "Password-> " + PasswordCustomer);
                     Send_email_customer(PasswordCustomer);
                 }

                 @Override
                 public void onFailure(Call<CustomerResponse> call, Throwable t) {
                     Log.d("TAG1", ServerResponse + " => getPasswordCustomer =>" + t.getMessage());
                     queries.Loader(Payment.this , "Error", Comment);
                 }
             });
         }
         catch (Exception e) {
             Log.d("TAG1", ServerResponse2 + " => getPasswordCustomer =>" + e.getMessage());
             queries.Loader(Payment.this , "Error", Comment);
         }
    }


    private void Send_email_customer(String password){

        String subject = "Contraseña de acceso B-Pro Innovaciones";
        String message = "Estimado cliente: " + nameCustomer.getText().toString() + "\n" + "Es un placer saludarle en este correo electrónico encontrara su acceso:\n\n" +
                "Usuario: " + customerEmail.getText() + "\nContraseña: "+password+"\n\n Cualquier duda o consulta  estamos a la orden. \n\n B-Pro Innovaciones.";
        //Enviar email
        JavaMailAPI javaMailAPI = new JavaMailAPI(this, customerEmail.getText().toString(),subject,message);
        javaMailAPI.execute();

        showDialog();
    }



    public void showDialog(){

        SweetAlertDialog alert = new SweetAlertDialog(Payment.this, SweetAlertDialog.PROGRESS_TYPE);
        alert.dismiss(); //DETENER LOADER

        SweetAlertDialog pDialog =  new SweetAlertDialog(Payment.this, SweetAlertDialog.SUCCESS_TYPE);
        pDialog.setTitleText("Transacción exitosa");
        pDialog.setContentText("Su pago se ha realizado correctamente");
        pDialog .setConfirmText("FINALIZAR");
        pDialog.setCancelable(false);
        pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {

                SweetAlertDialog pDialog =  new SweetAlertDialog(Payment.this, SweetAlertDialog.SUCCESS_TYPE);
                pDialog.setTitleText("Cuenta Creada");
                pDialog.setContentText("Verifica tu correo electrónico donde encontrara su acceso para iniciar sesión");
                pDialog .setConfirmText("OK");
                pDialog.setCancelable(false);
                pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        finish();
                        Bundle myBundle = new Bundle();
                        myBundle.putString("id",id_customer); //IDCustomer
                        myBundle.putString("name", nameCustomer.getText().toString()); //Nombre Usuario
                        myBundle.putString("email", customerEmail.getText().toString());
                        Intent intent = new Intent (getApplicationContext(), Login.class);
                        intent.putExtras(myBundle);//Guardamos los recuros
                        startActivityForResult(intent, 0);
                    }
                }).show();

            }
        }).show();


    }



}