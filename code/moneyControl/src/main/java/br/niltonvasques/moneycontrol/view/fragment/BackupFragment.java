package br.niltonvasques.moneycontrol.view.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.developerpaul123.filepickerlibrary.FilePickerActivity;
import com.github.developerpaul123.filepickerlibrary.enums.Request;
import com.github.developerpaul123.filepickerlibrary.enums.Scope;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.GregorianCalendar;

import br.niltonvasques.moneycontrol.MainActivity;
import br.niltonvasques.moneycontrol.activity.NVFragmentActivity;
import br.niltonvasques.moneycontrol.app.MoneyControlApp;
import br.niltonvasques.moneycontrol.database.DatabaseHandler;
import br.niltonvasques.moneycontrol.util.DateUtil;
import br.niltonvasques.moneycontrol.util.FileUtils;
import br.niltonvasques.moneycontrol.util.MessageUtils;
import br.niltonvasques.moneycontrolbeta.R;

public class BackupFragment extends Fragment {


	private static final String TAG = "[BackupFragment]";
    private static final int READ_STORAGE_PERMISSION_REQUEST_CODE = 6;

    private static final int REQUEST_BACKUP_DIR = 7;
    private static final int REQUEST_RESTORE_FILE = 8;

    private MoneyControlApp app;
	private DatabaseHandler db;
	private LayoutInflater inflater;

	private View myFragmentView;
    private String[] permissions = new String[] {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onResume() {
		super.onResume();
		((NVFragmentActivity) getActivity()).getSupportActionBar().setIcon(R.drawable.ic_launcher);
		((NVFragmentActivity) getActivity()).getSupportActionBar().setTitle(getResources().getStringArray(R.array.menu_array)[MainActivity.SOBRE_ITEM_MENU]);

		String txt = "Storage permissions? " + checkPermissionForReadExtertalStorage();
        Toast.makeText(getActivity(), txt, Toast.LENGTH_LONG).show();

        if (!checkPermissionForReadExtertalStorage()) {
            try {
                requestPermissionForReadExternalStorage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            File dir = new File(Environment.getExternalStorageDirectory().getPath());
            Log.i("BACKUP", "FILES: " + dir.listFiles());
        }
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
                    Intent filePicker = new Intent(getActivity(), FilePickerActivity.class);
                    filePicker.putExtra(FilePickerActivity.SCOPE, Scope.ALL);
                    filePicker.putExtra(FilePickerActivity.REQUEST, Request.DIRECTORY);
                    filePicker.putExtra(FilePickerActivity.INTENT_EXTRA_COLOR_ID, android.R.color.holo_green_dark);
//                    filePicker.putExtra(FilePickerActivity.MIME_TYPE, MimeType.PNG);
                    startActivityForResult(filePicker, REQUEST_BACKUP_DIR);
                } else {
                    MessageUtils.showDefaultErrorMessage(getActivity());
                }
            }
        });

		myFragmentView.findViewById(R.id.fragmentBackupBtnRestoreBackup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent filePicker = new Intent(getActivity(), FilePickerActivity.class);
                filePicker.putExtra(FilePickerActivity.SCOPE, Scope.ALL);
                filePicker.putExtra(FilePickerActivity.REQUEST, Request.FILE);
                filePicker.putExtra(FilePickerActivity.INTENT_EXTRA_COLOR_ID, android.R.color.holo_green_dark);
                startActivityForResult(filePicker, REQUEST_RESTORE_FILE);
            }
        });

		return myFragmentView;
	}

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_BACKUP_DIR && resultCode == Activity.RESULT_OK) {

            String filePath = data.
                    getStringExtra(FilePickerActivity.FILE_EXTRA_DATA_PATH);
            if(filePath != null) {
                File in = new File(db.getDbPath());
                File out = new File(filePath+"/moneycontrol"+ DateUtil.formatCalendarToDate(new GregorianCalendar())+".mcbk");
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
        else if(requestCode == REQUEST_RESTORE_FILE
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

    public boolean checkPermissionForReadExtertalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean allowed = true;
            for (String permission : permissions) {
                int result = getActivity().checkSelfPermission(permission);
                allowed = result == PackageManager.PERMISSION_GRANTED && allowed;
                System.out.println(permission + " " + (result == PackageManager.PERMISSION_GRANTED));
            }
            return allowed;
        }
        return false;
    }

    public void requestPermissionForReadExternalStorage() throws Exception {
        try {
            ActivityCompat.requestPermissions(getActivity(), permissions,
                    READ_STORAGE_PERMISSION_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
