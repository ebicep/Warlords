package com.ebicep.warlords.supporter;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.entity.Player;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public final class TebexService {

    private static final URI CHECKOUT_URI = URI.create("https://plugin.tebex.io/checkout");
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    private TebexService() {
    }

    public static String getSubscriptionPackageId() {
        return getSetting("warlords.tebex.supporter.subscriptionPackage", "TEBEX_SUPPORTER_SUBSCRIPTION_PACKAGE");
    }

    public static String getThirtyDayPackageId() {
        return getSetting("warlords.tebex.supporter.thirtyDayPackage", "TEBEX_SUPPORTER_30_DAY_PACKAGE");
    }

    public static boolean hasSecret() {
        return getSecret() != null;
    }

    public static CompletableFuture<String> createCheckout(Player player, String packageId) {
        String secret = getSecret();
        if (secret == null) {
            return CompletableFuture.failedFuture(new IllegalStateException("TEBEX_SECRET is not configured"));
        }
        if (packageId == null || packageId.isBlank()) {
            return CompletableFuture.failedFuture(new IllegalStateException("The Tebex package ID is not configured"));
        }

        JsonObject body = new JsonObject();
        body.addProperty("package_id", packageId);
        body.addProperty("username", player.getName());

        HttpRequest request = HttpRequest.newBuilder(CHECKOUT_URI)
                .header("X-Tebex-Secret", secret)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();

        return HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() < 200 || response.statusCode() >= 300) {
                        throw new IllegalStateException("Tebex checkout returned HTTP " + response.statusCode());
                    }
                    JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
                    if (!json.has("url") || json.get("url").isJsonNull()) {
                        throw new IllegalStateException("Tebex checkout did not return a URL");
                    }
                    return json.get("url").getAsString();
                });
    }

    private static String getSecret() {
        return getSetting("warlords.tebex.secret", "TEBEX_SECRET");
    }

    private static String getSetting(String property, String environmentVariable) {
        String propertyValue = System.getProperty(property);
        if (propertyValue != null && !propertyValue.isBlank()) {
            return propertyValue.trim();
        }
        String environmentValue = System.getenv(environmentVariable);
        if (environmentValue != null && !environmentValue.isBlank()) {
            return environmentValue.trim();
        }
        return null;
    }
}
