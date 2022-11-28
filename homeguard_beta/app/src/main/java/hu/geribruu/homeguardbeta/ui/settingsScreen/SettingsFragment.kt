package hu.geribruu.homeguardbeta.ui.settingsScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import hu.geribruu.homeguardbeta.R
import hu.geribruu.homeguardbeta.databinding.FragmentSettingsBinding

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private val settingsViewModel: SettingsViewModel by viewModels()
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        val navBar =
            requireActivity().findViewById<BottomNavigationView>(hu.geribruu.homeguardbeta.R.id.nav_view)
        navBar.visibility = View.VISIBLE

        binding.btnNavPostmanFragment.setOnClickListener {
            view?.let { view ->
                Navigation.findNavController(view).navigate(R.id.navigation_postman)
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
