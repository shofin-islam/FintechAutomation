
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
    "package",
    "mainactivity",
    "serverurl",
    "help"
})

public class Mobile {

    @JsonProperty("package")
    private String _package;
    @JsonProperty("mainactivity")
    private String mainactivity;
    @JsonProperty("serverurl")
    private String serverurl;
    @JsonProperty("help")
    private String help;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("package")
    public String getPackage() {
        return _package;
    }

    @JsonProperty("package")
    public void setPackage(String _package) {
        this._package = _package;
    }

    @JsonProperty("mainactivity")
    public String getMainactivity() {
        return mainactivity;
    }

    @JsonProperty("mainactivity")
    public void setMainactivity(String mainactivity) {
        this.mainactivity = mainactivity;
    }

    @JsonProperty("serverurl")
    public String getServerurl() {
        return serverurl;
    }

    @JsonProperty("serverurl")
    public void setServerurl(String serverurl) {
        this.serverurl = serverurl;
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
