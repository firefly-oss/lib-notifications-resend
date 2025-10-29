package com.firefly.core.notifications.provider.resend.core.v1;

import com.firefly.core.notifications.interfaces.dtos.email.v1.EmailRequestDTO;
import com.firefly.core.notifications.interfaces.dtos.email.v1.EmailResponseDTO;
import com.firefly.core.notifications.interfaces.interfaces.providers.email.v1.EmailProvider;
import com.firefly.core.notifications.provider.resend.config.v1.ResendConfig;
import com.firefly.core.notifications.provider.resend.properties.v1.ResendProperties;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ResendEmailProvider.class, ResendEmailProviderTest.TestBeans.class, ResendConfig.class})
class ResendEmailProviderTest {

    private static MockWebServer server;

    @BeforeAll
    static void startServer() throws Exception {
        server = new MockWebServer();
        server.start();
    }

    @AfterAll
    static void stopServer() throws Exception {
        server.shutdown();
    }

    @Configuration
    static class TestBeans {
        @Bean
        ResendProperties resendProperties() {
            ResendProperties p = new ResendProperties();
            p.setApiKey("test_key");
            p.setDefaultFrom("noreply@example.com");
            p.setBaseUrl(server.url("/").toString());
            return p;
        }


    }

    @Autowired
    private EmailProvider provider;

    @Test
    void sendEmail_success() {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("{\"id\":\"email_123\"}"));

        EmailRequestDTO req = EmailRequestDTO.builder()
                .to("user@example.com")
                .subject("Hello")
                .html("<b>World</b>")
                .build();

        EmailResponseDTO resp = provider.sendEmail(req).block();
        assertThat(resp).isNotNull();
        assertThat(resp.getMessageId()).isEqualTo("email_123");
    }
}
