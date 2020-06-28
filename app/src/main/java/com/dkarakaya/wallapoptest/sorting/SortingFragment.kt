package com.dkarakaya.wallapoptest.sorting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.dkarakaya.core.sorting.*
import com.dkarakaya.core.viewmodel.ViewModelFactory
import com.dkarakaya.wallapoptest.ProductListViewModel
import dagger.android.support.DaggerDialogFragment
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class SortingFragment : DaggerDialogFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: ProductListViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(ProductListViewModel::class.java)
    }

    private var sortingType: SortingType? = null

    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            sortingType = it.getSerializable(ARG_SORTING_TYPE) as SortingType
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(fragment_sorting, container, false)
        registerListeners(view)
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }

    private fun registerListeners(view: View) {
        view.rootView.buttonDistanceAsc.setOnClickListener {
            sortProducts(SortingType.DISTANCE_ASC)
        }

        view.rootView.buttonDistanceDesc.setOnClickListener {
            sortProducts(SortingType.DISTANCE_DESC)
        }

        view.rootView.buttonPriceAsc.setOnClickListener {
            sortProducts(SortingType.PRICE_ASC)
        }

        view.rootView.buttonPriceDesc.setOnClickListener {
            sortProducts(SortingType.PRICE_DESC)
        }
    }

    private fun sortProducts(sortingType: SortingType) {
        viewModel.setSortingType(sortingType)
        dismiss()
    }

    companion object {
        const val TAG_SORTINGFRAGMENT = "sortingfragment"
        private const val ARG_SORTING_TYPE = "sorting_type"
        fun newInstance(type: SortingType) =
            SortingFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_SORTING_TYPE, type)
                }
            }
    }
}
