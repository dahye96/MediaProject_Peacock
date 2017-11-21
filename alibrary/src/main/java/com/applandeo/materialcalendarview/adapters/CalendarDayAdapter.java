package com.applandeo.materialcalendarview.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.annimon.stream.Stream;
import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.R;
import com.applandeo.materialcalendarview.utils.CalendarProperties;
import com.applandeo.materialcalendarview.utils.DateUtils;
import com.applandeo.materialcalendarview.utils.DayColorsUtils;
import com.applandeo.materialcalendarview.utils.Detail;
import com.applandeo.materialcalendarview.utils.ImageUtils;
import com.applandeo.materialcalendarview.utils.SelectedDay;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Logger;

/**
 * This class is responsible for loading a one day cell.
 * <p>
 * Created by Mateusz Kornakiewicz on 24.05.2017.
 */

class CalendarDayAdapter extends ArrayAdapter<Date> {
    private CalendarPageAdapter mCalendarPageAdapter;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private int mMonth;
    private Calendar mToday = DateUtils.getCalendar();
    private ArrayList<Detail> detaillist;
    private CalendarProperties mCalendarProperties;

    private String uid;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    CalendarDayAdapter(CalendarPageAdapter calendarPageAdapter, Context context, CalendarProperties calendarProperties,
                       ArrayList<Date> dates, int month) {
        super(context, calendarProperties.getItemLayoutResource(), dates);
        mCalendarPageAdapter = calendarPageAdapter;
        mContext = context;
        mCalendarProperties = calendarProperties;
        mMonth = month < 0 ? 11 : month;
        mLayoutInflater = LayoutInflater.from(context);
        //firebase Auth정보 get


    }

    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        if (view == null) {
            view = mLayoutInflater.inflate(mCalendarProperties.getItemLayoutResource(), parent, false);
        }
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        TextView dayLabel = (TextView) view.findViewById(R.id.dayLabel);

        TextView daystring1 = (TextView) view.findViewById(R.id.daystring);
        TextView daystring2 = (TextView) view.findViewById(R.id.daystring2);
        // Fri Dec 01 00:00:00 GMT+00:00 2017
        SimpleDateFormat a = new SimpleDateFormat("yyyyMMdd");
        String newdate = a.format(getItem(position));
        callDatabase();

        Log.i("aa", newdate);
        Calendar day = new GregorianCalendar();
        day.setTime(getItem(position));
        //Log.i("a",String.valueOf(getItem(position)));
        // Loading an image of the event

        if (isSelectedDay(day)) {
            // Set view for all SelectedDays
            Stream.of(mCalendarPageAdapter.getSelectedDays())
                    .filter(selectedDay -> selectedDay.getCalendar().equals(day))
                    .findFirst().ifPresent(selectedDay -> selectedDay.setView(dayLabel));

            DayColorsUtils.setSelectedDayColors(mContext, dayLabel, mCalendarProperties.getSelectionColor());
        } else if (isCurrentMonthDay(day) && isActiveDay(day)) { // Setting current month day color
            DayColorsUtils.setCurrentMonthDayColors(mContext, day, mToday, dayLabel, mCalendarProperties.getTodayLabelColor());

        } else { // Setting not current month day color
            DayColorsUtils.setDayColors(dayLabel, ContextCompat.getColor(mContext,
                    R.color.nextMonthDayColor), Typeface.NORMAL, R.drawable.background_transparent);
        }
        if (newdate.equals("20171120")) {
            daystring1.setText("50000");
            daystring2.setText("bbb");
        }
        dayLabel.setText(String.valueOf(day.get(Calendar.DAY_OF_MONTH)));
        //equals.등록날짜

        return view;
    }

    private boolean isSelectedDay(Calendar day) {
        return mCalendarProperties.getCalendarType() != CalendarView.CLASSIC && day.get(Calendar.MONTH) == mMonth
                && mCalendarPageAdapter.getSelectedDays().contains(new SelectedDay(day));
    }

    private boolean isCurrentMonthDay(Calendar day) {
        return day.get(Calendar.MONTH) == mMonth;
    }

    private boolean isActiveDay(Calendar day) {
        return !((mCalendarProperties.getMinimumDate() != null && day.before(mCalendarProperties.getMinimumDate()))
                || (mCalendarProperties.getMaximumDate() != null && day.after(mCalendarProperties.getMaximumDate())));
    }

    private void loadIcon(TextView text, Calendar day) {
        if (mCalendarProperties.getEventDays() == null || mCalendarProperties.getCalendarType() != CalendarView.CLASSIC) {
            text.setVisibility(View.GONE);
            return;
        }

        Stream.of(mCalendarProperties.getEventDays()).filter(eventDate ->
                eventDate.getCalendar().equals(day)).findFirst().executeIfPresent(eventDay -> {

            //ImageUtils.loadResource(dayIcon, eventDay.getImageResource());

            // If a day doesn't belong to current month then image is transparent
            // if (!isCurrentMonthDay(day) || !isActiveDay(day)) {
            //    dayIcon.setAlpha(0.2f);
            //  }

        });
    }

    void callDatabase() {
        //getDB
        DatabaseReference databaseReference = mDatabase.getReference();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataSnapshot.getValue();
                detaillist = new ArrayList<>();

                for (DataSnapshot fileSnapshot : dataSnapshot.child("users").child(uid).child("ledger").getChildren()) {
                    String id = (String) fileSnapshot.child("date").getValue();
                    System.out.println(id);
                    String imgString = fileSnapshot.child("cateImageString").getValue(String.class);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
