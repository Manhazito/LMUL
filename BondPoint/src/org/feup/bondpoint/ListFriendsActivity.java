package org.feup.bondpoint;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ListFriendsActivity extends Activity {
	private static String TAG = "ListFriendsActivity";

	private ProgressDialog progressDialog = null;
	private ListView listView;
	private Intent friendIntent = null;

	private String[] friendNames = null;
	private byte[][] friendImgsBmp = null;
	private Bitmap[] imgsBmp = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String label = null;

		Log.i("TESTE", "Entrou na onCreate");
		friendIntent = getIntent();

		friendNames = friendIntent.getStringArrayExtra("names");
		int nFriends = friendNames.length - 1;

		friendImgsBmp = new byte[nFriends + 1][];
		imgsBmp = new Bitmap[nFriends + 1];

		for (int i = 0; i <= nFriends; i++) {
			label = "picture" + i;
			friendImgsBmp[i] = friendIntent.getByteArrayExtra(label);
			if (friendImgsBmp[i] != null) {
				imgsBmp[i] = BitmapFactory.decodeByteArray(friendImgsBmp[i], 0,
						friendImgsBmp[i].length);
			} else {
				if (i == nFriends) { // User Picture!
					imgsBmp[i] = BitmapFactory.decodeResource(
							this.getResources(), R.drawable.user_no_pic);
				} else {
					imgsBmp[i] = BitmapFactory.decodeResource(
							this.getResources(), R.drawable.av_no_pic);
				}
			}
		}

		setContentView(R.layout.activity_friend_selector);

		listView = (ListView) findViewById(R.id.friends_list_view);
		listView.setEmptyView(findViewById(R.id.empty_friends_list_view));

		MyAdapter adapter = new MyAdapter(this, R.layout.friend_selector_row,
				friendNames);

		if (adapter.isEmpty())
			Log.i(TAG, "Adapter is empty!");
		else
			Log.i(TAG, "Adapter has " + (adapter.getCount()) + " elements.");

		// Assign adapter to ListView
		listView.setAdapter(adapter);

		// ListView Item Click Listener
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String itemValue = (String) listView
						.getItemAtPosition(position);

				Toast.makeText(getApplicationContext(),
						"Position :" + position + "  ListItem : " + itemValue,
						Toast.LENGTH_LONG).show();
			}
		});
	}

	// tratamento das checkboxes de convidar amigos
	public void onCheckboxClicked(View view) {
		// Is the view now checked?
		boolean checked = ((CheckBox) view).isChecked();

		// Check which checkbox was clicked
		switch (view.getId()) {
		case R.id.checkbox_meat:
			if (checked) {
				// grava id do amigo
			} else {
				finish();
			}
			break;
		// TODO: Veggie sandwich
		}
	}

	private class MyAdapter extends ArrayAdapter<String> {

		public MyAdapter(Context context, int textViewResourceId,
				String[] objects) {
			super(context, textViewResourceId, objects);

			Log.i("TESTE", "Chegou aqui ao cenas do Adapter");
			// Carregar amigos!
		}

		@Override
		public View getDropDownView(int position, View convertView,
				ViewGroup parent) {
			return getCustomView(position, convertView, parent);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return getCustomView(position, convertView, parent);
		}

		public View getCustomView(int position, View convertView,
				ViewGroup parent) {

			LayoutInflater inflater = getLayoutInflater();
			View row = inflater.inflate(R.layout.friend_selector_row, parent,
					false);

			ImageView picture = (ImageView) row.findViewById(R.id.picture);
			TextView name = (TextView) row.findViewById(R.id.name);

			Log.i("TESTE", "Olá");
			picture.setImageBitmap(imgsBmp[position]);

			name.setText(friendNames[position]);

			return row;
		}
	}
}
