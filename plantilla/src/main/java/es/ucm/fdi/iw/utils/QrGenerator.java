package es.ucm.fdi.iw.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.EnumMap;
import java.util.Map;
 
import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import es.ucm.fdi.iw.LocalData;
import es.ucm.fdi.iw.constants.ConstantsFromFile;

public class QrGenerator {
	
	private static final Logger log = LogManager.getLogger(QrGenerator.class);
	
	public static void generateQrCode(String id, String username) throws UnknownHostException {
		InetAddress inetAddress = InetAddress.getLocalHost();
		String url = "http://" + inetAddress.getHostAddress() + ":" +ConstantsFromFile.PORT + "/user/" + id;
		int size = ConstantsFromFile.QR_IMG_SIZE;
		String fileType = ConstantsFromFile.PNG;
		
		File directory = new File(ConstantsFromFile.QR_DIR);
	    if (! directory.exists()){
	        directory.mkdir();
	    }
	    
	    String QrPath = ConstantsFromFile.QR_DIR + ConstantsFromFile.QR_IMG + username + "." + ConstantsFromFile.PNG;
		File myFile = new File(QrPath);
		
		try {
			
			Map<EncodeHintType, Object> hintMap = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
			hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
			
			// Now with zxing version 3.2.1 you could change border size (white border size to just 1)
			hintMap.put(EncodeHintType.MARGIN, 1); /* default = 4 */
			hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
 
			QRCodeWriter qrCodeWriter = new QRCodeWriter();
			BitMatrix byteMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, size,
					size, hintMap);
			int CrunchifyWidth = byteMatrix.getWidth();
			BufferedImage image = new BufferedImage(CrunchifyWidth, CrunchifyWidth,
					BufferedImage.TYPE_INT_RGB);
			image.createGraphics();
 
			Graphics2D graphics = (Graphics2D) image.getGraphics();
			graphics.setColor(Color.WHITE);
			graphics.fillRect(0, 0, CrunchifyWidth, CrunchifyWidth);
			graphics.setColor(Color.BLACK);
 
			for (int i = 0; i < CrunchifyWidth; i++) {
				for (int j = 0; j < CrunchifyWidth; j++) {
					if (byteMatrix.get(i, j)) {
						graphics.fillRect(i, j, 1, 1);
					}
				}
			}			
			
			ImageIO.write(image, fileType, myFile);
						
		} catch (WriterException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		log.info("\n\nYou have successfully created QR Code for user {}", id);
	}
}
