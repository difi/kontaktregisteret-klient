
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
 *         &lt;element name="foedselsnummer" type="{http://kontaktinfo.difi.no/xsd/kontaktinfo/metadata/201210}Foedselsnummer" maxOccurs="1000"/>
 *         &lt;element name="tjenesteeierId" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "foedselsnummer",
    "tjenesteeierId"
})
@XmlRootElement(name = "hentKontaktinfolisteRequest")
public class HentKontaktinfolisteRequest {

    @XmlElement(required = true)
    protected List<String> foedselsnummer;
    @XmlElement(required = true)
    protected String tjenesteeierId;

    /**
     * Gets the value of the foedselsnummer property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the foedselsnummer property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFoedselsnummer().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getFoedselsnummer() {
        if (foedselsnummer == null) {
            foedselsnummer = new ArrayList<String>();
        }
        return this.foedselsnummer;
    }

    /**
     * Gets the value of the tjenesteeierId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTjenesteeierId() {
        return tjenesteeierId;
    }

    /**
     * Sets the value of the tjenesteeierId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTjenesteeierId(String value) {
        this.tjenesteeierId = value;
    }

}
