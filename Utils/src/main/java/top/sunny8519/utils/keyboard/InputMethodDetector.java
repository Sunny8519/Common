package top.sunny8519.utils.keyboard;

import android.app.Activity;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

/**
 * @author niyang
 * @date 2018/12/27
 */
public class InputMethodDetector {

    public InputMethodDetector getInstance(Activity activity) {
        return new InputMethodDetector(activity);
    }

    private View mChildOfContent;
    private InputMethodListener mInputMethodListener;
    private int mLastBottom;
    private ViewTreeObserver.OnGlobalLayoutListener mDefaultGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            detectSoftKeyBoard();
        }
    };

    private InputMethodDetector(Activity activity) {
        FrameLayout content = activity.findViewById(android.R.id.content);
        mChildOfContent = content.getChildAt(0);
    }

    private void detectSoftKeyBoard() {
        final int decorViewHeight = this.mChildOfContent.getRootView().getHeight();
        int bottomLocation = computeContentViewBottomLocation();
        if (bottomLocation != mLastBottom) {
            final int diffHeight = decorViewHeight - bottomLocation;
            final InputMethodListener inputMethodListener = this.mInputMethodListener;
            if (inputMethodListener != null) {
                // 大于DecorView高度的四分之一即视软键盘弹出
                if (diffHeight > decorViewHeight / 4) {
                    Log.d(getClass().getSimpleName(), "软键盘弹出");
                    inputMethodListener.show();
                } else {
                    Log.d(getClass().getSimpleName(), "软键盘隐藏");
                    inputMethodListener.hidden();
                }
            }
            this.mLastBottom = bottomLocation;
        }
    }

    private int computeContentViewBottomLocation() {
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);
        return r.bottom;
    }

    public void setInputMethodListener(InputMethodDetector.InputMethodListener inputMethodListener) {
        this.mInputMethodListener = inputMethodListener;
    }

    public void bind(ViewTreeObserver.OnGlobalLayoutListener listener) {
        if (mChildOfContent != null) {
            mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(listener);
        }
    }

    public void bind() {
        bind(this.mDefaultGlobalLayoutListener);
    }

    public void unBind(ViewTreeObserver.OnGlobalLayoutListener listener) {
        if (mChildOfContent != null) {
            mChildOfContent.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        }
    }

    public void unBind() {
        unBind(this.mDefaultGlobalLayoutListener);
    }

    public interface InputMethodListener{
        void show();

        void hidden();
    }
}
