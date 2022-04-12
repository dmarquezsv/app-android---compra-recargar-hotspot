package beenet.sv.splynx_tas.model;

public class TokenResponse {

    private String access_token;
    private String refresh_token;
    private String access_token_expiration;
    private String refresh_token_expiration;

    public TokenResponse(String access_token, String refresh_token, String access_token_expiration, String refresh_token_expiration) {
        this.access_token = access_token;
        this.refresh_token = refresh_token;
        this.access_token_expiration = access_token_expiration;
        this.refresh_token_expiration = refresh_token_expiration;
    }

    public String getAccess_token() { return access_token; }

    public String getRefresh_token() { return refresh_token; }

    public String getAccess_token_expiration() { return access_token_expiration; }

    public String getRefresh_token_expiration() { return refresh_token_expiration; }

}
