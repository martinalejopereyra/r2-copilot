package org.example.onboardingcopilot.tools;

import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentIngestionService {

    private final VectorStore vectorStore;

    @Value("${ai.docs.path:./docs_folder}")
    private String docsPath;

    @EventListener(ApplicationReadyEvent.class)
    @Observed(name = "document.ingestion")
    public void autoIngest() {
        log.info("🚀 Starting R2 documentation ingestion from: {}", docsPath);

        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("file:" + docsPath + "/*.md");

            if (resources.length == 0) {
                log.warn("⚠️ No R2 documentation found at {}", docsPath);
                return;
            }

            var tokenSplitter = TokenTextSplitter.builder()
                    .withChunkSize(250)
                    .withMinChunkSizeChars(100)
                    .withMinChunkLengthToEmbed(5)
                    .withMaxNumChunks(10000)
                    .withKeepSeparator(true)
                    .build();

            for (Resource res : resources) {
                log.info("📄 Processing file: {}", res.getFilename());

                // 2. Extract content
                TextReader reader = new TextReader(res);
                List<Document> documents = reader.get();

                // attach source metadata before splitting
                documents.forEach(doc ->
                        doc.getMetadata().put("source", res.getFilename())
                );

                // 3. Transform (Split)
                List<Document> splitDocs = tokenSplitter.apply(documents);

                // 4. Load into Qdrant
                vectorStore.accept(splitDocs);
            }

            log.info("✅ Ingestion complete. Knowledge base is ready.");

        } catch (Exception e) {
            log.error("❌ Fatal error during ingestion: {}", e.getMessage());
        }
    }
}
