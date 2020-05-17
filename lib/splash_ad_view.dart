import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'define.dart';

class SplashAdView extends StatefulWidget {
  final AdsIsFinish asdIsFinish;
  final AdsTick adsTick;
  final AdsIsFailure adsFailure;
  final String appId;
  final String mCodeId;
  final int adCount;
  final bool debug;
  final bool isExpress;
  final double width;
  final double height;
  final bool supportDeepLink;

  const SplashAdView({
    Key key,
    this.asdIsFinish,
    this.adsTick,
    this.adsFailure,
    this.appId,
    this.mCodeId,
    this.adCount = 1,
    this.debug = false,
    this.isExpress = false,
    this.width = 0,
    this.height = 0,
    this.supportDeepLink = true,
  }) : super(key: key);

  @override
  _SplashAdViewState createState() => _SplashAdViewState();
}

// 设置事件监听方法
class _SplashAdViewState extends State<SplashAdView> {
  static const BasicMessageChannel<dynamic> messageChannel =
      const BasicMessageChannel(
          'plugins.pangolin.ads.event/splashview', StringCodec());
  Size size;

  @override
  void initState() {
    super.initState();

    messageChannel.setMessageHandler((value) async {
      if (mounted) {
        if (value == "finish") {
          this.widget.asdIsFinish();
        } else if (value == "failure") {
          this.widget.adsFailure();
        }
      }
    });
  }

  @override
  void dispose() {
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    size ??= MediaQuery.of(context).size;

    if (defaultTargetPlatform == TargetPlatform.android) {
      return AndroidView(
        viewType: 'plugins.pangolin.ads/splashview',
        onPlatformViewCreated: (id) {
          MethodChannel('splash_$id').invokeMethod('setId', {
            'mCodeId': widget.mCodeId,
            'adCount': widget.adCount,
            'debug': widget.debug,
            'isExpress': widget.isExpress ?? false,
            'supportDeepLink': widget.supportDeepLink ?? true,
            'width': widget.width,
            'height': widget.height,
          });
        },
      );
    }
    return Container();
//    else {
//      return UiKitView(
//        viewType: 'com.flutter_to_ads_banner_view',
//        creationParams: {
//          "gdtAppID": this.widget.appid,
//          "placementID": this.widget.placementID,
//        },
//        creationParamsCodec: const StandardMessageCodec(),
//      );
//    }
  }
}
