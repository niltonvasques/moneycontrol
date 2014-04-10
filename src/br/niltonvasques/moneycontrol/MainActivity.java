package br.niltonvasques.moneycontrol;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import br.niltonvasques.moneycontrol.activity.NVFragmentActivity;
import br.niltonvasques.moneycontrol.app.MoneyControlApp;
import br.niltonvasques.moneycontrol.database.DatabaseHandler;
import br.niltonvasques.moneycontrol.view.fragment.CategoriasFragment;
import br.niltonvasques.moneycontrol.view.fragment.MainFragment;

@SuppressLint("NewApi")
public class MainActivity extends NVFragmentActivity {
	
	private static final String TAG = "[MainActivity]";

	private MoneyControlApp app;
	private DatabaseHandler db;
	
	private String[] mDrawerItens;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		app = (MoneyControlApp) getApplication();
	
		
		mDrawerItens = getResources().getStringArray(R.array.planets_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,R.layout.drawer_list_item, mDrawerItens));
        
        changeFragment( new MainFragment() );
        getActionBar().setTitle(mDrawerItens[0]);
        
        mDrawerList.setOnItemClickListener(new OnItemClickListener() {
        	@Override
        	public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
        		if(mDrawerItens[position].equals("Principal")){
        			changeFragment( new MainFragment() );
        		}else
        		if(mDrawerItens[position].equals("Categorias")){
        			changeFragment(new CategoriasFragment());
        		}
        		
        		mDrawerLayout.closeDrawer(mDrawerList);
        		getActionBar().setTitle(mDrawerItens[position]);
        		
        	}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_activity_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public int getFragmentContentID() {
		return R.id.content_frame;
	}

}
