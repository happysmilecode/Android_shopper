package c.offerak.speedshopper.activity;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnPageChangeListener;
import com.onesignal.OneSignal;

import c.offerak.speedshopper.R;
import c.offerak.speedshopper.rest.Constants;
import c.offerak.speedshopper.utils.Utils;


public class TermsConditionActivity extends AppCompatActivity implements OnPageChangeListener {
    private static final String TAG = TermsConditionActivity.class.getSimpleName();
    Toolbar toolbar;
    Context context;
    TextView toolbar_title,pagecount;
    PDFView pdfView;
    WebView webView;
    Utils utils=new Utils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_termscondition);
        context = this;
        init();
    }

    private void init() {
        toolbar = findViewById(R.id.h_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.left_arrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText(R.string.term);

        pagecount= findViewById(R.id.page_count);
//        pdfView = findViewById(R.id.pdfview);
        webView = findViewById(R.id.webView);
        webView.setWebViewClient(new MyBrowser());
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.loadUrl(Constants.TERMS_CONDITION_URL);
//        openFile();
        OneSignal.addTrigger("terms", "loaded");
    }
    public void openFile() {
        /*pdfView.fromAsset("terms_dummy.pdf")
                .defaultPage(0)
                .onPageChange(TermsConditionActivity.this)
                .swipeVertical(true)
                .load();*/
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        pagecount.setText(page+"/"+pageCount);
    }

    private class MyBrowser extends WebViewClient {

        public MyBrowser() {
            utils.showDialog(context);
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            utils.hideDialog();
            super.onPageFinished(view, url);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //stopLocationUpdates();
    }

    @Override
    public void onResume() {
        super.onResume();
        OneSignal.addTrigger("terms", "loaded");
    }
}
