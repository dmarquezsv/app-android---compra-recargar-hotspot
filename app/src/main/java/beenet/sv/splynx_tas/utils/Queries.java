package beenet.sv.splynx_tas.utils;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import beenet.sv.splynx_tas.model.BankResponse;
import beenet.sv.splynx_tas.model.CustomerResponse;
import beenet.sv.splynx_tas.model.PaymentResponse;
import beenet.sv.splynx_tas.model.ServiceList;
import beenet.sv.splynx_tas.model.ServiceResponse;
import beenet.sv.splynx_tas.model.TokenResponse;
import beenet.sv.splynx_tas.service.RestApiService;
import beenet.sv.splynx_tas.service.RetrofitInstance;
import beenet.sv.splynx_tas.ui.Home;
import beenet.sv.splynx_tas.ui.InternetPackages;
import beenet.sv.splynx_tas.ui.Login;
import beenet.sv.splynx_tas.ui.Payment;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Queries  extends Activity {

        /**VARIABLES DE COMENTARIO PARA SER UTILIZADO RESPUESTA DEL PROGRAMADOR**/
        private String ServerResponse = "RESPUESTA DEL SERVIDOR => ";
        private String ServerResponse2 = "RESPUESTA DEL TRY catch =>";
        private String Comment  = "Vuelva intentar o más tarde";

        /**Permite acceder a los recursos específicos de la aplicación y a sus clases*/
        /**como lanzar Activities, difundir mensajes por el sistema, recibir Intents, etc.*/
        private Context mContext;

        /**INSTANCIA DE CLASES**/
        private Tools tools = new Tools(); //Herraminetas
        private Gson gson = new Gson(); //CONVERTIR A UN JSON
        private JsonParser parser = new JsonParser();//GENERAR UN JSON
        private DecimalFormat NumberFormat  = new DecimalFormat("0.00"); //Formato de de los decimal

        /**Variables del servicio de internet**/
       private List<ServiceResponse> servicesList; //Guardar en una lista los elementos de la api
       private List<ServiceList> elements; //Guarda los elementos a utilizar en la vista
       String id , title , price , package_duration;//Guardar la informacion del paquete
       String ResultJsonPartners; //Guarda los id de los partners que existen
       String assignPartners = "2";//Partners solicitado a buscar
       String valorPartners = gson.toJson(assignPartners); //Convertimos json
       float prices;

       /**Variables para el banco y instancia**/
       private EncryptionRSA encryptionRSA = new EncryptionRSA();//RSA INCRIPTACION
       static final MediaType JSON = MediaType.get("application/json; charset=utf-8"); //Tipo de dato  para ser ocupado okhttps
       BankResponse bankResponse = new BankResponse(); //OBTIENE LA RESPUSTA DEL BANCO SEGUN EL CODIGO

        /**VARIABLES A RETONAR**/
        String header;

        /*************************************************
         * Funcion que retorna el token del administrador.
         * ***********************************************
         */
    public void generate_token_admin(resultToken result , Context context){

            try {

                //Elemento de carga
                tools.showDialog(0, context,"" , "");

                //Importamos la conexion para la api de SPLYNX
                RestApiService apiService = RetrofitInstance.getApiService();

                //Enviamos los parametros a la api de tipo admin con su credenciales para obtener el token
                Call<TokenResponse> call = apiService.getToken("admin", "demo", "demo");

                call.enqueue(new Callback<TokenResponse>() {

                    @Override
                    public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                        TokenResponse tokenResponse = response.body(); // Obtenemos el resultado de la API de SPLYNX
                        header = "Splynx-EA (access_token="+tokenResponse.getAccess_token()+")";//Formato del header para splnyx y asignamos el TOKEN
                        result.onSuccess(header,tokenResponse.getRefresh_token() ,tokenResponse.getAccess_token_expiration(),""); //Retornamos los resultados
                    }

                    @Override
                    public void onFailure(Call<TokenResponse> call, Throwable t) {
                        Log.d("TAG1", ServerResponse + "GENERADOR TOKEN => " + t.getMessage()); //Log de error
                        Loader(context , "Error" , Comment); //Alerta para el usuario
                    }
                });

            }catch (Exception e) {
                Log.d("TAG1", ServerResponse2 + "GENERADOR TOKEN => " + e.getMessage()); //Log de error
                Loader(context , "Error" , Comment); //Alerta para el usuario
            }
        }


        /***************************************************
         * Funcion que renueva el token del administrador
         * *************************************************
         */
        public void refresh_token_admin(resultToken result , Context context , String  refresh_token , int access_token_expiration){

            try {

                /** UNIXTIME se encarga de obtener la hora actual y la fecha */
                Long unixTime = System.currentTimeMillis()/1000;
                Log.d("TAG1" , "UNIXTIME =>" + unixTime.intValue());

                /** Compruebe si el token sigue siendo válido. Estas condiciones se cumplen cuando la corriente
                 *  la marca de tiempo es mayor que el vencimiento del token de acceso
                 * */
                if(unixTime.intValue() > access_token_expiration){

                    // Importamos la conexion para la api de SPLYNX
                    RestApiService apiService = RetrofitInstance.getApiService();

                    //Enviamos los parametros a la api en este caso el refresh token para renovar el token
                    Call<TokenResponse> call = apiService.getRefreshToken(refresh_token);

                    call.enqueue(new Callback<TokenResponse>() {
                        @Override
                        public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                            TokenResponse tokenResponse = response.body(); // Obtenemos el resultado de la API de SPLYNX
                            header = "Splynx-EA (access_token="+tokenResponse.getAccess_token()+")";//Formato del header para splnyx y asignamos el TOKEN
                            result.onSuccess(header,tokenResponse.getRefresh_token() ,tokenResponse.getAccess_token_expiration(),"Ronovar Token"); //Retornamos los resultados
                        }

                        @Override
                        public void onFailure(Call<TokenResponse> call, Throwable t) {
                            Log.d("TAG1", ServerResponse + "refresh_token_admin => " + t.getMessage()); //Log de error
                            Loader(context , "Error" , Comment); //Alerta para el usuario
                        }
                    });
                }else {
                    /**Retornamos los resultados*/
                    result.onSuccess("","","","");
                }

            }catch (Exception e) {
                Log.d("TAG1", ServerResponse2 + "refresh_token_admin => " + e.getMessage()); //Log de error
                Loader(context , "Error" , Comment); //Alerta para el usuario
            }
        }


        /*****************************************************
         * Funcion que retornar los servicio de internet.
         * ***************************************************
         */
       public void GetAllServices(ResultList result , Context context ,String header){

           try {

               //Importamos la clases de la api
               RestApiService apiService = RetrofitInstance.getApiService();

               //Enviamos los parametros a la api con el token  para obtener los servicios
               Call<List<ServiceResponse>> call = apiService.getServices(header);

               call.enqueue(new Callback<List<ServiceResponse>>() {
                   @Override
                   public void onResponse(Call<List<ServiceResponse>> call, Response<List<ServiceResponse>> response) {

                       servicesList = response.body(); //Obtine el resultado de la api y lo guarda en una lista
                       elements = new ArrayList<>(); //Una ArrayList para agregar los item a la vista de recyclerView

                       for (int i = 0; i < servicesList.size(); i++) {

                           ResultJsonPartners = gson.toJson(response.body().get(i).getPartners_ids().get(0));//Obtenemos todos los partners
                           //Evaluamos el partners en este caso buscamos el 2 referencia A TAS
                           if (ResultJsonPartners.equals((valorPartners))) {

                               id = gson.toJson(response.body().get(i).getId());
                               id = id.replace("\"", "");

                               title = gson.toJson(response.body().get(i).getTitle());
                               title = title.replace("\"", "");

                               price = gson.toJson(response.body().get(i).getPrice());
                               price = price.replace("\"", "");
                               price.trim();//elimina los espacios en blanco en ambos extremos del string
                               prices = Float.parseFloat(price);

                               package_duration = gson.toJson(response.body().get(i).getBilling_days_count());
                               package_duration = package_duration.replace("\"", "");

                               //Agrega los elemento para la vista
                               elements.add(new ServiceList(id, title, NumberFormat.format(prices), package_duration.trim()));
                           }
                       }
                       Log.d("TAG1", "PAQUETES INTERNET ENCONTRADOS");
                       result.onSuccess(elements);// Retornamos la informacion que se almaceno para ser utilizado recyclerView
                       Loader(context, "", ""); //Detener elemento de carga
                   }

                   @Override
                   public void onFailure(Call<List<ServiceResponse>> call, Throwable t) {
                       Loader(context, "Error", Comment);
                       Log.d("TAG1", ServerResponse + "GetAllServices => " + t.getMessage());
                   }
               });
           }
           catch (Exception e) {
               Log.d("TAG1", ServerResponse2 + " => GetAllServices =>" + e.getMessage());
               Loader(context , "Error" , Comment);
           }

       }


        /**
         * Funcion para realizar el pago en el banco
         */
        public void payment_banco_agricola(Result result ,Context context , String  card_number , String card_cvc , String card_month , String card_year , String amount)  {
                try {

                    //Elemento de carga
                    loading(context);

                    //Construimos el json para capturar los datos de las tarjetas debito y credito
                    JsonObject json1 = parser.parse(
                            "{\"Card\": \""+card_number+"\"," +
                                    "\"InfoS\": \""+card_cvc+"\"," +
                                    "\"InfoV\": \""+card_year+card_month+"\"," +
                                    "\"Amount\": \""+tools.GenerateLeadingZeros(amount.replace(".",""),12)+"\"}"
                    ).getAsJsonObject();
                    Log.d("TAG1", "JSON1 ->" + json1.toString());

                    JsonObject json = parser.parse(
                            "{\"KeyInfo\": \"522b226f-55e0-4330-a167-4cf7bd83b912\"," +
                                    "\"PaymentData\": \""+ encryptionRSA.encrypt(json1.toString())+"\"}"
                    ).getAsJsonObject();

                    Log.d("TAG1", "JSON2->" + json.toString());

                    //conexion con la api del banco
                    OkHttpClient client = new OkHttpClient();
                    String url = "https://PaymentRest/Payment"; // URL DE LA API DEL BANCO DE PRUEBA
                    RequestBody body = RequestBody.create(json.toString(), JSON); //Enviamos el body  del json y le indicamos el formato de tipo json
                    Request request = new Request.Builder().url(url).post(body).build();

                    client.newCall(request).enqueue(new okhttp3.Callback() {
                        @Override
                        public void onFailure(okhttp3.Call call, IOException e) {
                            Log.d("TAG1", ServerResponse + " payment_banco_agricola => " + e.getMessage());
                            Loader(context , "Error" , Comment);
                        }
                        @Override
                        public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {

                            if (response.isSuccessful()) {

                                final String myResponse = response.body().string();//RESPUESTA DE LA API DEL BANCO
                                final PaymentResponse response_agricola = gson.fromJson(myResponse, PaymentResponse.class); //BUSCAR LOS DATOS ESPECIFICOS CON GETTERS
                                if(response_agricola.isSatisfactorio()){
                                    Log.d("TAG1",  "Transacción exitosa");
                                    result.onSuccess("Transacción exitosa");
                                }else{
                                    Loader(context , "Error" , bankResponse.mensajeError(gson.toJson(response_agricola.getDatos().getCodigo())));
                                    Log.d("TAG1",  bankResponse.mensajeError(gson.toJson(response_agricola.getDatos().getCodigo())));
                                }
                            }
                        }
                    });

            }catch (Exception e) {
                    Log.d("TAG1", ServerResponse2 + " => payment_banco_agricola =>" + e.getMessage());
                    Loader(context , "Error" , Comment);
                }
        }

        /**
         * Funcion para realizar el pago en el SPLYNX
         */
        public void payment_splynx(Result result , Context context ,String token , int id_user_customer , String Current_date , String price){
            try {

                Log.d("TAG1", "METODO DE PAGO DE SPLYNX");

                //Importamos la clases de la api de splynx
                RestApiService apiService = RetrofitInstance.getApiService();

                //consulta para crear el pago correspondiente en SPLYNX
                Call<CustomerResponse> call = apiService.postCreatePayment(
                        token,
                        id_user_customer,
                        "7",
                        "",
                        Current_date,
                        price,
                        "Pago factura Banco Agricola, Autorizacion:",
                        "",
                        "",
                        "",
                        "",
                        "");

                call.enqueue(new Callback<CustomerResponse>() {
                    @Override
                    public void onResponse(Call<CustomerResponse> call, Response<CustomerResponse> response) {
                        Log.d("TAG1","FACTURA CREADA" );
                        result.onSuccess("Factura Creada");
                    }

                    @Override
                    public void onFailure(Call<CustomerResponse> call, Throwable t) {
                        Log.d("TAG1", ServerResponse + " payment_splynx => " + t.getMessage());
                        Loader(context , "Error" , Comment);
                    }
                });


            } catch (Exception e) {
                Log.d("TAG1", ServerResponse2 + " => payment_splynx =>" + e.getMessage());
                Loader(context , "Error" , Comment);
            }
        }

            // interface sirve para interactuar con los componentes de IU y poder utilizar retonorno del valor
            public interface Result{
                void onSuccess(String value);
            }

            public interface resultToken{
                void onSuccess(String token , String refresh_token , String access_token_expiration ,String status);
            }

            public interface ResultList{
                void onSuccess(List<ServiceList> value);
            }


            /**
             * Funcion para detener elemento de carga y mostrar el mensaje de error
             * de forma asíncrona
             */
            public void Loader(Context context,String title,String text){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tools.showDialog(1, context,"" , "");
                        if(title == "Error"){
                            tools.showDialog(2, context,title , text);
                        }
                    }
                });
            }

            /**
             * Funcion para detener elemento de carga y mostrar el mensaje de error
             * de forma asíncrona
             */
            public void loading(Context context){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tools.showDialog(0, context,"" , "");
                    }
                });
            }





}
