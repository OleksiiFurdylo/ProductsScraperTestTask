import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by OleksiiF on 26.11.2017.
 */
@XmlRootElement(name = "offers")
public class OffersWrapper {
    private List<Goods> offer;

    public OffersWrapper () {
        offer = new ArrayList<Goods>();
    }

    public List<Goods> getOffer() {
        return offer;
    }

    public void setOffer(List<Goods> offer) {
        this.offer = offer;
    }
}
