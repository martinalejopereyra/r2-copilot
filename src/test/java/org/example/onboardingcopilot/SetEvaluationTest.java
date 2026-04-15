package org.example.onboardingcopilot;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.onboardingcopilot.config.TestDatabaseInitializer;
import org.example.onboardingcopilot.config.TestFlywayConfig;
import org.example.onboardingcopilot.config.TestMetricsConfig;
import org.example.onboardingcopilot.config.TestObservationConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.http.*;
import org.springframework.web.client.RestClient;

import java.util.Map;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import({TestMetricsConfig.class, TestObservationConfig.class, TestFlywayConfig.class})
@ContextConfiguration(initializers = TestDatabaseInitializer.class)
class SetEvaluationTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("classpath:Test-set.json")
    private Resource testSetResource;

    @LocalServerPort
    private int port;

    private RestClient restClient;

    @Autowired
    private ChatClient chatClient;


    @BeforeEach
    void setup() {
        restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    void goldenSetPassesAllCases() throws IOException {
        List<TestCase> cases = objectMapper.readValue(
                testSetResource.getInputStream(),
                new TypeReference<>() {
                }
        );

        List<String> failures = new ArrayList<>();

        for (TestCase gc : cases) {

            ResponseEntity<String> responseEntity = restClient.post()
                    .uri("/api/v1/chat")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(gc.question())
                    .retrieve()
                    .toEntity(String.class);

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

            String response = responseEntity.getBody() != null ? responseEntity.getBody() : "";

            boolean passed = isResponseCorrect(gc.question(), response, gc.expectedBehavior());

            if (!passed) {
                failures.add("""
                        FAILED: [%s / %s] "%s"
                          expected behavior: %s
                          actual response: %s
                        """.formatted(
                        gc.partnerId(),
                        gc.stage(),
                        gc.question(),
                        gc.expectedBehavior(),
                        response.substring(0, Math.min(300, response.length()))
                ));
            }
        }

        if (!failures.isEmpty()) {
            fail("Test set failures:\n" + String.join("\n", failures));
        }
    }

    private boolean isResponseCorrect(String question, String response, String expectedBehavior) {
        String judgement = chatClient.prompt()
                .user("""
                        You are evaluating whether a chatbot response correctly addresses the expected behavior.
                        
                        Question asked: %s
                        
                        Chatbot response: %s
                        
                        Expected behavior: %s
                        
                        Does the chatbot response correctly address the expected behavior?
                        Answer only YES or NO, nothing else.
                        """.formatted(question, response, expectedBehavior))
                .call()
                .content();

        return judgement != null && judgement.trim().toUpperCase().startsWith("YES");
    }

    private String getToken(String partnerId) {
        ResponseEntity<Map> responseEntity = restClient.post()
                .uri("http://localhost:9999/default/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body("grant_type=client_credentials&client_id=" + partnerId + "&client_secret=secret")
                .retrieve()
                .toEntity(Map.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assert responseEntity.getBody() != null;

        return responseEntity.getBody()
                .get("access_token")
                .toString();
    }

}