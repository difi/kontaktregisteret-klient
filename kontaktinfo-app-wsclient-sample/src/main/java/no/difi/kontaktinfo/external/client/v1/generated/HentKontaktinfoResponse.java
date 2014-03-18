
package no.difi.kontaktinfo.external.client.v1.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
 *         &lt;element name="kontaktinfo" type="{http://kontaktinfo.difi.no/xsd/kontaktinfo/metadata/201210}Kontaktinfo" minOccurs="0"/>
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
@XmlRootElement(name = "hentKontaktinfoResponse")
public class HentKontaktinfoResponse {

    protected Kontaktinfo kontaktinfo;

    /**
     * Gets the value of the kontaktinfo property.
     * 
     * @return
     *     possible object is
     *     {@link Kontaktinfo }
     *     
     */
    public Kontaktinfo getKontaktinfo() {
        return kontaktinfo;
    }

    /**
     * Sets the value of the kontaktinfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link Kontaktinfo }
     *     
     */
    public void setKontaktinfo(Kontaktinfo value) {
        this.kontaktinfo = value;
    }

}
