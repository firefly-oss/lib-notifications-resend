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

import com.firefly.core.notifications.interfaces.dtos.email.v1.EmailRequestDTO;
import com.firefly.core.notifications.interfaces.dtos.email.v1.EmailResponseDTO;
import com.firefly.core.notifications.interfaces.interfaces.providers.email.v1.EmailProvider;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ResendEmailProvider implements EmailProvider {

    @Override
    public Mono<EmailResponseDTO> sendEmail(EmailRequestDTO request) {
        // Placeholder implementation. Replace with actual Resend client usage.
        return Mono.just(EmailResponseDTO.error("Resend provider not yet implemented"));
    }
}