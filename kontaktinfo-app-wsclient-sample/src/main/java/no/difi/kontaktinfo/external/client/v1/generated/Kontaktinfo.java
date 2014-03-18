
package no.difi.kontaktinfo.external.client.v1.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Kontaktinfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Kontaktinfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="foedselsnummer" type="{http://kontaktinfo.difi.no/xsd/kontaktinfo/metadata/201210}Foedselsnummer"/>
 *         &lt;element name="e-postadresse" type="{http://kontaktinfo.difi.no/xsd/kontaktinfo/metadata/201210}E-postadresse" minOccurs="0"/>
 *         &lt;element name="mobiltelefonnummer" type="{http://kontaktinfo.difi.no/xsd/kontaktinfo/metadata/201210}Mobiltelefonnummer" minOccurs="0"/>
 *         &lt;element name="statuskode" type="{http://kontaktinfo.difi.no/xsd/kontaktinfo/metadata/201210}Statuskode"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Kontaktinfo", namespace = "http://kontaktinfo.difi.no/xsd/kontaktinfo/metadata/201210", propOrder = {
    "foedselsnummer",
    "ePostadresse",
    "mobiltelefonnummer",
    "statuskode"
})
public class Kontaktinfo {

    @XmlElement(namespace = "", required = true)
    protected String foedselsnummer;
    @XmlElement(name = "e-postadresse", namespace = "")
    protected String ePostadresse;
    @XmlElement(namespace = "")
    protected String mobiltelefonnummer;
    @XmlElement(namespace = "", required = true)
    protected Statuskode statuskode;

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
     * Gets the value of the ePostadresse property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEPostadresse() {
        return ePostadresse;
    }

    /**
     * Sets the value of the ePostadresse property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEPostadresse(String value) {
        this.ePostadresse = value;
    }

    /**
     * Gets the value of the mobiltelefonnummer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMobiltelefonnummer() {
        return mobiltelefonnummer;
    }

    /**
     * Sets the value of the mobiltelefonnummer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMobiltelefonnummer(String value) {
        this.mobiltelefonnummer = value;
    }

    /**
     * Gets the value of the statuskode property.
     * 
     * @return
     *     possible object is
     *     {@link Statuskode }
     *     
     */
    public Statuskode getStatuskode() {
        return statuskode;
    }

    /**
     * Sets the value of the statuskode property.
     * 
     * @param value
     *     allowed object is
     *     {@link Statuskode }
     *     
     */
    public void setStatuskode(Statuskode value) {
        this.statuskode = value;
    }

}
