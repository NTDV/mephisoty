package ru.valkovets.mephisoty.security.credentials;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.annotation.Nullable;
import lombok.Data;

@JacksonXmlRootElement(namespace = "cas", localName = "serviceResponse")
@Data
public class CasUserXml {
@Nullable
@JacksonXmlProperty(namespace = "cas", localName = "authenticationSuccess")
private AuthenticationSuccess authenticationSuccess;

//@Nullable
//@JacksonXmlProperty(namespace = "cas:authenticationFailure", localName = "code", isAttribute = true) // todo fix
//private String code;

@Nullable
@JacksonXmlProperty(namespace = "cas", localName = "authenticationFailure")
private String authenticationFailure;


@Data
public static class AuthenticationSuccess {
  @JacksonXmlProperty(namespace = "cas", localName = "user")
  private String mephiLogin;

  @Nullable
  @JacksonXmlProperty(namespace = "cas", localName = "attributes")
  private Attributes attributes;


  @Data
  public static class Attributes {
    @JacksonXmlProperty(namespace = "cas", localName = "authenticationDate")
    private String authenticationDate;

    @JacksonXmlProperty(namespace = "cas", localName = "longTermAuthenticationRequestTokenUsed")
    private Boolean longTermAuthenticationRequestTokenUsed;

    @JacksonXmlProperty(namespace = "cas", localName = "isFromNewLogin")
    private Boolean isFromNewLogin;

    @JacksonXmlProperty(namespace = "cas", localName = "fio")
    private String fio;

    @JacksonXmlProperty(namespace = "cas", localName = "fullname")
    private String fullName;
  }
}
}