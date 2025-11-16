package localhost.myapp.news;

/**
 * Google 뉴스 RSS의 <item> 정보를 그대로 담는 DTO 클래스
 */
public class GoogleNews {
    public String title; // 기사 제목
    public String link; // 기사 링크(URL)
    public String pubDate; // 게시 날짜
    public String source; // 매체(출처)
}