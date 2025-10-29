# lib-notifications-resend

Resend email adapter for Firefly Notifications.

## Install
```xml path=null start=null
<dependency>
  <groupId>com.firefly</groupId>
  <artifactId>lib-notifications-resend</artifactId>
  <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## Configuration (application.yml)
```yaml path=null start=null
resend:
  apiKey: ${RESEND_API_KEY}
  defaultFrom: "noreply@example.com"   # used when request.from is empty
  # baseUrl: https://api.resend.com      # override for tests
```

## Features
- HTML/text email, CC/BCC
- Attachments (filename, content base64-encoded, content_type)

Use `EmailService` from the core library; this adapter provides the provider bean.
