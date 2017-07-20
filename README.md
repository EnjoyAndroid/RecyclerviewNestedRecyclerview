# RecyclerviewNestedRecyclerview
An example of a recyclerview nested recyclerview


  最近项目中用到了两个RecyclerView嵌套的布局，即RecyclerView的item也是RecyclerView，其中遇到了两个比较典型的问题：1、当item的方向是垂直方向时，父RecyclerView首次加载会出现位移；2、当item的方向是水平方向时，父RecyclerView上下滑动之后，子RecyclerView位置会还原，本文主要解决以上两个问题。我们先来瞄一眼这两个问题的效果图：

![修复前.gif](http://upload-images.jianshu.io/upload_images/2032177-caf62e812c3c8243.gif?imageMogr2/auto-orient/strip)


来瞄一眼解决问题后的效果图：

![修复后.gif](http://upload-images.jianshu.io/upload_images/2032177-546c6cce03f9b4f4.gif?imageMogr2/auto-orient/strip)

 源码解析：
 http://www.jianshu.com/p/91b6ef2c4c29
