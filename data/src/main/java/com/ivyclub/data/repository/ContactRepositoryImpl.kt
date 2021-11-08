package com.ivyclub.data.repository

import com.ivyclub.data.ContactRepository
import com.ivyclub.data.model.FriendData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactRepositoryImpl @Inject constructor(
    private val contactDAO: ContactDAO
) : ContactRepository {
    override fun loadFriends(): List<FriendData> {
        return contactDAO.getFriends()
    }

    override fun saveFriend(friendData: FriendData) {
        contactDAO.insertFriendData(friendData)
    }
}