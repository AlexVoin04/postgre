
package postgre;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.http.entity.StringEntity;

public class App {

    public static void main(String[] args) throws Exception {
        // �������� �� ��� API ����
        postgre.Jwt.main(args);
        String apiKey = "���_API_����";
//
//        // URL ��� �������� ��������
        String createClusterURL = "https://mdb.api.cloud.yandex.net/managed-postgresql/v1/clusters";
//
//        // JSON-������������� ������ ��� �������� ��������
//
//
//        try {
//            String jsonBody = new String(Files.readAllBytes(Paths.get("cluster_creation_data.json")));
//            CloseableHttpClient httpClient = HttpClients.createDefault();
//            HttpPost httpPost = new HttpPost(createClusterURL);
//
//            // ��������� ���������� � ���� �������
//            httpPost.setHeader("Content-Type", "application/json");
//            httpPost.setHeader("Authorization", "Api-Key " + apiKey);
//            httpPost.setEntity(new StringEntity(jsonBody));
//
//            // ���������� �������
//            CloseableHttpResponse response = httpClient.execute(httpPost);
//            HttpEntity entity = response.getEntity();
//            String responseString = EntityUtils.toString(entity);
//
//            // ��������� ������
//            if (response.getStatusLine().getStatusCode() == 200) {
//                System.out.println("������� PostgreSQL ������� ������.");
//            } else {
//                System.err.println("������: " + responseString);
//            }
//
//            httpClient.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    Object getGreeting() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
