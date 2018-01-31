package pl.piomin.services.account;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

public class Test {

	public static void main(String[] args) throws Exception {
        KeyStore sslKeyStore = KeyStore.getInstance("JKS");
        FileInputStream fin = new FileInputStream("src/main/resources/account.jks");
        sslKeyStore.load(fin, "123456".toCharArray());
        Set<TrustAnchor> hashSet = new HashSet<TrustAnchor>();
        Enumeration<String> aliases = sslKeyStore.aliases();
        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            System.out.println("Alias " + alias + ": " + sslKeyStore.isCertificateEntry(alias));
            if (sslKeyStore.isCertificateEntry(alias)) {
                Certificate cert = sslKeyStore.getCertificate(alias);
                if (cert instanceof X509Certificate)
                    hashSet.add(new TrustAnchor((X509Certificate)cert, null));
            }
        }
	}

}
