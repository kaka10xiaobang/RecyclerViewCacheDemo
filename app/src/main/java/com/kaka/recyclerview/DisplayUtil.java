package com.kaka.recyclerview;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

/**
 * Created by Xiaoqi.
 * Date:2019-06-10 10:13.
 * Project:RecyclerViewDemo.
 */
public class DisplayUtil {

    public static int getScreenWidthByPix(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    public static int getScreenWidthByDp(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return (int) (dm.widthPixels / dm.density);
    }

    public static int getScreenHeightByPix(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    public static int getRealHeightByPix(Context context) {
        WindowManager mWindowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        if (mWindowManager == null) {
            return getScreenHeightByPix(context);
        }
        Display mDisplay = mWindowManager.getDefaultDisplay();
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        mDisplay.getRealMetrics(mDisplayMetrics);
        return mDisplayMetrics.heightPixels;
    }

    public static int getScreenHeightByDp(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return (int) (dm.heightPixels / dm.density);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        return px2dip(context, pxValue, true);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     * isUp 向上取整
     */
    public static int px2dip(Context context, float pxValue, boolean isUp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        if (isUp) {
            return (int) (pxValue / scale + 0.5f);
        } else {
            return (int) (pxValue / scale);
        }
    }


    /**
     * 设置某个View的margin
     *
     * @param view   需要设置的view
     * @param isDp   需要设置的数值是否为DP
     * @param left   左边距
     * @param right  右边距
     * @param top    上边距
     * @param bottom 下边距
     * @return
     */
    public static ViewGroup.LayoutParams setViewMargin(View view, boolean isDp, int left, int right, int top, int bottom) {
        if (view == null) {
            return null;
        }

        int leftPx = left;
        int rightPx = right;
        int topPx = top;
        int bottomPx = bottom;
        ViewGroup.LayoutParams params = view.getLayoutParams();
        ViewGroup.MarginLayoutParams marginParams = null;
        //获取view的margin设置参数
        if (params instanceof ViewGroup.MarginLayoutParams) {
            marginParams = (ViewGroup.MarginLayoutParams) params;
        } else {
            //不存在时创建一个新的参数
            marginParams = new ViewGroup.MarginLayoutParams(params);
        }

        //根据DP与PX转换计算值
        if (isDp) {
            leftPx = px2dip(view.getContext(), left);
            rightPx = px2dip(view.getContext(), right);
            topPx = px2dip(view.getContext(), top);
            bottomPx = px2dip(view.getContext(), bottom);
        }
        //设置margin
        marginParams.setMargins(leftPx, topPx, rightPx, bottomPx);
        view.setLayoutParams(marginParams);
        return marginParams;
    }


    @SuppressWarnings("deprecation")
    /**
     * 兼容4.0与4.1以上系统设置BackgroundDrawable方法不同
     */
    public static void setBackgroundDrawableCompat(View view, Drawable drawable) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackgroundDrawable(drawable);
        } else {
            view.setBackground(drawable);
        }
    }

    /**
     * 获取屏幕分辨率
     *
     * @param context
     * @return
     */
    public static String getStrScreenResolution(Application context) {

        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);//display = getWindowManager().getDefaultDisplay();display.getMetrics(dm)（把屏幕尺寸信息赋值给DisplayMetrics dm）;
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        return width + "*" + height;
    }

    /**
     * 获取 是否是平板设备
     *
     * @param context
     * @return true:平板,false:手机
     */
    public static boolean isTabletDevice(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >=
                Configuration.SCREENLAYOUT_SIZE_LARGE;
    }


    /**
     * 修改屏幕当前亮度
     *
     * @param context
     * @param brightness
     */
    public static void setScreenBritness(Activity context, int brightness) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.screenBrightness = Float.valueOf(brightness) * (1f / 255f);
        context.getWindow().setAttributes(lp);
    }

    /**
     * 获取系统亮度
     *
     * @return
     */
    public static int getSystemBrightness(Activity context) {
        int systemBrightness = 0;
        try {
            systemBrightness = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return systemBrightness;
    }

    /**
     * 检测是否开启了自动亮度调整
     *
     * @param activity
     * @return
     */
    public static boolean isAutoBrightness(Activity activity) {
        try {
            int autoBrightness = Settings.System.getInt(
                    activity.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE);
            if (autoBrightness == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                return true;
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 关闭系统自动调节亮度
     *
     * @param activity
     */
    public static void closeAutoBrightness(Activity activity) {
        Settings.System.putInt(activity.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
    }

    /**
     * 打开自动调节亮度
     *
     * @param activity
     */
    public static void openAutoBrightness(Activity activity) {
        setScreenBritness(activity, -1);
        Settings.System.putInt(activity.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
    }

    /**
     * 获取屏幕密度
     */
    public static int getPixelRation(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.densityDpi;
    }

}
