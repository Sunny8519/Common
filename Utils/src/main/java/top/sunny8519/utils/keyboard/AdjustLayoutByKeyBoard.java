package top.sunny8519.utils.keyboard;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import java.lang.reflect.Method;

/**
 * @author niyang
 * @date 2018/8/1
 */
public class AdjustLayoutByKeyBoard {

    private static final String TAG = AdjustLayoutByKeyBoard.class.getSimpleName();

    public static void assistActivity(Activity activity) {
        new AdjustLayoutByKeyBoard(activity);
    }

    private View mChildOfContent;
    private int lastBottom;
    private FrameLayout.LayoutParams frameLayoutParams;
    private int mDecorViewHeight;
    private boolean isFirst = true;
    private int contentHeight;
    private int mStatusBarHeight;

    private AdjustLayoutByKeyBoard(Activity activity) {

        FrameLayout content = (FrameLayout) activity.findViewById(android.R.id.content);
        mChildOfContent = content.getChildAt(0);

        if (mChildOfContent == null) {
            return;
        }

        frameLayoutParams = (FrameLayout.LayoutParams)
                mChildOfContent.getLayoutParams();

        this.mStatusBarHeight = getStatusBarHeight(activity);
        Log.d(TAG, "StatusBarHeight = " + this.mStatusBarHeight);

        //界面出现变动都会调用这个监听事件
        mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                mDecorViewHeight = mChildOfContent.getRootView().getHeight();
                Log.d(TAG, "DecorView height = " + mDecorViewHeight);
                possiblyResizeChildOfContent1();
            }
        });
        Log.d(TAG, "hasNavBar = " + hasNavBar(activity));
    }

    /**
     * 重新调整根布局的高度
     */
    private void possiblyResizeChildOfContent() {
        if (isFirst) {
            contentHeight = mChildOfContent.getHeight();
            isFirst = false;
        }
        int bottomLocation = computeContentViewBottomTopLocation();
        if (bottomLocation != lastBottom) {
            int heightDifference = this.mDecorViewHeight - bottomLocation;
            if (heightDifference > 100) {
                // ContentView height
                // bottomTopLocation[1]:状态栏高度
                frameLayoutParams.height = this.mDecorViewHeight - heightDifference - this.mStatusBarHeight;
                Log.d(TAG, "ContentView height = " + frameLayoutParams.height);
            } else {
                frameLayoutParams.height = contentHeight;
            }
            mChildOfContent.requestLayout();
            lastBottom = bottomLocation;
        }
    }

    private void possiblyResizeChildOfContent1() {
        int bottomTopLocation = computeContentViewBottomTopLocation();

        if (bottomTopLocation != lastBottom) {
            int heightDifference = this.mDecorViewHeight - bottomTopLocation;
            //ContentView height
            frameLayoutParams.height = this.mDecorViewHeight - heightDifference - this.mStatusBarHeight;
            Log.d(TAG, "ContentView height = " + frameLayoutParams.height);
            mChildOfContent.requestLayout();
            lastBottom = bottomTopLocation;
        }
    }

    private int computeContentViewBottomTopLocation() {
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);
        Log.d(TAG, "ContentView bottom = " + r.bottom);
        return r.bottom;
    }

    private static final String SHOW_NAV_BAR_RES_NAME = "config_showNavigationBar";
    private static String sNavBarOverride;

    @TargetApi(14)
    private boolean hasNavBar(Context context) {
        Resources res = context.getResources();
        int resourceId = res.getIdentifier(SHOW_NAV_BAR_RES_NAME, "bool", "android");
        if (resourceId != 0) {
            boolean hasNav = res.getBoolean(resourceId);
            if ("1".equals(sNavBarOverride)) {
                hasNav = false;
            } else if ("0".equals(sNavBarOverride)) {
                hasNav = true;
            }
            return hasNav;
        } else {
            return !ViewConfiguration.get(context).hasPermanentMenuKey();
        }
    }

    /**
     * 获取系统状态栏高度
     *
     * @param context 上下文
     * @return 状态栏高度, 如果系统状态栏的高度获取不到，默认返回20dp
     */
    public static int getStatusBarHeight(Context context) {
        final Resources resources = context.getResources();
        int resId = resources.getIdentifier("status_bar_height", "dimen", "android");
        return (int) resources.getDimension(resId);
    }

    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                @SuppressLint("PrivateApi")
                Class c = Class.forName("android.os.SystemProperties");
                @SuppressWarnings("unchecked")
                Method m = c.getDeclaredMethod("get", String.class);
                m.setAccessible(true);
                sNavBarOverride = (String) m.invoke(null, "qemu.hw.mainkeys");
            } catch (Throwable e) {
                sNavBarOverride = null;
            }
        }
    }

}
