import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'define.dart';

class NativeAdView extends StatefulWidget {
  final AdsIsFinish asdIsFinish;
  final AdsTick adsTick;
  final AdsIsFailure adsFailure;
  final String appId;
  final String mCodeId;
  final double width;
  final double height;
  final bool supportDeepLink;
  final int adCount;
  final bool debug;

  const NativeAdView({
    Key key,
    this.asdIsFinish,
    this.adsTick,
    this.adsFailure,
    this.appId,
    this.mCodeId,
    this.width = 0,
    this.height = 100,
    this.supportDeepLink = true,
    this.adCount = 1,
    this.debug = false,
  })  : assert(width != null),
        assert(height != null),
        super(key: key);

  @override
  _NativeAdViewState createState() => _NativeAdViewState();
}

// 设置事件监听方法
class _NativeAdViewState extends State<NativeAdView> {
  static const BasicMessageChannel<dynamic> messageChannel =
      const BasicMessageChannel(
          'plugins.pangolin.ads.event/nativeview', StringCodec());
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

    var width = widget.width == 0 ? size.width : widget.width;
    if (defaultTargetPlatform == TargetPlatform.android) {
      return Container(
        height: widget.height ?? 0,
        width: width,
        child: AndroidView(
          viewType: 'plugins.pangolin.ads/nativeview',
          onPlatformViewCreated: (id) {
            MethodChannel('native_$id').invokeMethod('setId', {
              'mCodeId': widget.mCodeId,
              'supportDeepLink': widget.supportDeepLink,
              'width': width,
              'height': widget.height,
              'adCount': widget.adCount,
              'debug': widget.debug,
            });
          },
        ),
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
