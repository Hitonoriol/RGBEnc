import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;

public class Main {
	static File aespl;
	static AES aes;
	static ImageDecoder imgd;

	public static void main(String[] args) throws Exception {
		aespl = new File("aespayload.key");
		if (!aespl.exists()) {
			aes = new AES();
			putExternal(aespl.getName(), aes.getPayload());
		} else {
			aes = new AES(getExternal(aespl.getName()));
		}

		System.out.println("AES payload restored...\nTo change it, delete aespayload.key");

		imgd = new ImageDecoder();

		if (args.length > 0) {
			runCmd(String.join(" ", args));
			System.exit(0);
		}

		System.out.println(
				"RGBenc\nCommands:\nenc <filepath> | Encode bytes from file to png\ndec <filepath.png> | Decode png-stored bytes to file\naen <filepath> | Encrypt with aes using current payload and encode to png\nade <filepath.png> | Decode png and decrypt aes using current payload\nrel <payloadfile> | Reload payload from file (if filename is not specified, payload will be loaded from aespayload.key)\nreg | Generate temporary payload\nsav <payloadfile> | Save current payload to file (if filename is not specified, payload will be saved to aespayload.key)");
		@SuppressWarnings("resource")
		Scanner sc = new Scanner(System.in);
		while (true) {
			System.out.print(">> ");
			runCmd(sc.nextLine());
		}
	}

	public static void runCmd(String img) throws IOException {
		img = img.trim();
		String cmd = img.substring(0, 3).trim();
		if (img.length() > 3)
			img = img.substring(4).trim();
		File farg = new File(img);
		String fn = System.currentTimeMillis() + "";
		if (cmd.equals("dec")) {
			imgd.decodeRGB(img, System.currentTimeMillis() + "");
			System.out.println("Decoded successfully!");
		} else if (cmd.equals("enc")) {
			imgd.encodeRGB(img, System.currentTimeMillis() + "");
			System.out.println("Encoded successfully!");
		} else if (cmd.equals("aen")) {
			FileUtils.writeByteArrayToFile(new File("enc" + farg.getName()),
					aes.encrypt(Files.readAllBytes(farg.toPath())));
			imgd.encodeRGB(new File("enc" + farg.getName()).getName(), fn);
			new File("enc" + farg.getName()).delete();
			System.out.println("Encrypted and encoded successfully!");
		} else if (cmd.equals("ade")) {
			imgd.decodeRGB(img, fn);
			FileUtils.writeByteArrayToFile(new File(fn), aes.decrypt(Files.readAllBytes(new File(fn).toPath())));
			System.out.println("Decrypted and decoded successfully!");
		} else if (cmd.equals("rel")) {
			if (img.equals(cmd))
				img = "aespayload.key";
			aes = new AES(getExternal(img));
			System.out.println("Reloaded payload from file!");
		} else if (cmd.equals("sav")) {
			if (img.equals(cmd))
				img = "aespayload.key";
			putExternal(img, aes.getPayload());
			System.out.println("Current payload saved to " + img);
		} else if (cmd.equals("reg")) {
			aes = new AES();
			System.out.println("Temporary payload: " + aes.getPayload());
		} else if (cmd.equals("set")) {
			aes = new AES(img);
		} else if (cmd.equals("exi"))
			System.exit(0);
	}

	public static void putExternal(String name, String text) {
		try {
			File file = new File(name);
			PrintWriter pw = new PrintWriter(file);
			pw.print(text);
			pw.close();
		} catch (Exception e) {

		}
	}

	public static String getExternal(String name) {
		try {
			File file = new File(name);
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line1 = br.readLine();
			br.close();
			return line1;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
}