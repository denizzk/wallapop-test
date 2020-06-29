package com.dkarakaya.core.util

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber


abstract class RecyclerViewPaginator(recyclerView: RecyclerView) : RecyclerView.OnScrollListener() {

    /*
     * Variables to keep track of the current page
     * */
    private var currentPage: Int = 0
    private var visibleItemCount = 0
    private var totalItemCount = 0
    private var lastVisibleItemPosition = 0
    private var findFirstVisibleItemPosition = 0
    private var findLastVisibleItemPosition = 0

    /*
     * This is a hack to ensure that the app is notified
     * only once to fetch more data. Since we use
     * scrollListener, there is a possibility that the
     * app will be notified more than once when user is
     * scrolling. This means there is a chance that the
     * same data will be fetched from the backend again.
     * This variable is to ensure that this does NOT
     * happen.
     * */
    private var loading = false

    /*
     * We pass the RecyclerView to the constructor
     * of this class to get the LayoutManager
     * */
    private val layoutManager: RecyclerView.LayoutManager?

    /*
     * isLastPage() where the UI can specify if
     * this is the last page - this data usually comes from the backend.
     *
     * loadPage() where the UI can specify to load
     * more data when this method is called.
     *
     * We can also specify another method called
     * isLoading() - to let the UI display a loading View.
     * Since I did not need to display this, I have
     * commented it out.
     * */
    //public abstract boolean isLoading()

    abstract val isLastPage: Boolean

    init {
        recyclerView.addOnScrollListener(this)
        this.layoutManager = recyclerView.layoutManager
    }

    /*
     * I have added a reset method here
     * that can be called from the UI because
     * if we have a filter option in the app,
     * we might need to refresh the whole data set
     * */
    fun reset() {
        currentPage = 0
    }

    private val gridLayoutManager = layoutManager as GridLayoutManager

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        findFirstVisibleItemPosition = gridLayoutManager.findFirstVisibleItemPosition()
        findLastVisibleItemPosition = gridLayoutManager.findLastCompletelyVisibleItemPosition()
        distanceRange(findFirstVisibleItemPosition to findLastVisibleItemPosition)
        //check for scroll down
        if (dy > 0) {
            if (isLastPage) return
            visibleItemCount = gridLayoutManager.childCount
            totalItemCount = gridLayoutManager.itemCount
            lastVisibleItemPosition = gridLayoutManager.findLastVisibleItemPosition()
            Timber.e("BEFORE - visibleItemCount + lastVisibleItemPosition >= totalItemCount - $visibleItemCount + $lastVisibleItemPosition >= $totalItemCount")
            if (visibleItemCount + lastVisibleItemPosition >= totalItemCount) {
                Timber.e("IN - visibleItemCount + lastVisibleItemPosition >= totalItemCount - $visibleItemCount + $lastVisibleItemPosition >= $totalItemCount")
                if (!loading) {
                    currentPage++
                    loading = true
                    loadPage(currentPage)
                }
            } else {
                loading = false
            }
        }
    }


    abstract fun loadPage(pageNumber: Int)

    abstract fun distanceRange(range: Pair<Int, Int>)

    companion object {
        /*
         * This is the Page Limit for each request
         * i.e. every request will fetch 10 transactions
        * */
        const val PAGE_SIZE = 10
    }
}
