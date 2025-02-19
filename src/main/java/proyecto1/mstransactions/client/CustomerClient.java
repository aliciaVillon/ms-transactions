package proyecto1.mstransactions.client;

import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import proyecto1.mstransactions.dto.CustomerDTO;
import reactor.core.publisher.Mono;

@Component
public class CustomerClient {

    private final WebClient webClient;

    public CustomerClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8081").build();
    }

    public Mono<CustomerDTO> getCreditById(String customerId) {
        return webClient.get()
                .uri("/v1.0/customers/{id}", customerId)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response ->
                        Mono.error(new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "Cliente no encontrado")))
                .onStatus(HttpStatus::is5xxServerError, response ->
                        Mono.error(new ResponseStatusException(
                                HttpStatus.INTERNAL_SERVER_ERROR, "Error en el servicio ms-customer")))
                .bodyToMono(CustomerDTO.class);
    }
}