package pl.com.sebastianbodzak.domain;

import pl.com.sebastianbodzak.infrastructure.Document;

import java.util.Date;

/**
 *
 * Created by Dell on 2016-08-08.
 */
public class DocumentFactory {

    public static Document createFrom(Receipt receipt) {
        String receiptContent = writeProductsNamesAndPricesFrom(receipt);
        String additionalInformation = addAdditionalInformationFrom(receipt);
        receiptContent = receiptContent + additionalInformation;

        String shopName = creatShopName();
        String headline = createHeadLine();
        String footer = createFooter();

        return new Document(shopName, headline, footer, receiptContent);
    }

    private static String writeProductsNamesAndPricesFrom(Receipt receipt) {
        String productsList = "";
        StringBuilder sb = new StringBuilder(productsList);
        for (Product prod : receipt.getProducts())
            sb.append(prod.getName() + " " + prod.getBruttoPrice().toString() + "\n");
        return sb.toString();
    }

    private static String addAdditionalInformationFrom(Receipt receipt) {
        Date date = receipt.getConfirmationDate();
        return "\n Total netto sum: " + receipt.getTotalNettoSum().toString() +
                "\n Total brutto sum: " + receipt.getTotalBruttoSum().toString() +
                "\n\n" + date.toString() + "\n";
    }

    private static String creatShopName() {
        //some method
        return "Liverpool Market"; //injected by system configuration; it is simulation only
    }

    private static String createHeadLine() {
        //some method
        return "Hello! You bought:"; //injected by system configuration; it is simulation only
    }

    private static String createFooter() {
        //some method
        return "Thank you for shopping. Have a nice day!"; //injected by system configuration; it is simulation only
    }
}
