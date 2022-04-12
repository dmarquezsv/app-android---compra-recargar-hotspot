package beenet.sv.splynx_tas.model;

public class CustomerResponse {

    private String id;
    private String password;
    private String name;
    private String status;

    public CustomerResponse(String id, String password, String name , String status) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.status = status;
    }

    public String getId() { return id; }

    public String getPassword() { return password; }

    public String getName() { return name; }

    public String getStatus() { return status; }
}
