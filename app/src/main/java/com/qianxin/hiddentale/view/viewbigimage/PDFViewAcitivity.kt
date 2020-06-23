package com.qianxin.hiddentale.view.viewbigimage

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.view.View
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.util.FitPolicy
import com.qianxin.hiddentale.R
import java.io.File

class PdfViewActivity : Activity() {
    private var name: String? = null
    private var id: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        setContentView(R.layout.pdf_view_activity)
        intent = intent
        name = intent.getStringExtra(INTENT_TITLE)
        id = intent.getStringExtra(INTENT_ID)

        val pdfView: PDFView = findViewById(R.id.pdfView)
        //File pdfFile = new File(getCacheDir() + "/testthreepdf/" + "testing.pdf");
        //Uri path = Uri.fromFile(pdfFile);
        //pdfView.fromUri(path).load();
        pdfView.setVisibility(View.VISIBLE)
        var n: String? = null
        var file: File? = null
        try {
            n = "klfskjkf$id"
            file = File(DataKeeper.tempPath.toString() + n)
            if (!file!!.exists()) {
                file = DownloadUtil.downLoadFile(this, "/temp/$n", "", name)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        pdfView.fromFile(file)
                .pageFitPolicy(FitPolicy.WIDTH)
                .enableSwipe(true) //pdf文档翻页是否是水平翻页，默认是左右滑动翻页
                .swipeHorizontal(false) //
                .enableDoubletap(true) //设置默认显示第0页
                .defaultPage(0) //允许在当前页面上绘制一些内容，通常在屏幕中间可见。
                //             .onDraw(onDrawListener)
                //                // 允许在每一页上单独绘制一个页面。只调用可见页面
                //                .onDrawAll(onDrawListener)
                //设置加载监听
                //设置页面滑动监听
                //                .onPageScroll(onPageScrollListener)
                //                .onError(onErrorListener)
                // 首次提交文档后调用。
                //                .onRender(onRenderListener)
                // 渲染风格（就像注释，颜色或表单）
                .enableAnnotationRendering(true)
                .password(null)
                .scrollHandle(null) // 改善低分辨率屏幕上的渲染
                .enableAntialiasing(true) // 页面间的间距。定义间距颜色，设置背景视图
                .spacing(0) // add dynamic spacing to fit each page on its own on the screen
                .autoSpacing(false) // mode to fit pages in the view
                .pageFitPolicy(FitPolicy.WIDTH) // fit each page to the view, else smaller pages are scaled relative to largest page.
                .fitEachPage(true)
                .load()
    }


    companion object {
        fun createIntent(context: Activity?, name: String?, no: String?): Intent {
            val intent = Intent(context, PdfViewActivity::class.java)
            intent.putExtra(INTENT_TITLE, name)
            intent.putExtra(INTENT_ID, no)
            return intent
        }
    }
}