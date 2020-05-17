import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

typedef AdsIsFinish = void Function();

typedef AdsIsFailure = void Function();

typedef AdsTick = void Function(num);

class BannerAdView extends StatefulWidget {
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

  const BannerAdView({
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
  _BannerAdViewState createState() => _BannerAdViewState();
}

// 设置事件监听方法
class _BannerAdViewState extends State<BannerAdView> {
  static const BasicMessageChannel<dynamic> messageChannel =
      const BasicMessageChannel(
          'plugins.nova.ads.event/bannerview', StringCodec());
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
          viewType: 'plugins.pangolin.ads/bannerview',
          onPlatformViewCreated: (id) {
            MethodChannel('banner_$id').invokeMethod('setBannerId', {
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
