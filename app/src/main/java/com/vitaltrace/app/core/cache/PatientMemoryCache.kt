package com.vitaltrace.app.core.cache

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PatientMemoryCache @Inject constructor() {
    private data class Entry(val value: Any, val storedAtMillis: Long)
    private val entries = mutableMapOf<String, Entry>()
    private val ttlMillis = 120_000L

    @Synchronized
    fun <T : Any> get(key: String): T? {
        val entry = entries[key] ?: return null
        if (System.currentTimeMillis() - entry.storedAtMillis > ttlMillis) {
            entries.remove(key)
            return null
        }
        @Suppress("UNCHECKED_CAST")
        return entry.value as? T
    }

    @Synchronized
    fun put(key: String, value: Any) {
        entries[key] = Entry(value, System.currentTimeMillis())
    }

    @Synchronized
    fun invalidate(prefix: String) {
        entries.keys.removeAll { it.startsWith(prefix) }
    }

    @Synchronized
    fun clear() = entries.clear()
}
