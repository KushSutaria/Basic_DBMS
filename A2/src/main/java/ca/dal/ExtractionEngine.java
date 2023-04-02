package ca.dal;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ca.dal.StaticVariables.API_KEY;

public class ExtractionEngine {


    /**
     * Fetches raw data from newsAPI and sends title and content to Code B
     */
    public static void extractData() {
        try {

            String news_api_endpoint = "https://newsapi.org/v2/everything?q=";
            String[] keywords = {"Canada", "University", "Dalhousie", "Halifax", "Canada+education", "Moncton", "hockey", "Fredericton", "celebration"};
            String query = String.join("%20OR%20", keywords);
            String urlStr = news_api_endpoint + query + "&apiKey=" + API_KEY;


            URL url = new URL(urlStr);
            //System.out.println(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
            }
            rd.close();
            //System.out.println(response);
            List<String> title = new ArrayList<>();
            List<String> content = new ArrayList<>();

            Pattern patternTitle = Pattern.compile("\"title\":\"([^\"]*)\"");
            Matcher matcherTitle = patternTitle.matcher(response);

            Pattern patternContent = Pattern.compile("\"content\":\"([^\"]*)\"");
            Matcher matcherContent = patternContent.matcher(response);

            while (matcherTitle.find() && matcherContent.find()) {
                title.add(matcherTitle.group(1));
                content.add(matcherContent.group(1));

            }


            int titleLength = title.size();
            for (int i = 0; i < titleLength; i++) {
                DataProcessingEngine.DataProcessor(title.get(i), content.get(i));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

