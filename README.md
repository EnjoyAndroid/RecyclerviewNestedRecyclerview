# RecyclerviewNestedRecyclerview
An example of a recyclerview nested recyclerview


一、概述
  最近项目中用到了两个RecyclerView嵌套的布局，即RecyclerView的item也是RecyclerView，其中遇到了两个比较典型的问题：1、当item的方向是垂直方向时，父RecyclerView首次加载会出现位移；2、当item的方向是水平方向时，父RecyclerView上下滑动之后，子RecyclerView位置会还原，本文主要解决以上两个问题。我们先来瞄一眼这两个问题的效果图：

![修复前.gif](http://upload-images.jianshu.io/upload_images/2032177-caf62e812c3c8243.gif?imageMogr2/auto-orient/strip)

  可以明显的看到当item的Recyclerview是垂直方向时，打开页面时“title1”不见了；当item的Recyclerview是水平方向时，我们把 Inner Title1-x和 Inner Title2-x滑动一定距离之后，上下滑动父Recyclerview，Inner Title1-x和 Inner Title2-x的位置又还原了。

二、解决
  2.1 首先解决垂直嵌套问题，这个比较简单，主要是子Recyclerview抢占焦点导致，我们只需要让其父布局获取焦点即可解决，完整代码如下：
```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
              xmlns:tools="http://schemas.android.com/tools"
              android:layout_width="match_parent"
              android:layout_height="wrap_content"
              android:gravity="center_vertical"
              android:orientation="vertical"
              android:paddingLeft="14dp"
              android:paddingRight="14dp"
              android:focusableInTouchMode="true"
              android:focusable="true"
    >

    <TextView
        android:id="@+id/tv_title"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:paddingTop="20dp"
        android:paddingBottom="20dp"
        android:layout_gravity="center_vertical"
        android:layout_marginRight="30dp"
        android:gravity="center"
        android:maxLines="1"
        android:textColor="#333333"
        android:textSize="18sp"
        tools:text="2017-06"/>

    <android.support.v7.widget.RecyclerView
        android:id="@+id/rv_item"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:background="#fff"
        />
</LinearLayout>
```
关键代码就是这两句：
```
android:focusableInTouchMode="true"
android:focusable="true"
```
完整源码我已经提交到GitHub，布局参见：item_vertical.xml，后面会提供仓库地址。

  2.2 解决水平嵌套，父Recyclerview上下滑动位置还原问题。该问题的解决思路比较清晰：1.设置Recyclerview的滑动监听，每次滑动结束，记录滑动位置，包括position和offset；2.调用LinearLayoutManager.scrollToPositionWithOffset()方法滑动到上一次记录的位置即可。所以解决问题的关键点就在于计算滑动的position和offset，关键代码如下：
```
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            switch (newState) {
                case RecyclerView.SCROLL_STATE_IDLE:

                    int offset = recyclerView.computeHorizontalScrollOffset();
                    mEntity.scrollPosition = mLayoutManager.findFirstVisibleItemPosition() < 0 ? mEntity.scrollPosition : mLayoutManager.findFirstVisibleItemPosition() + 1;
                    if (mItemWidth <= 0) {
                        View item = mLayoutManager.findViewByPosition(mEntity.scrollPosition);
                        if (item != null) {
                            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) item.getLayoutParams();
                            mItemWidth = item.getWidth();
                            mItemMargin = layoutParams.rightMargin;
                        }
                    }
                    if (offset > 0 && mItemWidth > 0) {
                        //offset % mItemWidth：得到当前position的滑动距离
                        //mEntity.scrollPosition * mItemMargin：得到（0至position）的所有item的margin
                        //用当前item的宽度-所有margin-当前position的滑动距离，就得到offset。
                        mEntity.scrollOffset = mItemWidth - offset % mItemWidth + mEntity.scrollPosition * mItemMargin;
                    }
                    break;
            }
        }
```
position的计算比较简单，直接调用 :
```
LinearLayoutManager.findFirstVisibleItemPosition() + 1
```
即可，而offset的计算要稍微复杂一点，计算公式如下：
```
    //offset % mItemWidth：得到当前position的滑动距离
    //mEntity.scrollPosition * mItemMargin：得到（0至position）的所有item的margin
    //用当前item的宽度-所有margin-当前position的滑动距离，就得到offset。
    mEntity.scrollOffset = mItemWidth - offset % mItemWidth + mEntity.scrollPosition * mItemMargin;
```

至此，滑动的position和offset已经全部获取到，接下来只需要调用如下代码，即可恢复到上一次滑动的位置，解决位置还原问题。
```
layoutManager.scrollToPositionWithOffset(item.scrollPosition, item.scrollOffset);
```

完整代码如下，对应HorizontalAdapter.class：
```

public class HorizontalAdapter extends BaseQuickAdapter<Entity, BaseViewHolder> {

    public static final String TAG = HorizontalAdapter.class.getSimpleName();

    public HorizontalAdapter(List<Entity> data) {
        super(R.layout.item_horizontal, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Entity item) {
        helper.setText(R.id.tv_title, item.title);
        final RecyclerView recyclerView = helper.getView(R.id.rv_item);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(OrientationHelper.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        InnerAdapter innerAdapter = new InnerAdapter(item.innerEntities);
        recyclerView.setAdapter(innerAdapter);
        if (item.scrollOffset > 0) {
            layoutManager.scrollToPositionWithOffset(item.scrollPosition, item.scrollOffset);
        }
        recyclerView.addOnScrollListener(new MyOnScrollListener(item, layoutManager));
    }


    private class MyOnScrollListener extends RecyclerView.OnScrollListener {

        private LinearLayoutManager mLayoutManager;
        private Entity mEntity;
        private int mItemWidth;
        private int mItemMargin;

        public MyOnScrollListener(Entity shopItem, LinearLayoutManager layoutManager) {
            mLayoutManager = layoutManager;
            mEntity = shopItem;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            switch (newState) {
                case RecyclerView.SCROLL_STATE_IDLE:

                    int offset = recyclerView.computeHorizontalScrollOffset();
                    mEntity.scrollPosition = mLayoutManager.findFirstVisibleItemPosition() < 0 ? mEntity.scrollPosition : mLayoutManager.findFirstVisibleItemPosition() + 1;
                    if (mItemWidth <= 0) {
                        View item = mLayoutManager.findViewByPosition(mEntity.scrollPosition);
                        if (item != null) {
                            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) item.getLayoutParams();
                            mItemWidth = item.getWidth();
                            mItemMargin = layoutParams.rightMargin;
                        }
                    }
                    if (offset > 0 && mItemWidth > 0) {
                        //offset % mItemWidth：得到当前position的滑动距离
                        //mEntity.scrollPosition * mItemMargin：得到（0至position）的所有item的margin
                        //用当前item的宽度-所有margin-当前position的滑动距离，就得到offset。
                        mEntity.scrollOffset = mItemWidth - offset % mItemWidth + mEntity.scrollPosition * mItemMargin;
                    }
                    break;
            }
        }
    }


    private class InnerAdapter extends BaseQuickAdapter<Entity.InnerEntity, BaseViewHolder> {

        public InnerAdapter(List<Entity.InnerEntity> datas) {
            super(R.layout.item_horizontal_inner, datas);
        }

        @Override
        protected void convert(final BaseViewHolder helper, final Entity.InnerEntity item) {

            helper.setText(R.id.title, item.innerTitle);
            ((ImageView) helper.getView(R.id.iv)).setImageResource(item.innerImageId);
        }

    }
}

```
来瞄一眼解决问题后的效果图：

![修复后.gif](http://upload-images.jianshu.io/upload_images/2032177-546c6cce03f9b4f4.gif?imageMogr2/auto-orient/strip)


三、总结
  由于时间关系(其实就是懒)，不想写adapter的重复代码，所以直接用的[BaseRecyclerViewAdapterHelper](https://github.com/CymChad/BaseRecyclerViewAdapterHelper)，感谢作者提供的优秀框架，如果是自己写adapter代码，只需要把convert的代码在onBindViewHolder()中实现即可。

[完整工程下载](https://github.com/EnjoyAndroid/RecyclerviewNestedRecyclerview)

  喜欢就start一下吧，如果有啥问题欢迎在issue或者评论里面反馈。
