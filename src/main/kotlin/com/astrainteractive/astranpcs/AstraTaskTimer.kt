package com.astrainteractive.astranpcs

import com.astrainteractive.astralibs.async.AsyncHelper
import com.astrainteractive.astralibs.async.AsyncTask
import kotlinx.coroutines.*

class AstraTaskTimer : AsyncTask {
    companion object {
        private val jobs: List<Job> = mutableListOf()
        suspend fun cancelJobs() {
            val a: List<Deferred<Unit>> = jobs.map { AsyncHelper.async { it.cancelAndJoin() } }
            a.awaitAll()
        }
    }

    private val asyncTask = object : AsyncTask {}
    lateinit var job: Job


    fun runTaskTimer(period: Long, block: suspend CoroutineScope.() -> Unit): AstraTaskTimer {
        job = asyncTask.launch(asyncTask.coroutineContext) {
            while (true) {
                delay(period)
                block(this)
                ensureActive()
            }
        }
        return this
    }

    suspend fun cancel() {
        job.cancelAndJoin()
    }
}