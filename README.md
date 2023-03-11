# 兼容国产系统的设置壁纸方式

## 使用方式：
#### 1.添加依赖：
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
```
dependencies {
	 implementation 'com.github.SherlockGougou:SetWallpaper:v1.3.0'
}
```
#### 2.调用代码：
```
SetWallpaper.setWallpaper(MainActivity.this, uri);
```