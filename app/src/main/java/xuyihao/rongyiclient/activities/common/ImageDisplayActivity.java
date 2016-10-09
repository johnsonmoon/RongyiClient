package xuyihao.rongyiclient.activities.common;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import xuyihao.rongyiclient.R;

/**
 * 显示图片的activity
 * <pre>
 *    设计思路：
 *    (1)通过传值获取图片的路径名(列表)，或者给出图片下载的URL并
 *    (2)获取图片(本地、下载)将图片文件转换成Bitmap列表
 *    (3)逐张显示图片
 *    模式：
 *    (1)IMAGE_DISPLAY_MODE_DOWNLOAD 下载模式，需要给出图片下载地址列表
 *    (2)IMAGE_DISPLAY_MODE_LOCALFILE 本地模式，需要给出图片路径名列表
 *    (3)IMAGE_DISPLAY_MODE_BITMAP Bitmap模式，需要给出图片的Bitmap列表
 * </pre>
 *
 * Created by Xuyh at 2016-10-09 12:45.
 */
public class ImageDisplayActivity extends AppCompatActivity {
    /**
     * activity开启模式
     */
    public static int IMAGE_DISPLAY_MODE_DOWNLOAD = 0;
    public static int IMAGE_DISPLAY_MODE_LOCALFILE = 1;
    public static int IMAGE_DISPLAY_MODE_BITMAP = 2;
    /**
     * 图片URL列表
     */
    private List<String> imageURLList = new ArrayList<>();
    /**
     * 图片文件路径名列表
     */
    private List<String> imageFilePathNameList = new ArrayList<>();
    /**
     * 图片Bitmap列表
     */
    private List<Bitmap> bitmapList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);
    }
}
