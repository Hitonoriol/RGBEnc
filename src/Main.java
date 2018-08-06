
import java.io.File;

import java.io.IOException;

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
			FileUtils.writeByteArrayToFile(aespl, aes.getPayload());
			System.out.println("Generated new aespayload! To set your own key, type [key <yourkey>]");
		} else {
			aes = new AES(getExternal(aespl.getName()));
			System.out.println("Restored aespayload from file!");
		}

		imgd = new ImageDecoder();

		if (args.length > 0) {
			runCmd(String.join(" ", args));
			System.exit(0);
		}

		System.out.println(
				"RGBenc\nCommands:\nenc <filepath> | Encode bytes from file to png\ndec <filepath.png> | Decode png-stored bytes to file\naen <filepath> | Encrypt with aes using current payload and encode to png\nade <filepath.png> | Decode png and decrypt aes using current payload\nrel <payloadfile> | Reload payload from file (if filename is not specified, payload will be loaded from aespayload.key)\ngen | Generate new random key\nsav <payloadfile> | Save current payload to file (if filename is not specified, payload will be saved to aespayload.key)\nkey <yourkey> | Set your own encryption key");
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
			FileUtils.writeByteArrayToFile(new File(img), aes.getPayload());
			System.out.println("Current payload saved to " + img);
		} else if (cmd.equals("gen")) {
			aes = new AES();
			FileUtils.writeByteArrayToFile(aespl, aes.getPayload());
		} else if (cmd.equals("key")) {
			aes = new AES(img);
			FileUtils.writeByteArrayToFile(aespl, aes.getPayload());
		} else if (cmd.equals("exi"))
			System.exit(0);
	}

	public static byte[] getExternal(String name) {
		try {
			return Files.readAllBytes(new File(name).toPath());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}