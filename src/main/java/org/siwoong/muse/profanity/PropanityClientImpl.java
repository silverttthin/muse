package org.siwoong.muse.profanity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


@Slf4j
@Component
@RequiredArgsConstructor
public class PropanityClientImpl implements PropanityClient {

    private final RestTemplate restTemplate;

    @Value("${recommender.base-url}")
    private String baseUrl;

    @Override
    public Boolean isProfanity(String text) {
        String url = UriComponentsBuilder
            .fromHttpUrl(baseUrl)
            .path("/profanity/check")
            .queryParam("text", text)
            .toUriString();

        Boolean response =
            restTemplate.getForObject(url, Boolean.class);

        log.info("Is {} profanity? : {}", text, response);
        return response;
    }
}
