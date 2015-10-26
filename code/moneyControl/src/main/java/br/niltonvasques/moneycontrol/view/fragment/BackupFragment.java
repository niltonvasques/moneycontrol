package br.niltonvasques.moneycontrol.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.devpaul.filepickerlibrary.FilePickerActivity;
import com.devpaul.filepickerlibrary.enums.FileType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import br.niltonvasques.moneycontrol.MainActivity;
import br.niltonvasques.moneycontrol.activity.NVFragmentActivity;
import br.niltonvasques.moneycontrol.app.MoneyControlApp;
import br.niltonvasques.moneycontrol.database.DatabaseHandler;
import br.niltonvasques.moneycontrol.util.DateUtil;
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
                if (db.checkDataBase()) {
                    Intent filePickerIntent = new Intent(getActivity(), FilePickerActivity.class);
                    filePickerIntent.putExtra(FilePickerActivity.REQUEST_CODE, FilePickerActivity.REQUEST_DIRECTORY);
                    startActivityForResult(filePickerIntent, FilePickerActivity.REQUEST_DIRECTORY);
                } else {
                    MessageUtils.showDefaultErrorMessage(getActivity());
                }
            }
        });

		myFragmentView.findViewById(R.id.fragmentBackupBtnRestoreBackup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent filePickerIntent = new Intent(getActivity(), FilePickerActivity.class);
                filePickerIntent.putExtra(FilePickerActivity.REQUEST_CODE, FilePickerActivity.REQUEST_FILE);
                startActivityForResult(filePickerIntent, FilePickerActivity.REQUEST_FILE);
            }
        });

		return myFragmentView;
	}

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == FilePickerActivity.REQUEST_DIRECTORY
                && resultCode == Activity.RESULT_OK) {

            String filePath = data.
                    getStringExtra(FilePickerActivity.FILE_EXTRA_DATA_PATH);
            if(filePath != null) {
                File in = new File(db.getDbPath());
                File out = new File(filePath+"/moneycontrol"+DateUtil.formatCalendarToDate(new GregorianCalendar())+".mcbk");
                System.out.println(in);
                System.out.println(out);
                try {
                    FileInputStream fis = new FileInputStream(in);
                    FileOutputStream fos = new FileOutputStream(out);
                    FileUtils.copyFile(fis, fos);
                    MessageUtils.showMessage(getActivity(), app.getString(R.string.fragment_backup_success_title),
                            app.getString(R.string.fragment_backup_success_message)+out.getPath());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    MessageUtils.showDefaultErrorMessage(getActivity());
                } catch (IOException e) {
                    e.printStackTrace();
                    MessageUtils.showDefaultErrorMessage(getActivity());
                }
            }
        }
        else if(requestCode == FilePickerActivity.REQUEST_FILE
                && resultCode == Activity.RESULT_OK) {
            String filePath = data.
                    getStringExtra(FilePickerActivity.FILE_EXTRA_DATA_PATH);
            System.out.println("Request file result");
            if(filePath != null) {
                try {
                    File out = new File(filePath);
                    if (!filePath.contains(".mcbk") || !out.exists() || !db.importDatabase(out.getPath())) {
                        MessageUtils.showDefaultErrorMessage(getActivity());
                    }else{
                        MessageUtils.showMessage(getActivity(), app.getString(R.string.fragment_backup_success_title), app.getString(R.string.fragment_backup_success_restore));
                    }
                } catch (IOException e) {
                    MessageUtils.showDefaultErrorMessage(getActivity());
                    e.printStackTrace();
                }
            }else{
                MessageUtils.showDefaultErrorMessage(getActivity());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
