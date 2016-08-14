package pl.com.sebastianbodzak.infrastructure;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Dell on 2016-08-11.
 */
public class DocumentTest {

    private static final String SHOP_NAME = "test name";
    private static final String HEADLINE = "headline";
    private static final String FOOTER = "footer";
    private static final String CONTENT = "content";

    @Test
    public void shouldCreateDocument() {
        Document document = new Document(SHOP_NAME, HEADLINE, FOOTER, CONTENT);

        assertEquals(SHOP_NAME, document.getShopName());
        assertEquals(HEADLINE, document.getHeadLine());
        assertEquals(FOOTER, document.getFooter());
        assertEquals(CONTENT, document.getContent());
    }
}
