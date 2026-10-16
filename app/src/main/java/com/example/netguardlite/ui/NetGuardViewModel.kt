package com.example.netguardlite.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.netguardlite.model.NetworkStatus
import com.example.netguardlite.model.TestResult
import com.example.netguardlite.network.ConnectivityTester
import com.example.netguardlite.network.NetworkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NetGuardViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = NetworkRepository(app)
    val tester = ConnectivityTester()

    val status: StateFlow<NetworkStatus> = repo.observe()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), repo.snapshot())

    private val _test = MutableStateFlow<TestResult>(TestResult.Idle)
    val test: StateFlow<TestResult> = _test.asStateFlow()

    // Checklist manual. Hari 7-8: simpan ke DataStore agar tidak hilang saat app ditutup.
    private val _checked = MutableStateFlow<Set<String>>(emptySet())
    val checked: StateFlow<Set<String>> = _checked.asStateFlow()

    fun runTest() {
        if (_test.value is TestResult.Running) return
        viewModelScope.launch {
            _test.value = TestResult.Running
            _test.value = tester.run()
        }
    }

    fun toggleChecklist(id: String) {
        _checked.update { if (id in it) it - id else it + id }
    }
}
