package beenet.sv.splynx_tas.model;

public class BankResponse {

    private  String Codigo;
    private   String RespuestaBA; //Respuesta del banco

    public String getCodigo() {
        return Codigo;
    }

    /**
     * Respuestas segun el codigo de error del banco agricola
     * */

    public String mensajeError(String codigoError){

        if(codigoError.equals("\"00\"")){
            RespuestaBA = "AUTORIZADO";
        }else if(codigoError.equals("\"01\"")){
            RespuestaBA = "LLAMAR AL EMISOR";
        }
        else if(codigoError.equals("\"02\"")){
            RespuestaBA = "LLAMAR AL EMISOR";
        }
        else if(codigoError.equals("\"04\"")){
            RespuestaBA = "TARJETA BLOQUEADA";
        }
        else if(codigoError.equals("\"05\"")){
            RespuestaBA = "LLAMAR AL EMISOR";
        }
        else if(codigoError.equals("\"07\"")){
            RespuestaBA = "TARJETA BLOQUEADA";
        }
        else if(codigoError.equals("\"12\"")){
            RespuestaBA = "TRANSACCION INVALIDA";
        }
        else if(codigoError.equals("\"13\"")){
            RespuestaBA = "amount INVALIDO";
        }
        else if(codigoError.equals("\"14\"")){
            RespuestaBA = "LLAMAR AL EMISOR";
        }
        else if(codigoError.equals("\"15\"")){
            RespuestaBA = "EMISOR NO DISPONIBLE";
        }
        else if(codigoError.equals("\"19\"")){
            RespuestaBA = "REINTENTE TRANSACCION";
        }
        else if(codigoError.equals("\"25\"")){
            RespuestaBA = "LLAMAR AL EMISOR";
        }
        else if(codigoError.equals("\"30\"")){
            RespuestaBA = "ERROR DE FORMATO";
        }
        else if(codigoError.equals("\"39\"")){
            RespuestaBA = "NO ES CUENTA DE CREDITO";
        }
        else if(codigoError.equals("\"31\"")){
            RespuestaBA = "BANCO NO SOPORTADO";
        }
        else if(codigoError.equals("\"41\"")){
            RespuestaBA = "TARJETA BLOQUEADA";
        }
        else if(codigoError.equals("\"43\"")) {
            RespuestaBA = "TARJETA BLOQUEADA";
        }
        else if(codigoError.equals("\"48\"")) {
            RespuestaBA = "CREDENCIAL INVALIDA";
        }
        else if(codigoError.equals("\"50\"")) {
            RespuestaBA = "LLAMAR AL EMISOR";
        }
        else if(codigoError.equals("\"51\"")) {
            RespuestaBA = "FONDOS INSUFICIENTES";
        }
        else if(codigoError.equals("\"52\"")) {
            RespuestaBA = "NO ES CUENTA DE CHEQUES";
        }
        else if(codigoError.equals("\"53\"")) {
            RespuestaBA = "NO ES CUENTA DE AHORROS";
        }
        else if(codigoError.equals("\"54\"")) {
            RespuestaBA = "TARJETA EXPIRADA";
        }
        else if(codigoError.equals("\"55\"")) {
            RespuestaBA = "PIN INCORRECTO";
        }
        else if(codigoError.equals("\"56\"")) {
            RespuestaBA = "TARJETA NO VALIDA";
        }
        else if(codigoError.equals("\"57\"")) {
            RespuestaBA = "TRANSACCION NO PERMITIDA";
        }
        else if(codigoError.equals("\"58\"")) {
            RespuestaBA = "TRANSACCION NO PERMITIDA";
        }
        else if(codigoError.equals("\"59\"")) {
            RespuestaBA = "SOSPECHA DE FRAUDE";
        }
        else if(codigoError.equals("\"61\"")) {
            RespuestaBA = "ACTIVIDAD DE LIMITE EXCEDIDO";
        }
        else if(codigoError.equals("\"62\"")) {
            RespuestaBA = "TARJETA RESTRINGIDA";
        }
        else if(codigoError.equals("\"65\"")) {
            RespuestaBA = "MAXIMO PERMITIDO ALCANZADO";
        }
        else if(codigoError.equals("\"75\"")) {
            RespuestaBA = "INTENTOS DE PIN EXCEDIDO";
        }
        else if(codigoError.equals("\"82\"")) {
            RespuestaBA = "NO HSM";
        }
        else if(codigoError.equals("\"83\"")) {
            RespuestaBA = "CUENTA NO EXISTE";
        }
        else if(codigoError.equals("\"84\"")) {
            RespuestaBA = "CUENTA NO EXISTE";
        }
        else if(codigoError.equals("\"85\"")) {
            RespuestaBA = "REGISTRO NO ENCONTRADO";
        }
        else if(codigoError.equals("\"86\"")) {
            RespuestaBA = "AUTORIZACION NO VALIDA";
        }
        else if(codigoError.equals("\"87\"")) {
            RespuestaBA = "CVV2 INVALIDO";
        }
        else if(codigoError.equals("\"88\"")) {
            RespuestaBA = "ERROR EN LOG DE TRANSACCIONES";
        }
        else if(codigoError.equals("\"89\"")) {
            RespuestaBA = "RUTA DE SERVICIO NO VALIDA";
        }
        else if(codigoError.equals("\"91\"")) {
            RespuestaBA = "EMISOR NO DISPONIBLE";
        }
        else if(codigoError.equals("\"92\"")) {
            RespuestaBA = "EMISOR NO DISPONIBLE";
        }
        else if(codigoError.equals("\"93\"")) {
            RespuestaBA = "TRANSACCION NO PUEDE SER PROCESADA";
        }
        else if(codigoError.equals("\"94\"")) {
            RespuestaBA = "TRANSACCION DUPLICADA";
        }
        else if(codigoError.equals("\"96\"")) {
            RespuestaBA = "SISTEMA NO DISPONIBLE";
        }
        else if(codigoError.equals("\"97\"")) {
            RespuestaBA = "TOKEN DE SEGURIDAD INVALIDO";
        }
        else if(codigoError.equals("\"D0\"")) {
            RespuestaBA = "SISTEMA NO DISPONIBLE";
        }
        else if(codigoError.equals("\"D1\"")) {
            RespuestaBA = "COMERCIO INVALIDO";
        }
        else if(codigoError.equals("\"H0\"")) {
            RespuestaBA = "FOLIO YA EXISTE";
        }
        else if(codigoError.equals("\"H1\"")) {
            RespuestaBA = "CHECK IN EXISTENTE";
        }
        else if(codigoError.equals("\"H2\"")) {
            RespuestaBA = "SERVICIO DE RESERVACION NO PERMITIDO";
        }
        else if(codigoError.equals("\"H3\"")) {
            RespuestaBA = "RESERVA NO ENCONTRADA EN EL SISTEMA";
        }
        else if(codigoError.equals("\"H4\"")) {
            RespuestaBA = "TARJETA NO ENCONTRADA CHECK IN";
        }
        else if(codigoError.equals("\"H5\"")) {
            RespuestaBA = "EXCEDE SOBREGIRO DE CHECK IN";
        }
        else if(codigoError.equals("\"N0\"")) {
            RespuestaBA = "AUTORIZACION INHABILITADA";
        }
        else if(codigoError.equals("\"N1\"")) {
            RespuestaBA = "TARJETA INVALIDA";
        }
        else if(codigoError.equals("\"N2\"")) {
            RespuestaBA = "PREAUTORIZACIONES COMPLETAS";
        }
        else if(codigoError.equals("\"N3\"")) {
            RespuestaBA = "amount MAXIMO ALCANZADO";
        }
        else if(codigoError.equals("\"N4\"")) {
            RespuestaBA = "amount MAXIMO ALCANZADO";
        }
        else if(codigoError.equals("\"N5\"")) {
            RespuestaBA = "MAXIMO DEVOLUCIONES ALCANZADO";
        }
        else if(codigoError.equals("\"N6\"")) {
            RespuestaBA = "MAXIMO PERMITIDO ALCANZADO";
        }
        else if(codigoError.equals("\"N7\"")) {
            RespuestaBA = "LLAMAR AL EMISOR";
        }
        else if(codigoError.equals("\"N8\"")) {
            RespuestaBA = "CUENTA SOBREGIRADA";
        }
        else if(codigoError.equals("\"N9\"")) {
            RespuestaBA = "INTENTOS PERMITIDOS ALCANZADO";
        }
        else if(codigoError.equals("\"O0\"")) {
            RespuestaBA = "LLAMAR AL EMISOR";
        }
        else if(codigoError.equals("\"O1\"")) {
            RespuestaBA = "NEG FILE PROBLEM";
        }
        else if(codigoError.equals("\"O2\"")) {
            RespuestaBA = "amount DE RETIRO NO PERMITIDO";
        }
        else if(codigoError.equals("\"O3\"")) {
            RespuestaBA = "DELINQUENT";
        }
        else if(codigoError.equals("\"O4\"")) {
            RespuestaBA = "LIMITE EXCEDIDO";
        }
        else if(codigoError.equals("\"O7\"")) {
            RespuestaBA = "FORCE POST";
        }
        else if(codigoError.equals("\"O8\"")) {
            RespuestaBA = "SIN CUENTA";
        }
        else if(codigoError.equals("\"O5\"")) {
            RespuestaBA = "PIN REQUERIDO";
        }
        else if(codigoError.equals("\"O6\"")) {
            RespuestaBA = "DIGITO VERIFICADOR INVALIDO";
        }
        else if(codigoError.equals("\"R8\"")) {
            RespuestaBA = "TARJETA BLOQUEADA";
        }
        else if(codigoError.equals("\"T1\"")) {
            RespuestaBA = "amount INVALIDO";
        }
        else if(codigoError.equals("\"T2\"")) {
            RespuestaBA = "FECHA DE TRANSACCION INVALIDA";
        }
        else if(codigoError.equals("\"T5\"")) {
            RespuestaBA = "LLAMAR AL EMISOR";
        }
        return  RespuestaBA;
    }
}
