package com.example.qraptor

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import java.text.DateFormat
import java.util.Date

class HistoryAdapter(context: Context, private val historyList: List<ScannedData>) :
    ArrayAdapter<ScannedData>(context, R.layout.item_history, historyList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_history, parent, false)

        val item = historyList[position]
        val contentTextView: TextView = view.findViewById(R.id.contentTextView)
        val formatTextView: TextView = view.findViewById(R.id.formatTextView)
        val timestampTextView: TextView = view.findViewById(R.id.timestampTextView)

        contentTextView.text = item.content
        formatTextView.text = item.format
        timestampTextView.text = DateFormat.getDateTimeInstance().format(Date(item.timestamp))

        return view
    }
}
