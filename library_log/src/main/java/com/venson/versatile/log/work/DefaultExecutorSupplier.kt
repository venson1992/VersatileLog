package com.venson.versatile.log.work

import android.os.Process
import java.util.concurrent.*

/*
 * Singleton class for default executor supplier
 */
class DefaultExecutorSupplier private constructor() {
    /*
    thread pool executor for background tasks
     */
    private val mForBackgroundTasks: ThreadPoolExecutor

    /*
    thread pool executor for light weight background tasks
     */
    private val mForLightWeightBackgroundTasks: ThreadPoolExecutor

    /*
    thread pool executor for main thread tasks
     */
    private val mMainThreadExecutor: Executor

    /*
    returns the thread pool executor for background task
     */
    fun forBackgroundTasks(): ThreadPoolExecutor {
        return mForBackgroundTasks
    }

    /*
    returns the thread pool executor for light weight background task
     */
    fun forLightWeightBackgroundTasks(): ThreadPoolExecutor {
        return mForLightWeightBackgroundTasks
    }

    /*
    returns the thread pool executor for main thread task
     */
    fun forMainThreadTasks(): Executor {
        return mMainThreadExecutor
    }

    companion object {
        /*
        Number of cores to decide the number of threads
        */
        val NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors()

        /*
        an instance of DefaultExecutorSupplier
        */
        val instance: DefaultExecutorSupplier by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            DefaultExecutorSupplier()
        }

    }

    /*
    constructor for  DefaultExecutorSupplier
     */
    init {

        // setting the thread factory
        val backgroundPriorityThreadFactory: ThreadFactory =
            PriorityThreadFactory(Process.THREAD_PRIORITY_BACKGROUND)

        // setting the thread pool executor for mForBackgroundTasks;
        mForBackgroundTasks = ThreadPoolExecutor(
            NUMBER_OF_CORES * 2,
            NUMBER_OF_CORES * 2,
            60L,
            TimeUnit.SECONDS,
            LinkedBlockingQueue(),
            backgroundPriorityThreadFactory
        )

        // setting the thread pool executor for mForLightWeightBackgroundTasks;
        mForLightWeightBackgroundTasks = ThreadPoolExecutor(
            NUMBER_OF_CORES * 2,
            NUMBER_OF_CORES * 2,
            60L,
            TimeUnit.SECONDS,
            LinkedBlockingQueue(),
            backgroundPriorityThreadFactory
        )

        // setting the thread pool executor for mMainThreadExecutor;
        mMainThreadExecutor = MainThreadExecutor()
    }
}