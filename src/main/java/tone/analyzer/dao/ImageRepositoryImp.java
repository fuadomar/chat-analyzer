package tone.analyzer.dao;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.annotation.PostConstruct;

import javax.imageio.ImageIO;
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

  @Value("${profile.thumb.image.repository}")
  private String profielThumbImageStorageLocation;

  @PostConstruct
  public void init() {
    createDirectory(profileImageStorageLocation);
    createDirectory(toneAnalyzerImageStorageLocation);
    createDirectory(profielThumbImageStorageLocation);
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
      BufferedImage img = ImageIO.read(new ByteArrayInputStream(document.getContent()));
      File outputImageFile = new File(toneAnalyzerImageStorageLocation, document.getName());
      ImageIO.write(img, "png", outputImageFile);
      // saveImage(document, toneAnalyzerImageStorageLocation);
    }
  }

  private void saveImage(Document document, String imageLocation) throws IOException {

    BufferedOutputStream stream =
        new BufferedOutputStream(new FileOutputStream(new File(imageLocation, document.getName())));
    stream.write(document.getContent());
    stream.close();

    //BufferedImage img = ImageIO.read(new ByteArrayInputStream(document.getContent()));
    BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
    img.createGraphics().drawImage(ImageIO.read(new ByteArrayInputStream(document.getContent()))
        .getScaledInstance(100, 100, Image.SCALE_SMOOTH), 0, 0, null);
    ImageIO.write(img, "jpg",
        new FileOutputStream(new File(profielThumbImageStorageLocation, document.getThumbNail())));
  }

  private void createDirectory(String path) {
    File file = new File(path);
    file.mkdirs();
  }

  private BufferedImage scale(BufferedImage source, double ratio) {
    int w = (int) (source.getWidth() * ratio);
    int h = (int) (source.getHeight() * ratio);
    BufferedImage bi = getCompatibleImage(w, h);
    Graphics2D g2d = bi.createGraphics();
    double xScale = (double) w / source.getWidth();
    double yScale = (double) h / source.getHeight();
    AffineTransform at = AffineTransform.getScaleInstance(xScale, yScale);
    g2d.drawRenderedImage(source, at);
    g2d.dispose();
    return bi;
  }

  private BufferedImage getCompatibleImage(int w, int h) {
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice gd = ge.getDefaultScreenDevice();
    GraphicsConfiguration gc = gd.getDefaultConfiguration();
    BufferedImage image = gc.createCompatibleImage(w, h);
    return image;
  }

}
