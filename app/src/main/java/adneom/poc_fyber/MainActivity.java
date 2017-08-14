package adneom.poc_fyber;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.fyber.Fyber;
import com.fyber.ads.AdFormat;
import com.fyber.ads.videos.RewardedVideoActivity;
import com.fyber.requesters.RequestCallback;
import com.fyber.requesters.RequestError;
import com.fyber.requesters.RewardedVideoRequester;

public class MainActivity extends AppCompatActivity implements RequestCallback{

    private final String APP_ID = "";
    //OPTIONAL
    private final String USER_ID = "";
    private final String SECURITY_TOKEN = "";

    //use to show the video
    private Intent rewardIntent;
    // We need a request code that we can use later to identify the activity the video activity when it finishes
    protected static final int REWARDED_VIDEO_REQUEST_CODE = 5678;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //start fyber
    @Override
    protected void onResume() {
        super.onResume();
        Fyber.with(APP_ID,MainActivity.this)
                //.withUserId(USER_ID)
                .withSecurityToken(SECURITY_TOKEN)
                .start();

        requestVideo();
    }

    //create the requester here
    private void requestVideo() {
        RewardedVideoRequester.create(this).request(this);
    }

    //video is available
    @Override
    public void onAdAvailable(Intent intent) {
        rewardIntent = intent;
        Log.i("Test","available");
        //starting the video here
        startActivityForResult(rewardIntent,REWARDED_VIDEO_REQUEST_CODE);
    }

    //video is not available
    @Override
    public void onAdNotAvailable(AdFormat adFormat) {
        rewardIntent = null;
        Log.i("Test","not available");
    }

    //video is not available
    @Override
    public void onRequestError(RequestError requestError) {
        Log.i("Test","not available, something wrong with the request : "+requestError.getDescription());
    }

    // As is standard in Android, onActivityResult will be called when any activity closes that had been started with startActivityForResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // handle the closing of the video
        if (resultCode == RESULT_OK && requestCode == REWARDED_VIDEO_REQUEST_CODE) {

            // check the engagement status
            String engagementResult = data.getStringExtra(RewardedVideoActivity.ENGAGEMENT_STATUS);
            switch (engagementResult) {
                case RewardedVideoActivity.REQUEST_STATUS_PARAMETER_FINISHED_VALUE:
                    // The user watched the entire video and will be rewarded
                    Log.i("Test", "The video ad was dismissed because the user completed it");
                    break;
                case RewardedVideoActivity.REQUEST_STATUS_PARAMETER_ABORTED_VALUE:
                    // The user stopped the video early and will not be rewarded
                    Log.i("Test", "The video ad was dismissed because the user explicitly closed it");
                    break;
                case RewardedVideoActivity.REQUEST_STATUS_PARAMETER_ERROR:
                    // An error occurred while showing the video and the user will not be rewarded
                    Log.i("Test", "The video ad was dismissed error during playing");
                    break;
            }
        }
    }
}
