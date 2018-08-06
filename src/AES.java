import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class AES {

	private byte[] key = new byte[16], iv = new byte[16];

	public AES(String payload) {
		this.key = convKey(payload);
		genIV();

	}

	public AES(byte[] payload) {
		this.key = payload;
		genIV();
	}

	private byte[] convKey(String pas) {
		try {
			char[] password = pas.toCharArray();
			byte[] salt = new byte[16];
			new Random().nextBytes(salt);
			SecretKeyFactory kf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			PBEKeySpec spec = new PBEKeySpec(password, salt, 8192, 256);
			SecretKey tmp = kf.generateSecret(spec);
			SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
			byte[] key = secret.getEncoded();
			MessageDigest sha = MessageDigest.getInstance("SHA-1");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16);
			return key;
		} catch (Exception e) {
			e.printStackTrace();
			return (genKey());
		}
	}

	public AES() {
		genKeys();
	}

	public byte[] encrypt(byte[] value) {
		try {
			byte[] key = this.key;
			byte[] initVector = this.iv;
			IvParameterSpec iv = new IvParameterSpec(initVector);
			SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
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

	private void genIV() {
		try {
			MessageDigest sha;
			sha = MessageDigest.getInstance("SHA-1");
			this.iv = sha.digest(key);
			this.iv = Arrays.copyOf(this.iv, 16);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

	}

	public byte[] decrypt(byte[] encrypted) {
		try {
			encrypted = trim(encrypted);
			byte[] key = this.key;
			byte[] initVector = this.iv;
			IvParameterSpec iv = new IvParameterSpec(initVector);
			SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
			byte[] original = cipher.doFinal(Base64.getDecoder().decode(encrypted));
			return original;
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

	private byte[] genKey() {
		byte[] buf = new byte[16];
		new Random().nextBytes(buf);
		return buf;
	}

	public void genKeys() {
		this.key = genKey();
		genIV();
	}

	public byte[] getPayload() {
		return key;
	}

}
