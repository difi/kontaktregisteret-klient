
package no.difi.kontaktinfo.external.client.v1.generated;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="kontaktinfo" type="{http://kontaktinfo.difi.no/xsd/kontaktinfo/metadata/201210}Kontaktinfo" maxOccurs="1000"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "kontaktinfo"
})
@XmlRootElement(name = "hentKontaktinfolisteResponse")
public class HentKontaktinfolisteResponse {

    @XmlElement(required = true)
    protected List<Kontaktinfo> kontaktinfo;

    /**
     * Gets the value of the kontaktinfo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the kontaktinfo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getKontaktinfo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Kontaktinfo }
     * 
     * 
     */
    public List<Kontaktinfo> getKontaktinfo() {
        if (kontaktinfo == null) {
            kontaktinfo = new ArrayList<Kontaktinfo>();
        }
        return this.kontaktinfo;
    }

}
