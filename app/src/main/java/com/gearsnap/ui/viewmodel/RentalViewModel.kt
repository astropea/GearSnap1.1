package com.gearsnap.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gearsnap.data.RentalItem
import com.gearsnap.data.RentalRepository
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RentalViewModel(private val repository: RentalRepository = RentalRepository()) : ViewModel() {

    private val _allItems = MutableStateFlow<List<RentalItem>>(emptyList())
    val allItems: StateFlow<List<RentalItem>> = _allItems.asStateFlow()

    private val _filteredItems = MutableStateFlow<List<RentalItem>>(emptyList())
    val filteredItems: StateFlow<List<RentalItem>> = _filteredItems.asStateFlow()

    val searchQuery = MutableStateFlow("")
    val selectedCategory = MutableStateFlow<String?>(null)
    val priceRange = MutableStateFlow(0f..100f)
    val sortBy = MutableStateFlow<String?>(null)
    val isLoading = MutableStateFlow(false)

    private var listener: ListenerRegistration? = null

    init {
        startListening()

        viewModelScope.launch { searchQuery.collect { applyFilters() } }
        viewModelScope.launch { selectedCategory.collect { applyFilters() } }
        viewModelScope.launch { priceRange.collect { applyFilters() } }
        viewModelScope.launch { sortBy.collect { applyFilters() } }
    }

    private fun startListening() {
        isLoading.value = true
        listener = repository.listenAll { items ->
            _allItems.value = items
            applyFilters()
            isLoading.value = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        listener?.remove()
    }

    fun reload() {
        startListening()
    }

    fun applyFilters() {
        viewModelScope.launch {
            val q = searchQuery.value.takeIf { it.isNotBlank() }
            val cat = selectedCategory.value
            val minPd = priceRange.value.start.toDouble()
            val maxPd = priceRange.value.endInclusive.toDouble()

            var result = _allItems.value

            q?.let { query ->
                val qLower = query.lowercase()
                result = result.filter { it.title.lowercase().contains(qLower) || it.description.lowercase().contains(qLower) }
            }

            cat?.let { c ->
                result = result.filter { it.category.equals(c, true) }
            }

            result = result.filter { it.pricePerDay in minPd..maxPd }

            when (sortBy.value) {
                "priceAsc" -> result = result.sortedBy { it.pricePerDay }
                "priceDesc" -> result = result.sortedByDescending { it.pricePerDay }
                "date" -> result = result.sortedByDescending { it.datePosted }
                "rating" -> result = result.sortedByDescending { it.ownerRating }
            }

            _filteredItems.value = result
        }
    }

    fun search(text: String) { searchQuery.value = text }
    fun sortByPriceAsc() { sortBy.value = "priceAsc" }
    fun sortByPriceDesc() { sortBy.value = "priceDesc" }
    fun sortByDate() { sortBy.value = "date" }
    fun sortByRating() { sortBy.value = "rating" }

    suspend fun uploadPhoto(uri: android.net.Uri): String = repository.uploadPhoto(uri)

    suspend fun createRental(item: RentalItem): Boolean = repository.createRentalItem(item)

    suspend fun sendMessage(toId: String, fromId: String, text: String): Boolean {
        return repository.sendMessage(toId = toId, fromId = fromId, text = text)
    }

    // Helpers utilitaires pour UI
    fun getCurrentUserId(): String? {
        return com.gearsnap.auth.AuthRepository().getCurrentUser()?.uid
    }

    fun getCurrentUserName(): String? {
        return com.gearsnap.auth.AuthRepository().getCurrentUser()?.displayName
    }
}
