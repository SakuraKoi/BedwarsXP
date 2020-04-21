package sakura.kooi.utils.GithubUpdateChecker;

import lombok.AllArgsConstructor;
import sakura.kooi.utils.GithubUpdateChecker.org.json.JSONObject;

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
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        StringBuilder result = new StringBuilder();
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        rd.close();
        JSONObject json = new JSONObject(result.toString());
        String tag = json.getString("tag_name");
        String link = json.getString("html_url");

        if (currentVersion.compareTo(tag) < 0) {
            callback.accept(link);
        }
    }
}
