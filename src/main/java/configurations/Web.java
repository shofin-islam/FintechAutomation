
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
    "browsers",
    "baseurl",
    "help"
})

public class Web {

    @JsonProperty("browsers")
    private List<String> browsers;
    @JsonProperty("baseurl")
    private String baseurl;
    @JsonProperty("help")
    private String help;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("browsers")
    public List<String> getBrowsers() {
        return browsers;
    }

    @JsonProperty("browsers")
    public void setBrowsers(List<String> browsers) {
        this.browsers = browsers;
    }

    @JsonProperty("baseurl")
    public String getBaseurl() {
        return baseurl;
    }

    @JsonProperty("baseurl")
    public void setBaseurl(String baseurl) {
        this.baseurl = baseurl;
    }

    @JsonProperty("help")
    public String getHelp() {
        return help;
    }

    @JsonProperty("help")
    public void setHelp(String help) {
        this.help = help;
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
