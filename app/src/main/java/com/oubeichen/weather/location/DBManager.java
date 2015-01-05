package com.oubeichen.weather.location;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import android.content.Context;
import android.database.Cursor;
import android.os.Environment;

public class DBManager {
	private static final int BUFFER_SIZE = 400000;
	private static final String PACKAGE_NAME = "com.oubeichen.weather";
	public static final String DB_NAME = "citychina.db";
	private static final String DB_PATH = "/data"
			+ Environment.getDataDirectory().getAbsolutePath() + "/"
			+ PACKAGE_NAME + "/databases";

	/** copy the database under raw*/
	public static void copyDatabase(Context context) {

		File file = new File(DB_PATH);
		if (!file.isDirectory())
			file.mkdir();
		String dbfile = DB_PATH + "/" + DB_NAME;

		try {
			if (new File(dbfile).length() == 0) {

				FileOutputStream fos = new FileOutputStream(dbfile);
				byte[] buffer = new byte[BUFFER_SIZE];

				readDB(context, fos, buffer);

				fos.close();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void readDB(Context context, FileOutputStream fos, byte[] buffer)
			throws IOException {
		int count;
		InputStream is;
		is = context.getResources().getAssets().open(DB_NAME);
		while ((count = is.read(buffer)) > 0) {
			fos.write(buffer, 0, count);
		}
		is.close();
	}
    
    public static String getCityId(Context context, String cityName) {
        DBHelper helper = new DBHelper(context);
        copyDatabase(context);
        String cityCode = null;
        String sql = "select * from city_table where CITY =" + "'"
                + cityName + "'" + ";";
        Cursor cursor = helper.getReadableDatabase()
                .rawQuery(sql, null);
        if (cursor != null) {
            cursor.moveToFirst();
            cityCode = cursor.getString(cursor
                    .getColumnIndex("WEATHER_ID"));
        }
        cursor.close();
        helper.close();
        return cityCode;
    }
}
