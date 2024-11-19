
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
    "web",
    "mobile",
    "api"
})

public class Config {

    @JsonProperty("web")
    private Web web;
    @JsonProperty("mobile")
    private Mobile mobile;
    @JsonProperty("api")
    private Api api;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("web")
    public Web getWeb() {
        return web;
    }

    @JsonProperty("web")
    public void setWeb(Web web) {
        this.web = web;
    }

    @JsonProperty("mobile")
    public Mobile getMobile() {
        return mobile;
    }

    @JsonProperty("mobile")
    public void setMobile(Mobile mobile) {
        this.mobile = mobile;
    }

    @JsonProperty("api")
    public Api getApi() {
        return api;
    }

    @JsonProperty("api")
    public void setApi(Api api) {
        this.api = api;
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
