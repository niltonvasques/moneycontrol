package br.niltonvasques.moneycontrol.util;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

public class AssetUtil {
	
	public static Drawable loadDrawableFromAsset(Context context, String path) throws IOException{
		InputStream ims = context.getAssets().open(path);
        // load image as Drawable
        Drawable d = Drawable.createFromStream(ims, null);
        
        return d;
	}
	
	public static Bitmap loadBitmapFromAsset(Context context, String path) throws IOException{
		InputStream ims = context.getAssets().open(path);
        
        return BitmapFactory.decodeStream(ims);
	}

}
