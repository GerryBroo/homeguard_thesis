package hu.geribruu.homeguardbeta.ui.postmanScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import hu.geribruu.homeguardbeta.databinding.FragmentPostmanBinding

@AndroidEntryPoint
class PostmanFragment : Fragment() {

    private val viewModel: PostmanViewModel by viewModels()
    private var _binding: FragmentPostmanBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPostmanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navBar =
            requireActivity().findViewById<BottomNavigationView>(hu.geribruu.homeguardbeta.R.id.nav_view)
        navBar.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
