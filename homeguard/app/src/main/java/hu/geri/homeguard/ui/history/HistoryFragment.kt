package hu.geri.homeguard.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import hu.geri.homeguard.databinding.FragmentHistoryBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HistoryViewModel by viewModel()
    private lateinit var adapter: HistoryListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadHistory()
        setupRecyclerView()
        binding.btnDeleteHistory.setOnClickListener {
            viewModel.deleteHistory()
        }
    }

    private fun setupRecyclerView() {
        adapter = HistoryListAdapter()
        binding.recyclerViewHistory.adapter = adapter
        binding.recyclerViewHistory.layoutManager = LinearLayoutManager(context)

        activity?.let { activity ->
            viewModel.historyItems.observe(
                activity,
                Observer { histories ->
                    histories.let { adapter.submitList(it) }
                }
            )
        }
    }
}