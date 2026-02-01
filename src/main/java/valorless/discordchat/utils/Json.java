package valorless.discordchat.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import valorless.valorlessutils.json.BukkitAwareObjectTypeAdapter;

public class Json {
	public static final Gson builder = new GsonBuilder()
            .disableHtmlEscaping()
            .serializeSpecialFloatingPointValues()
            .setLenient()
            .registerTypeAdapterFactory(BukkitAwareObjectTypeAdapter.FACTORY)
            .create();
}
