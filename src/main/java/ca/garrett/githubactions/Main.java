package ca.garrett.githubactions;

import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {

        if (args.length > 0) {
            if (Arrays.stream(args).toList().contains("yaml")) {
                configYamlValidate();
            }
            if (Arrays.stream(args).toList().contains("images")) {
                gitCommitPushDiff();
            }
        }
    }

    private static void gitCommitPushDiff() throws IOException {
        String cmdGitDiff = "git diff --name-only HEAD HEAD~1";
        String resultGitDiff = execCmd(cmdGitDiff);

        List<String> changedFilesList = Arrays.stream(resultGitDiff.split("\n")).toList();

        System.out.println(changedFilesList);

        List<String> changedImages = new ArrayList<>();

        for(String changedFile: changedFilesList) {
            List<String> pathParts = Arrays.stream(changedFile.split("/")).toList();
            if (pathParts.contains("images")) {
                changedImages.add(pathParts.get(4));
            }
        }

        List<String> changedImagesFinal = changedImages.stream().distinct().toList();

        FileWriter fileWriter = new FileWriter("./manifest");
        PrintWriter printWriter = new PrintWriter(fileWriter);

        for(String image : changedImagesFinal) {
            printWriter.println(image);
        }

        printWriter.close();

        String cmdGitAdd = "git add .";
        System.out.println("git add results: " + execCmd(cmdGitAdd));

        String cmdGitCommit = "git commit -m building-image-manifest";
        System.out.println("git commit results: " + execCmd(cmdGitCommit));

        String cmdGitPush = "git push";
        System.out.println("git push results: " +execCmd(cmdGitPush));
    }

    private static String execCmd(String cmd) {
        String result = null;
        try (InputStream inputStream = Runtime.getRuntime().exec(cmd).getInputStream();
             Scanner s = new Scanner(inputStream).useDelimiter("\\A")) {
            result = s.hasNext() ? s.next() : null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
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