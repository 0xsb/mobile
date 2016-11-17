package cmcc.mobile.admin.util;

import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import net.glxn.qrgen.core.AbstractQRCode;
import net.glxn.qrgen.core.exception.QRGenerationException;
import net.glxn.qrgen.core.image.ImageType;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class QRCodeUtil extends AbstractQRCode {

	public static final MatrixToImageConfig DEFAULT_CONFIG = new MatrixToImageConfig();

	public static final String PATH = PropertiesUtil.getAppByKey("FILE_UPLOAD_PATH");

	protected final String text;
	protected MatrixToImageConfig matrixToImageConfig = DEFAULT_CONFIG;

	protected QRCodeUtil(String text) {
		this.text = text;
		qrWriter = new QRCodeWriter();
	}

	/**
	 * Create a QR code from the given text. <br>
	 * <br>
	 * <p>
	 * There is a size limitation to how much you can put into a QR code. This
	 * has been tested to work with up to a length of 2950 characters.<br>
	 * <br>
	 * </p>
	 * <p>
	 * The QRCode will have the following defaults: <br>
	 * {size: 100x100}<br>
	 * {imageType:PNG} <br>
	 * <br>
	 * </p>
	 * Both size and imageType can be overridden: <br>
	 * Image type override is done by calling {@link QRCodeUtil#to(ImageType)}
	 * e.g. QRCode.from("hello world").to(JPG) <br>
	 * Size override is done by calling {@link QRCodeUtil#withSize} e.g.
	 * QRCode.from("hello world").to(JPG).withSize(125, 125) <br>
	 *
	 * @param text
	 *            the text to encode to a new QRCode, this may fail if the text
	 *            is too large. <br>
	 * @return the QRCode object <br>
	 */
	public static QRCodeUtil from(String text) {
		return new QRCodeUtil(text);
	}

	/**
	 * Creates a a QR Code from the given {@link VCard}.
	 * <p>
	 * The QRCode will have the following defaults: <br>
	 * {size: 100x100}<br>
	 * {imageType:PNG} <br>
	 * <br>
	 * </p>
	 * 
	 * @param vcard
	 *            the vcard to encode as QRCode
	 * @return the QRCode object
	 */
	// public static QRCodeUtil from(VCard vcard) {
	// return new QRCodeUtil(vcard.toString());
	// }

	/**
	 * Overrides the imageType from its default
	 * {@link net.glxn.qrgen.core.image.ImageType#PNG}
	 *
	 * @param imageType
	 *            the {@link net.glxn.qrgen.core.image.ImageType} you would like
	 *            the resulting QR to be
	 * @return the current QRCode object
	 */
	public QRCodeUtil to(ImageType imageType) {
		this.imageType = imageType;
		return this;
	}

	/**
	 * Overrides the size of the qr from its default 125x125
	 *
	 * @param width
	 *            the width in pixels
	 * @param height
	 *            the height in pixels
	 * @return the current QRCode object
	 */
	public QRCodeUtil withSize(int width, int height) {
		this.width = width;
		this.height = height;
		return this;
	}

	/**
	 * Overrides the default charset by supplying a
	 * {@link com.google.zxing.EncodeHintType#CHARACTER_SET} hint to
	 * {@link com.google.zxing.qrcode.QRCodeWriter#encode}
	 *
	 * @param charset
	 *            the charset as string, e.g. UTF-8
	 * @return the current QRCode object
	 */
	public QRCodeUtil withCharset(String charset) {
		return withHint(EncodeHintType.CHARACTER_SET, charset);
	}

	/**
	 * Overrides the default error correction by supplying a
	 * {@link com.google.zxing.EncodeHintType#ERROR_CORRECTION} hint to
	 * {@link com.google.zxing.qrcode.QRCodeWriter#encode}
	 *
	 * @param level
	 *            the error correction level to use by
	 *            {@link com.google.zxing.qrcode.QRCodeWriter#encode}
	 * @return the current QRCode object
	 */
	public QRCodeUtil withErrorCorrection(ErrorCorrectionLevel level) {
		return withHint(EncodeHintType.ERROR_CORRECTION, level);
	}

	/**
	 * Sets hint to {@link com.google.zxing.qrcode.QRCodeWriter#encode}
	 *
	 * @param hintType
	 *            the hintType to set
	 * @param value
	 *            the concrete value to set
	 * @return the current QRCode object
	 */
	public QRCodeUtil withHint(EncodeHintType hintType, Object value) {
		hints.put(hintType, value);
		return this;
	}

	@Override
	public File file() {
		File file;
		try {
			file = createTempFile();
			MatrixToImageWriter.writeToPath(createMatrix(text), imageType.toString(), file.toPath(),
					matrixToImageConfig);
		} catch (Exception e) {
			throw new QRGenerationException("Failed to create QR image from text due to underlying exception", e);
		}

		return file;
	}

	@Override
	public File file(String name) {
		File file;
		try {
			file = createTempFile(PATH, name);
			MatrixToImageWriter.writeToPath(createMatrix(text), imageType.toString(), file.toPath(),
					matrixToImageConfig);
		} catch (Exception e) {
			throw new QRGenerationException("Failed to create QR image from text due to underlying exception", e);
		}

		return file;
	}

	/**
	 * 创建二维码的图片文件（继承）
	 */
	protected File createTempFile(String path, String name) throws IOException {
		File file = new File(path + File.separator + name + "." + imageType.toString().toLowerCase());
		if(file.exists()){
			file.delete();
		}
		file.createNewFile();
		file.deleteOnExit();
		return file;
	}

	@Override
	protected void writeToStream(OutputStream stream) throws IOException, WriterException {
		MatrixToImageWriter.writeToStream(createMatrix(text), imageType.toString(), stream, matrixToImageConfig);
	}

	private File createTempSvgFile() throws IOException {
		return createTempSvgFile("QRCode");
	}

	private File createTempSvgFile(String name) throws IOException {
		File file = File.createTempFile(name, ".svg");
		file.deleteOnExit();
		return file;
	}

	public QRCodeUtil withColor(int onColor, int offColor) {
		matrixToImageConfig = new MatrixToImageConfig(onColor, offColor);
		return this;
	}
}
