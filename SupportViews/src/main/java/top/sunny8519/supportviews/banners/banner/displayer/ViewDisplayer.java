package top.sunny8519.supportviews.banners.banner.displayer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

/**
 * @author niyang
 * @date 2018/12/15
 */
public interface ViewDisplayer<V extends View> {

    @NonNull
    V createView(Context context);

    void displayImage(Context context, Object path, V view);
}
