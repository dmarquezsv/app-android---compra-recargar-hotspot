package beenet.sv.splynx_tas.model;

import java.io.Serializable;

public class ServiceList implements Serializable {

    /**
     * Clase que modela el objeto JSON que trae los servicio de internet de la API Y
     * */
    private String Service_Id;
    private String Service_Title;
    private String Service_price;
    private String service_Available;

    public ServiceList(String service_Id, String service_Title, String service_price, String service_Available) {
        Service_Id = service_Id;
        Service_Title = service_Title;
        Service_price = service_price;
        this.service_Available = service_Available;
    }

    public String getService_Id() {
        return Service_Id;
    }

    public String getService_Title() {
        return Service_Title;
    }

    public String getService_price() {
        return Service_price;
    }

    public String getService_Available() { return service_Available; }
}
