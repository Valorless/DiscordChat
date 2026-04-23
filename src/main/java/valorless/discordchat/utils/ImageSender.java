package valorless.discordchat.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClients;

import valorless.discordchat.Main;
import valorless.valorlessutils.logging.Log;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageSender {
    public static void sendImage(BufferedImage image, String fileName) throws IOException {
    	
    	String url = Main.config.getString("webserver.upload-url");
    	Log.debug(Main.plugin, "Attempting to contact webserver");
    	Log.debug(Main.plugin, url);
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(url);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        byte[] imageBytes = baos.toByteArray();

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addBinaryBody("image", imageBytes, ContentType.APPLICATION_OCTET_STREAM, fileName);
        builder.addTextBody("key", Main.config.getString("webserver.secret-key"));

        HttpEntity multipart = builder.build();
        httppost.setEntity(multipart);

        HttpResponse response = httpclient.execute(httppost);
    	Log.debug(Main.plugin, response.toString());
        // Handle response if needed
    }
}