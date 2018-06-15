package uk.co.apptouch.facebooksharing;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.model.ShareVideo;
import com.facebook.share.model.ShareVideoContent;
import com.facebook.share.widget.ShareDialog;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_VIDEO_CODE = 1000;
    Button shareLink, sharePhoto, shareVideo;
    CallbackManager callbackManager;
    ShareDialog shareDialog;

    // private Target target = new Target;

    Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

            SharePhoto sharePhoto = new SharePhoto.Builder()
                    .setBitmap(bitmap)
                    .build();

            if(ShareDialog.canShow(SharePhotoContent.class));
            {
                SharePhotoContent content = new SharePhotoContent.Builder()
                        .addPhoto(sharePhoto)
                        .build();
                shareDialog.show(content);
            }

        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_main);

        shareLink = findViewById(R.id.shareLink);
        sharePhoto = findViewById(R.id.sharePhoto);
        shareVideo = findViewById(R.id.shareVideo);

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        shareLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onSuccess(Sharer.Result result) {
                        Toast.makeText(MainActivity.this, "Shared Successful", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(MainActivity.this, "Shared Cancelled", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onError(FacebookException error) {
                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });


                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                        .setQuote("Link to nothing")
                        .setContentUrl(Uri.parse("https://www.google.com"))
                        .build();

                if (ShareDialog.canShow(ShareLinkContent.class))
                {
                    shareDialog.show(linkContent);
                }
            }
        });

        sharePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onSuccess(Sharer.Result result) {
                        Toast.makeText(MainActivity.this, "Shared Successful", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(MainActivity.this, "Shared Cancelled", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onError(FacebookException error) {
                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

                Picasso.with(getBaseContext())
                        .load("http://pngimg.com/uploads/google/google_PNG19635.png")
                        .into(target);
            }
        });


        shareVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select video"), REQUEST_VIDEO_CODE);

            }
        });


        
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK)
        {

            if(requestCode == REQUEST_VIDEO_CODE){
                Uri selectedVideo = data.getData();

                ShareVideo video = new ShareVideo.Builder()
                        .setLocalUrl(selectedVideo)
                        .build();

                ShareVideoContent videoContent = new ShareVideoContent.Builder()
                        .setContentTitle("Video here")
                        .setContentDescription("Video desc here")
                        .setVideo(video)
                        .build();

                if (ShareDialog.canShow(ShareVideoContent.class))
                {
                    shareDialog.show(videoContent);
                }
            }
        }
    }

    private void printKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "uk.co.apptouch.facebooksharing",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }
}
