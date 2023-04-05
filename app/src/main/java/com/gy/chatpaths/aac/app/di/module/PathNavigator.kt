package com.gy.chatpaths.aac.app.di.module

import android.util.Log
import androidx.lifecycle.LiveData
import com.gy.chatpaths.model.Path
import com.gy.chatpaths.model.PathUser
import com.gy.chatpaths.model.source.CPRepository
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors
import javax.inject.Inject

data class PathState(
    val hasPrevious: Boolean,
    val hasNext: Boolean,
    val currentPath: Path?,
    val parent: Path?,
)

/**
 * The ViewModel for SmartChat was getting too big and unwieldly. It's also not very testible.
 * The objective of the [PathNavigator] is to simplify the navigation of the path so that the
 * view model can call the navigator to navigate through the path and be updated with a new model
 * of the current state, thus hiding all the internal data structures that keep track of the logic.
 *
 * The PathNavigator will need to use a Repository to obtain the information. This way we can create a
 * simple model without having to use a database and inject the response for the call to test the
 * functionality of each action the user makes in the call.
 *
 * The PathNavigator does not implement any LiveData as it does not have a lifecycle.
 *
 * [PathNavigator] maintains a pointer to the current element, a prevlist, and a nextlist
 *
 * When moving cards between lists, we deal from the top. When we get a new deck of cards, we
 * need to reverse the order so the new cards are on the top instead of the bottom [0]
 */
@Module
@InstallIn(FragmentComponent::class)
class PathNavigator @Inject constructor() : LiveData<PathState>() {

    @Inject
    lateinit var currentUser: CurrentUser

    @Inject
    lateinit var repository: CPRepository

    /**
     * Tells the navigator to show disabled paths instead of skipping over them
     */
    var showDisabled: Boolean = false
        private set

    suspend fun setShowDisabled(show: Boolean) {
        if (show != showDisabled) {
            showDisabled = show
            dataHasChanged()
        }
    }

    // Thread based mutex
    private val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    var collectionId: Int? = null
        private set
    private var notify = true

    var currentPath: Path? = null
        private set(value) {
            field = value
            if (notify) postValue(getPathState())
        }
    private val prevList: MutableList<Path> = mutableListOf()
    private val nextList: MutableList<Path> = mutableListOf()

    private val _parentHistory: MutableList<Path> = mutableListOf()
    val parentHistory: List<Path> = _parentHistory

    private fun hasNext(): Boolean {
        return if (showDisabled) {
            nextList.isNotEmpty()
        } else {
            null != nextList.find { it.enabled }
        }
    }

    private fun hasPrevious(): Boolean {
        return if (showDisabled) {
            prevList.isNotEmpty()
        } else {
            null != prevList.find { it.enabled }
        }
    }

    private fun clearCache() {
        if (currentPath != null) {
            currentPath = null
        }
        prevList.clear()
        nextList.clear()
    }

    suspend fun update(path: Path) {
        withContext(dispatcher) {
            currentPath?.apply {
                if (path.pathId == pathId) {
                    setCurrent(path)
                }
            }
        }
    }

    fun getPathState(): PathState {
        val ps = PathState(
            currentPath = currentPath,
            hasPrevious = hasPrevious(),
            hasNext = hasNext(),
            parent = _parentHistory.lastOrNull(),
        )
        Log.d("pathstate", ps.toString())
        return ps
    }

    /**
     * Set the collection by Id for this module
     *
     * @return true if the ID was valid, false otherwise
     */
    suspend fun setCollection(id: Int): Path? {
        return withContext(dispatcher) {
            collectionId = id
            currentUser.getUser()?.let {
                clearCache()
                initializeNextListRoot(it, id)

                return@withContext next()
            }
            null
        }
    }

    private suspend fun initializeNextListRoot(it: PathUser, id: Int) {
        repository.getChildrenOfParent(
            userId = it.userId,
            collectionId = id,
            parentId = null,
            showAll = showDisabled,
        )?.apply {
            nextList.addAll(this)
            nextList.reverse()
        }
    }

    suspend fun next(): Path? {
        return withContext(dispatcher) {
            while (nextList.isNotEmpty()) {
                val next = nextList.removeLast()
                if (next.enabled || showDisabled) {
                    // If current path is set
                    moveCurrentToPrevious()
                    setCurrent(next)
                    return@withContext currentPath
                } else {
                    prevList.add(next)
                }
            }
            return@withContext null
        }
    }

    private fun setCurrent(newCurrentPath: Path) {
        currentPath = newCurrentPath
    }

    /**
     * [0 = first] -> [n = last]
     */
    private fun moveCurrentToPrevious() {
        currentPath?.apply {
            prevList.add(this)
            val orig = notify
            notify = false
            currentPath = null
            notify = orig
        }
    }

    /**
     * [0=last] -> [n=first]
     */
    private fun moveCurrentToNext() {
        currentPath?.apply {
            nextList.add(this)
            val orig = notify
            notify = false
            currentPath = null
            notify = orig
        }
    }

    suspend fun previous(): Path? {
        return withContext(dispatcher) {
            while (prevList.isNotEmpty()) {
                val prev = prevList.removeLast()
                if (prev.enabled || showDisabled) {
                    // If current path is set
                    moveCurrentToNext()
                    setCurrent(prev)
                    return@withContext currentPath
                } else {
                    nextList.add(prev)
                }
            }
            return@withContext null
        }
    }

    suspend fun select(): Path? {
        return withContext(dispatcher) {
            currentPath?.let { cp ->
                if (!showDisabled) { // update the counts if we're not showing disabled paths
                    repository.incrementPathCount(
                        userId = cp.userId,
                        pathCollectionId = cp.collectionId,
                        pathId = cp.pathId,
                    )
                }
                repository.getChildrenOfParent(
                    userId = cp.userId,
                    collectionId = cp.collectionId,
                    parentId = cp.pathId,
                    showAll = showDisabled,
                )?.apply {
                    if (this.isNotEmpty()) {
                        _parentHistory.add(cp)
                        clearCache()
                        nextList.addAll(this)
                        nextList.reverse()
                        next()
                    } else {
                        cp.timesUsed += 1
                        // Force UI update
                        currentPath = cp
                    }
                }
                return@withContext currentPath
            }
            null
        }
    }

    suspend fun back(): Path? {
        return withContext(dispatcher) {
            if (prevList.isNotEmpty()) {
                // put current on next, then deal with the lists
                moveCurrentToNext()

                // reverse and insert into next because we're in the middle of two lists
                // [n] previous [0]  <--  us  --> [0] next [n]
                prevList.reverse()
                nextList.addAll(prevList.toList())
                prevList.clear()
                next()
            } else {
                // Empty, we need to make a call to the parent if they exist
                currentPath?.let { cp ->
                    currentUser.getUser()?.let {
                        val parent = _parentHistory.removeLastOrNull()
                        clearCache()
                        repository.getChildrenOfParent(
                            userId = it.userId,
                            collectionId = cp.collectionId,
                            parentId = parent?.parentId,
                            showAll = showDisabled,
                        )?.apply {
                            nextList.addAll(this)
                            nextList.reverse()
                        }

                        if (null != parent) {
                            setTo(parent)
                            return@withContext currentPath
                        } else {
                            return@withContext next()
                        }
                    }
                }
            }
            null
        }
    }

    /**
     * Sets the current tier to the specified parent matching elements in the list.
     * It does this by walking through the next items until it finds the one you're looking for.
     *
     * This does not make any database calls, just navigates the current list going forward
     */
    private fun setTo(path: Path?) {
        path?.apply {
            if (pathId == currentPath?.pathId) {
                return
            }
            // find the parent and move the rest to previous
            while (nextList.isNotEmpty()) {
                val nextPath = nextList.removeLast()
                if (pathId == nextPath.pathId) {
                    setCurrent(nextPath)
                    return
                } else {
                    prevList.add(nextPath)
                }
            }
        }
    }

    /**
     * Jump to any point in the path graph for the collection.
     * Nothing is done if the path is not part of the collection.
     */
    suspend fun goto(path: Path): Path? {
        return withContext(dispatcher) {
            // We can only jump to a path in the current collection
            if (path.collectionId != collectionId) return@withContext currentPath

            repository.getChildrenOfParent(
                userId = path.userId,
                collectionId = path.collectionId,
                parentId = path.parentId,
                showAll = showDisabled,
            )?.apply {
                clearCache()
                nextList.addAll(this)
                nextList.reverse()
            }
            setTo(path)
            return@withContext currentPath
        }
    }

    /**
     * When more complex changes have been made, such as an add/remove, lets just fetch new data
     * This is what this method is for
     */
    suspend fun dataHasChanged() {
        withContext(dispatcher) {
            // Get reference points
            val previous = prevList.lastOrNull()
            val next = nextList.lastOrNull()
            val current = currentPath

            notify = false
            clearCache()
            if (current != null) goto(current)
            if (currentPath == null && next != null) goto(next)
            if (currentPath == null && previous != null) goto(previous)
            if (currentPath == null) {
                // fetch new data
                collectionId?.let { id ->
                    currentUser.getUser()?.let {
                        clearCache()
                        initializeNextListRoot(it, id)

                        next()
                    }
                }
            }
            notify = true

            postValue(getPathState()) // notifies observer
        }
    }
}
