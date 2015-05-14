package br.niltonvasques.moneycontrol;

import java.util.GregorianCalendar;
import java.util.Queue;
import java.util.Stack;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import br.niltonvasques.moneycontrol.activity.NVFragmentActivity;
import br.niltonvasques.moneycontrol.app.MoneyControlApp;
import br.niltonvasques.moneycontrol.database.DatabaseHandler;
import br.niltonvasques.moneycontrol.util.DateUtil;
import br.niltonvasques.moneycontrol.view.fragment.CategoriasFragment;
import br.niltonvasques.moneycontrol.view.fragment.ContasFragment;
import br.niltonvasques.moneycontrol.view.fragment.InvestimentosFragment;
import br.niltonvasques.moneycontrol.view.fragment.OrcamentoFragment;
import br.niltonvasques.moneycontrol.view.fragment.ReportsFragment;
import br.niltonvasques.moneycontrol.view.fragment.TransacoesFragment;
import br.niltonvasques.moneycontrolbeta.R;

@SuppressLint("NewApi")
public class MainActivity extends NVFragmentActivity {
	
	private static final int CONTAS_ITEM_MENU 			= 0;
	private static final int TRANSACOES_ITEM_MENU 		= 1;
	private static final int ORCAMENTO_ITEM_MENU 		= 2;
	private static final int CATEGORIAS_ITEM_MENU 		= 3;
	private static final int INVESTIMENTOS_ITEM_MENU 	= 4;
	private static final int RELATORIOS_ITEM_MENU 		= 5;
	private static final int SOBRE_ITEM_MENU 			= 6;

	private static final String TAG = "[MainActivity]";

	private MoneyControlApp app;
	private DatabaseHandler db;

	private String[] mDrawerItens;
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private ListView mDrawerList;	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		app = (MoneyControlApp) getApplication();


		mDrawerItens = getResources().getStringArray(R.array.menu_array);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		// Set the adapter for the list view
		mDrawerList.setAdapter(new ArrayAdapter<String>(this,R.layout.drawer_list_item, mDrawerItens));

		changeFragment( new ContasFragment() );
		getSupportActionBar().setTitle(mDrawerItens[CONTAS_ITEM_MENU]);

		mDrawerList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
				switchContent(position);				
				mDrawerLayout.closeDrawer(mDrawerList);


			}

			
		});

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {

			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				//                getActionBar().setTitle(mTitle);
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
					//                	getActionBar().setTitle(mDrawerTitle);
					invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
				}
			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
					//                	getActionBar().setTitle(mDrawerTitle);
					invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
				}
			}
		};

		// Set the drawer toggle as the DrawerListener
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		Log.d(TAG, "onPrepareOptionsMenu");
		// If the nav drawer is open, hide action items related to the content view
		try{
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.action_add).setVisible(!drawerOpen);
		menu.findItem(R.id.action_transfer).setVisible(!drawerOpen);
		}catch(Exception e){}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG, "onOptionsItemSelected");
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public int getFragmentContentID() {
		return R.id.content_frame;
	}
	
	private void switchContent(int position) {
		getSupportActionBar().setIcon(R.drawable.ic_launcher);
		getSupportActionBar().setTitle(mDrawerItens[position]);
		
		if(mDrawerItens[position].equals(mDrawerItens[CONTAS_ITEM_MENU])){
			changeFragment( new ContasFragment() );
		}else if(mDrawerItens[position].equals(mDrawerItens[CATEGORIAS_ITEM_MENU])){
			changeFragment(new CategoriasFragment());
		}else if(mDrawerItens[position].equals(mDrawerItens[RELATORIOS_ITEM_MENU])){
			changeFragment(new ReportsFragment());
		}else if(mDrawerItens[position].equals(mDrawerItens[TRANSACOES_ITEM_MENU])){
			GregorianCalendar dateRange = new GregorianCalendar();
			dateRange.set(GregorianCalendar.DAY_OF_MONTH, 1);
			Fragment fragment = new TransacoesFragment();
			Bundle args = new Bundle();
			args.putString("range", DateUtil.sqlDateFormat().format(dateRange.getTime()));
			fragment.setArguments(args);
			changeFragment(fragment);
		}else if(mDrawerItens[position].equals(mDrawerItens[INVESTIMENTOS_ITEM_MENU])){
			changeFragment(new InvestimentosFragment());
		}else if(mDrawerItens[position].equals(mDrawerItens[ORCAMENTO_ITEM_MENU])){
			changeFragment(new OrcamentoFragment());
		}
	}

}
