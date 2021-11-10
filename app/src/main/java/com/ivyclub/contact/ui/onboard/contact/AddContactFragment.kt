package com.ivyclub.contact.ui.onboard.contact

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.ivyclub.contact.R
import com.ivyclub.contact.databinding.FragmentAddContactBinding
import com.ivyclub.contact.model.PhoneContactData
import com.ivyclub.contact.ui.MainActivity
import com.ivyclub.contact.util.BaseFragment
import com.ivyclub.contact.util.SkipDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddContactFragment : BaseFragment<FragmentAddContactBinding>(R.layout.fragment_add_contact) {

    private lateinit var contactAdapter: ContactAdapter
    private lateinit var contactList: MutableList<PhoneContactData>
    private val viewModel: AddContactViewModel by viewModels()
    private val navController by lazy { findNavController() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contactList = mutableListOf()
        initRecyclerView()
        initButtons()
        initAppBar()
    }

    private fun initRecyclerView() {
        contactAdapter = ContactAdapter()
        binding.rvContactList.adapter = contactAdapter
    }

    private fun initButtons() = with(binding) {
        btnLoad.setOnClickListener {
            requestPermission()
        }
        btnCommit.setOnClickListener {
            viewModel.saveFriendsData(contactAdapter.addSet.toMutableList())
            val intent = Intent(context, MainActivity::class.java)
            activity?.setResult(RESULT_OK, intent)
            activity?.finish()
        }
        btnCommit.isClickable = false
    }

    private fun loadContact() {
        with(binding) {
            val buttonAnimation = AnimationUtils.loadAnimation(context, R.anim.button_down)
            val textAnimation = AnimationUtils.loadAnimation(context, R.anim.text_gone)
            val recyclerViewAnimation =
                AnimationUtils.loadAnimation(context, R.anim.recyclerview_fade_in)
            btnLoad.startAnimation(buttonAnimation)
            tvIntroduce.startAnimation(textAnimation)
            rvContactList.visibility = View.VISIBLE
            rvContactList.startAnimation(recyclerViewAnimation)
            btnLoad.isClickable = false
            btnLoad.text = "시작하기"
            contactAdapter.submitList(viewModel.getContactList())
            btnCommit.isClickable = true
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            loadContact()
        } else {
            activity?.finish()
        }
    }

    private fun requestPermission() {
        requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
    }

    private fun initAppBar() {
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.tbAddContact.setupWithNavController(navController, appBarConfiguration)
        binding.tbAddContact.title = ""
        binding.tbAddContact.inflateMenu(R.menu.menu_on_boarding_add)
        binding.tbAddContact.setOnMenuItemClickListener {
            if (it.itemId == R.id.skip) {
                SkipDialog(ok, context).showDialog()
            } else if(it.itemId == R.id.select_all) {
                contactAdapter.selectAllItem()
            }
            true
        }
    }

    private val ok = DialogInterface.OnClickListener { _, _ ->
        activity?.finish()
    }

}