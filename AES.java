import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES {
	private final int KEYLENGTH = 16;

	private String payload;

	public AES(String payload) {
		if (payload.length() == 33)
			this.payload = payload;
		else {
			this.payload = genKeys();
			System.out.println(
					"Incorrect payload format! Must be (<16 alphanumeric chars>:<16 alphanumeric chars>).\nGenerated random instead: "
							+ payload);
		}
	}

	public AES() {
		this.payload = genKeys();
	}

	public byte[] encrypt(byte[] value) {
		try {
			String[] pk = payload.split("\\:");
			String key = pk[1];
			String initVector = pk[0];
			IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
			SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
			byte[] encrypted = cipher.doFinal(value);
			return Base64.getEncoder().encode(encrypted);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	byte[] trim(byte[] bytes) {
		int i = bytes.length - 1;
		while (i >= 0 && bytes[i] == 0) {
			--i;
		}

		return Arrays.copyOf(bytes, i + 1);
	}

	public byte[] decrypt(byte[] encrypted) {
		try {
			encrypted = trim(encrypted);
			String[] pk = payload.split("\\:");
			String key = pk[1];
			String initVector = pk[0];
			IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
			SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
			byte[] original = cipher.doFinal(Base64.getDecoder().decode(encrypted));
			return original;
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

	private String rndChar() {
		String av = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
		return av.charAt(new Random().nextInt(av.length())) + "";
	}

	private String genKey() {
		String out = "";
		int i = 0;
		while (i < KEYLENGTH) {
			out += rndChar();
			i++;
		}
		return out;
	}

	public String genKeys() {
		return genKey() + ":" + genKey();
	}

	public String getPayload() {
		return payload;
	}

}
