package dag.oldenburg.sascodetest.adapters.driven.soap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;

@Configuration
public class SoapClientConfiguration {

  @Value("${mock-soap-service.url}")
  String soapServiceUrl;

  @Value("${mock-soap-service.context-path}")
  String contextPath;

  @Bean
  public WebServiceTemplate webServiceTemplate() {
    Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
    marshaller.setContextPath(contextPath);
    WebServiceTemplate webServiceTemplate = new WebServiceTemplate(marshaller, marshaller);
    webServiceTemplate.setDefaultUri(soapServiceUrl);

    return webServiceTemplate;
  }

}