package org.example.onboardingcopilot.tools;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DocAgentService implements AgentTool {

    private final VectorStore vectorStore;
    private final MeterRegistry meterRegistry;

    @Tool(description = """
            Searches the official R2 platform documentation for integration guides, API specs, and troubleshooting info.
            Call this when the partner asks about APIs, endpoints, authentication, webhooks, or any technical specification.
            Always call this before answering any question about how the platform works.
            """)
    public String docAgentTool(@Nullable String request) {

        String query = (request != null && !request.isBlank())
                ? request
                : "integration guide getting started";

        meterRegistry.counter("doc.agent.calls").increment();

        List<Document> documents = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query)
                        .topK(4)
                        .similarityThreshold(0.5)
                        .build()
        );

        log.info("DocAgent query='{}' returned {} chunks", query, documents.size());
        documents.forEach(d -> log.info("  source={} score={}",
                d.getMetadata().get("source"),
                d.getMetadata().get("distance")));

        if (documents.isEmpty()) {
            meterRegistry.counter("doc.agent.empty.results").increment();
            return "No documentation found for this query. Direct the partner to #r2-support on Slack.";
        }

        return documents.stream()
                .map(doc -> {
                    String source = (String) doc.getMetadata()
                            .getOrDefault("source", "unknown");
                    return "[source: " + source + "]\n" + doc.getText();
                })
                .collect(Collectors.joining("\n---\n"));
    }
}

