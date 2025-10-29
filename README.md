# lib-notifications-resend

Resend email adapter for Firefly Notifications.

## About Resend

[Resend](https://resend.com) is a modern email API service designed for developers. This library provides a Spring Boot integration that implements the Firefly `EmailProvider` interface, allowing you to send transactional emails through Resend's infrastructure.

## Installation

Add the dependency to your `pom.xml`:

```xml path=null start=null
<dependency>
  <groupId>com.firefly</groupId>
  <artifactId>lib-notifications-resend</artifactId>
  <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## Configuration

Configure Resend in your `application.yml`:

```yaml path=null start=null
resend:
  apiKey: ${RESEND_API_KEY}           # Required: Your Resend API key
  defaultFrom: "noreply@example.com"   # Required: Default sender email address
  baseUrl: https://api.resend.com      # Optional: Override for testing (defaults to https://api.resend.com)
```

### Getting Your API Key

1. Sign up at [resend.com](https://resend.com)
2. Navigate to API Keys in your dashboard
3. Create a new API key
4. Set it as an environment variable: `export RESEND_API_KEY=re_...`

## Usage

This library automatically registers a `ResendEmailProvider` bean that implements the `EmailProvider` interface. Use the `EmailService` from `lib-notifications-core` to send emails:

```java path=null start=null
@Autowired
private EmailService emailService;

public void sendWelcomeEmail(String userEmail) {
    EmailRequestDTO request = EmailRequestDTO.builder()
        .to(List.of(userEmail))
        .subject("Welcome!")
        .html("<h1>Welcome to our platform</h1>")
        .text("Welcome to our platform")
        .build();
    
    emailService.sendEmail(request)
        .subscribe(response -> {
            if (response.isSuccess()) {
                log.info("Email sent with ID: {}", response.getMessageId());
            } else {
                log.error("Failed to send email: {}", response.getError());
            }
        });
}
```

## Features

- **HTML and plain text emails**: Send rich HTML emails with plain text fallback
- **CC and BCC**: Include additional recipients via carbon copy or blind carbon copy
- **Attachments**: Attach files with automatic base64 encoding
- **Reactive**: Built on Project Reactor for non-blocking email delivery
- **Automatic retries**: Errors are handled gracefully and logged

### Sending Emails with Attachments

```java path=null start=null
EmailAttachmentDTO attachment = EmailAttachmentDTO.builder()
    .filename("invoice.pdf")
    .content(pdfBytes)
    .contentType("application/pdf")
    .build();

EmailRequestDTO request = EmailRequestDTO.builder()
    .to(List.of("customer@example.com"))
    .subject("Your Invoice")
    .html("<p>Please find your invoice attached.</p>")
    .attachments(List.of(attachment))
    .build();

emailService.sendEmail(request).subscribe();
```

## API Response

Resend returns a unique message ID for each successfully sent email, which is included in the `EmailResponseDTO.messageId` field. You can use this ID to track delivery status via Resend's dashboard or webhooks.
