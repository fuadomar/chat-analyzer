package tone.analyzer.dao;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tone.analyzer.domain.model.Document;

/**
 * Created by Dell on 1/17/2018.
 */
@Component
public class ImageRepositoryImp implements ImageRepository {

    private static final Logger LOG = Logger.getLogger(ImageRepositoryImp.class);

    @Value("${profile.image.repository}")
    private String profileImageStorageLocation;

    @Value("${tone.analyzer.image.repository}")
    private String toneAnalyzerImageStorageLocation;


    @PostConstruct
    public void init() {
        createDirectory(profileImageStorageLocation);
        createDirectory(toneAnalyzerImageStorageLocation);
    }

    @Override
    public void add(Document document, boolean isBAse64Image) throws IOException {

        if (org.springframework.util.StringUtils.isEmpty(document.getName())) {
            LOG.info("file name cant be null");
            throw new IOException();
        }

        if (!isBAse64Image) {
            saveImage(document, profileImageStorageLocation);
        } else {
            saveImage(document, toneAnalyzerImageStorageLocation);
        }
    }

    private void saveImage(Document document, String imageLocation) throws IOException {
        BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(imageLocation, document.getName())));
        stream.write(document.getContent());
        stream.close();
    }

    private void createDirectory(String path) {
        File file = new File(path);
        file.mkdirs();

    }
}