package beenet.sv.splynx_tas.service;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {

    //Retrofit es un cliente de servidores REST para Android
    private static Retrofit retrofit = null;

    public static RestApiService getApiService() {
        if (retrofit == null) {
            //Configuracion con la api para splynx
            retrofit = new Retrofit
                    .Builder()
                    .baseUrl("https://demo.proyecto/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit.create(RestApiService.class);
    }

}
