#使用方式

    implementation 'com.sonnyjack.library:network:0.1.0'

或者

    api 'com.sonnyjack.library:network:0.1.0'
    
该库引用com.android.support:appcompat-v7:27.1.0，如果你想统一你项目中的appcompat-v7的版本，可像这样引用：

       api ('com.sonnyjack.library:network:0.1.0'){
            exclude(group: 'com.android.support', module: 'appcompat-v7')
        }

如果你的项目混淆：

-dontwarn com.sonnyjack.library.network.**

-keep class com.sonnyjack.library.network.** {*;}

-dontwarn okhttp3.**

-dontwarn okio.**

-dontwarn javax.annotation.**

-dontwarn org.conscrypt.**

#A resource is loaded with a relative path so the package of this class must be preserved.

-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase



# 用法：


    BaseHttpParams baseHttpParams = new  BaseHttpParams();
    baseHttpParams.setContext(this);//上下文
    baseHttpParams.setLoading(true);//可用于是否显示loading等待框，需用户在回调的onBefore方法中实现
    baseHttpParams.setHttpUrl("接口地址");
    baseHttpParams.setTag(System.currentTimeMillis());
    baseHttpParams.addParam("参数key", "参数value");

    HttpUtils.getInstance().getAsync(baseHttpParams, new IHttpCallBack() {
          @Override
          public void onBefore(BaseHttpParams httpParams) {

          }

          @Override
          public void onProgress(BaseHttpParams httpParams, long bytesRead, long contentLength, boolean finish) {

          }

          @Override
          public void onAfter(BaseHttpParams httpParams) {

          }

          @Override
          public void onFail(BaseHttpParams httpParams, int error, String message) {

          }

          @Override
          public void onSuccess(BaseHttpParams httpParams, String body) {

          }
    });


# 开发者可自行第二次分装，详情可查看demo。


如果遇到什么问题可以加我Q：252624617  或者issues反馈