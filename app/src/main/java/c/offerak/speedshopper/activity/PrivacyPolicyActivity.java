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

import c.offerak.speedshopper.R;
import c.offerak.speedshopper.rest.Constants;
import c.offerak.speedshopper.utils.Utils;


public class PrivacyPolicyActivity extends AppCompatActivity implements OnPageChangeListener {
    private static final String TAG = PrivacyPolicyActivity.class.getSimpleName();
    Toolbar toolbar;
    Context context;
    TextView txttitle,pagecount;
    PDFView pdfView;
    WebView webView;
    Utils utils=new Utils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacypolicy);
        context = this;
        init();
    }

    private void init() {
        toolbar = findViewById(R.id.h_toolbar);
        setSupportActionBar(toolbar);
        txttitle = findViewById(R.id.toolbar_title);
        txttitle.setText(R.string.privacy);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.left_arrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        pagecount=findViewById(R.id.page_count);
//        pdfView = (PDFView) findViewById(R.id.pdfview);
        webView = findViewById(R.id.webView);
        webView.setWebViewClient(new MyBrowser());
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.loadUrl(Constants.PRIVACY_POLICY_URL);
//        openFile();
    }

    private void openFile() {
        /*pdfView.fromAsset("privacy_dummy.pdf")
                .defaultPage(0)
                .onPageChange(PrivacyPolicyActivity.this)
                .swipeVertical(true)
                .load();*/
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        pagecount.setText(page+"/"+pageCount);
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
}
