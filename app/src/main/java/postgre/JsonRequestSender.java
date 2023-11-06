package postgre;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Supplier;

public class JsonRequestSender {
    public static <T> T sendAndGetJson(HttpRequest httpRequest, Class<T> aClass) {
        HttpClient client = HttpClient.newHttpClient();
        try {
            var response = client.send(httpRequest, new JsonBodyHandler<>(aClass));
            if (response.statusCode() != 200) {
                System.err.println(response.statusCode() + " " + response.body().get());
                return null;
            }
            return response.body().get();
        } catch (IOException e) {
            System.err.println("Error in sendAndGetJson");
            e.printStackTrace();
            return null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    public static <W> HttpResponse.BodySubscriber<Supplier<W>> asJson(Class<W> targetType) {
        HttpResponse.BodySubscriber<InputStream> upstream = HttpResponse.BodySubscribers.ofInputStream();
        return HttpResponse.BodySubscribers.mapping(
                upstream,
                inputStream -> () -> {
                    try (InputStream stream = inputStream) {
                        ObjectMapper objectMapper = new ObjectMapper();
                        return objectMapper.readValue(stream, targetType);
                    } catch (IOException e) {
                        System.err.println("Error in asJson");
                        e.printStackTrace();
                        return null;
                    }
                });
    }

    static class JsonBodyHandler<W> implements HttpResponse.BodyHandler<Supplier<W>> {
        private final Class<W> wClass;

        public JsonBodyHandler(Class<W> wClass) {
            this.wClass = wClass;
        }

        @Override
        public HttpResponse.BodySubscriber<Supplier<W>> apply(HttpResponse.ResponseInfo responseInfo) {
            return asJson(wClass);
        }
    }
}
