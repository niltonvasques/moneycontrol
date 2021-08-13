package br.niltonvasques.moneycontrol.activity;

import java.util.Stack;

import br.niltonvasques.moneycontrol.view.fragment.ContasFragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.MenuItem;

public abstract class NVFragmentActivity extends AppCompatActivity {

	private Fragment overFragment;
	private Stack<Fragment> stackFragments = new Stack<Fragment>();

	private void changeFragment(Fragment fragment, boolean push) {
		if(push && overFragment != null) stackFragments.push(overFragment);
		overFragment = fragment;
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();		
		fragmentTransaction.replace(getFragmentContentID(), fragment);
		fragmentTransaction.commit();
	}
	
	public void changeFragment(Fragment fragment) {
		changeFragment(fragment, true);
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
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(overFragment != null)
			overFragment.onOptionsItemSelected(item);
		return true;
	}
	
	public abstract int getFragmentContentID();
	
	public Fragment getOverFragment(){
		return overFragment;
	}
	
	@Override
	public void onBackPressed() {
		if(stackFragments.empty() || getOverFragment() instanceof ContasFragment){
			stackFragments.clear();
			super.onBackPressed();
		}else{
			changeFragment(stackFragments.pop(), false);
		}
	}
	

}
