package br.niltonvasques.moneycontrol.view.custom;

import java.lang.reflect.Field;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.DatePicker;

/**
 * This is a hack, but the MonthPicker isn't very easily customized.
 *
 * @author Josh Long
 */
public class MonthPicker extends DatePicker {

	private final String TAG = getClass().getName();

	/**
	 * This is *fragile* - it would be nice if we had some way to get at these fields directly from the API and systematically
	 * support enabling or disabling certain ones, or, barring that, if the NumberPicker component itself was exposed and the resulting DatePicker
	 * exposed as a composition of the NumberPicker that we could derive from.
	 *
	 * @param c the context
	 */
	private void init(Context c) {
//		boolean accessible = false;
//		Field field = null;
//		try {
//			field = ReflectionUtils.findField(DatePicker.class, "mDayPicker");
//			accessible = field.isAccessible();
//			field.setAccessible(true);
//			View val = (View) ReflectionUtils.getField(field, this);
//			val.setVisibility(View.GONE);
//		} catch (Exception ex) {
//			Log.d(TAG, "Exception when trying to override the field " +
//					"used in the superclass, " + DatePicker.class.getName() +
//					". Has the field name changed?", ex);
//		} finally {
//			if (field != null && field.isAccessible() != accessible)
//				field.setAccessible(accessible);
//		}
		
		try {
            Field[] datePickerDialogFields = getClass().getDeclaredFields();
            for (Field datePickerDialogField : datePickerDialogFields) {
                if (datePickerDialogField.getName().equals("mDatePicker")) {
                    datePickerDialogField.setAccessible(true);
                    DatePicker datePicker = (DatePicker) datePickerDialogField.get(this);
                    Field datePickerFields[] = datePickerDialogField.getType()
                            .getDeclaredFields();
                    for (Field datePickerField : datePickerFields) {
                        if ("mDayPicker".equals(datePickerField.getName())
                                || "mDaySpinner".equals(datePickerField
                                        .getName())) {
                            datePickerField.setAccessible(true);
                            Object dayPicker = new Object();
                            dayPicker = datePickerField.get(datePicker);
                            ((View) dayPicker).setVisibility(View.GONE);
                        }
                    }
                }
            }
        } catch (Exception ex) {
        }
	}

	public MonthPicker(Context context) {
		super(context);
		init(context);
	}

	public MonthPicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public MonthPicker(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
}