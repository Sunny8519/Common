package top.sunny8519.supportviews.banners.banner.displayer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ImageView;

/**
 * @author niyang
 * @date 2018/12/15
 */
public abstract class ImageViewDisplayer implements ViewDisplayer<ImageView> {

    @NonNull
    @Override
    public ImageView createView(Context context) {
        return new ImageView(context);
    }
}
