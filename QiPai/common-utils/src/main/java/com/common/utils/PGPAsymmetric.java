package com.common.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Iterator;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPCompressedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPEncryptedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedDataList;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPObjectFactory;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyEncryptedData;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyDataDecryptorFactoryBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyKeyEncryptionMethodGenerator;
import org.bouncycastle.util.io.Streams;

/**
 * PGP文件加密工具
 *
 * @author 张成轩
 */
public class PGPAsymmetric {

	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	/**
	 * 获取私钥对象
	 * 
	 * @param skc
	 *            密钥集合对象
	 * @param keyId
	 *            密钥ID
	 * @param passowrd
	 *            密码
	 * @return 私钥对象
	 * @throws PGPException
	 * @throws NoSuchProviderException
	 */
	@SuppressWarnings("deprecation")
	private static PGPPrivateKey privateKey(PGPSecretKeyRingCollection skc, long keyId, String passowrd)
			throws PGPException, NoSuchProviderException {

		// 获取密钥对象
		PGPSecretKey sk = skc.getSecretKey(keyId);
		if (sk == null)
			return null;
		// 获取私钥对象
		return sk.extractPrivateKey(passowrd.toCharArray(), "BC");
	}

	/**
	 * 获取公钥对象
	 * 
	 * @param file
	 *            文件对象
	 * @return 公钥对象
	 * @throws IOException
	 * @throws PGPException
	 */
	private static PGPPublicKey publicKey(File file) throws IOException, PGPException {

		// 密钥流
		InputStream is = new BufferedInputStream(new FileInputStream(file));
		PGPPublicKey sk;
		try {
			// 获取公钥对象
			sk = publicKey(is);
		} catch (IOException e) {
			throw e;
		} catch (PGPException e) {
			throw e;
		} finally {
			try {
				is.close();
			} catch (IOException e) {
			}
		}
		return sk;
	}

	/**
	 * 获取公钥对象
	 * 
	 * @param is
	 *            流
	 * @return 公钥对象
	 * @throws IOException
	 * @throws PGPException
	 */
	private static PGPPublicKey publicKey(InputStream is) throws IOException, PGPException {

		// 密钥集
		PGPPublicKeyRingCollection krc = new PGPPublicKeyRingCollection(PGPUtil.getDecoderStream(is));
		// 遍历密钥集获取公钥
		Iterator<?> kri = krc.getKeyRings();
		while (kri.hasNext()) {
			PGPPublicKeyRing kr = (PGPPublicKeyRing) kri.next();
			Iterator<?> pki = kr.getPublicKeys();
			while (pki.hasNext()) {
				PGPPublicKey pk = (PGPPublicKey) pki.next();
				if (pk.isEncryptionKey())
					return pk;
			}
		}
		throw new IllegalArgumentException("Can't find key in ring.");
	}

	/**
	 * 加密文件
	 * 
	 * @param keyFile
	 *            密钥文件对象
	 * @param file
	 *            文件对象
	 * @param targetFile
	 *            目标文件对象
	 * @param armor
	 *            加壳
	 * @throws IOException
	 * @throws NoSuchProviderException
	 * @throws PGPException
	 */
	public static void encryptFile(File file, File targetFile, File keyFile, boolean armor)
			throws IOException, NoSuchProviderException, PGPException {

		// 获取输出文件流
		OutputStream os = new BufferedOutputStream(new FileOutputStream(targetFile));
		try {
			// 获取私钥
			PGPPublicKey pk = publicKey(keyFile);
			// 加密文件
			encryptFile(file, os, pk, armor);
		} catch (IOException e) {
			throw e;
		} catch (PGPException e) {
			throw e;
		} finally {
			try {
				os.close();
			} catch (IOException e) {
			}
		}
	}

	/**
	 * 加密文件
	 * 
	 * @param keyFile
	 *            密钥文件对象
	 * @param file
	 *            文件对象
	 * @param targetFile
	 *            目标文件对象
	 * @param armor
	 *            加壳
	 * @throws IOException
	 * @throws NoSuchProviderException
	 * @throws PGPException
	 */
	public static void encryptFile(File file, File targetFile, InputStream kis, boolean armor)
			throws IOException, NoSuchProviderException, PGPException {

		// 获取输出文件流
		OutputStream os = new BufferedOutputStream(new FileOutputStream(targetFile));
		try {
			// 获取私钥
			PGPPublicKey pk = publicKey(kis);
			// 加密文件
			encryptFile(file, os, pk, armor);
		} catch (IOException e) {
			throw e;
		} catch (PGPException e) {
			throw e;
		} finally {
			try {
				os.close();
			} catch (IOException e) {
			}
		}
	}

	/**
	 * 加密文件
	 * 
	 * @param pk
	 *            密钥对象
	 * @param file
	 *            文件对象
	 * @param os
	 *            输出流
	 * @param armor
	 *            加壳
	 * @throws IOException
	 * @throws PGPException
	 * @throws NoSuchProviderException
	 */
	public static void encryptFile(File file, OutputStream os, PGPPublicKey pk, boolean armor)
			throws IOException, PGPException, NoSuchProviderException {

		// 判断是否加壳
		if (armor)
			os = new ArmoredOutputStream(os);
		// 获取构建器
		JcePGPDataEncryptorBuilder builder = new JcePGPDataEncryptorBuilder(PGPEncryptedData.AES_256);
		// 设置参数
		builder.setWithIntegrityPacket(false).setSecureRandom(new SecureRandom()).setProvider("BC");
		// 加密对象
		PGPEncryptedDataGenerator cPk = new PGPEncryptedDataGenerator(builder);
		cPk.addMethod(new JcePublicKeyKeyEncryptionMethodGenerator(pk).setProvider("BC"));
		// 输出流
		OutputStream nos = null;
		PGPCompressedDataGenerator comData = null;
		try {
			// 加密文件
			nos = cPk.open(os, new byte[1 << 16]);
			comData = new PGPCompressedDataGenerator(PGPCompressedData.ZIP);
			PGPUtil.writeFileToLiteralData(comData.open(nos), PGPLiteralData.BINARY, file, new byte[1 << 16]);
		} catch (IOException e) {
			throw e;
		} catch (PGPException e) {
			throw e;
		} finally {
			if (comData != null)
				try {
					comData.close();
				} catch (Exception e) {
				}
			if (nos != null)
				try {
					nos.close();
				} catch (Exception e) {
				}
		}
	}

	/**
	 * 解密文件
	 * 
	 * @param file
	 *            文件对象
	 * @param targetFile
	 *            目标文件对象
	 * @param keyFile
	 *            密钥文件对象
	 * @param password
	 *            密码
	 * @throws IOException
	 * @throws NoSuchProviderException
	 * @throws PGPException
	 */
	public static void decryptFile(File file, File targetFile, File keyFile, String password)
			throws IOException, NoSuchProviderException, PGPException {

		InputStream is = null;
		InputStream kis = null;
		try {
			// 文件流
			is = new BufferedInputStream(new FileInputStream(file));
			// 密钥文件流
			kis = new BufferedInputStream(new FileInputStream(keyFile));
			// 解密文件
			decryptFile(is, targetFile, kis, password);
		} catch (IOException e) {
			throw e;
		} finally {
			if (is != null)
				try {
					is.close();
				} catch (Exception e) {
				}
			if (kis != null)
				try {
					kis.close();
				} catch (Exception e) {
				}
		}
	}

	/**
	 * @param is
	 *            文件流
	 * @param targetFile
	 *            目标文件
	 * @param kis
	 *            密钥流
	 * @param password
	 *            密码
	 * @throws IOException
	 * @throws PGPException
	 */
	public static void decryptFile(InputStream is, File targetFile, InputStream kis, String password)
			throws IOException, PGPException {

		PGPObjectFactory factory = new PGPObjectFactory(PGPUtil.getDecoderStream(is));
		PGPEncryptedDataList enc;
		Object o = factory.nextObject();
		if (o instanceof PGPEncryptedDataList)
			enc = (PGPEncryptedDataList) o;
		else
			enc = (PGPEncryptedDataList) factory.nextObject();
		Iterator<?> it = enc.getEncryptedDataObjects();
		PGPPrivateKey sKey = null;
		PGPPublicKeyEncryptedData pbe = null;
		PGPSecretKeyRingCollection pgpSec = new PGPSecretKeyRingCollection(PGPUtil.getDecoderStream(kis));
		while (sKey == null && it.hasNext()) {
			pbe = (PGPPublicKeyEncryptedData) it.next();
			try {
				sKey = privateKey(pgpSec, pbe.getKeyID(), password);
			} catch (NoSuchProviderException e) {
			} catch (PGPException e) {
			}
		}
		if (sKey == null)
			throw new PGPException("secret key not found.");

		InputStream keyIS = pbe
				.getDataStream(new JcePublicKeyDataDecryptorFactoryBuilder().setProvider("BC").build(sKey));
		PGPObjectFactory fac = new PGPObjectFactory(keyIS);
		Object message = fac.nextObject();
		if (message instanceof PGPCompressedData) {
			PGPCompressedData cData = (PGPCompressedData) message;
			InputStream cis = new BufferedInputStream(cData.getDataStream());
			PGPObjectFactory objFac = new PGPObjectFactory(cis);
			message = objFac.nextObject();
			while (!(message instanceof PGPLiteralData)) {
				message = objFac.nextObject();
			}
		}
		PGPLiteralData ld = (PGPLiteralData) message;
		InputStream ldis = ld.getInputStream();
		OutputStream os = new BufferedOutputStream(new FileOutputStream(targetFile));
		try {
			Streams.pipeAll(ldis, os);
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				os.close();
			} catch (IOException e) {
			}
		}

	}

	public static void main(String[] args) {
		// File file = new File("data/PX0100_20161207_1.txt.GPG");
		File keyFile = new File("data/forturnwingclub.gpg");
		String password = "hna.201400";
		File filedir = new File("data/srcFile");
		File[] files = filedir.listFiles();
		for (File file : files) {
			// file = new File("data/srcFile/PX0100_20161214_1.txt.GPG");
			File targetFile = new File("data/targetFile/" + file.getName() + ".txt");
			try {
				PGPAsymmetric.decryptFile(file, targetFile, keyFile, password);
			} catch (NoSuchProviderException | IOException | PGPException e) {
				System.out.println("失败的文件：" + file.getName());
				e.printStackTrace();

			}
		}

	}
}