package c.offerak.speedshopper.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import c.offerak.speedshopper.R;

public class FAQ_Details extends AppCompatActivity {

    public static final String TAG = FAQ_Details.class.getSimpleName();
    Context context;
    TextView txtQuestion, txtAnswer, titleText;
    ImageView imgScreenShot, toggleButton, backButton;
    String stringQuestion, stringAnswer, imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq_details);
        context = this;
        init();
    }

    public void init() {
        txtQuestion = findViewById(R.id.txtQuestion);
        txtAnswer = findViewById(R.id.txtAnswer);
        imgScreenShot = findViewById(R.id.imgScreenShot);
        titleText = findViewById(R.id.txtTitle);
        toggleButton = findViewById(R.id.toggleButton);
        backButton = findViewById(R.id.backButton);

        titleText.setText(R.string.faq);
        toggleButton.setVisibility(View.INVISIBLE);
        backButton.setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        stringQuestion = intent.getStringExtra("question");
        stringAnswer = intent.getStringExtra("answer");
        imagePath = intent.getStringExtra("image");
        Log.e(TAG, "imagePath: "+imagePath );

        txtQuestion.setText(stringQuestion);
        txtAnswer.setText(stringAnswer);
        Glide.with(context).load(imagePath).into(imgScreenShot);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
