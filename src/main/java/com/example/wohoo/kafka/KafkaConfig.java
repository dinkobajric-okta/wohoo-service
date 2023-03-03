package com.example.wohoo.kafka;

import static java.lang.String.format;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import com.example.wohoo.security.TemporaryKeyStore;

import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class KafkaConfig {
    public static Properties buildDefaults(String trustedCert, String clientCert, String clientKey, String kafkaUrl) throws URISyntaxException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException  {
        Properties props = new Properties();

        List<String> hostPorts = Collections.emptyList();

        for (String url : kafkaUrl.split(",")) {
            URI uri = new URI(url);
            hostPorts.add(format("%s:%d", uri.getHost(), uri.getPort()));

            switch (uri.getScheme()) {
                case "kafka":
                    props.put("spring.kafka.security.protocol", "PLAINTEXT");
                    break;
                case "kafka+ssl":
                    props.put("spring.kafka.security.protocol", "SSL");
                    props.put("spring.kafka.properties.ssl.endpoint.identification.algorithm", "");

                    TemporaryKeyStore tempTrustStore = TemporaryKeyStore.createWithRandomPassword(trustedCert);
                    TemporaryKeyStore tempKeyStore = TemporaryKeyStore.createWithRandomPassword(clientKey, clientCert);

                    File trustStore = tempTrustStore.storeTemp();
                    File keyStore = tempKeyStore.storeTemp();

                    props.put("spring.kafka.ssl.trust-store-type", tempTrustStore.type());
                    props.put("spring.kafka.ssl.trust-store-location", "file://" + trustStore.getAbsolutePath());
                    props.put("spring.kafka.ssl.trust-store-password", tempTrustStore.password());
                    props.put("spring.kafka.ssl.key-store-type", tempKeyStore.type());
                    props.put("spring.kafka.ssl.key-store-location", "file://" + keyStore.getAbsolutePath());
                    props.put("spring.kafka.ssl.key-store-password", tempKeyStore.password());
                    break;
                default:
                    throw new IllegalArgumentException(format("unknown scheme; %s", uri.getScheme()));
            }
        }

        props.put("spring.kafka.bootstrap-servers", hostPorts.stream().collect(Collectors.joining(",")));

        return props;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer(ConfigurableEnvironment env) throws KafkaConfigEnvironmentalVariableException, URISyntaxException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        var psc = new PropertySourcesPlaceholderConfigurer();

        // Short-circuit properties generation when not in prod profile
        if(!Arrays.asList(env.getActiveProfiles()).contains("prod")) {
           return psc;
        }

        String trustedCert = env.getProperty("KAFKA_TRUSTED_CERT");
        String clientCert = env.getProperty("KAFKA_CLIENT_CERT");
        String clientKey = env.getProperty("KAFKA_CLIENT_CERT_KEY");
        String kafkaUrl = env.getProperty("KAFKA_URL");

        if (!StringUtils.hasText(trustedCert)) {
            throw new KafkaConfigEnvironmentalVariableException("KAFKA_TRUSTED_CERT");
        }

        if (!StringUtils.hasText(clientCert)) {
            throw new KafkaConfigEnvironmentalVariableException("KAFKA_CLIENT_CERT");
        }

        if (!StringUtils.hasText(clientKey)) {
            throw new KafkaConfigEnvironmentalVariableException("KAFKA_CLIENT_CERT_KEY");
        }

        if (!StringUtils.hasText(kafkaUrl)) {
            throw new KafkaConfigEnvironmentalVariableException("KAFKA_URL");
        }

        psc.setProperties(KafkaConfig.buildDefaults(trustedCert, clientCert, clientKey, kafkaUrl));

        return psc;
    }
}
