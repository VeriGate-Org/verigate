package verigate.adapter.dharesponse.infrastructure.email;

import jakarta.mail.BodyPart;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import java.io.ByteArrayInputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.dharesponse.application.handlers.DefaultProcessDhaResponseCommandHandler;

/** Parses MIME-formatted email messages. */
public class MimeEmailParser
    implements DefaultProcessDhaResponseCommandHandler.MimeParser {

  private static final Logger logger =
      LoggerFactory.getLogger(MimeEmailParser.class);

  @Override
  public DefaultProcessDhaResponseCommandHandler.ParsedEmail parse(
      byte[] rawEmail) {
    try {
      Session session =
          Session.getDefaultInstance(new Properties());
      MimeMessage message = new MimeMessage(
          session, new ByteArrayInputStream(rawEmail));

      String from =
          message.getFrom() != null
              && message.getFrom().length > 0
              ? message.getFrom()[0].toString() : "";
      String subject = message.getSubject() != null
          ? message.getSubject() : "";
      String inReplyTo =
          message.getHeader("In-Reply-To") != null
              ? message.getHeader("In-Reply-To")[0] : null;

      String body = extractTextBody(message);

      return new DefaultProcessDhaResponseCommandHandler
          .ParsedEmail(from, subject, body, inReplyTo);
    } catch (Exception e) {
      throw new DefaultProcessDhaResponseCommandHandler
          .PermanentProcessingException(
              "Failed to parse MIME email: " + e.getMessage());
    }
  }

  private String extractTextBody(MimeMessage message)
      throws Exception {
    Object content = message.getContent();
    if (content instanceof String text) {
      return text;
    }
    if (content instanceof MimeMultipart multipart) {
      return extractTextFromMultipart(multipart);
    }
    return "";
  }

  private String extractTextFromMultipart(
      MimeMultipart multipart) throws Exception {
    StringBuilder text = new StringBuilder();
    for (int i = 0; i < multipart.getCount(); i++) {
      BodyPart part = multipart.getBodyPart(i);
      if (part.isMimeType("text/plain")) {
        text.append(part.getContent().toString());
      } else if (part.isMimeType("text/html")
          && text.isEmpty()) {
        // Fallback to HTML if no plain text found
        text.append(part.getContent().toString());
      } else if (part.getContent()
          instanceof MimeMultipart nestedMultipart) {
        text.append(
            extractTextFromMultipart(nestedMultipart));
      }
    }
    return text.toString();
  }
}
