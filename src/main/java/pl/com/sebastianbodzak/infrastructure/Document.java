package pl.com.sebastianbodzak.infrastructure;

/**
 * Created by Dell on 2016-08-08.
 */
public class Document {

    private String shopName;
    private String headLine;
    private String content;
    private String footer;

    public Document(String shopName, String headLine, String footer, String content) {
        this.shopName = shopName;
        this.headLine = headLine;
        this.footer = footer;
        this.content = content;
    }

    public String getShopName() {
        return shopName;
    }

    public String getHeadLine() {
        return headLine;
    }

    public String getContent() {
        return content;
    }

    public String getFooter() {
        return footer;
    }
}

