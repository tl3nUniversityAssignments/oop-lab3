package com.example.qraptor

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HistoryFragment : Fragment(R.layout.fragment_history) {
    private lateinit var listView: ListView
    private lateinit var adapter: HistoryAdapter
    private lateinit var historyList: MutableList<ScannedData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listView = view.findViewById(R.id.history_list)

        val sharedPreferences = requireContext().getSharedPreferences("qr_history", Context.MODE_PRIVATE)
        val jsonHistory = sharedPreferences.getString("history", "[]")

        historyList = Gson().fromJson(jsonHistory, object : TypeToken<MutableList<ScannedData>>() {}.type)

        adapter = HistoryAdapter(requireContext(), historyList)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val clickedItem = historyList[position]
            val clipboard = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(null, clickedItem.content)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, R.string.copied_to_clipboard, Toast.LENGTH_LONG).show()
        }
    }
}