package com.ho.exportingphonenumbersandroid

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ho.exportingphonenumbersandroid.data.ContactItemModel
import com.ho.exportingphonenumbersandroid.databinding.FragmentFirstBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {
    private val CONTACTS_PERMISSION_CODE = 100
    private lateinit var contactsViewModel: ContactsViewModel
    private lateinit var contactsAdapter: ContactsAdapter
    private lateinit var contactsRecyclerView: RecyclerView

    private var _rootView: View? = null
    private val rootView get() = _rootView!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _rootView = inflater.inflate(R.layout.fragment_first, container, false)

        // Initialize ViewModel
        contactsViewModel = ViewModelProvider(this).get(ContactsViewModel::class.java)

        // Initialize RecyclerView
        contactsRecyclerView = rootView.findViewById(R.id.contactsRecyclerView)
        contactsRecyclerView.layoutManager = LinearLayoutManager(context)

        // Observe the contacts data from ViewModel
        contactsViewModel.contacts.observe(viewLifecycleOwner, Observer { contactList ->
            contactsAdapter = ContactsAdapter(contactList) { contactId ->
                contactsViewModel.toggleSelection(contactId)
            }
            contactsRecyclerView.adapter = contactsAdapter
        })

        // Load contacts if permission is granted
        if (hasContactsPermission()) {
            contactsViewModel.loadContacts(requireContext())
        } else {
            requestContactsPermission()
        }

        // Set up the export button
        val exportButton: Button = rootView.findViewById(R.id.exportButton)
        exportButton.setOnClickListener {
            exportSelectedContacts()
        }

        return rootView
    }

    private fun hasContactsPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestContactsPermission() {
        requestPermissions(arrayOf(android.Manifest.permission.READ_CONTACTS), CONTACTS_PERMISSION_CODE)
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
                Toast.makeText(requireContext(), "Permission denied to read contacts", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun exportSelectedContacts() {
        val selectedContacts = contactsViewModel.contacts.value?.filter { it.isSelected }?.map { it.phoneNumber }

        // Get the current date and format it
        val currentDate = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault()).format(Date())

        // Create the file with the current date in the filename
        val outputFile = File(requireContext().getExternalFilesDir(null), "contacts_$currentDate.txt")

        outputFile.printWriter().use { out ->
            selectedContacts?.forEach { out.println(it) }
        }

        // Share the file
        val uri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", outputFile)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        startActivity(Intent.createChooser(shareIntent, "Share contacts via"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _rootView = null // Avoid memory leaks
    }
}


