package lee.com.test;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;


public class HorizontalAdapter extends BaseQuickAdapter<Entity, BaseViewHolder> {

    public static final String TAG = HorizontalAdapter.class.getSimpleName();

    public interface OnInnerAdapterClickListener {
        void onInnerItemClick(Entity parent, Entity.InnerEntity item);
    }

    private OnInnerAdapterClickListener mListener;

    public void setOnInnerAdapterListener(OnInnerAdapterClickListener listener) {
        mListener = listener;
    }


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
        InnerAdapter innerAdapter = new InnerAdapter(item, item.innerEntities);
        recyclerView.setAdapter(innerAdapter);
        if (item.scrollOffset > 0) {
            layoutManager.scrollToPositionWithOffset(item.scrollPosition, item.scrollOffset);
//            Log.d("adapter", "position: " + helper.getLayoutPosition() + "      item.scrollPosition: " + item.scrollPosition + "      scrollOffset: " + item.scrollOffset);
        }
        recyclerView.addOnScrollListener(new MyOnScrollListener(item, layoutManager));
    }



    private class MyOnScrollListener extends RecyclerView.OnScrollListener {

        private LinearLayoutManager mLayoutManager;
        private Entity mShopItem;
        private int mItemWidth;
        private int mItemMarging;

        public MyOnScrollListener(Entity shopItem, LinearLayoutManager layoutManager) {
            mLayoutManager = layoutManager;
            mShopItem = shopItem;
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
                    mShopItem.scrollPosition = mLayoutManager.findFirstVisibleItemPosition() < 0 ? mShopItem.scrollPosition : mLayoutManager.findFirstVisibleItemPosition() + 1;
                    if (mItemWidth <= 0) {
                        View item = mLayoutManager.findViewByPosition(mShopItem.scrollPosition);
                        if (item != null) {
                            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) item.getLayoutParams();
                            mItemWidth = item.getWidth();
                            mItemMarging = layoutParams.rightMargin;
                        }
                    }
                    if (offset > 0 && mItemWidth > 0) {
                        mShopItem.scrollOffset = mItemWidth - offset % mItemWidth + mShopItem.scrollPosition * mItemMarging;
                    }
//                    Log.i("adapter", "  mShopItem.scrollPosition: " + mShopItem.scrollPosition + "    mShopItem.scrollOffset: " + mShopItem.scrollOffset);
//                    Log.d("adapter", " offset: " + offset);
//                    Log.d("adapter", " itemWidth: " + mItemWidth + "   mItemMarging: " + mItemMarging);
                    break;
            }
        }
    }


    private class InnerAdapter extends BaseQuickAdapter<Entity.InnerEntity, BaseViewHolder> {

        private Entity mParent;

        public InnerAdapter(Entity parent, List<Entity.InnerEntity> datas) {
            super(R.layout.item_horizontal_inner, datas);
            mParent = parent;
        }

        @Override
        protected void convert(final BaseViewHolder helper, final Entity.InnerEntity item) {

            helper.setText(R.id.title, item.innerTitle);
            ((ImageView) helper.getView(R.id.iv)).setImageResource(item.innerImageId);

            helper.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onInnerItemClick(mParent, item);
                    }
                }
            });
        }

    }
}
