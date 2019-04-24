## How to update client certificate (virksomhetssertifikat) in the oppslagstjenesten java-example code. 

1. Add your new keypair to the keystore found in src\main\resources\certs\kontaktinfo-client-test.jks . Use 'changeit' as key password (You can use another password, but then you need to update the class no.difi.oppslagstjenesten.client.cxf.ClientKeystorePasswordCallbackHandler.java to provide the right password) Remember the alias you choosed to use.

2. Edit the properties file client_sec.properties found in src\main\resources. Update the property 'org.apache.ws.security.crypto.merlin.keystore.alias' to match the alias of your new keys.

If you have used a proper test 'virksomhetssertifikat'-certificate (i.e. from Buypass TEST4 cert-chain) and your organization has been granted access to the ver1 environment of the service, the example code test should now run without failure.


## How to update SoapUI client certificate.

1. Add your new keypair to the keystore 'kontaktinfo-client-test.jks' found in the SoapUI project folder. 

2. Load the project in SoapUI. Double-click on the 'oppslagstjeneste-ws-16-02' project element i the projects list and choose the 'WS-Security configurations' tab.

3. First check that the keystore is loaded properly. Choose the 'keystores' tab and check that the keystore 'kontaktinfo-client-test.jks' has status 'OK'. If not check that the correct path is given for the keystore. Try to remove it and add it once more. Keystore password is 'changeit', 'Default Alias' is the alias of your new keypair and 'Alias password' is the corresponding password.

4. Go to the 'Outgoing WS-Security Configurations' tab and chooose the configuration named 'out'

5. Choose 'Signature' from the list of WSS entries 

6. In the 'Signature' configuration view you should now see a Keystore and Alias list. Make sure that 'kontaktinfo-client-test.jks' is choosen as Keystore.

7. In the Alias list you should now see the alias of your new keypair. Choose the correct alias.

8. Repeat step 5-7 for the 'out with signed PaaVegneAv'-configuration

9. Close the project-properties window. You should now be able to run all tests. If a request fails make sure that the correct outgoing WSS configuration is choosen for the request. (Check 'Aut' in the lower left-corner of the request window). The logs available on the bottom of the soapui-gui can also give helpful information.