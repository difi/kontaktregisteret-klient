
package no.difi.kontaktinfo.external.client.v1.generated;

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
 *         &lt;element name="foedselsnummer" type="{http://kontaktinfo.difi.no/xsd/kontaktinfo/metadata/201210}Foedselsnummer"/>
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
@XmlRootElement(name = "hentKontaktinfoRequest")
public class HentKontaktinfoRequest {

    @XmlElement(required = true)
    protected String foedselsnummer;
    @XmlElement(required = true)
    protected String tjenesteeierId;

    /**
     * Gets the value of the foedselsnummer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFoedselsnummer() {
        return foedselsnummer;
    }

    /**
     * Sets the value of the foedselsnummer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFoedselsnummer(String value) {
        this.foedselsnummer = value;
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
