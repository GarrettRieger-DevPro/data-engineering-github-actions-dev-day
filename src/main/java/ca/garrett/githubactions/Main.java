package ca.garrett.githubactions;

import org.yaml.snakeyaml.Yaml;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        configYamlValidate();
        if (args.length > 0) {
            if (Objects.equals(args[0], "images")) {
                gitCommitPushDiff();
            }
        }
    }

    private static void gitCommitPushDiff() {
        System.out.println("images arg picked up!");
    }

    private static void configYamlValidate() throws Exception {
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