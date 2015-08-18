package com.nostra13.universalimageloader.sample.fragment.fallback;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nostra13.universalimageloader.sample.R;
import com.nostra13.universalimageloader.sample.fragment.BaseFragment;
import com.nostra13.universalimageloader.sample.recyclerview.BaseAdapter;

import java.util.List;

/**
 * Created by Matt Allen
 * 18/08/15
 * mattallen092@gmail.com
 */
public abstract class BaseFallbackImageFragment<T> extends BaseFragment
{
	private RecyclerView mList;

	@Nullable @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);
		mList = (RecyclerView) view.findViewById(R.id.list);
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		BaseAdapter<T> adapter = getAdapter();
		adapter.setItems(getDataSet());
		mList.setAdapter(adapter);
		mList.setItemAnimator(new DefaultItemAnimator());
		mList.setLayoutManager(new LinearLayoutManager(getActivity()));
	}

	protected abstract BaseAdapter<T> getAdapter();

	protected abstract List<T> getDataSet();
}
