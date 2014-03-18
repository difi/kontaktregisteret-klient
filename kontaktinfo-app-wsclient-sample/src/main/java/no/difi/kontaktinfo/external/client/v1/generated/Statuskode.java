
package no.difi.kontaktinfo.external.client.v1.generated;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Statuskode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="Statuskode">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="SAMTYKKET_GENERELT"/>
 *     &lt;enumeration value="SAMTYKKET_SPESIFIKT"/>
 *     &lt;enumeration value="IKKE_SAMTYKKET"/>
 *     &lt;enumeration value="IKKE_REGISTRERT"/>
 *     &lt;enumeration value="SAMTYKKE_AVVIST"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "Statuskode", namespace = "http://kontaktinfo.difi.no/xsd/kontaktinfo/metadata/201210")
@XmlEnum
public enum Statuskode {

    SAMTYKKET_GENERELT,
    SAMTYKKET_SPESIFIKT,
    IKKE_SAMTYKKET,
    IKKE_REGISTRERT,
    SAMTYKKE_AVVIST;

    public String value() {
        return name();
    }

    public static Statuskode fromValue(String v) {
        return valueOf(v);
    }

}
