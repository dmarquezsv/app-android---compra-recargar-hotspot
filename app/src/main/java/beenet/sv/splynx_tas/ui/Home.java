package beenet.sv.splynx_tas.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;

import beenet.sv.splynx_tas.R;
import beenet.sv.splynx_tas.model.ServiceList;
import beenet.sv.splynx_tas.model.ServiceListAdapter;
import beenet.sv.splynx_tas.model.TariffResponse;
import beenet.sv.splynx_tas.service.RestApiService;
import beenet.sv.splynx_tas.service.RetrofitInstance;
import beenet.sv.splynx_tas.utils.Queries;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Home extends AppCompatActivity {

    /**ELEMENTO DE LA VISTA DE XML*/

    /** MOSTRAR TEXTO*/
    TextView  plan , StatusPlan , PaidDate;

    /**ELEMENTO DE  TIPO LISTA*/
    RecyclerView recyclerView; //Elemento de la vista de tipo lista.

    /**VARIABLES DE COMENTARIO PARA SER UTILIZADO RESPUESTA DEL PROGRAMADOR*/
    String ServerResponse = "RESPUESTA DEL SERVIDOR => ";
    String ServerResponse2 = "RESPUESTA DEL TRY catch =>";
    String Comment  = "Vuelva intentar o más tarde";

    /**Instancia de clases*/
    Queries queries = new Queries();
    Gson gson = new Gson(); //CONVERTIR A UN JSON
    SweetAlertDialog alert;

    /**Variables Globales*/
    //VARIABLES DEL CLIENTE DE AUTENTICADO
    String nameUser , email , idCustomer,tokenCustomer, statusCustomer;
    List<TariffResponse> service; //Guardar en una lista los elementos de la api de los planes de TAS

    /**VARIABLES DEL SERVICIO ACTIVO DEL CLIENTE*/
    String planActive , PlanStatus ,PurchaseDate;
    String search = "active";//Buscar el plan activo
    String status = gson.toJson(search); //Convertimos json para ser utilizado con la respuesta json
    String getStatus;//Guardar el status del plan del cliente de la respuesta de la api
    String IdService;

    /**VARIABLES DEL TOKEN DEL ADMINISTRADOR*/
    String tokenAdmin;//Guardar el token de tipo admin
    String refresh_token;
    String access_token_expiration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        /**Referenciando variables con la vista de recyclerView ("LISTAR SERVICIO")*/
        recyclerView = findViewById(R.id.listRecyclerView2);//Referenciando variable  con la vista RecyClerView
        recyclerView.setHasFixedSize(true);//se utiliza para mantenga el mismo tamaño del recyclerView.
        recyclerView.setLayoutManager(new LinearLayoutManager(Home.this));//El efecto de esta configuración es que el componente baja verticalmente

        /**Referenciando variables con la vista de TextView*/
        plan = findViewById(R.id.internet_plan);
        StatusPlan = findViewById(R.id.StatusInter);
        PaidDate = findViewById(R.id.paiddate);

        /**Recuperando valor enviado proveniente de Login*/
        Bundle myBundle = this.getIntent().getExtras();
        if (myBundle != null){
            nameUser = myBundle.getString("name");
            email = myBundle.getString("email");
            idCustomer =  myBundle.getString("id");
            tokenCustomer = myBundle.getString("tokenCustomer");
            statusCustomer = myBundle.getString("statusCustomer");
        }

        /**Referenciando nombre del usuario en el navbar*/
        setTitle(nameUser);

        /***********************************************************************
         * Funcion que envia datos a la API, retorna el valor del token de admin
         **********************************************************************/
        queries.generate_token_admin(new Queries.resultToken() {
            @Override
            public void onSuccess(String token , String refresh_token_api, String access_token_expiration_api ,String status) {

                /**Almacenar token del adminitrador y la renovacion del token*/
                tokenAdmin = token;
                refresh_token = refresh_token_api;
                access_token_expiration =  access_token_expiration_api;

                /**Funcion que muestra los  servicios de internet atraves de la API, recibe como paramentro valor del token.*/
                queries.GetAllServices(new Queries.ResultList() {
                    @Override
                    public void onSuccess(List<ServiceList> value) {
                        /**ServiceListAdapter es una clases donde se encarga mostrar la vista de los servicio y el manejo de evento del onclick*/
                        ServiceListAdapter serviceListAdapter = new ServiceListAdapter(value, Home.this, new ServiceListAdapter.OnItemClickListener() {
                            @Override
                            public Void onItemClick(ServiceList item) {
                                /**Si el cliente tiene un servicio activo no podra seguir comprando hasta que se le expire*/
                                if(statusCustomer.equals("active")){
                                    alert =  new SweetAlertDialog(Home.this, SweetAlertDialog.WARNING_TYPE);
                                    alert.setTitleText("Oops...");
                                    alert.setContentText("Actualmente tiene un paquete activo.");
                                    alert.setCancelable(false);
                                    alert.show();
                                }else{
                                    /**la funcion se encargara de enviar la informacion que ha seleccionado el usuario*/
                                    get_pack_info(item);
                                }
                                return null;
                            }
                        });
                        /**Contenido que trae de la api se envia a la vista recyclerView*/
                        recyclerView.setAdapter(serviceListAdapter);
                    }
                },Home.this,tokenAdmin);
            }
        },Home.this);

        Log.d("TAG1" , "ID CUSTOMER => " + idCustomer);
        Log.d("TAG1" , "STATUS CUSTOMER =>" + statusCustomer);
        Log.d("TAG1" , "TOKEN CUSTOMER => " + tokenCustomer);

        getService();
    }

    /***********************************************
     * OBTENER EL PLAN DEL CLIENTE                 *
     ***********************************************/
    void getService(){
      try {

          //Importamos la clases de la api de splynx
          RestApiService apiService = RetrofitInstance.getApiService();
          //Enviamos los datos a la api para de tipo customer para obtener el servicio activo del cliente
          Call<List<TariffResponse>> call = apiService.getCustomerService(tokenCustomer,idCustomer);

          call.enqueue(new Callback<List<TariffResponse>>() {
              @Override
              public void onResponse(Call<List<TariffResponse>> call, Response<List<TariffResponse>> response) {
                  service = response.body(); //Obtine el resultado de la api y lo guarda en una lista
                  for (int i = 0; i < service.size(); i++) {
                      getStatus = gson.toJson(response.body().get(i).getStatus());
                      if (getStatus.equals(status)){

                          PurchaseDate = gson.toJson(response.body().get(i).getStart_date());
                          PurchaseDate = PurchaseDate.replace("\"", "");
                          PaidDate.setText(PurchaseDate);

                          planActive = gson.toJson(response.body().get(i).getDescription());
                          planActive = planActive.replace("\"", "");
                          plan.setText(planActive);

                          PlanStatus =  gson.toJson(response.body().get(i).getStatus());
                          PlanStatus = PlanStatus.replace("\"", "");
                          //Si el estado del cliente se encuentra en bloqueado significa que el servicio ha expirado.
                          if(statusCustomer.equals("blocked")){PlanStatus = "EXPIRADO";}
                          //Si el estado del cliente se encutra activo significa que el servicio no ha expirado
                          if(PlanStatus.equals("active")){PlanStatus = "ACTIVO";}
                          StatusPlan.setText(PlanStatus);
                          //Obtenemos el id del servicio
                          IdService = gson.toJson(response.body().get(i).getId());
                          IdService = IdService.replace("\"", "");
                      }
                  }
              }

              @Override
              public void onFailure(Call<List<TariffResponse>> call, Throwable t) {
                  Log.d("TAG1", ServerResponse + " => getService =>" + t.getMessage());
                  queries.Loader(Home.this , "Error", Comment);
              }
          });
        } catch (Exception e) {
          Log.d("TAG1", ServerResponse2 + " => getService =>" + e.getMessage());
          queries.Loader(Home.this , "Error", Comment);
      }
    }

    /******************************************************************************
     * Iniciarlizar la vista Payment y recibir el item para se enviado a la vista.*
     ******************************************************************************/
    public void get_pack_info(ServiceList item){
        // Los Intents permiten intercambiar datos entre aplicaciones o componentes de aplicaciones,
        Intent intent = new Intent(this,Payment_authenticated.class);
        intent.putExtra("itemPackage", item);//ITEM DEL SERVICIO SELECCIONADO  POR EL USUARIO
        intent.putExtra("tokenAdmin",tokenAdmin);
        intent.putExtra("refresh_token" , refresh_token);
        intent.putExtra("access_token_expiration" , access_token_expiration);
        intent.putExtra("id",idCustomer);
        intent.putExtra("idService",IdService);
        intent.putExtra("statusCustomer",statusCustomer);
        startActivity(intent);//Inicia la activada
    }

    /***************************
     * Evento para retroceder.*
     *************************/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        boolean handled = false;
        switch (keyCode) {
            //Evento cuando retrocede
            case KeyEvent.KEYCODE_BACK:
                handled = true;
                close_application();
                break;
        }
        return handled || super.onKeyDown(keyCode, event);
    }

    /***************************
     * Creacion del menu.*
     *************************/
    @Override public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.activity_menu,menu);
        menu.add(0,0,0,email);
        menu.add(1,1,1,"Salir");
        return  true;
    }

    /***************************
     * Opciones del menu.*
     *************************/
    @Override public  boolean onOptionsItemSelected(MenuItem  menu_options){
        int opc = menu_options.getItemId();
        if( opc== 1){ close_application(); return  true; }//Cerrar la aplicacion
        return super.onOptionsItemSelected(menu_options); //Retornar la opcion
    }

    /************************************************
     *Alerta para salir del home y regresar al login*
     ************************************************/
    public void close_application(){
        // Mensaje de confirmacion
        alert =  new SweetAlertDialog(Home.this, SweetAlertDialog.WARNING_TYPE);
        alert.setTitleText("¿Estas seguro?");
        alert.setContentText("¿Quieres salir de la Aplicacion?");
        alert.setConfirmText("Si");
        alert.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        finish();
                        Intent intent = new Intent (getApplicationContext(), Login.class);
                        startActivityForResult(intent, 0);
                    }
                })
                .setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
        alert.setCancelable(false);
    }



}