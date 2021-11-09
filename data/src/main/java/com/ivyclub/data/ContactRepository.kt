package com.ivyclub.data

import com.ivyclub.data.model.FriendData
import com.ivyclub.data.model.GroupData

interface ContactRepository {
    fun loadFriends(): List<FriendData>
    fun saveFriend(friendData: FriendData)
    fun loadGroups(): List<GroupData>
    fun saveNewGroup(groupData: GroupData)
}