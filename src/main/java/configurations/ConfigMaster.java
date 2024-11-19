
package configurations;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "Config",
    "accounts",
    "creditcards",
    "adminuser"
})

public class ConfigMaster {

    @JsonProperty("Config")
    private Config config;
    @JsonProperty("accounts")
    private List<Account> accounts;
    @JsonProperty("creditcards")
    private List<Creditcard> creditcards;
    @JsonProperty("adminuser")
    private List<Adminuser> adminuser;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("Config")
    public Config getConfig() {
        return config;
    }

    @JsonProperty("Config")
    public void setConfig(Config config) {
        this.config = config;
    }

    @JsonProperty("accounts")
    public List<Account> getAccounts() {
        return accounts;
    }

    @JsonProperty("accounts")
    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    @JsonProperty("creditcards")
    public List<Creditcard> getCreditcards() {
        return creditcards;
    }

    @JsonProperty("creditcards")
    public void setCreditcards(List<Creditcard> creditcards) {
        this.creditcards = creditcards;
    }

    @JsonProperty("adminuser")
    public List<Adminuser> getAdminuser() {
        return adminuser;
    }

    @JsonProperty("adminuser")
    public void setAdminuser(List<Adminuser> adminuser) {
        this.adminuser = adminuser;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
