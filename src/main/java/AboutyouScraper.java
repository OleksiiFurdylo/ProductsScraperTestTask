import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.*;

import javax.xml.bind.JAXBException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;


/**
 * Created by OleksiiF on 24.11.2017.
 */
public class AboutyouScraper {
    private static List<String> categories = new ArrayList<>();
    static final int BRAND_POSITION_SUBSTRING = 13;
    static int requestCounter = 0;

    static {

        categories.add("20201");
        categories.add("20202");
        categories.add("138113");
    }

    public static void main(String[] args) throws IOException, URISyntaxException {

        long startTime = System.currentTimeMillis();
        long beforeUsedMem = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
        String searchedValue = ""; for(int arg=0; arg<=args.length-1; arg++) { searchedValue += args[arg]+" "; }
        String fileName = "./offers "+searchedValue+".xml";
        List<String> goodsUrls = new ArrayList<>();
        List<Goods> goods = new ArrayList<>();

        findUrlsInAllCategories(categories, searchedValue, goodsUrls);
        goods.addAll(fillGoodsInformation(goodsUrls));
        convertObjectToXml(goods, fileName);
        stopProfiler(startTime, beforeUsedMem, goods.size());

    }

    private static void findUrlsInAllCategories(List<String> categories, String searchedValue, List<String> goodsUrls) throws IOException, URISyntaxException {

        for(String category: categories) {
            URI url = new URI("https://www.aboutyou.de/suche?term=" +
                    URLEncoder.encode(searchedValue, "UTF-8") +
                    "&category=" +
                    category);
            goodsUrls.addAll(findUrls(url));

        }
    }


    private static void stopProfiler(long startTime, long beforeUsedMem, int numberOfOffers) {
        long endTime   = System.currentTimeMillis();
        long afterUsedMem=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
        System.out.println("Run-time: "+(endTime - startTime)+" ms");
        System.out.println("Memory Footprint: "+(afterUsedMem-beforeUsedMem)+" bytes");
        System.out.println("Amount of extracted products: " + numberOfOffers);
        System.out.println("Amount of triggered HTTP request: "+requestCounter);
    }

    private static void convertObjectToXml(List<Goods> goodsAL, String fileName) {
        try {
            OffersWrapper offerWrapper = new OffersWrapper();
            offerWrapper.setOffer(goodsAL);


            JAXBContext context = JAXBContext.newInstance(Goods.class, OffersWrapper.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            marshaller.marshal(offerWrapper, new File(fileName));

        }catch(JAXBException e){
            e.printStackTrace();
        }
    }


    private static List<Goods> fillGoodsInformation(List<String> goodsUrls) throws IOException, URISyntaxException {
        List<Goods> goods = new ArrayList<>();

        for (String page : goodsUrls) {
            Document goodsDescriptionSingleUnit = Jsoup.connect(page).get(); requestCounter++;

            String name = goodsDescriptionSingleUnit
                    .getElementsByAttributeValueContaining("class", "name_").text();

            String brandInLink = goodsDescriptionSingleUnit.getElementsByAttributeValueContaining("class", "brand_").attr("href");
                String[] brandNameInBrunch = brandInLink.substring(0, brandInLink.indexOf('?')).split("/");
            String brand = brandNameInBrunch[3];// as brand name comes after 3 brunches and before '?' character

            String finalPrice = goodsDescriptionSingleUnit
                    .getElementsByAttributeValueContaining("class", "beforePrice_").text();
                if (finalPrice.equals("")) {finalPrice = goodsDescriptionSingleUnit.getElementsByAttributeValueContaining("class", "finalPrice").text();}

            String initialPrice = goodsDescriptionSingleUnit
                    .getElementsByAttributeValueContaining("class", "originalPrice_").text();
                if(initialPrice.equals("")) { initialPrice = finalPrice; }

            String description = goodsDescriptionSingleUnit
                    .getElementsByAttributeValueContaining("class", "orderedList_").text();
                String articleNumberWithOthers = goodsDescriptionSingleUnit.select(":containsOwn(Artikel-Nr: )").text();

            String realArticleNumber = articleNumberWithOthers
                    .substring(articleNumberWithOthers.indexOf("Artikel-Nr: ") + 12, articleNumberWithOthers.length());

                String [] shippingBlock = goodsDescriptionSingleUnit
                    .getElementsByAttributeValueContaining("class", "promises").text().split(" ");
            String shipping = ""; for (int i = 3; i<=6; i++) { shipping +=shippingBlock[i]; }


            Elements scriptsFromPage = goodsDescriptionSingleUnit.getElementsByTag("script");
            String colour = "";
            for(Element block: scriptsFromPage) {
                if(block.html().toString().contains("\"color\":")) {
                    int indexOfColorName = block.html().toString().indexOf("color_detail") + 81; // distance between "color_detail" and actual name of color
                    String colorAndCharacters = block.html().toString().substring(indexOfColorName, indexOfColorName+20); // color name length is certainly less than number
                    colour = colorAndCharacters.split("\"")[0];
                    break;
                }
            }

            goods.add(new Goods(name, brand, colour, finalPrice, initialPrice, description, realArticleNumber, shipping));
        }

            return goods;

    }

    private static List<String> findUrls(URI whereToSearch) throws IOException {


        List<String> goodsUrls = new ArrayList<>();
        String pageLinkNumber = "";
        String pageNumbers = "1";


        Document goodsSearchPage  = Jsoup.connect(whereToSearch.toString()).get(); requestCounter++;
        String [] allPagesLinkNumberArray = goodsSearchPage.getElementsByAttributeValue("class" , "yiiPager").text().split(" ");
        pageNumbers = (allPagesLinkNumberArray[allPagesLinkNumberArray.length-1]).equals("")?"1":allPagesLinkNumberArray[allPagesLinkNumberArray.length-1];
        if(goodsSearchPage.getElementsByAttributeValue("class", "row text-center empty-advices").text().contains(("Deine Suche nach"))) return goodsUrls;

        for(int i=2; i<= Integer.valueOf(pageNumbers)+1; i++){
            Elements articleElement = goodsSearchPage.getElementsByAttributeValue("class", "product-image loaded");
            articleElement.addAll(goodsSearchPage.getElementsByAttributeValue("class", "product-image is-loading  "));

                for(Element address: articleElement){
                     Element aElement = address.child(0);
                        String url = aElement.attr("href");
                        goodsUrls.add("http://www.aboutyou.de"+url);
                }
            pageLinkNumber = whereToSearch + "&page=" + i;
            goodsSearchPage  = Jsoup.connect(pageLinkNumber).get(); requestCounter++;
        }
        return goodsUrls;
    }

}