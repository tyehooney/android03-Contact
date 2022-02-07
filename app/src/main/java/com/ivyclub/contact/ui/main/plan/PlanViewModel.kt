package com.ivyclub.contact.ui.main.plan

import android.text.format.DateUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivyclub.contact.ui.plan_list.PlanListItemViewModel
import com.ivyclub.contact.util.throttleFist
import com.ivyclub.data.ContactRepository
import com.ivyclub.data.model.SimplePlanData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlanViewModel @Inject constructor(
    private val repository: ContactRepository
) : ViewModel() {

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _planListItems = MutableLiveData<List<PlanListItemViewModel>>()
    val planListItems: LiveData<List<PlanListItemViewModel>> = _planListItems
    private val planMap = hashMapOf<Long, SimplePlanData>()
    private val pageSize = 20

    private var planListSnapshot = emptyList<SimplePlanData>()

    init { getMyPlans() }

    private fun getMyPlans() {
        _loading.value = true

        val current = System.currentTimeMillis()
        getPlansBefore(current)
        getPlansAfter(current)
    }

    fun getPlansAfter(time: Long) {
        viewModelScope.launch {
            repository.getPagedPlanListAfter(time, pageSize)
                .throttleFist(DateUtils.SECOND_IN_MILLIS)
                .transform { newPlanList ->
                    if (newPlanList.isNotEmpty()) {
                        updatePlans(newPlanList)
                        emit(planListSnapshot.mapToPlanItemList(setFriendMap()))
                    } else {
                        cancel()
                    }
                }.collect { planListItemViewModels ->
                    _planListItems.postValue(planListItemViewModels)
                    _loading.postValue(false)
                }
        }
    }

    fun getPlansBefore(time: Long) {
        viewModelScope.launch {
            repository.getPagedPlanListBefore(time, pageSize)
                .throttleFist(DateUtils.SECOND_IN_MILLIS)
                .transform { newPlanList ->
                    if (newPlanList.isNotEmpty()) {
                        updatePlans(newPlanList)
                        emit(planListSnapshot.mapToPlanItemList(setFriendMap()))
                    } else {
                        cancel()
                    }
                }.collect { planListItemViewModels ->
                    _planListItems.postValue(planListItemViewModels)
                    _loading.postValue(false)
                }
        }
    }

    private fun updatePlans(newPlans: List<SimplePlanData>) {
        newPlans.forEach { newPlan ->
            planMap[newPlan.id] = newPlan
        }
        planListSnapshot = planMap.values.toList().sortedBy { it.date.time }
    }

    fun refreshPlanItems() {
        val previousItems = planListItems.value
        if (previousItems.isNullOrEmpty()) return

        viewModelScope.launch {
            val newItems = planListSnapshot.mapToPlanItemList(setFriendMap())
            if (previousItems != newItems) {
                _planListItems.value = newItems
            }
        }
    }

    private suspend fun setFriendMap(): Map<Long, String> {
        val friendMap = mutableMapOf<Long, String>()
        repository.getSimpleFriendData()?.forEach {
            friendMap[it.id] = it.name
        }
        return friendMap
    }

    private fun List<SimplePlanData>.mapToPlanItemList(friendMap: Map<Long, String>)
    : List<PlanListItemViewModel> {
        val planItems = mutableListOf<PlanListItemViewModel>()

        forEach { planData ->
            val friends = mutableListOf<String>()
            planData.participant.forEach { friendId ->
                friendMap[friendId]?.let { friendName ->
                    friends.add(friendName)
                }
            }
            planItems.add(
                PlanListItemViewModel(planData, friends)
            )
        }

        return planItems
    }
}