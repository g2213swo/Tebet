import net.mamoe.mirai.internal.deps.okhttp3.OkHttpClient;
import net.mamoe.mirai.internal.deps.okhttp3.Request;
import net.mamoe.mirai.internal.deps.okhttp3.Response;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

public class Test {

    static OkHttpClient client = new OkHttpClient.Builder()
            .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("localhost", 10808)))
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();


    public static void main(String[] args) {
        Request request = new Request.Builder()
                .url("https://pro-api.coinmarketcap.com/v1/cryptocurrency/quotes/latest?id=1")
                .addHeader("Accepts", "application/json")
                .addHeader("X-CMC_PRO_API_KEY", "136598b6-988e-4c41-911d-832e8e192114")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


