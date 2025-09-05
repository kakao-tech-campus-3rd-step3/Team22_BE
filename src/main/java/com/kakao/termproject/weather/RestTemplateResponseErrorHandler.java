package com.kakao.termproject.weather;

import com.kakao.termproject.weather.exception.ClientErrorException;
import com.kakao.termproject.weather.exception.ServerErrorException;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

@Component
public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {

  @Override
  public boolean hasError(ClientHttpResponse response) throws IOException {
    return response.getStatusCode().isError();
  }

  @Override
  public void handleError(URI url, HttpMethod method, ClientHttpResponse response)
      throws IOException {
    HttpStatusCode statusCode = response.getStatusCode();
    String responseBody = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);

    if (statusCode.is5xxServerError()) {
      throw new ServerErrorException(statusCode, responseBody);
    }

    if (statusCode.is4xxClientError()) {
      throw new ClientErrorException(statusCode, responseBody);
    }
  }

}
