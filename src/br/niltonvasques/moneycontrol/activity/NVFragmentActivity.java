package br.niltonvasques.moneycontrol.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;

public abstract class NVFragmentActivity extends FragmentActivity{

	private Fragment overFragment;

	public void changeFragment(Fragment fragment) {
		overFragment = fragment;
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();		
		fragmentTransaction.replace(getFragmentContentID(), fragment);
		fragmentTransaction.commit();
	}

	public void setOverFragment(Fragment overFragment){
		this.overFragment = overFragment;
	}

	public void removeOverFragment() {
		if(overFragment != null){
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			ft.detach(overFragment);
			ft.commit();
			overFragment = null;
		}
	}
	
	public abstract int getFragmentContentID();
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		overFragment.onOptionsItemSelected(item);
		return false;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		overFragment.onPrepareOptionsMenu(menu);
		return super.onPrepareOptionsMenu(menu);
	}

}
