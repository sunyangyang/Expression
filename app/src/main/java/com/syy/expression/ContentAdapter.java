package com.syy.expression;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.syy.expression.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ContentViewHolder> {
    private Context mContext;
    private List<ExpressionBean> mContents = new ArrayList<>();
    private ContentClickCallback mCallback;

    public ContentAdapter(Context context, ContentClickCallback callback) {
        mContext = context;
        mCallback = callback;
    }

    public void addContent(ExpressionBean content) {
        mContents.add(content);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public ContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ContentViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_content, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ContentViewHolder holder, int position) {
        ExpressionBean expressionBean = mContents.get(position);

        if (TextUtils.isEmpty(expressionBean.answer)) {
            holder.mLayoutContent.setGravity(Gravity.RIGHT);
            holder.mTvContent.setText(mContents.get(position).content);
            holder.mTvContent.setTag(mContents.get(position));
            holder.mTvContent.setOnClickListener(clickListener);
            holder.mImIcon.setVisibility(View.GONE);
            holder.mImIconRight.setVisibility(View.VISIBLE);
            holder.mTvContent.setBackgroundResource(R.drawable.bg_text_content);
        } else {
            holder.mImIcon.setVisibility(View.VISIBLE);
            holder.mImIconRight.setVisibility(View.GONE);
            holder.mLayoutContent.setGravity(Gravity.LEFT);
            holder.mTvContent.setText(expressionBean.answer);
            holder.mTvContent.setBackgroundResource(R.drawable.bg_text_content_green);
        }
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getTag() != null && v.getTag() instanceof ExpressionBean) {
                ExpressionBean bean = (ExpressionBean) v.getTag();
                mCallback.onContentClick(bean);
            }
        }
    };

    @Override
    public int getItemCount() {
        return mContents.size();
    }

    public class ContentViewHolder extends RecyclerView.ViewHolder {

        public TextView mTvContent;
        public ImageView mImIcon;
        public ImageView mImIconRight;
        public LinearLayout mLayoutContent;
        public ContentViewHolder(@NonNull View itemView) {
            super(itemView);
            mTvContent = itemView.findViewById(R.id.tv_content);
            mImIcon = itemView.findViewById(R.id.img_avatar);
            mImIconRight = itemView.findViewById(R.id.img_avatar_right);
            mLayoutContent = itemView.findViewById(R.id.layout_content);
        }
    }
}
