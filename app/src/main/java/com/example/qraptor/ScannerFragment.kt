package com.example.qraptor

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.zxing.Result

private const val CAMERA_REQUEST_CODE = 101

class ScannerFragment : Fragment(R.layout.fragment_scanner) {
    private lateinit var codeScanner: CodeScanner
    private lateinit var hintView: TextView
    private lateinit var activity : FragmentActivity


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_scanner, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val scannerView = view.findViewById<CodeScannerView>(R.id.scanner_view)
        hintView = view.findViewById(R.id.scanner_hint)
        activity = requireActivity()

        setupPermissions()

        val sharedPreferences = activity.getSharedPreferences("qr_history", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val jsonHistory = sharedPreferences.getString("history", "[]")
        val historyList: MutableList<ScannedData> = Gson().fromJson(jsonHistory, object: TypeToken<MutableList<ScannedData>>() {}.type)

        codeScanner = CodeScanner(activity, scannerView)
        codeScanner.decodeCallback = DecodeCallback { result: Result ->
            activity.runOnUiThread {
                hintView.text = getString(R.string.code_scan_last, result.text)

                val scannedData = ScannedData(
                    content = result.text,
                    format = result.barcodeFormat.name,
                    timestamp = System.currentTimeMillis()
                )

                historyList.add(scannedData)
                editor.putString("history", Gson().toJson(historyList))
                editor.apply()

                val dialog = ScanResultDialog(activity, scannedData)
                dialog.setOnDismissListener {
                    codeScanner.startPreview()
                }
                dialog.show()
            }
        }

        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            activity.runOnUiThread {
                Toast.makeText(activity, "Camera initialization error: ${it.message}",
                    Toast.LENGTH_LONG).show()
            }
        }

        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(activity,
            android.Manifest.permission.CAMERA)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(activity,
            arrayOf(android.Manifest.permission.CAMERA),
            CAMERA_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(activity, "You need the camera permission to be able to use this app!", Toast.LENGTH_SHORT)
                } else {
                    // successful
                }
            }
        }
    }
}