package br.niltonvasques.moneycontrol.view.fragment;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import br.niltonvasques.moneycontrol.MainActivity;
import br.niltonvasques.moneycontrol.activity.NVFragmentActivity;
import br.niltonvasques.moneycontrol.app.MoneyControlApp;
import br.niltonvasques.moneycontrol.database.DatabaseHandler;
import br.niltonvasques.moneycontrol.util.FileUtils;
import br.niltonvasques.moneycontrol.util.MessageUtils;
import br.niltonvasques.moneycontrolbeta.R;

public class BackupFragment extends Fragment{


	private static final String TAG = "[BackupFragment]";

	private MoneyControlApp app;
	private DatabaseHandler db;
	private LayoutInflater inflater;

	private View myFragmentView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onResume() {
		super.onResume();
		((NVFragmentActivity)getActivity()).getSupportActionBar().setIcon(R.drawable.ic_launcher);
		((NVFragmentActivity)getActivity()).getSupportActionBar().setTitle(getResources().getStringArray(R.array.menu_array)[MainActivity.SOBRE_ITEM_MENU]);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		this.inflater = inflater;

		app = (MoneyControlApp) getActivity().getApplication();

		myFragmentView = inflater.inflate(R.layout.fragment_backup, container, false);

		db = app.getDatabase();

		myFragmentView.findViewById(R.id.fragmentBackupBtnSaveBackup).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(db.checkDataBase()){
					File in = new File(db.getDbPath());
					File out = new File(Environment.getExternalStorageDirectory(), "/moneycontrol.backup");
                    System.out.println(in);
                    System.out.println(out);
					try {
						FileInputStream fis = new FileInputStream(in);
						FileOutputStream fos = new FileOutputStream(out);
						FileUtils.copyFile(fis, fos);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
						MessageUtils.showDefaultErrorMessage(getActivity());
					} catch (IOException e) {
						e.printStackTrace();
						MessageUtils.showDefaultErrorMessage(getActivity());
					}
				}else{
					MessageUtils.showDefaultErrorMessage(getActivity());
				}
			}
		});

		myFragmentView.findViewById(R.id.fragmentBackupBtnRestoreBackup).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                try {
                    File out = new File(Environment.getExternalStorageDirectory(), "/moneycontrol.backup");
                    if(!out.exists() || !db.importDatabase(out.getPath())){
                        MessageUtils.showDefaultErrorMessage(getActivity());
                    }
                } catch (IOException e) {
                    MessageUtils.showDefaultErrorMessage(getActivity());
                    e.printStackTrace();
                }
            }
		});

		return myFragmentView;
	}


}
