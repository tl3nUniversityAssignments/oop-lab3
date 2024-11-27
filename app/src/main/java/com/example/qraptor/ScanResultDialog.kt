package com.example.qraptor

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialog
import com.google.zxing.Result

class ScanResultDialog(context: Context,
    result: Result) : AppCompatDialog(context) {

        init {
            setTitle(R.string.scan_result)
            setContentView(R.layout.dialog_scan_result)

            findViewById<TextView>(R.id.result)!!.text = result.text
            findViewById<TextView>(R.id.format)!!.text = result.barcodeFormat.toString()
            findViewById<View>(R.id.copy)!!.setOnClickListener {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText(null, result.text)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, R.string.copied_to_clipboard, Toast.LENGTH_LONG).show()
                dismiss()
            }
            findViewById<View>(R.id.close)!!.setOnClickListener {
                dismiss()
            }
        }
}