package localhost.myapp.news;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.w3c.dom.*;
import javax.xml.parsers.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.http.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/news/google-news")
public class GoogleNewsServlet extends HttpServlet {

    private static final String RSS_URL = "https://news.google.com/rss?hl=ko&gl=KR&ceid=KR:ko";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json; charset=UTF-8");

        List<GoogleNews> items = new ArrayList<>();

        try {
            // ───────────────────────────────────────────────
            // 1) HttpClient로 RSS XML 가져오기
            // ───────────────────────────────────────────────
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(RSS_URL))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String xml = response.body();

            // ───────────────────────────────────────────────
            // 2) XML String → Document 파싱
            // ───────────────────────────────────────────────
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document doc = builder.parse(new java.io.ByteArrayInputStream(xml.getBytes()));
            doc.getDocumentElement().normalize();

            NodeList itemNodes = doc.getElementsByTagName("item");

            // ───────────────────────────────────────────────
            // 3) 각 <item> 파싱
            // ───────────────────────────────────────────────
            for (int i = 0; i < itemNodes.getLength(); i++) {
                Node node = itemNodes.item(i);
                if (node.getNodeType() != Node.ELEMENT_NODE)
                    continue;

                Element e = (Element) node;

                GoogleNews news = new GoogleNews();
                news.title = getText(e, "title");
                news.link = getText(e, "link");
                news.pubDate = getText(e, "pubDate");

                NodeList sourceNodes = e.getElementsByTagName("source");
                if (sourceNodes.getLength() > 0) {
                    news.source = sourceNodes.item(0).getTextContent();
                }

                items.add(news);
            }

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        // 결과 JSON 출력
        String json = new Gson().toJson(items);
        try (PrintWriter out = resp.getWriter()) {
            out.print(json);
        }
    }

    private String getText(Element parent, String tagName) {
        NodeList list = parent.getElementsByTagName(tagName);
        if (list.getLength() == 0)
            return null;
        return list.item(0).getTextContent();
    }
}
