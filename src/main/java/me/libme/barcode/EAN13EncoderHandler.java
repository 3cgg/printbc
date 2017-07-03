package me.libme.barcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.oned.EAN13Writer;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;

public class EAN13EncoderHandler {


    /**
     * 编码
     * @param contents
     * @param width
     * @param height
     * @param imgPath
     */
    public void encode(String contents, int width, int height, String imgPath) {
        int codeWidth = 3 + // start guard
                (7 * 6) + // left bars
                5 + // middle guard
                (7 * 6) + // right bars
                3; // end guard
        codeWidth = Math.max(codeWidth, width);
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(contents,
                    BarcodeFormat.PDF_417, codeWidth, height, null);

            MatrixToImageWriter
                    .writeToPath(bitMatrix, "png", new File(imgPath).toPath());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param codeS String coded to a barcode
     * @param qrfile File with generated image when completed.
     * @param width in pixels
     * @param height in pixels
     * @param fontSize in pixels
     * @throws Exception
     */
    public void generateBarCode(String codeS,
                                File qrfile,
                                int width,
                                int height,
                                int fontSize) throws Exception {
        int codeWidth = 3 + // start guard
                (7 * 6) + // left bars
                5 + // middle guard
                (7 * 6) + // right bars
                3; // end guard
        codeWidth = Math.max(codeWidth, width);
        FileOutputStream qrCode = null;
        try {
            // Encode URL in QR format
            BitMatrix matrix;
            com.google.zxing.Writer writer = new EAN13Writer();
            //writer = new QRCodeWriter(); in case of BarcodeFormat.QR_CODE
            try {
                matrix = writer.encode(codeS, BarcodeFormat.EAN_13, codeWidth, height);
            } catch (WriterException e) {
                //logger.error("Error generando el QR", e);
                throw new Exception("Error generando el QR");
            }
            // Create buffered image to draw to
            BufferedImage image = new BufferedImage(codeWidth,
                    height, BufferedImage.TYPE_INT_RGB);
            // Iterate through the matrix and draw the pixels to the image
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < codeWidth; x++) {
                    int grayValue = (matrix.get(x, y) ? 0 : 1) & 0xff;
                    image.setRGB(x, y, (grayValue == 0 ? 0 : 0xFFFFFF));
                }
            }
            Graphics graphics = image.getGraphics();
            graphics.drawImage(image, 0, 0, null);


            Font f = new Font("Arial", Font.PLAIN, fontSize);

            FontRenderContext frc = image.getGraphics().getFontMetrics().getFontRenderContext();
            Rectangle2D rect = f.getStringBounds(codeS, frc);
            graphics.setColor(Color.red);

            //add 10 pixels to width to get 5 pixels of padding in left/right
            //add 6 pixels to height to get 3 pixels of padding in top/bottom
            graphics.fillRect(
                    (int)Math.ceil((image.getWidth()/2)-((rect.getWidth()+10)/2)),
                    (int)Math.ceil(image.getHeight() - (rect.getHeight()+6)),
                    (int)Math.ceil(rect.getWidth()+10),
                    (int)Math.ceil(rect.getHeight()+6));
            // add the watermark text
            graphics.setFont(f);
            graphics.setColor(Color.BLACK);
            graphics.drawString(codeS,
                    (int)Math.ceil((image.getWidth()/2)-((rect.getWidth())/2)),
                    (int)Math.ceil(image.getHeight() - 6));
            graphics.dispose();

            qrCode = new FileOutputStream(qrfile);
            ImageIO.write(image, "png", qrCode);

        } catch (Exception ex) {
            throw new Exception("Error generando el QR");
        } finally {
            try {
                qrCode.close();
            } catch (Exception ex) {
                throw new Exception("Error generando el QR");
            }
        }
    }



    /**
     * 编码
     * @param contents
     * @param width
     * @param height
     * @param imgPath
     */
    public void encode2(String contents, int width, int height,int fontSize, String imgPath) {
        int codeWidth = 3 + // start guard
                (7 * 6) + // left bars
                5 + // middle guard
                (7 * 6) + // right bars
                3; // end guard
        codeWidth = Math.max(codeWidth, width);
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(contents,
                    BarcodeFormat.EAN_13, codeWidth, height, null);

            MatrixToImageConfig config=new MatrixToImageConfig();

            BufferedImage original= MatrixToImageWriter.toBufferedImage(bitMatrix,config);

            height=original.getHeight();
            codeWidth=original.getWidth();

            int realWidth=codeWidth+0;
            int realHeight=height+16;

            //add 10 pixels to width to get 5 pixels of padding in left/right
            // Create buffered image to draw to
            BufferedImage image = new BufferedImage(
                    realWidth,
                    realHeight, BufferedImage.TYPE_INT_RGB);

            for (int y = 0; y < realHeight; y++) {
                for (int x = 0; x < realWidth; x++) {
                    image.setRGB(x, y, Color.white.getRGB());
                }
            }


            // Iterate through the matrix and draw the pixels to the image
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < codeWidth; x++) {
                    image.setRGB(x, y, original.getRGB(x,y));
                }
            }


            Graphics graphics = image.getGraphics();
            graphics.drawImage(image, 0, 0, null);


            Font f = new Font("Arial", Font.PLAIN, fontSize);

            FontRenderContext frc = image.getGraphics().getFontMetrics().getFontRenderContext();
            Rectangle2D rect = f.getStringBounds(contents, frc);

            //graphics.setColor(Color.red);

            //add 10 pixels to width to get 5 pixels of padding in left/right
            //add 6 pixels to height to get 3 pixels of padding in top/bottom
//            graphics.fillRect(
//                    (int)Math.ceil((image.getWidth()/2)-((rect.getWidth()+10)/2)),
//                    (int)Math.ceil(image.getHeight() - rect.getHeight()+3),
//                    (int)Math.ceil(rect.getWidth()+10),
//                    (int)Math.ceil(rect.getHeight()+6));
            // add the watermark text
            graphics.setFont(f);
            graphics.setColor(Color.black);
            graphics.drawString(contents,
                    (int)Math.ceil((image.getWidth()/2)-((rect.getWidth())/2)),
                    (int)Math.ceil(image.getHeight() - 2));
            graphics.dispose();


            ImageIO.write(image, "png", new File(imgPath));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        String imgPath = "d:/zxing_EAN13.png";
        // 益达无糖口香糖的条形码
        String contents = "6923450657713";

        int width = 105, height = 50;
        EAN13EncoderHandler handler = new EAN13EncoderHandler();
        //handler.encode(contents, width, height, imgPath);

        //handler.generateBarCode(contents, new File(imgPath), width,height,5);
        handler.encode2(contents, width, height,13,imgPath);

        System.out.println("Michael ,you have finished zxing EAN13 encode.");
    }




}
