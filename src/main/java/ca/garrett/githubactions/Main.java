package ca.garrett.githubactions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONWrappedObject;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.yaml.snakeyaml.Yaml;

import javax.xml.crypto.dsig.keyinfo.KeyValue;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        InputStream inputStream = new FileInputStream("src/main/resources/config.yaml");

        Yaml yaml = new Yaml();
        ArrayList<Object> data = yaml.load(inputStream);

        for (Object o : data) {
            validateData(o);
        }

    }

    private static void validateData(Object data) throws Exception {
        String formatted = data.toString().replace("{", "").replace("}", "");
        List<String> lines = Arrays.stream(formatted.split(",")).toList();
        Map<String, String> properties = new HashMap<>();

        for(String line : lines) {
            String[] keyvalues = line.split("=");
            properties.put(keyvalues[0].trim(), keyvalues[1].trim());
        }

        List<String> validProperties = new ArrayList<>();
        validProperties.add("name");
        validProperties.add("debug");
        validProperties.add("id");

        for (String key: properties.keySet()) {
            System.out.println(key + ": " + properties.get(key));
        }

        // check that all properties are present
        if (!properties.keySet().containsAll(validProperties)) {
            throw new Exception("Keyset does not contain all valid properties:" +  properties);
        }

        if (!validProperties.containsAll(properties.keySet())) {
            throw new Exception("There are additional invalid properties" +  properties);
        }

        try {
            Integer.parseInt(properties.get("name"));
            throw new Exception("Invalid name (must be string): " + properties.get("name"));
        } catch (NumberFormatException e) {

        }


    }
}