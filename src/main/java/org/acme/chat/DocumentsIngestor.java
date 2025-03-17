package org.acme.chat;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.IngestionResult;
import io.minio.MinioClient;
import io.quarkus.logging.Log;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.acme.chat.minio.MinioDocumentLoader;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.File;
import java.util.List;

import static dev.langchain4j.data.document.splitter.DocumentSplitters.recursive;


@ApplicationScoped
public class DocumentsIngestor {

    @ConfigProperty(name = "minio.bucket-name")
    String bucketName;

    @Inject
    EmbeddingModel embeddingModel;

    @Inject
    EmbeddingStore<TextSegment> elasticEmbeddingStore;

    @Inject
    MinioClient minioClient;

    public void ingest(@Observes StartupEvent event) throws Exception {
        Log.infof("Ingesting documents...");
        Log.infof("Embedding Store: " + elasticEmbeddingStore.getClass().getSimpleName());
        Log.infof("Embedding Model: " + embeddingModel.getClass().getSimpleName());

        //List<Document> documents = FileSystemDocumentLoader.loadDocuments(new File("/Users/sshaaf/git/rag-documents").toPath(), new ApacheTikaDocumentParser());
        List<Document> documents = new MinioDocumentLoader(minioClient).loadDocuments(bucketName, new ApacheTikaDocumentParser());
        Log.infof("Total documents loaded by Apache Tika: " + documents.size());

        var ingestor = EmbeddingStoreIngestor.builder()
                .embeddingStore(elasticEmbeddingStore)
                .embeddingModel(embeddingModel)
                .documentSplitter(recursive(5000, 0))
                .build();

        IngestionResult result = ingestor.ingest(documents);
        System.out.println("Tokens count: "+result.tokenUsage());
        Log.infof("Ingested %d documents.%n", documents.size());
    }
}
