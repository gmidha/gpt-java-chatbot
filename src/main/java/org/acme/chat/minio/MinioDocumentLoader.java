package org.acme.chat.minio;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentLoader;
import dev.langchain4j.data.document.DocumentParser;
import io.minio.GetObjectArgs;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.messages.Item;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@ApplicationScoped
public class MinioDocumentLoader {


    private final MinioClient minioClient;

    public MinioDocumentLoader(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public Document loadDocument(String bucket, String fileName, DocumentParser parser) throws Exception {
        // Retrieve file from MinIO
        InputStream inputStream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucket)
                        .object(fileName)
                        .build()
        );

        MinioS3Source source = new MinioS3Source(inputStream, bucket, fileName);
        return DocumentLoader.load(source, parser);

    }

    public List<Document> loadDocuments(String bucketName, DocumentParser parser) {
        return StreamSupport.stream(minioClient.listObjects(
                        ListObjectsArgs.builder()
                                .bucket(bucketName)
                                .build()
                ).spliterator(), false)
                .map(result -> {
                    try {
                        return result.get(); // Extracts Item from Result<Item>
                    } catch (Exception e) {
                        throw new RuntimeException("Error retrieving file from MinIO", e);
                    }
                })
                .map(Item::objectName)
                .map(fileName -> {
                    try {
                        return loadDocument(bucketName, fileName, parser);
                    } catch (Exception e) {
                        throw new RuntimeException("Error loading document: " + fileName, e);
                    }
                })
                .collect(Collectors.toList());
    }

}
