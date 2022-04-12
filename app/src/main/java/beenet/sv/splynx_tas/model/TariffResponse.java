package beenet.sv.splynx_tas.model;

public class TariffResponse {

    private String description;
    private String status;
    private String id;
    private String start_date;

    public TariffResponse(String description, String status, String id, String start_date) {
        this.description = description;
        this.status = status;
        this.id = id;
        this.start_date = start_date;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getId() {
        return id;
    }

    public String getStart_date() { return start_date; }
}
