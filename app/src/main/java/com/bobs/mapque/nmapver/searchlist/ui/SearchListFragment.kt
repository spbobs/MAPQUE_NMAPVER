package com.bobs.mapque.nmapver.searchlist.ui

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.widget.SearchView
import com.bobs.baselibrary.base.BaseFragment
import com.bobs.baselibrary.util.loge
import com.bobs.mapque.nmapver.R
import com.bobs.mapque.nmapver.databinding.FragmentSearchListBinding
import com.bobs.mapque.nmapver.searchlist.data.model.SearchItem
import com.bobs.mapque.nmapver.searchlist.vm.SearchListViewModel
import com.bobs.mapque.nmapver.util.ext.hideKeyboard
import com.bobs.mapque.nmapver.util.ext.hideLoading
import com.bobs.mapque.nmapver.util.ext.showLoading
import com.bobs.mapque.nmapver.util.ext.toast
import com.bobs.mapque.nmapver.util.listener.MapListener
import com.bobs.mapque.nmapver.util.listener.SearchListListener
import kotlinx.android.synthetic.main.fragment_search_list.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class SearchListFragment(private val mapListener: MapListener<SearchItem>?) : BaseFragment<FragmentSearchListBinding>() {
    companion object{
        fun newInstance(args: Bundle? = null, mapListener: MapListener<SearchItem>?) : SearchListFragment{
            val searchListFragment = SearchListFragment(mapListener)

            args?.let {
                searchListFragment.arguments = args
            }

            return searchListFragment
        }
    }

    override var layoutId: Int = R.layout.fragment_search_list

    private val searchListViewModel: SearchListViewModel by viewModel { parametersOf() }

    private var isInitView = true

    private var compareType = SearchListViewModel.ListCompareType.SEARCH_QUERY
    private var isAsc = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewDataBinding.run{
            model = searchListViewModel
        }

        // 서치뷰 세팅
        search_view.run {
            isSubmitButtonEnabled = true

            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    // 검색 버튼 눌려졌을 때 처리
                    hideKeyboard()
                    showLoading()

                    query?.let {
                        if (!searchListViewModel.search(query)) {
                            toast("검색 결과가 없습니다.")
                        }

                        hideLoading()
                    }
                    return true
                }

                override fun onQueryTextChange(query: String?): Boolean {
                    return true
                }
            })
        }

        // 전체 목록 보기 버튼 세팅
        refreshAllBtn.setOnClickListener {
            resetSearchView()

            searchListViewModel.refreshList()
        }

        // 정렬 방법 스피너 세팅
        compare_spinner.run {
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    pos: Int,
                    id: Long
                ) {
                    if (isInitView) {
                        isInitView = false
                        return
                    }

                    when (pos) {
                        0 -> compareType = SearchListViewModel.ListCompareType.SEARCH_QUERY
                        1 -> compareType = SearchListViewModel.ListCompareType.SEARCH_DATE
                    }

                    searchListViewModel.listCompare(compareType, isAsc)

                    loge("pos: $pos, compareType: ${compareType.name}, isAsc: $isAsc")

                    resetSearchView()
                }
            }
        }

        // 차순 정렬 버튼 세팅
        compare_button.run {
            setOnClickListener {
                isAsc = if (isAsc) {
                    setImageResource(R.drawable.sharp_arrow_drop_down_black_24)
                    false
                } else {
                    setImageResource(R.drawable.sharp_arrow_drop_up_black_24)
                    true
                }

                searchListViewModel.listCompare(compareType, isAsc)

                resetSearchView()
            }
        }

        // 검색 목록 리사이클러뷰 세팅
        searchlist_recycler.run{
            adapter =
                SearchListAdapter(
                    mapListener!!,
                    object :
                        SearchListListener<SearchItem> {
                        override fun shareData(item: SearchItem) {
//                            searchListViewModel.shareData(activity as MainActivity, item)
                        }

                        override fun deleteItem(item: SearchItem) {
                            searchListViewModel.delete(item)
                        }
                    })

            setOnTouchListener { view, motionEvent ->
                resetSearchView()
                false
            }
        }
    }

    private fun resetSearchView() {
        // 서치뷰 활성화 상태에서 다른 뷰를 터치시 초기화 시킨다
        search_view.setQuery("", false)
        search_view.clearFocus()

        hideKeyboard()
    }

    override fun onResume() {
        super.onResume()
        searchListViewModel.refreshList()
    }
}