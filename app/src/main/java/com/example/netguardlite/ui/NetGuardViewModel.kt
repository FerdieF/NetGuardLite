package com.example.netguardlite.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.netguardlite.data.ChecklistStore
import com.example.netguardlite.model.NetworkStatus
import com.example.netguardlite.model.TestResult
import com.example.netguardlite.network.ConnectivityTester
import com.example.netguardlite.network.NetworkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NetGuardViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = NetworkRepository(app)
    private val store = ChecklistStore(app)
    private val tester = ConnectivityTester()

    val status: StateFlow<NetworkStatus> = repo.observe()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), repo.snapshot())

    private val _test = MutableStateFlow<TestResult>(TestResult.Idle)
    val test: StateFlow<TestResult> = _test.asStateFlow()

    // Checklist manual. simpan ke DataStore.
    val checked: StateFlow<Set<String>> = store.checked
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    fun runTest() {
        if (_test.value is TestResult.Running) return
        viewModelScope.launch {
            _test.value = TestResult.Running
            _test.value = tester.run()
        }
    }

    fun toggleChecklist(id: String) {
        viewModelScope.launch { store.toggle(id) }
    }
}
