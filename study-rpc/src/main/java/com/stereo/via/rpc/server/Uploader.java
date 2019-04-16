package com.stereo.via.rpc.server;

import com.stereo.via.rpc.api.IProgress;
import com.stereo.via.rpc.api.IUpload;
import com.stereo.via.rpc.exc.ProtocolException;
import com.stereo.via.rpc.exc.RpcRuntimeException;
import com.stereo.via.rpc.io.AbstractInput;
import com.stereo.via.rpc.io.AbstractOutput;
import com.stereo.via.rpc.utils.MD5CheckSum;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.String.format;

/**
 * 数据上传
 * 
 * @author liujing
 */
public class Uploader implements IUpload
{

	AbstractInput in;
	AbstractOutput out;

	private int lenght;

	private volatile int cLenght;

	private String targetPath;

	private String fileName;

	private String md5;

	private File tempFile;

	private File repository;

	private static AtomicInteger COUNTER = new AtomicInteger(0);

	private AtomicInteger counter = new AtomicInteger(0);

	private static final String UID = UUID.randomUUID().toString()
			.replace('-', '_');

	private final MD5CheckSum checkSum = new MD5CheckSum();

	public Uploader(AbstractInput in, AbstractOutput out) throws IOException
	{
		this.in = in;
		this.out = out;
		this.lenght = in.readMethodArgLength();
		this.fileName = in.readString();
		this.targetPath = in.readString();
		if (StringUtils.isBlank(targetPath))
			this.targetPath = File.separator;
	}

	@Override
	public boolean upload() throws Exception
	{
		return upload(null);
	}

	@Override
	public boolean upload(IProgress listener) throws Exception {
		File temp = getTempFile();
		byte[] buf = null;
		BufferedOutputStream os = null;
		try
		{
			os = new BufferedOutputStream(new FileOutputStream(temp));
			int len = 0;
			//读取len
			while (IUpload.FILE_UPLOAD_EOF_MARKER != (len = in.readInt()))
			{
				//读取字节
				//buf = new byte[len];
				buf = in.readBytes();
				if (buf.length != len)
					throw new ProtocolException("uploader read packet data actual length error");

				//process checksum
				checkSum.process(buf , 0 , len);

				//写字节
				os.write(buf, 0, len);

				counter.incrementAndGet();
				cLenght += buf.length;

				os.flush();
				if (listener != null)
					listener.update(lenght, cLenght, counter.get());

			}
			os.close();
			if (cLenght != lenght)
				throw new ProtocolException("uploader read total length != actual length error");
			if (checkSum())
			{
				File targetDir = new File(repository, targetPath);
				if (targetDir.isFile() && targetDir.exists())
					targetDir = repository;
				if (!targetDir.exists() && !targetDir.isDirectory())
					targetDir.mkdirs();
				File targetFile = new File(targetDir , fileName);
				if(targetFile.exists())
					targetFile.delete();
				temp.renameTo(targetFile);
				out.writeString(IUpload.FILE_UPLOAD_COMPLETED);
				return true;
			}
			else
			{
				out.writeString(IUpload.FILE_UPLOAD_FAILED);
				if (temp.exists())
					temp.delete();
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (in!=null)
				in.close();
			if (os != null)
				os.close();
			if (temp.exists())
				temp.delete();
			throw new RpcRuntimeException(e);
		} finally
		{
			if (in!=null)
				in.close();
			if (os != null)
				os.close();
		}
	}

	protected boolean checkSum() throws Exception
	{
		md5 = in.readString();
		String localMD5 = checkSum.processed();
		return localMD5.equals(md5);
	}

	private static String getUniqueId() {
		final int limit = 100000000;
		int current = COUNTER.getAndIncrement();
		String id = Integer.toString(current);
		if (current < limit) {
			id = ("00000000" + id).substring(id.length());
		}
		return id;
	}

	protected File getTempFile() {
		if (tempFile == null) {
			String tempFileName = format(fileName + "_%s_%s.tmp", UID, getUniqueId());
			tempFile = new File(repository, tempFileName);
		}
		return tempFile;
	}

	public int getLenght() {
		return lenght;
	}

	public int currentLenght() {
		return cLenght;
	}

	public String getFileName() {
		return fileName;
	}

	public String getMD5String() {
		return md5;
	}

	public void setRepositoryPath(String repositoryPath)
	{
		if (StringUtils.isEmpty(repositoryPath))
			repository = new File(System.getProperty("java.io.tmpdir"));
		else
			repository = new File(repositoryPath);
		this.repository = new File(repositoryPath);
	}

	@Override
	public String toString() {
		return "Uploader{" +
				"lenght=" + lenght +
				", cLenght=" + cLenght +
				", targetPath='" + targetPath + '\'' +
				", fileName='" + fileName + '\'' +
				", md5='" + md5 + '\'' +
				", tempFile=" + tempFile +
				", repository=" + repository +
				", counter=" + counter +
				'}';
	}
}