
package configurations;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "accountnumber",
    "cardnumber",
    "phonenumber",
    "email",
    "expire",
    "cvv"
})

public class Creditcard {

    @JsonProperty("accountnumber")
    private String accountnumber;
    @JsonProperty("cardnumber")
    private String cardnumber;
    @JsonProperty("phonenumber")
    private String phonenumber;
    @JsonProperty("email")
    private String email;
    @JsonProperty("expire")
    private String expire;
    @JsonProperty("cvv")
    private String cvv;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("accountnumber")
    public String getAccountnumber() {
        return accountnumber;
    }

    @JsonProperty("accountnumber")
    public void setAccountnumber(String accountnumber) {
        this.accountnumber = accountnumber;
    }

    @JsonProperty("cardnumber")
    public String getCardnumber() {
        return cardnumber;
    }

    @JsonProperty("cardnumber")
    public void setCardnumber(String cardnumber) {
        this.cardnumber = cardnumber;
    }

    @JsonProperty("phonenumber")
    public String getPhonenumber() {
        return phonenumber;
    }

    @JsonProperty("phonenumber")
    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    @JsonProperty("email")
    public String getEmail() {
        return email;
    }

    @JsonProperty("email")
    public void setEmail(String email) {
        this.email = email;
    }

    @JsonProperty("expire")
    public String getExpire() {
        return expire;
    }

    @JsonProperty("expire")
    public void setExpire(String expire) {
        this.expire = expire;
    }

    @JsonProperty("cvv")
    public String getCvv() {
        return cvv;
    }

    @JsonProperty("cvv")
    public void setCvv(String cvv) {
        this.cvv = cvv;
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
