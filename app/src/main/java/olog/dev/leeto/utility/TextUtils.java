package olog.dev.leeto.utility;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TextUtils {

    private TextUtils(){
        throw new AssertionError("not instantiable");
    }

    public static String dateToString(Date date){
        return new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(date);
    }

}
