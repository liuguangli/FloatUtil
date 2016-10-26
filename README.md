## FloatUtil
一个简单的浮窗工具。封装了浮窗的使用方法，并作了系统、版本的兼容处理，帮你绕过权限的限制。
![浮窗](https://github.com/liuguangli/FloatUtil/blob/master/files/float_drag.gif)
### 开始
项目使用 [jitpack](https://jitpack.io) 做开源库的托管，你需要在 .gradle 中添加  [jitpack](https://jitpack.io) 
的仓库。

    allprojects {
        repositories {
            jcenter()
            maven { url "https://jitpack.io" }
        }
    }
    
在添加 FloatUtil 的依赖引用：
   
   
    dependencies {
         compile 'com.github.liuguangli:FloatUtil:master-SNAPSHOT'
    }


### 创建一个简单的浮窗

      SimpleView floatView = new SimpleView(this);
      FloatUtil.showFloatView(floatView, null);
    
SimpleView 是你自定义的 View，就这么简单，浮窗显示出来了。

![浮窗](https://github.com/liuguangli/FloatUtil/blob/master/files/simple_float.gif)
    
### 关闭浮窗

      FloatUtil.hideFloatView(context, SimpleView.class, false);
同一个 View 类，同时只能显示一个实例， 关闭的时候指定一个 class 对象便能知道关闭哪个浮窗实例。 最后一个参数
决定要不要将当前实例缓存，以便下次快速显示并维持状态，false 代表不缓存，true 表示要缓存。

### 向浮窗传递参数

FloatUtil 提供了一个接口：ParamReceiver。你自定义的 View 实现这个接口便能接收参数。

     public class SimpleViewWitchParam extends FrameLayout implements ParamReceiver {
        public static final java.lang.String PARAM = "PARAM";
        public static final String CONTENT = "content";
        @Override
        public void onParamReceive(Bundle bundle) {
            // 在这个回调方法中接收参数并解析
            if (bundle != null) {
                String param = bundle.getString(PARAM);
                
            }
        }
    }
    
然后在添加这个 SimpleViewWitchParam 到浮窗。
     
        SimpleViewWitchParam floatView = new SimpleViewWitchParam(this);
        Bundle bundle = new Bundle();
        bundle.putString(SimpleViewWitchParam.PARAM, "我是传过来的参数");
        FloatUtil.showFloatView(floatView, bundle);
        
![浮窗](https://github.com/liuguangli/FloatUtil/blob/master/files/float_param.gif)        
        
### 指定层级和对齐方式
       
        SimpleViewWitchParam floatView = new SimpleViewWitchParam(this);
        // 居中对齐，浮窗层级为 WindowManager.LayoutParams.TYPE_TOAST
        FloatUtil.showFloatView(floatView, Gravity.CENTER,WindowManager.LayoutParams.TYPE_TOAST , null);
        
浮窗类型 type 决定了浮窗的层级，关于浮窗层级的详细理解可以参考我的博客:[《浮窗开发之窗口层级》](http://www.liuguangli.win/archives/476),
Android 系统对窗体的某些层级有权限限制，例如 WindowManager.LayoutParams.TYPE_PHONE 类型的窗体需要授权。

### 智能浮窗（突破授权）

FloatUtil 提供智能方式添加浮窗，针对特定的系统版本、机型为你选择合适的浮窗 type ，越过授权（详细参考我的博客：[越过用户授权使用浮窗](http://www.liuguangli.win/archives/484))，你不需要再去关注复杂的处理过程。
   
        SimpleViewWitchParam floatView = new SimpleViewWitchParam(this);
        Bundle bundle = new Bundle();
        bundle.putString(SimpleViewWitchParam.PARAM, "智能浮窗");
        // 指定浮窗显示的位置
        Point point = new Point();
        point.x = 0;
        point.y = 0;
        FloatUtil.showSmartFloate(floatView, Gravity.CENTER, point, bundle);
                

## MIT License

Copyright (c) 2016 刘光利

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.


