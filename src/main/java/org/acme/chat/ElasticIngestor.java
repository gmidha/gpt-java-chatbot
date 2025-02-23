package org.acme.chat;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import dev.langchain4j.data.document.parser.TextDocumentParser;

import java.util.List;
import io.quarkus.logging.Log;


@ApplicationScoped
public class ElasticIngestor {

    @ConfigProperty(name = "documents.dir")
    String documentsDir;

    @Inject
    EmbeddingModel embeddingModel;
    @Inject
    EmbeddingStore<TextSegment> elasticEmbeddingStore;

    public void ingest(@Observes StartupEvent event) {
        Log.infof("Ingesting documents...");
        Log.infof("Embedding Store: "+elasticEmbeddingStore.getClass().getSimpleName());
        Log.infof("Embedding Model: "+embeddingModel.getClass().getSimpleName());

        List<Document> documents = FileSystemDocumentLoader.loadDocuments(documentsDir, new TextDocumentParser());
        Log.infof("Total documents loaded by Apache Tika: "+ documents.size());

        documents
                .forEach(document -> {
                    TextSegment textSegment = document.toTextSegment();
                    Embedding content = embeddingModel.embed(textSegment).content();
                    elasticEmbeddingStore.add(content, textSegment);
                });
    }

}
