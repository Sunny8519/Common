package top.sunny8519.utils.keyboard;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import top.sunny8519.utils.ViewUtils;

import java.lang.ref.WeakReference;

/**
 * @author niyang
 * @date 2018/12/27
 */
public class SoftKeyboardDetector {

    private static final String TAG = SoftKeyboardDetector.class.getSimpleName();
    private static final int KEYBOARD_VISIBLE_THRESHOLD_DIP = 100;

    public static Unregister registerKeyboardEventListener(Activity activity, final OnKeyboardEventListener listener) {
        if (activity == null || listener == null) {
            Log.e(TAG, "Activity or listener is null!");
            return null;
        }

        if (activity.getWindow() != null) {
            WindowManager.LayoutParams attributes = activity.getWindow().getAttributes();
            if (attributes != null) {
                int softInputMode = attributes.softInputMode;
                if (softInputMode == WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING
                        || softInputMode == WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN) {
                    Log.e(TAG, "SoftKeyboard detector can't work with softInputMode is SOFT_INPUT_ADJUST_NOTHING or SOFT_INPUT_ADJUST_PAN");
                    return null;
                }
            }
        }

        final View activityRoot = getActivityRoot(activity);

        if (activityRoot == null) {
            Log.e(TAG, "Activity root is null!");
            return null;
        }

        final Activity activityEctype = activity;
        final ViewTreeObserver.OnGlobalLayoutListener layoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {

            private final Rect visibleFrame = new Rect();
            private final int threshold = ViewUtils.dip2px(activityEctype, KEYBOARD_VISIBLE_THRESHOLD_DIP);
            private boolean wasKeyboardOpened = false;

            @Override
            public void onGlobalLayout() {
                activityRoot.getWindowVisibleDisplayFrame(visibleFrame);
                int heightDiff = activityRoot.getRootView().getHeight() - visibleFrame.height();
                boolean isOpen = heightDiff > threshold;
                if (isOpen == wasKeyboardOpened) {
                    return;
                }

                wasKeyboardOpened = isOpen;
                listener.onKeyboardEvent(isOpen);
            }
        };

        activityRoot.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
        return new DefaultUnRegister(activity, layoutListener);
    }

    public static boolean isKeyboardVisible(Activity activity) {
        Rect windowFrame = new Rect();
        View root = getActivityRoot(activity);

        if (root != null) {
            root.getWindowVisibleDisplayFrame(windowFrame);
            int heightDiff = root.getRootView().getHeight() - windowFrame.height();
            return heightDiff > ViewUtils.dip2px(activity, KEYBOARD_VISIBLE_THRESHOLD_DIP);
        }
        return false;
    }

    @Nullable
    public static View getActivityRoot(Activity activity) {
        if (activity != null) {
            return activity.findViewById(android.R.id.content);
        }
        return null;
    }

    public static final class DefaultUnRegister implements Unregister {

        private WeakReference<Activity> activityRef;
        private WeakReference<ViewTreeObserver.OnGlobalLayoutListener> listenerRef;

        public DefaultUnRegister(Activity activity, ViewTreeObserver.OnGlobalLayoutListener listener) {
            this.activityRef = new WeakReference<>(activity);
            this.listenerRef = new WeakReference<>(listener);
        }

        @SuppressLint("ObsoleteSdkInt")
        @Override
        public void execute() {
            Activity activity = activityRef.get();
            ViewTreeObserver.OnGlobalLayoutListener listener = listenerRef.get();

            if (activity != null && listener != null) {
                View root = SoftKeyboardDetector.getActivityRoot(activity);
                if (root != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        root.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
                    } else {
                        root.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
                    }
                }
            }

            activityRef.clear();
            listenerRef.clear();
        }
    }

    public interface Unregister {
        void execute();
    }

    public interface OnKeyboardEventListener {
        void onKeyboardEvent(boolean isShown);
    }
}
