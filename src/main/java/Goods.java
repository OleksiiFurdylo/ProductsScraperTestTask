import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by OleksiiF on 24.11.2017.
 */
@XmlRootElement(name = "Offer")
@XmlType(propOrder = {"name", "brand", "colour", "price", "initialPrice", "description", "articleId", "shippingCosts"})

public class Goods {
    private String name;
    private String brand;
    private String colour;
    private String price;
    private String initialPrice;
    private String description;
    private String articleId;
    private String shippingCosts;

    public Goods() {
    }

    public Goods(String name, String brand, String colour, String price, String initialPrice, String description, String articleId, String shippingCosts) {
        this.name = name;
        this.brand = brand;
        this.colour = colour;
        this.price = price;
        this.initialPrice = initialPrice;
        this.description = description;
        this.articleId = articleId;
        this.shippingCosts = shippingCosts;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getInitialPrice() {
        return initialPrice;
    }

    public void setInitialPrice(String initialPrice) {
        this.initialPrice = initialPrice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getShippingCosts() {
        return shippingCosts;
    }

    public void setShippingCosts(String shippingCosts) {
        this.shippingCosts = shippingCosts;
    }

    @Override
    public String toString() {
        return "Goods{" +
                "name='" + name + '\'' +
                ", brand='" + brand + '\'' +
                ", colour='" + colour + '\'' +
                ", price='" + price + '\'' +
                ", initialPrice='" + initialPrice + '\'' +
                ", description='" + description + '\'' +
                ", articleId='" + articleId + '\'' +
                ", shippingCosts='" + shippingCosts + '\'' +
                '}';
    }
}
