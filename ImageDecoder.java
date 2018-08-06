import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

public class ImageDecoder {
	private byte[] decodeRGB(File file) throws IOException {
		BufferedImage image = ImageIO.read(file);
		byte[] out = new byte[image.getWidth() * image.getHeight() * 3];
		int x = 0, y = 0, clr = 0, ptr = 0;
		Color color;
		while (y < image.getHeight()) {
			while (x < image.getWidth()) {
				clr = image.getRGB(x, y);
				color = new Color(clr);
				out[ptr] = (byte) color.getRed();
				out[++ptr] = (byte) color.getGreen();
				out[++ptr] = (byte) color.getBlue();
				ptr++;
				x++;
			}
			x = 0;
			y++;
		}
		return out;
	}

	public void decodeRGB(String imageName, String outputName) throws IOException {
		File out = new File(outputName);
		FileUtils.writeByteArrayToFile(out, decodeRGB(new File(imageName)));
	}

	private BufferedImage encodeRGB(File file) throws IOException {
		byte[] buf = Files.readAllBytes(file.toPath());
		int ar = buf.length, maxwidth = (int) Math.ceil(ar / 3.0);
		BigInteger mw = BigInteger.valueOf(maxwidth);
		BigInteger height = new FermatsFactoring().factoring(mw);
		BigInteger width = (mw.divide(height));
		System.out.println("Creating image: " + width + "x" + height);
		BufferedImage out = new BufferedImage(width.intValue(), height.intValue(), BufferedImage.TYPE_INT_RGB);
		int x = 0, y = 0, ptr = 0, dl = 0;
		Color cbf = new Color(0, 0, 0);
		while (y < height.intValue()) {
			while (x < width.intValue()) {
				dl = ar - ptr;
				try {
					if (dl > 2)
						cbf = new Color(buf[ptr] & 0xFF, buf[++ptr] & 0xFF, buf[++ptr] & 0xFF);
					else if (dl == 2)
						cbf = new Color(buf[ptr] & 0xFF, buf[++ptr] & 0xFF, 0);
					else if (dl == 1)
						cbf = new Color(buf[ptr] & 0xFF, 0, 0);
					ptr++;
					out.setRGB(x, y, cbf.getRGB());
				} catch (Exception e) {
					e.printStackTrace();
					return out;
				}
				x++;
			}
			x = 0;
			y++;
		}
		return out;
	}

	public void encodeRGB(String inpFile, String outImage) throws IOException {
		ImageIO.write(encodeRGB(new File(inpFile)), "png",
				new File(FilenameUtils.getExtension(inpFile) + "_" + outImage + ".png"));
	}
}
