package org.acme.chat;

import io.quarkus.elasticsearch.restclient.lowlevel.ElasticsearchClientConfig;
import io.quarkus.runtime.configuration.ConfigurationException;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.ssl.SSLContexts;
import org.elasticsearch.client.RestClientBuilder;

import javax.net.ssl.SSLContext;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

@ElasticsearchClientConfig
public class SSLConfig implements RestClientBuilder.HttpClientConfigCallback {

    @Override
    public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
        try {
            // Load the Elasticsearch CA certificate from resources inside the JAR
            InputStream certInputStream = getClass().getClassLoader().getResourceAsStream("elasticsearch-ca.crt");
            if (certInputStream == null) {
                throw new ConfigurationException("Elasticsearch CA certificate not found in resources");
            }

            // Generate a trusted certificate
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            Certificate trustedCa = factory.generateCertificate(certInputStream);

            // Create a TrustStore and load the CA certificate
            KeyStore trustStore = KeyStore.getInstance("JKS");
            trustStore.load(null, null);
            trustStore.setCertificateEntry("ca", trustedCa);

            // Build SSLContext with the TrustStore
            SSLContext sslContext = SSLContexts.custom()
                    .loadTrustMaterial(trustStore, null)
                    .build();

            httpClientBuilder.setSSLContext(sslContext);
            // Disable hostname verification to prevent CN mismatch errors
            httpClientBuilder.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE);

        } catch (Exception e) {
            throw new RuntimeException("Failed to configure Elasticsearch SSL", e);
        }
        return httpClientBuilder;
    }
}
