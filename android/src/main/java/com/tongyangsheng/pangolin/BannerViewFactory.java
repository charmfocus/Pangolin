package com.tongyangsheng.pangolin;

import android.app.Activity;
import android.content.Context;

import io.flutter.app.FlutterActivity;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.StandardMessageCodec;
import io.flutter.plugin.platform.PlatformView;
import io.flutter.plugin.platform.PlatformViewFactory;

public class BannerViewFactory extends PlatformViewFactory {
    private final BinaryMessenger messenger;
    private final Activity activity;

    public BannerViewFactory(Activity activity, BinaryMessenger messenger ) {
        super(StandardMessageCodec.INSTANCE);
        this.messenger = messenger;
        this.activity = activity;
    }

    @Override
    public PlatformView create(Context context, int i, Object o) {
        return new FlutterAdsBannerView(activity,context,messenger,i);
    }
}
