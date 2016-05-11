package br.com.anteros.vendas.gui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.List;

public abstract class BaseSpinnerAdapter<T> extends ArrayAdapter<T> {

	private int resourceLayout;
	private int resourceRadioButton;
	private T selectedItem;
	private List<T> objects;

	public BaseSpinnerAdapter(Context context, int resourceLayout, int resourceRadioButton, List<T> objects) {
		super(context, resourceLayout, resourceRadioButton, objects);
		this.resourceLayout = resourceLayout;
		this.resourceRadioButton = resourceRadioButton;
		this.objects = objects;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		selectedItem = getItem(position);
		String textLabel = getTextLabel(selectedItem);
		TextView textView = new TextView(getContext());
		textView.setGravity(Gravity.CENTER_HORIZONTAL);
		textView.setTextColor(Color.BLACK);
		textView.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
		textView.setText(textLabel);
		return textView;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(resourceLayout, null);
		}

		final T item = getItem(position);

		if (item != null) {
			RadioButton radioButton = (RadioButton) convertView.findViewById(resourceRadioButton);

			radioButton.setClickable(false);
			radioButton.setChecked(item.equals(selectedItem));

			bindDropDownListView(convertView, parent, position, item);
		}

		return convertView;
	}

	public int getIndex(T item) {
		return this.objects.indexOf(item);
	}

	public boolean contains(T item) {
		if (objects != null) {
			return objects.contains(item);
		}
		return false;
	}

	public abstract void bindDropDownListView(View convertView, ViewGroup parent, int position, T item);

	public abstract String getTextLabel(T item);

	public List<T> getObjects() {
		return objects;
	}

	public int getResourceLayout() {
		return resourceLayout;
	}

	public void setResourceLayout(int resourceLayout) {
		this.resourceLayout = resourceLayout;
	}

	public int getResourceRadioButton() {
		return resourceRadioButton;
	}

	public void setResourceRadioButton(int resourceRadioButton) {
		this.resourceRadioButton = resourceRadioButton;
	}

}