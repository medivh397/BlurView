# BlurView

A very fast and easy-to-use dynamic blur view for Android

<br/><br/>
<center class = "half">
 
<img src="https://github.com/medivh397/BlurView/blob/main/screenshot.jpg" height="40%" width="40%" />  <img src="https://github.com/medivh397/BlurView/blob/main/demo.gif" height="40%" width="40%" />

</center>

## How to use
BlurView can be used as a regular FrameLayout. It blurs its underlying content and draws it as a background.

```xml
  <com.medivh.blurview.core.BlurLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        >

        //put any view here if you need,such as LinearLayout,TextView,ImageView,etc.
        
  </com.medivh.blurview.core.BlurLayout>
```

## How about the performance?
Because of the high optimized implementation,BlurView can be very fast even on some low performance devices.
