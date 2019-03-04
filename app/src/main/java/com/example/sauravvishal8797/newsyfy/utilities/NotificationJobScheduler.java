package com.example.sauravvishal8797.newsyfy.utilities;

import android.content.Context;
import android.util.Log;

import com.example.sauravvishal8797.newsyfy.service.NotificationJobService;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

public class NotificationJobScheduler {

    private Context mContext;

    //Unique Tag for the Job
    private static final String UNIQUE_TAG = "trending_news_notification_tag";

    //Declaring FirebaseJobDispatcher object
    private FirebaseJobDispatcher firebaseJobDispatcher;

    private int recurringPeriod = 60;

    public NotificationJobScheduler(Context context){
        mContext = context;
        firebaseJobDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(mContext));
    }

    public void scheduleJob(){
        Job myJob = firebaseJobDispatcher.newJobBuilder()
                // the JobService that will be called
                .setService(NotificationJobService.class)
                // uniquely identifies the job
                .setTag(UNIQUE_TAG)
                // a recurring job
                .setRecurring(true)
                // persist forever
                .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                // start between 0 and 60 seconds from now
                .setTrigger(Trigger.executionWindow(recurringPeriod, recurringPeriod+5))
                // don't overwrite an existing job with the same tag
                .setReplaceCurrent(false)
                // retry with exponential backoff
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                // constraints that need to be satisfied for the job to run
                .setConstraints(
                        // only run on an unmetered network
                        Constraint.ON_ANY_NETWORK
                )
                .build();
        firebaseJobDispatcher.mustSchedule(myJob);
    }
}
