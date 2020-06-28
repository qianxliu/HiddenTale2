package com.qianxin.hiddentale.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.baize.fireeyekotlin.utils.log.L
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.util.FitPolicy
import com.qianxin.hiddentale.R
import com.qianxin.hiddentale.utils.CommonUtils.downloadFile
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

/*
 * 下载图片，重复下载提示已存在
 */
object RxSaveImage {

    @SuppressLint("CheckResult")
    private fun saveImageAndPathObservable(context: Activity, url: String, title: String): Observable<String> {
        return Observable.create(object : ObservableOnSubscribe<String> {
            override fun subscribe(emitter: ObservableEmitter<String>) {
                //检查图片是否已存在
                val appDir = File(context.getExternalFilesDir(null), "长安宝藏相册")
                if (appDir.exists()) {
                    val file = File(appDir, getFileName(url, title))
                    if (file.exists()) {
                        emitter.onError(Exception("图片已存在"))
                    }
                }
                if (!appDir.exists()) {
                    //没有目录创建目录
                    appDir.mkdir()
                }
                val file = File(appDir, getFileName(url, title)) //图片保存目标文件

                try {
                    //下载
                    val fileDo = Glide.with(context)
                            .load(url)
                            .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                            .get()
                    if (fileDo != null) {
                        //复制图片（将Glide中下载到缓存的图片copy到图库）
                        copyFileTo(fileDo.absolutePath, file.path)

                        //通知图库更新
                        val uri = Uri.fromFile(file)
                        val scannerIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri)
                        context.sendBroadcast(scannerIntent)
                    } else {
                        emitter.onError(Exception("无法下载到图片"))
                    }
                } catch (e: Exception) {
                    emitter.onError(e)
                }
                emitter.onNext("")
                emitter.onComplete()
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    @SuppressLint("CheckResult")
    fun saveImageToGallery(context: Activity, imageUrl: String, imageTitle: String) {
        showToast("开始下载图片")
        saveImageAndPathObservable(context, imageUrl, imageTitle)
                .subscribe({
                    val appDir: File
                    try {
                        appDir = File(context.getExternalFilesDir(null), "长安宝藏相册")
                        showToast("已保存至${appDir.absolutePath}")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, {
                    showToast("${it.message}")
                })
    }

    fun copyFileTo(oldPath: String, newPath: String) {
        try {
            val fis = FileInputStream(oldPath)
            val fos = FileOutputStream(newPath)
            fis.copyTo(fos)
            fos.flush()
            fis.close()
            fos.close()
        } catch (e: Exception) {
            L.e(msg = "${e.message}")
        }
    }

    private fun getFileName(imageUrl: String, mImageTitle: String): String {
        return when {
            imageUrl.contains(".gif") -> {
                "${mImageTitle.replace("/", "-")}.gif"
            }
            imageUrl.contains(".png") -> {
                "${mImageTitle.replace("/", "-")}.png"
            }
            imageUrl.contains(".jpeg") -> {
                "${mImageTitle.replace("/", "-")}.jpeg"
            }
            else -> {
                "${mImageTitle.replace("/", "-")}.jpg"
            }
        }
//        return when {
//            imageUrl.contains(".gif") -> "${handleStr(imageUrl)}.gif"
//            imageUrl.contains(".png") -> "${handleStr(imageUrl)}.png"
//            imageUrl.contains(".jpeg") -> "${handleStr(imageUrl)}.jpeg"
//            else -> "${handleStr(imageUrl)}.jpg"
//        }
    }

    //将图片链接转变为唯一命名
    private fun handleStr(imageUrl: String): String {
        if (imageUrl.contains("https://")) {
            val tageImg1 = imageUrl.replace("https://", "")
            return tageImg1.replace(".", "-")
        } else {
            val tageImg1 = imageUrl.replace("http://", "")
            return tageImg1.replace(".", "-")
        }
    }

    //PDF
    @SuppressLint("CheckResult")
    fun savePdfToGallery(context: Activity, pdfUrl: String, pdfTitle: String) {
        showToast("正在加载")
        savePdfAndPathObservable(context, pdfUrl, pdfTitle)
                .subscribe({
                    try {
                        showToast("加载完成!")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, {
                    showToast("${it.message}")
                })
    }

    @SuppressLint("CheckResult")
    private fun savePdfAndPathObservable(context: Activity, url: String, title: String): Observable<String> {
        return Observable.create(object : ObservableOnSubscribe<String> {
            override fun subscribe(emitter: ObservableEmitter<String>) {
                //检查图片是否已存在
                val appDir = File(context.getExternalFilesDir(null), "Data")
                val file = File(appDir, getFileName(url, title))
                if (!appDir.exists()) {
                    appDir.mkdir()
                    if (!file.exists()) {
                        downloadFile(file, url)
                    }
                }
                try {
                    //下载
                    val pdfView: PDFView = context.findViewById(R.id.pdfView)
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
                } catch (e: Exception) {
                    emitter.onError(e)
                }
                emitter.onNext("")
                emitter.onComplete()
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}