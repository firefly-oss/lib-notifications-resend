/*
 * Copyright 2025 Firefly Software Solutions Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.firefly.core.notifications.provider.resend.core.v1;

import com.firefly.core.notifications.interfaces.dtos.email.v1.EmailAttachmentDTO;
import com.firefly.core.notifications.interfaces.dtos.email.v1.EmailRequestDTO;
import com.firefly.core.notifications.interfaces.dtos.email.v1.EmailResponseDTO;
import com.firefly.core.notifications.interfaces.interfaces.providers.email.v1.EmailProvider;
import com.firefly.core.notifications.provider.resend.properties.v1.ResendProperties;
import com.firefly.common.client.RestClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.*;

@Slf4j
@Component
public class ResendEmailProvider implements EmailProvider {

    @Autowired
    private ResendProperties properties;

    @Autowired
    private RestClient resendClient;

    @Override
    public Mono<EmailResponseDTO> sendEmail(EmailRequestDTO request) {
        return Mono.fromCallable(() -> buildPayload(request))
                .flatMap(payload -> resendClient
                        .post("/emails", ResendSendResponse.class)
                        .withBody(payload)
                        .execute())
                .map(resp -> EmailResponseDTO.success(resp.getId()))
                .onErrorResume(ex -> {
                    log.error("Error sending email via Resend", ex);
                    return Mono.just(EmailResponseDTO.error(ex.getMessage()));
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    private Map<String, Object> buildPayload(EmailRequestDTO req) {
        Map<String, Object> body = new LinkedHashMap<>();
        String from = StringUtils.hasText(req.getFrom()) ? req.getFrom() : properties.getDefaultFrom();
        body.put("from", from);
        body.put("to", req.getTo());
        body.put("subject", req.getSubject());
        if (StringUtils.hasText(req.getHtml())) {
            body.put("html", req.getHtml());
        }
        if (StringUtils.hasText(req.getText())) {
            body.put("text", req.getText());
        }
        if (req.getCc() != null && !req.getCc().isEmpty()) {
            body.put("cc", req.getCc());
        }
        if (req.getBcc() != null && !req.getBcc().isEmpty()) {
            body.put("bcc", req.getBcc());
        }
        if (req.getAttachments() != null && !req.getAttachments().isEmpty()) {
            List<Map<String, Object>> atts = new ArrayList<>();
            for (EmailAttachmentDTO a : req.getAttachments()) {
                if (a == null || a.getContent() == null || a.getContent().length == 0) continue;
                Map<String, Object> att = new HashMap<>();
                att.put("filename", a.getFilename());
                att.put("content", Base64.getEncoder().encodeToString(a.getContent()));
                if (StringUtils.hasText(a.getContentType())) {
                    att.put("content_type", a.getContentType());
                }
                atts.add(att);
            }
            if (!atts.isEmpty()) {
                body.put("attachments", atts);
            }
        }
        return body;
    }

    @Data
    static class ResendSendResponse {
        private String id;
    }
}
