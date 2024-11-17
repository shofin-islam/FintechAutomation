package utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import configurations.Account;
import configurations.ConfigMaster;
import configurations.Web;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ConfigManager {

	private static final String CONFIG_FILE_PATH = "src/main/resources/Configuration.json";

	private ConfigMaster configMaster;
	 private JsonNode apiConfig;

	public ConfigManager() {
		loadConfig();
	}

	private void loadConfig() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			configMaster = mapper.readValue(new File(CONFIG_FILE_PATH), ConfigMaster.class);

		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to load configuration", e);
		}
		
		 try {
	            ObjectMapper apiMapper = new ObjectMapper();
	            apiConfig = apiMapper.readTree(new File(CONFIG_FILE_PATH));
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	}
	

	public ConfigMaster getConfigMaster() {
		return configMaster;
	}

	public Web getWebConfig() {

		return configMaster.getConfig().getWeb();
	}

	public  List<Account> getAccounts() {

		return configMaster.getAccounts();
	}

	public List<String> getBrowsers() {
		return configMaster.getConfig().getWeb().getBrowsers();
	}

	public String getWebBaseUrl() {
		return configMaster.getConfig().getWeb().getBaseurl();
	}

	// seperarely manage api config as i dont have added api config to pojo classed. 
	
	
	public String getAuthTokenUrl() {
        return apiConfig.get("apis").get("auth").get("token_url").asText();
    }

    public String getClientId() {
        return apiConfig.get("apis").get("auth").get("client_id").asText();
    }

    public String getClientSecret() {
        return apiConfig.get("apis").get("auth").get("client_secret").asText();
    }

    public JsonNode getFeatureConfig(String feature) {
        return apiConfig.get("apis").get("api").get(feature);
    }
}
