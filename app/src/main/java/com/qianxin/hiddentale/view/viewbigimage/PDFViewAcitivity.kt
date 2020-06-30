package com.qianxin.hiddentale.view.viewbigimage

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.github.barteksc.pdfviewer.PDFView
import com.qianxin.hiddentale.R
import com.qianxin.hiddentale.utils.RxSaveImage

class PdfViewActivity : AppCompatActivity() {
    private var uri: String? = null
    private var title: String? = null


    //底部滑动实现同点击标题栏左右按钮效果>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        setContentView(R.layout.pdf_view_activity)
        uri = intent.getStringExtra("INTENT_URI")
        title = intent.getStringExtra("INTENT_TITLE")

        val pdfView: PDFView = findViewById(R.id.pdfView)
        //File pdfFile = new File(getCacheDir() + "/testthreepdf/" + "testing.pdf");
        //Uri path = Uri.fromFile(pdfFile);
        //pdfView.fromUri(path).load();
        pdfView.setVisibility(View.VISIBLE)
        val n: String?
        n = "klfskjkf$title"
        RxSaveImage.savePdfToGallery(this, uri.toString(), n.toString())
    }


    fun createIntent(context: AppCompatActivity?, uri: String?, title: String?): Intent? {
        val intent = Intent(context, PdfViewActivity::class.java)
        intent.putExtra("INTENT_URI", uri)
        intent.putExtra("INTENT_TITLE", title)
        return intent
    }
}
