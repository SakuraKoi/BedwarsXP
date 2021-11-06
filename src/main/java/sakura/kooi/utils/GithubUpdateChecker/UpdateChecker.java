package sakura.kooi.utils.GithubUpdateChecker;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.AllArgsConstructor;
import lombok.Cleanup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.function.Consumer;

@AllArgsConstructor
public class UpdateChecker {
    private String user;
    private String repo;
    private String currentVersion;
    private Consumer<String> callback;

    public void check() throws IOException {
        URL url = new URL("https://api.github.com/repos/"+user+"/"+repo+"/releases/latest");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        @Cleanup BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        StringBuilder result = new StringBuilder();
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        JsonParser parser = new JsonParser();

        JsonObject json = parser.parse(result.toString()).getAsJsonObject();
        if (json.has("tag_name") && json.has("html_url")) {
            String tag = json.get("tag_name").getAsString();
            String link = json.get("html_url").getAsString();

            if (currentVersion.compareTo(tag) < 0) {
                callback.accept(link);
            }
        }
    }
}
