package beenet.sv.splynx_tas.model;

import java.util.List;

public class ServiceResponse {

    /**
     * Clase que modela el objeto JSON que trae los servicio de internet de la API
     * */

    private String id;
    private String title;
    private String price;
    private String billing_days_count;
    private List<String> partners_ids;


    public ServiceResponse(String id, String title, String price, String billing_days_count, List<String> partners_ids) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.billing_days_count = billing_days_count;
        this.partners_ids = partners_ids;
    }

    public String getId() { return id; }

    public String getTitle() { return title; }

    public String getPrice() { return price; }

    public String getBilling_days_count() { return billing_days_count; }

    public List<String> getPartners_ids() { return partners_ids; }

}
