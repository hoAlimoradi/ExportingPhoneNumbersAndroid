package com.ho.exportingphonenumbersandroid

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ho.exportingphonenumbersandroid.databinding.FragmentFirstBinding
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FirstFragment : Fragment() {
    private lateinit var contactsViewModel: ContactsViewModel
    private lateinit var contactsAdapter: ContactsAdapter

    private var _binding: FragmentFirstBinding? = null
    val binding: FragmentFirstBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        contactsViewModel = ViewModelProvider(this)[ContactsViewModel::class.java]

        contactsAdapter = ContactsAdapter { contactId ->
            contactsViewModel.toggleSelection(contactId)
        }
        binding.contactsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = contactsAdapter
        }

        contactsViewModel.contacts.observe(viewLifecycleOwner) { contactList ->
            contactsAdapter.updateList(contactList)
        }

        // Load contacts if permission is granted
        if (hasContactsPermission()) {
            contactsViewModel.loadContacts(requireContext())
        } else {
            requestContactsPermission()
        }

        binding.exportAsTextButton.setOnClickListener {
            exportSelectedContactsAsText()
        }

        binding.exportAsJsonButton.setOnClickListener {
            exportSelectedContactsAsJson()
        }
    }

    private fun hasContactsPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestContactsPermission() {
        requestPermissions(
            arrayOf(Manifest.permission.READ_CONTACTS),
            CONTACTS_PERMISSION_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CONTACTS_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, load contacts
                contactsViewModel.loadContacts(requireContext())
            } else {
                // Permission denied, show a message to the user
                Toast.makeText(
                    requireContext(),
                    "Permission denied to read contacts",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun exportSelectedContactsAsText() {
        val selectedContacts = contactsViewModel.getSelectedItems()
            ?.map { it.phoneNumber }
            ?: return

        val currentDate = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault())
            .format(Date())
        val directory = requireContext().getExternalFilesDir(null)
        val outputFile = File(directory, "contacts_$currentDate.txt")

        outputFile.printWriter().use { out ->
            selectedContacts.forEach { out.println(it) }
        }
        shareContacts(outputFile)
    }

    private fun exportSelectedContactsAsJson() {
        val selectedContacts = contactsViewModel.getSelectedItems() ?: return
        val json = Json.encodeToString(selectedContacts)
        val currentDate = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault())
            .format(Date())
        val directory = requireContext().getExternalFilesDir(null)
        val outputFile = File(directory, "contacts_$currentDate.txt")

        outputFile.printWriter().use { out ->
            out.println(json)
        }
        shareContacts(outputFile)
    }

    private fun shareContacts(file: File) {
        val uri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            file
        )
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        startActivity(Intent.createChooser(shareIntent, "Share contacts via"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val CONTACTS_PERMISSION_CODE = 100
    }
}


