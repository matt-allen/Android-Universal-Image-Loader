package com.nostra13.universalimageloader.sample.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.nostra13.universalimageloader.sample.R;

import java.util.List;

/**
 * Created by Matt Allen
 * 08/08/15
 * mattallen092@gmail.com
 */
public abstract class BaseAdapter<T> extends RecyclerView.Adapter<ViewHolder>
{
	private List<T> mItems;

	public void setItems(List<T> items)
	{
		mItems = items;
		notifyDataSetChanged();
	}

	public List<T> getItems()
	{
		return mItems;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
	{
		return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_fallback, viewGroup, false));
	}

	@Override
	public int getItemCount()
	{
		return mItems != null ? mItems.size() : 0;
	}
}
