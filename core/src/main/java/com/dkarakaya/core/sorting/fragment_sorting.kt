package com.dkarakaya.core.sorting

import android.view.View
import android.widget.Button
import com.dkarakaya.core.R
import kotlinx.android.synthetic.main.fragment_sorting.view.*

// workaround for importing synthetic from other modules
inline val fragment_sorting: Int get() = R.layout.fragment_sorting
inline val View.buttonDistanceAsc: Button get() = buttonAscDistance
inline val View.buttonDistanceDesc: Button get() = buttonDescDistance
inline val View.buttonPriceAsc: Button get() = buttonAscPrice
inline val View.buttonPriceDesc: Button get() = buttonDescPrice
