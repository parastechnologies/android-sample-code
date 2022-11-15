package com.lyft.android.scissors2;

import android.graphics.Bitmap;
import android.net.Uri;
import android.view.ViewTreeObserver;

import androidx.annotation.Nullable;

import static com.lyft.android.scissors2.CropView.Extensions.LoaderType;
import static com.lyft.android.scissors2.CropViewExtensions.resolveBitmapLoader;

public class LoadRequest {

    private final CropView cropView;
    private BitmapLoader bitmapLoader;
    private LoaderType loaderType = LoaderType.CLASS_LOOKUP;

    LoadRequest(CropView cropView) {
        Utils.checkNotNull(cropView, "cropView == null");
        this.cropView = cropView;
    }

    public LoadRequest using(@Nullable BitmapLoader bitmapLoader) {
        this.bitmapLoader = bitmapLoader;
        return this;
    }

    public LoadRequest using(LoaderType loaderType) {
        this.loaderType = loaderType;
        return this;
    }

    /**
     * Load a {@link Bitmap} using a {@link BitmapLoader} into {@link CropView}
     *
     * @param model Model used by {@link BitmapLoader} to load desired {@link Bitmap}
     */
    public void load(@Nullable Object model) {
        if (cropView.getWidth() == 0 && cropView.getHeight() == 0) {
            // Defer load until layout pass
            deferLoad(model);
            return;
        }
        performLoad(model);
    }

    void performLoad(Object model) {
        if (bitmapLoader == null) {
            bitmapLoader = resolveBitmapLoader(cropView, loaderType);
        }
        bitmapLoader.load(model, cropView);
    }

    void deferLoad(final Object model) {
        if (!cropView.getViewTreeObserver().isAlive()) {
            return;
        }
        cropView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (cropView.getViewTreeObserver().isAlive()) {
                            //noinspection deprecation
                            cropView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }
                        performLoad(model);
                    }
                }
        );
    }
}
