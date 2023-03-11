# 兼容国产系统的设置壁纸方式

## 使用方式：
##### Step 1. 在你project层级的build.gradle中，添加仓库地址:
```
allprojects {
    repositories {
	...
	maven { url 'https://jitpack.io' }
    }
}
```

##### Step 2. 在你主module的build.gradle中添加依赖：
最新版本：[![](https://jitpack.io/v/SherlockGougou/SetWallpaper.svg)](https://jitpack.io/#SherlockGougou/SetWallpaper)
```
dependencies {
    implementation 'com.github.SherlockGougou:SetWallpaper:v2.1.0'
}
```

#### 2.调用代码：
```
SetWallpaper.setWallpaper(MainActivity.this, path, APP_AUTHORITY);
// APP_AUTHORITY是你App中设置的值
```
