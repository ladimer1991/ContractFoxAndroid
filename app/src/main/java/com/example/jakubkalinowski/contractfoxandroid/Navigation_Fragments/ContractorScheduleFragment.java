package com.example.jakubkalinowski.contractfoxandroid.Navigation_Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.jakubkalinowski.contractfoxandroid.Model.ContractorDutySession;
import com.example.jakubkalinowski.contractfoxandroid.Model.ContractorSingleDaySchedule;
import com.example.jakubkalinowski.contractfoxandroid.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by MD on 10/27/2016.
 */

public class ContractorScheduleFragment extends Fragment implements DatePickerDialog.OnDateSetListener {
    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    private Button mButton;


    //Material Dialog
    private MaterialDialog.Builder mInputDescriptionDialog;
    private MaterialDialog.Builder mInputTimeAvailabilityDialog;

    private FragmentActivity mParentActivityContext;

    private CharSequence mEventDescription;

    //Firebase database reference
    private DatabaseReference mDatabaseAllContractorScheduleReference;
    private DatabaseReference mDatabaseCurrentContractorScheduleReference;
    private String contractorIDKey;
    private Map<String, ContractorSingleDaySchedule> contractorScheduleMap = new HashMap<>();
    ArrayList<Integer> x = new ArrayList<Integer>();
    int counter = 1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.contractor_schedule_fragment_layout, parent, false);
    }


    @Override
    public void onAttach(Context context) {

        Activity a;
        if (context instanceof Activity) {
            a = (Activity) context;
            mParentActivityContext = (FragmentActivity) a;
        }
        super.onAttach(context);
    }


    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);

        mButton = (Button) view.findViewById(R.id.scheduleEventContractorScheduleButton);
        mDatabaseAllContractorScheduleReference = FirebaseDatabase.getInstance().
                getReference().child("contractor_schedule");

        getThreeWeeksAvailableHashMap();


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //get currentcontractor's schedule in a map interface.
        if (user != null) {
            contractorIDKey = user.getUid().toString();
            mDatabaseCurrentContractorScheduleReference = mDatabaseAllContractorScheduleReference.
                    child(contractorIDKey);
            mDatabaseCurrentContractorScheduleReference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    String timeStampKey = dataSnapshot.getKey();
                    ContractorSingleDaySchedule allDayScheduleObjectValue = dataSnapshot.
                            getValue(ContractorSingleDaySchedule.class);
                    if (timeStampKey != null && allDayScheduleObjectValue != null) {
                        contractorScheduleMap.put(timeStampKey, allDayScheduleObjectValue);

                    }
                    x.add(counter++);
                    System.out.println("child_added_to_hashmap-->" + counter);
                    System.out.println();
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    //this will update an existing key-value pair
                    String timeStampKey = dataSnapshot.getKey();
                    ContractorSingleDaySchedule allDayScheduleObjectValue = dataSnapshot.
                            getValue(ContractorSingleDaySchedule.class);
                    if (timeStampKey != null && allDayScheduleObjectValue != null) {
                        contractorScheduleMap.put(timeStampKey, allDayScheduleObjectValue);
                    }
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            //user is not signed in
        }


        mInputDescriptionDialog = new MaterialDialog.Builder(getActivity())
                .title("New contract")
                .content("set a new appointment")
                .inputType(InputType.TYPE_CLASS_TEXT)
                .negativeText(R.string.cancel_button_dialog_editText)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        // TODO
                        dialog.dismiss();
                    }
                }).positiveText("Set a date").input(getString(R.string.hint_dialog_editText), "",
                        new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                // Do something
                                dialog.dismiss();
                                if (input == "") {
                                    //insert something
                                } else {
                                    mEventDescription = input;
                                    promptPickADate(mEventDescription);
                                }

                            }
                        }).canceledOnTouchOutside(false);


        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mInputDescriptionDialog.show();
            }
        });
    }


    //all logic in here
    public void promptPickTimes(final long pickedDate, final CharSequence description) {
        //open show
        //setting up the builder here because it was causing issues earlier
        mInputTimeAvailabilityDialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.dialogBox_pick_a_time_header)
                .items(R.array.time_availability_list)
                .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                        /**
                         * If you use alwaysCallMultiChoiceCallback(), which is discussed below,
                         * returning false here won't allow the newly selected check box to actually be selected.
                         * See the limited multi choice dialog example in the sample project for details.
                         **/


                        ContractorSingleDaySchedule singleDaySchedule =
                                contractorScheduleMap.get(String.valueOf(pickedDate));

                        //if user has already scheduled something
                        if (singleDaySchedule == null) {
                            System.out.println("schedule object not found, created new schedule object");
                            singleDaySchedule = new
                                    ContractorSingleDaySchedule(null, null, true,
                                    getReadableDate(pickedDate), pickedDate);
                        }


                        if (which.length == 0) {
                            //nothing is selected, dont save anything to firebase

                            System.out.println("Nothing is selected");
                        } else if (which[0] == 0 && which.length == 1) {
                            // morning time range selected
                            //converting description to string for firebase
                            ContractorDutySession morningSession =
                                    new ContractorDutySession(description.toString(), "morning",
                                            getMorningStartTime(pickedDate),
                                            getMorningEndTime(pickedDate),
                                            getReadableMorningStartTime(pickedDate),
                                            getReadableMorningEndTime(pickedDate), false,
                                            getReadableDate(pickedDate));
                            singleDaySchedule.setMorningSession(morningSession);
                            if (singleDaySchedule.getMorningSession() != null &&
                                    singleDaySchedule.getEveningSession() != null &&
                                    singleDaySchedule.getMorningSession().
                                            getAvailableDuringSession() == false &&
                                    singleDaySchedule.getEveningSession().
                                            getAvailableDuringSession() == false) {
                                singleDaySchedule.setAvailableAllDay(false);
                            }
                            mDatabaseAllContractorScheduleReference.child(contractorIDKey).
                                    child(String.valueOf(singleDaySchedule.
                                            getTimeInMilliseconds())).setValue(singleDaySchedule);


                        } else if (which[0] == 1 && which.length == 1) {
                            // afternoon time range selected

                            ContractorDutySession afternoonSession = new ContractorDutySession
                                    (description.toString(),
                                            "afternoon", getAfternoonStartTime(pickedDate),
                                            getAfternoonEndTime(pickedDate),
                                            getReadableMorningStartTime(pickedDate),
                                            getReadableAfternoonEndTime(pickedDate), false,
                                            getReadableDate(pickedDate));
                            singleDaySchedule.setEveningSession(afternoonSession);
                            if (singleDaySchedule.getMorningSession() != null &&
                                    singleDaySchedule.getEveningSession() != null &&
                                    singleDaySchedule.getMorningSession().
                                            getAvailableDuringSession() == false &&
                                    singleDaySchedule.getEveningSession().
                                            getAvailableDuringSession() == false) {
                                singleDaySchedule.setAvailableAllDay(false);
                            }
                            mDatabaseAllContractorScheduleReference.child(contractorIDKey).
                                    child(String.valueOf(singleDaySchedule.getTimeInMilliseconds()))
                                    .setValue(singleDaySchedule);

                        } else if (which.length == 2) {
                            // both is selected
                            ContractorDutySession morningSession =
                                    new ContractorDutySession(description.toString(), "morning",
                                            getMorningStartTime(pickedDate),
                                            getMorningEndTime(pickedDate),
                                            getReadableMorningStartTime(pickedDate),
                                            getReadableMorningEndTime(pickedDate), false,
                                            getReadableDate(pickedDate));
                            ContractorDutySession afternoonSession = new ContractorDutySession
                                    (description.toString(),
                                            "afternoon", getAfternoonStartTime(pickedDate),
                                            getAfternoonEndTime(pickedDate),
                                            getReadableMorningStartTime(pickedDate),
                                            getReadableAfternoonEndTime(pickedDate), false,
                                            getReadableDate(pickedDate));
                            singleDaySchedule.setEveningSession(afternoonSession);
                            singleDaySchedule.setMorningSession(morningSession);
                            if (singleDaySchedule.getMorningSession() != null &&
                                    singleDaySchedule.getEveningSession() != null &&
                                    singleDaySchedule.getMorningSession().
                                            getAvailableDuringSession() == false &&
                                    singleDaySchedule.getEveningSession().
                                            getAvailableDuringSession() == false) {
                                singleDaySchedule.setAvailableAllDay(false);
                            }
                            mDatabaseAllContractorScheduleReference.child(contractorIDKey).
                                    child(String.valueOf(singleDaySchedule.getTimeInMilliseconds()))
                                    .setValue(singleDaySchedule);
                        }
                        return true;
                    }
                })
                .positiveText(R.string.saveBookingTimeText)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        // TODO
                        dialog.dismiss();
                    }
                }).canceledOnTouchOutside(false);

        MaterialDialog timeInputDialog = mInputTimeAvailabilityDialog.build();

        timeInputDialog.show();

    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        //call timepicker attribute here

        Calendar pickedDate = Calendar.getInstance();
        pickedDate.set(Calendar.MONTH, monthOfYear);
        pickedDate.set(Calendar.YEAR, year);
        pickedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        pickedDate.set(Calendar.HOUR_OF_DAY, 0);
        pickedDate.set(Calendar.MINUTE, 0);
        pickedDate.set(Calendar.SECOND, 0);
        pickedDate.set(Calendar.MILLISECOND, 0);
        long pickedDateInMillis = pickedDate.getTimeInMillis();
        view.dismiss();
        //send the date through here


        promptPickTimes(pickedDateInMillis, mEventDescription);
    }


    /**
     * This method is necessary as it helps to create empty slots for a 3 week timeframe
     */
    public void getThreeWeeksAvailableHashMap() {
        int dayCounter = 1;
        Map<String, ContractorSingleDaySchedule> threeWeeksAvailableMap = new HashMap<>();
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        int day_range = 21;
        for (int i = 0; i < day_range; i++) {
            c.add(Calendar.DAY_OF_YEAR, 1);
            System.out.println(c.getTime()+"---"+c.getTimeInMillis());
            threeWeeksAvailableMap.put(String.valueOf(c.getTimeInMillis()),
                    new ContractorSingleDaySchedule(null, null, true,
                            getReadableDate(c.getTimeInMillis()),c.getTimeInMillis()));
        }

    }

    /**
     * This method shows the dialog and limits user entry to 3 weeks starting today
     *
     * @param description
     */
    public void promptPickADate(CharSequence description) {
        int maxNumberOfDaysAhead = 21;
        mEventDescription = description;
        Calendar now = Calendar.getInstance();
        Calendar threeWeeksFromNow = Calendar.getInstance();
        threeWeeksFromNow.add(Calendar.DAY_OF_YEAR, maxNumberOfDaysAhead);
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                ContractorScheduleFragment.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setMaxDate(threeWeeksFromNow);
        dpd.setMinDate(now);
        dpd.setOkText("pick availability");
        dpd.show(mParentActivityContext.getFragmentManager(), "Datepickerdialog");

    }

    public long getMorningStartTime(long time) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        c.set(Calendar.HOUR_OF_DAY, 9);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return c.getTimeInMillis();
    }

    /**
     * Return time based on morning or afternoon session
     *
     * @param dateInMilliseconds
     */
    public String getReadableMorningStartTime(long dateInMilliseconds) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        c.setTimeInMillis(dateInMilliseconds);
        c.set(Calendar.HOUR_OF_DAY, 9);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return sdf.format(c.getTime());
    }


    /**
     * Return time based on morning or afternoon session
     *
     * @param dateInMilliseconds
     */
    public String getReadableMorningEndTime(long dateInMilliseconds) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 14);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return sdf.format(c.getTime());
    }

    public long getMorningEndTime(long time) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        c.set(Calendar.HOUR, 14);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return c.getTimeInMillis();
    }


    /**
     * Return time based on morning or afternoon session
     *
     * @param dateInMilliseconds
     */
    public String getReadableAfternoonStartTime(long dateInMilliseconds) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 15);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return sdf.format(c.getTime());

    }

    public long getAfternoonStartTime(long time) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        c.set(Calendar.HOUR_OF_DAY, 15);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return c.getTimeInMillis();
    }

    /**
     * Return time based on morning or afternoon session
     *
     * @param dateInMilliseconds
     */
    public String getReadableAfternoonEndTime(long dateInMilliseconds) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 20);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return sdf.format(c.getTime());

    }

    public long getAfternoonEndTime(long time) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        c.set(Calendar.HOUR_OF_DAY, 20);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return c.getTimeInMillis();

    }


    public String getReadableDate(long dateInMilliseconds) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(dateInMilliseconds);
        return getDayOfWeek(dateInMilliseconds) + " " + formatter.format(c.getTime());
    }


    public String getDayOfWeek(long dateInMilliseconds) {
        String[] days = {"", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(dateInMilliseconds);
        return days[c.get(Calendar.DAY_OF_WEEK)];
    }

    //Jatinder work here -- [START]
    // create a single helper method to return ContractorDutySession.java ContractorSingleDaySession


    //Jatinder work here -- [END]


}




