package beenet.sv.splynx_tas.model;

public class PaymentResponse {

    private String Mensaje;
    private String Error;
    private boolean Satisfactorio;
    private  BankResponse Datos;

    public PaymentResponse(String mensaje, String error, boolean satisfactorio, BankResponse datos) {
        Mensaje = mensaje;
        Error = error;
        Satisfactorio = satisfactorio;
        Datos = datos;
    }

    public String getMensaje() {
        return Mensaje;
    }

    public String getError() {
        return Error;
    }

    public boolean isSatisfactorio() {
        return Satisfactorio;
    }

    public BankResponse getDatos() {
        return Datos;
    }

}
