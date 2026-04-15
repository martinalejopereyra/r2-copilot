package org.example.onboardingcopilot.tools.provider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.onboardingcopilot.model.OnboardingStatus;
import org.example.onboardingcopilot.tools.LogProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Service
@Profile("!test")
@RequiredArgsConstructor
@Slf4j
public class LokiLogProvider implements LogProvider {

    private final RestClient restClient;

    @Value("${loki.url:http://localhost:3100}")
    private String lokiUrl;

    @Override
    public List<String> getLogs(String partnerId, OnboardingStatus status) {
        try {
            String query = "{container=\"r2-copilot-app\"} |= `" + partnerId + "`";

            long end = System.currentTimeMillis() * 1_000_000L;
            long start = end - (3600L * 1_000_000_000L); // 1 hour ago

            URI uri = UriComponentsBuilder
                    .fromUriString(lokiUrl + "/loki/api/v1/query_range")
                    .queryParam("query", query)
                    .queryParam("start", start)
                    .queryParam("end", end)
                    .queryParam("limit", 20)
                    .queryParam("direction", "backward")
                    .build(false)
                    .encode()
                    .toUri();

            LokiResponse response = restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(LokiResponse.class);

            if (response == null || response.data() == null
                    || response.data().result() == null) {
                return List.of();
            }

            return response.data().result().stream()
                    .flatMap(stream -> stream.values().stream())
                    .map(entry -> entry.get(1))
                    .filter(line -> line.contains("/v1/auth")
                            || line.contains("/v1/webhooks")
                    ).limit(15)
                    .toList();

        } catch (Exception e) {
            log.warn("Failed to fetch logs from Loki for partner={}: {}",
                    partnerId, e.getMessage());
            return List.of();
        }
    }

    record LokiResponse(String status, LokiData data) {
    }

    record LokiData(String resultType, List<LokiStream> result) {
    }

    record LokiStream(Map<String, String> stream, List<List<String>> values) {
    }
}