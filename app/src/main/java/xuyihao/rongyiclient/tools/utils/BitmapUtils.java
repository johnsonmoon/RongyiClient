package xuyihao.rongyiclient.tools.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.util.List;

import xuyihao.JohnsonHttpConnector.connectors.http.Downloader;
import xuyihao.rongyiclient.activities.MainActivity;

/**
 * Created by Xuyh at 2016/10/10 上午 09:41.
 */
public class BitmapUtils {
    /**
     * 下载图片到内存并赋值Bitmap列表
     *
     * @param downloader 下载器
     * @param urlList 图片URL
     * @param imageBitMaps 需要赋值的Bitmap列表
     * @param offset 开始位置
     * @param size 长度
     */
    public static void downloadFiles(Downloader downloader, List<String> urlList, List<Bitmap> imageBitMaps, int offset, int size){
        for(int i = offset; i < (offset + size); i++){
            byte[] imageByteArray = downloader.downloadByGet(urlList.get(i));
            imageBitMaps.add(BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length));
        }
    }

    /**
     * 下载图片到本地存储并赋值Bitmap列表
     *
     * @param downloader 下载器
     * @param urlList 图片URL
     * @param imageBitMaps 需要赋值的Bitmap列表
     * @param offset 开始位置
     * @param size 长度
     */
    public static void downloadFilesToLocal(Downloader downloader, List<String> urlList, List<Bitmap> imageBitMaps, int offset, int size){
        for(int i = offset; i < (offset + size); i++){
            String filePathName = MainActivity.BASE_TEMP_FILE_PATH + File.separator + "tempImage" + i +".jpg";
            if(downloader.downloadByGet(filePathName, urlList.get(i))) {
                imageBitMaps.add(BitmapFactory.decodeFile(filePathName));
            }
        }
    }

    /**
     * 从本地存储加载图片到BitMap列表
     *
     * @param filePathNameList 图片路径列表
     * @param imageBitMaps 需要赋值的Bitmap列表
     * @param offset 开始位置
     * @param size 长度
     */
    public static void initializeFiles(List<String> filePathNameList, List<Bitmap> imageBitMaps, int offset, int size){
        for(int i = 0; i < (offset + size); i++){
            if(new File(filePathNameList.get(i)).exists()){
                imageBitMaps.add(BitmapFactory.decodeFile(filePathNameList.get(i)));
            }
        }
    }
}
