package valorless.discordchat.discord;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.json.JSONObject;

public class DiscordUtils {
    private final String botToken;

    public DiscordUtils() {
    	botToken = Bot.config.GetString("token");
    }
    
    public String getUserGlobalName(String userId) {
        try {
            // Prepare the HTTP client and request
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://discord.com/api/v10/users/" + userId))
                    .header("Authorization", "Bot " + botToken) // Provide the bot token here
                    .timeout(Duration.ofMillis(5000L))
                    .build();

            // Send the request
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Parse the JSON response
            JSONObject json = new JSONObject(response.body());
            String globalName = json.optString("global_name");  // Get the global name from the response

            // Return the global name, or a default if not set
            return globalName.isEmpty() ? "No global name set" : globalName;
        } catch (Exception e) {
            //e.printStackTrace();
            return "Error retrieving global name";
        }
    }
}