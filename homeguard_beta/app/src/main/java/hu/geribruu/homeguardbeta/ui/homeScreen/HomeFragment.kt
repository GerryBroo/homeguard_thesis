package hu.geribruu.homeguardbeta.ui.homeScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import hu.geribruu.homeguardbeta.databinding.FragmentHomeBinding
import hu.geribruu.homeguardbeta.ui.homeScreen.components.CircularProgressView
import kotlinx.android.synthetic.main.fragment_home.progress

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progress.animateProgress(CircularProgressView.CircularProgressState.SAFETY)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
