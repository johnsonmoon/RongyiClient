package xuyihao.rongyiclient.tools.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import xuyihao.rongyiclient.tools.MIME_FileType;

/**
 * 文件操作类
 * 
 * @author Xuyh at 2016年9月19日 下午10:22:55.
 *
 */
public class FileUtils {
	/**
	 * 检查文件路径是否存在，若不存在创建之(包括所有父路径)
	 * 
	 * @param path
	 */
	public static boolean checkAndCreateFilePath(String path) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			flag = file.mkdirs();
		}
		return flag;
	}

	/**
	 * 传入文件名，返回文件对应的MIME类型
	 * 
	 * @param fileName
	 * @return
	 */
	public static MIME_FileType getFileMIMEType(String fileName) {
		int begin = fileName.lastIndexOf(".") + 1;
		String name = fileName.substring(begin);
		if (name.equals("txt")) {
			return MIME_FileType.Text_txt;
		} else if (name.equals("mp3")) {
			return MIME_FileType.Audio_mp3;
		} else if (name.equals("jpg")) {
			return MIME_FileType.Image_jpg;
		} else if (name.equals("png")) {
			return MIME_FileType.Image_png;
		} else if (name.equals("jpeg")) {
			return MIME_FileType.Image_jpeg;
		} else if (name.equals("gif")) {
			return MIME_FileType.Image_gif;
		} else if (name.equals("mp4")) {
			return MIME_FileType.Video_mp4;
		} else if (name.equals("rmvb")) {
			return MIME_FileType.Audio_rmvb;
		} else if (name.equals("avi")) {
			return MIME_FileType.Video_avi;
		} else {
			return MIME_FileType.Application_bin;
		}
	}

	/**
	 * 生成zip压缩包
	 * 
	 * 通过传入的文件名、文件路径名生成相应的zip压缩包
	 *
	 * @param zipFilePathName zip压缩包的文件路径名
	 * @param containedFilePathName 包含文件的文件路径名
	 * @param containedFileName 包含文件的文件名
	 * @return true if succeeded, false if failed
	 */
	public static boolean generateZipPackage(String zipFilePathName, List<String> containedFilePathName,
			List<String> containedFileName) {
		boolean flag = false;
		try {
			byte[] bytes = new byte[1024];
			ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFilePathName));
			for (int i = 0; i < containedFilePathName.size(); i++) {
				FileInputStream fileInputStream = new FileInputStream(containedFilePathName.get(i));
				zipOutputStream.putNextEntry(new ZipEntry(containedFileName.get(i)));
				;
				int length = 0;
				while ((length = fileInputStream.read(bytes)) != -1) {
					zipOutputStream.write(bytes, 0, length);
				}
				zipOutputStream.closeEntry();
				fileInputStream.close();
			}
			zipOutputStream.close();
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		}
		return flag;
	}

	/**
	 * 生成zip压缩包
	 * 
	 * 将目录下的所有文件压缩到zip文件下
	 *
	 * @param filePath 需要压缩的目录
	 * @param zipFilePathName 压缩文件路径名
	 * @return
	 */
	public static boolean generateDirectoryFilesToZipPackage(String filePath, String zipFilePathName) {
		boolean flag = false;
		File sourceFile = new File(filePath);
		if (!sourceFile.exists()) {
			System.out.println("\"" + filePath + "\"" + "Does not exists!");
			flag = false;
		} else {
			try {
				File zipFile = new File(zipFilePathName);
				if (zipFile.exists()) {
					System.out.println(zipFilePathName + " already exists!");
				} else {
					File[] containedFileArray = sourceFile.listFiles();
					if (containedFileArray == null || containedFileArray.length < 1) {
						System.out.println("No files exist in directory " + filePath);
					} else {
						byte[] bytes = new byte[1024];
						ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFilePathName));
						for (int i = 0; i < containedFileArray.length; i++) {
							FileInputStream fileInputStream = new FileInputStream(containedFileArray[i]);
							zipOutputStream.putNextEntry(new ZipEntry(containedFileArray[i].getName()));
							int length = 0;
							while ((length = fileInputStream.read(bytes)) != -1) {
								zipOutputStream.write(bytes, 0, length);
							}
							zipOutputStream.closeEntry();
							fileInputStream.close();
						}
						zipOutputStream.close();
						flag = true;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				flag = false;
			}
		}
		return flag;
	}
}