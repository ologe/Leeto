package olog.dev.leeto.data.model;

import android.support.annotation.NonNull;

import java.util.Date;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import olog.dev.leeto.utility.DateUtils;

@Value
@ToString
@EqualsAndHashCode
public class Stop {

    Date date;
    Location location;

    public Stop(@NonNull Date date, @NonNull Location location) {
        this.date = date;
        this.location = location;
    }

    @NonNull
    public String getFormattedDate(){
        return DateUtils.toString(date);
    }

}
