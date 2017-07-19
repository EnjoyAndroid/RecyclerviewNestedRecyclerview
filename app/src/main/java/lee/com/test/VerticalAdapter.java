package lee.com.test;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;


public class VerticalAdapter extends BaseQuickAdapter<Entity, BaseViewHolder> {

    public static final String TAG = VerticalAdapter.class.getSimpleName();

    public VerticalAdapter(List<Entity> data) {
        super(R.layout.item_vertical, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Entity item) {
        helper.setText(R.id.tv_title, item.title);
        final RecyclerView recyclerView = helper.getView(R.id.rv_item);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(layoutManager);
        InnerAdapter innerAdapter = new InnerAdapter(item, item.innerEntities);
        recyclerView.setAdapter(innerAdapter);
    }

    public interface OnInnerAdapterClickListener {
        void onInnerItemClick(Entity parent, Entity.InnerEntity item);
    }

    private OnInnerAdapterClickListener mListener;

    public void setOnInnerAdapterListener(OnInnerAdapterClickListener listener) {
        mListener = listener;
    }

    private class InnerAdapter extends BaseQuickAdapter<Entity.InnerEntity, BaseViewHolder> {

        private Entity mParent;

        public InnerAdapter(Entity parent, List<Entity.InnerEntity> datas) {
            super(R.layout.item_vertical_inner, datas);
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
