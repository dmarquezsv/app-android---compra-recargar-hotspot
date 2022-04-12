package beenet.sv.splynx_tas.service;

import java.util.List;

import beenet.sv.splynx_tas.model.CustomerResponse;
import beenet.sv.splynx_tas.model.ServiceResponse;
import beenet.sv.splynx_tas.model.TariffResponse;
import beenet.sv.splynx_tas.model.TokenResponse;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RestApiService {

    /**
     * Llamadas a los metodos API.
     * El cliente para hacer Peticiones es Retrofit y se encuentra inicializado en api/RetrofitClient
     */

    /** Generar token de tipo customer o admin **/
    @FormUrlEncoded
    @POST("api/2.0/admin/auth/tokens")
    Call<TokenResponse> getToken(
            @Field("auth_type") String auth_type,
            @Field("login") String login,
            @Field("password") String password
    );

    /** renovar token de tipo customer o admin**/
    @GET("api/2.0/admin/auth/tokens/{refresh_token}")
    Call<TokenResponse> getRefreshToken(
            @Path("refresh_token") String refresh_token
    );

    /** Obtener las tarifas**/
    @GET("api/2.0/admin/tariffs/internet")
    Call<List<ServiceResponse>> getServices(
            @Header("Authorization") String authorization
    );

    /** Crear el usuario **/
    @FormUrlEncoded
    @POST("api/2.0/admin/customers/customer")
    Call<CustomerResponse>createUser(
            @Header("Authorization") String authorization,
            @Field("login") String login,
            @Field("status") String status,
            @Field("partner_id") int  partner_id,
            @Field("location_id") int  location_id,
            @Field("name") String  name,
            @Field("email") String  email,
            @Field("billing_email") String  billing_email,
            @Field("phone") String  phone,
            @Field("category") String  category,
            @Field("street_1") String  street_1,
            @Field("street_2") String  street_2,
            @Field("zip_code") String  zip_code,
            @Field("city") String  city,
            @Field("date_add") String  date_add,
            @Field("billing_type") String  billing_type
    );

    /** Realizar el pago en splynx **/
    @FormUrlEncoded
    @POST("api/2.0/admin/finance/payments")
    Call<CustomerResponse> postCreatePayment(
            @Header("Authorization") String authorization,
            @Field("customer_id") int customer_id,
            @Field("payment_type") String payment_type,
            @Field("receipt_number") String receipt_number,
            @Field("date") String date,
            @Field("amount") String amount,
            @Field("comment") String comment,
            @Field("field_1") String field_1,
            @Field("field_2") String field_2,
            @Field("field_3") String field_3,
            @Field("field_4") String field_4,
            @Field("field_5") String field_5
    );

    /** Agregar el servicio al cliente**/
    @FormUrlEncoded
    @POST("api/2.0/admin/customers/customer/{id}/internet-services")
    Call<CustomerResponse> postCreateInternetServices(
            @Header("Authorization") String authorization,
            @Path("id") String Id,
            @Field("customer_id") int customer_id,
            @Field("tariff_id") int tariff_id,
            @Field("status") String status,
            @Field("description") String description,
            @Field("quantity") int quantity,
            @Field("unit_price") String unit_price,
            @Field("start_date") String start_date,
            @Field("end_date") String end_date,
            @Field("router_id") int router_id,
            @Field("login") String login,
            @Field("taking_ipv4") int taking_ipv4,
            @Field("ipv4") String ipv4,
            @Field("ipv4_route") String ipv4_route,
            @Field("ipv4_pool_id") int ipv4_pool_id,
            @Field("mac") String mac,
            @Field("port_id") int port_id
    );

    /** Obtener la informacion del cliente **/
    @GET("api/2.0/admin/customers/customer/{id}")
    Call<CustomerResponse> getCustomerInfo(
            @Header("Authorization") String authorization,
            @Path("id") String Id
    );

    /** Obtener informacion del cliente segun logueo **/
    @GET("api/2.0/admin/customers/customer")
    Call<List<CustomerResponse>> getCustomerID(
            @Header("Authorization") String authorization,
            @Query("login") String email
    );

    /** Obtener la tarifa con la id del servicio activo **/
    @GET("api/2.0/admin/customers/customer/{id}/internet-services")
    Call<List<TariffResponse>> getCustomerService(
            @Header("Authorization") String authorization,
            @Path("id") String Id
    );

    /** Actualizar el estado del cliente **/
    @FormUrlEncoded
    @PUT("api/2.0/admin/customers/customer/{id}")
    Call<CustomerResponse> UpdateStatusCustomer(
            @Header("Authorization") String authorization,
            @Path("id") String Id,
            @Field("status") String status
    );

    /** Cambiar la tarifa del cliente **/
    @FormUrlEncoded
    @PUT("api/2.0/admin/tariffs/change-tariff/{id}?type=internet")
    Call<TariffResponse> ChangeTariff(
            @Header("Authorization") String authorization,
            @Path("id") String Id,
            @Field("newTariffId") int newTariffId,
            @Field("targetDate") String targetDate
    );

}
