# AndroidProgressView
Progress view with arc, line and circle shapes and gradient effect


![](ProgressView.gif)

# Attributes
| Name  | Description | Type | Default | Range |
| ------------- | ------------- | ------------- | ------------- | ------------- |
| pvDirection  | Direction of the progress  | enum | fromLeft (clockwise) | fromLeft, fromRight |
| pvShape  | Shape of the progress view  | enum | arc | arc, circle, line |
| pvProgress  | Progress value  | float | 0 | 0 to 1 |
| pvBackgroundColor  | Progress background color  | color | Color.BLACK | - |
| pvProgressColor  | Progress color  | color | Color.RED | - |
| pvBackgroundWidth  | Progress background width  | dimension | 2dp | - |
| pvProgressWidth  | Progress width  | dimension | 10dp | - |
| pvAnimateDuration  | Animation duration  | integer | 1500 | - |

# Home To Install
```
allprojects {
    repositories {
        ...
        jcenter()
    }
}
```
```
implementation 'com.shimaami.android:progressview:1.0'
```
If you have problems with appcompat use
```
implementation ("com.shimaami.android:progressview:1.0") {
    exclude module: "appcompat-v7"
}
```

# Home To Use
### XML
```
<com.progress.progressview.ProgressView
            android:id="@+id/progressView"
            android:layout_width="wrap_content"
            android:layout_height="150dp"
            android:layout_gravity="center_horizontal"
            app:pvDirection="fromLeft"
            app:pvShape="arc"
            app:pvProgress="1" />
```
### Apply gradient effect
```
 int[] colorList = new int[]{Color.GREEN, Color.YELLOW, Color.RED};
        mProgressView.applyGradient(colorList);
```
