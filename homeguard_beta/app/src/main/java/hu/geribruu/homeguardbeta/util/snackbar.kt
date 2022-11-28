package hu.geribruu.homeguardbeta.util

import android.app.Activity
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

inline fun Fragment.snackbar(@StringRes resId: Int, duration: Int = Snackbar.LENGTH_SHORT) {
    Snackbar.make(view!!, resId, duration).show()
}
