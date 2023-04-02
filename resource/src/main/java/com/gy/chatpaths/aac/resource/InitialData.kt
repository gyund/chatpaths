package com.gy.chatpaths.aac.resource

import android.content.Context
import com.gy.chatpaths.aac.data.PathUser
import com.gy.chatpaths.aac.data.R
import com.gy.chatpaths.aac.data.source.CPRepository
import com.gy.chatpaths.aac.resource.template.collection.Essentials
import com.gy.chatpaths.aac.resource.template.collection.Starter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class InitialData {

    companion object {
        const val COLLECTION_ID_LETSCHAT = 1

        /**
         * Initialize/Update the database with factory values
         */
        suspend fun populate(context: Context, repository: CPRepository) {
            withContext(Dispatchers.IO) {
                val users = arrayOf(PathUser(1, context.getString(R.string.username_default), null))
                users.forEach {
                    repository.addUser(
                        it,
                        overwrite = false,
                    )
                }
                Essentials(context, repository, 1).build()
                Starter(context, repository, 1).build()
            }
        }

//        internal fun pathCreate(id : Int, pathName: String) : Path {
//            return Path(id, "path_${pathName}", "ic_${pathName}" )
//        }
//
//        internal fun listOfPaths(): List<Path> {
//            val ml = mutableListOf<Path>()
//            enumValues<AppDatabase.PATH>().forEach {
//                if(it.alt_img == "") {
//                    ml.add(Path(it.value, "path_${it.name}",null))
//                } else {
//                    ml.add(Path(it.value, "path_${it.name}", "ic_${it.alt_img ?: it.name}"))
//                }
//            }
//
//            ml.add(Path(10000, "path_empty_string", null))
//
//            return ml
//        }
    }
}
