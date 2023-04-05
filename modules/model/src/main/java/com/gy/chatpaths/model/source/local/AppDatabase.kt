package com.gy.chatpaths.model.source.local

import android.content.Context
import android.database.DatabaseUtils
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.gy.chatpaths.aac.data.BuildConfig
import com.gy.chatpaths.model.Path
import com.gy.chatpaths.model.PathCollection
import com.gy.chatpaths.model.PathUser

@Database(
    entities = [
        Path::class,
        PathCollection::class,
        PathUser::class,
    ],
    version = AppDatabase.DATABASE_VERSION,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pathDao(): PathDao
    abstract fun pathCollectionDao(): PathCollectionDao
    abstract fun pathUserDao(): PathUserDao

    companion object {
        const val DATABASE_VERSION = 13

        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun inMigrationTransaction(
            database: SupportSQLiteDatabase,
            migrationProcedure: () -> Unit,
        ) {
            database.beginTransaction()
            database.execSQL("PRAGMA foreign_keys=off;")

            migrationProcedure()

            database.execSQL("PRAGMA foreign_keys=on;")
            database.setTransactionSuccessful()
            database.endTransaction()
        }

//        val MIGRATION_10_11 = object : Migration(10,11) {
//            override fun migrate(database: SupportSQLiteDatabase) {
//                //updatePath(database)
//                //updatePathCollection(database)
//                updatePathToCollections(database)
//                updatePathUserML(database)
//                //updatePathUser(database)
//                //updateUserPathUniqueImages(database)
//                updateUserPathCollectionPrefs(database)
//                updatePathUserDetailView(database)
//                //updateUserPathCollectionPrefsView(database)
//            }
//
//            private fun updatePathToCollections(database: SupportSQLiteDatabase) {
//                val TABLE_NAME = "PathToCollections"
//                val TABLE_TEMP = "TABLE_TEMP"
//                database.execSQL("PRAGMA foreign_keys=off;")
//                database.beginTransaction()
//                database.execSQL("CREATE TABLE IF NOT EXISTS `${TABLE_TEMP}`  (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `pathCollectionId` INTEGER NOT NULL, `pathId` INTEGER NOT NULL, `parentId` INTEGER, `defaultPosition` INTEGER, FOREIGN KEY(`pathId`) REFERENCES `Path`(`pathId`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`pathCollectionId`) REFERENCES `PathCollection`(`collectionId`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`parentId`) REFERENCES `Path`(`pathId`) ON UPDATE NO ACTION ON DELETE CASCADE )")
//                database.execSQL("INSERT INTO `${TABLE_TEMP}` (`id`, `pathCollectionId`, `pathId`, `parentId`) SELECT `id`, `pathCollectionId`, `pathId`, `parentId` FROM `${TABLE_NAME}`")
//                database.execSQL("DROP TABLE `${TABLE_NAME}`")
//                database.execSQL("ALTER TABLE `${TABLE_TEMP}` RENAME TO `${TABLE_NAME}`")
//                database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_PathToCollections_pathCollectionId_pathId_parentId` ON `${TABLE_NAME}` (`pathCollectionId`, `pathId`, `parentId`)")
//                database.execSQL("CREATE INDEX IF NOT EXISTS `index_PathToCollections_pathCollectionId` ON `${TABLE_NAME}` (`pathCollectionId`)")
//                database.execSQL("CREATE INDEX IF NOT EXISTS `index_PathToCollections_pathId` ON `${TABLE_NAME}` (`pathId`)")
//                database.execSQL("CREATE INDEX IF NOT EXISTS `index_PathToCollections_parentId` ON `${TABLE_NAME}` (`parentId`)")
//                database.setTransactionSuccessful()
//                database.endTransaction()
//                database.execSQL("PRAGMA foreign_keys=on;")
//            }
//
//            private fun updatePathUserML(database: SupportSQLiteDatabase) {
//                val TABLE_NAME = "PathUserML"
//                val TABLE_TEMP = "TABLE_TEMP"
//                database.execSQL("PRAGMA foreign_keys=off;")
//                database.beginTransaction()
//                database.execSQL("CREATE TABLE IF NOT EXISTS `${TABLE_TEMP}` (`userId` INTEGER NOT NULL, `pathCollectionId` INTEGER NOT NULL, `pathId` INTEGER NOT NULL, `enabled` INTEGER NOT NULL DEFAULT 1, `timesUsed` INTEGER NOT NULL DEFAULT 0, `anchored` INTEGER NOT NULL DEFAULT 0, `position` INTEGER, PRIMARY KEY(`userId`, `pathCollectionId`, `pathId`), FOREIGN KEY(`userId`) REFERENCES `PathUser`(`userId`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`pathCollectionId`) REFERENCES `PathCollection`(`collectionId`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`pathId`) REFERENCES `Path`(`pathId`) ON UPDATE NO ACTION ON DELETE CASCADE )")
//                database.execSQL("INSERT INTO `${TABLE_TEMP}` (`userId`, `pathCollectionId`, `pathId`, `enabled`, `timesUsed`) SELECT `userId`, `pathCollectionId`, `pathId`, `enabled`, `timesUsed` FROM `${TABLE_NAME}`")
//                database.execSQL("DROP TABLE `${TABLE_NAME}`")
//                database.execSQL("ALTER TABLE `${TABLE_TEMP}` RENAME TO `${TABLE_NAME}`")
//                database.execSQL("CREATE INDEX IF NOT EXISTS `index_PathUserML_userId` ON `${TABLE_NAME}` (`userId`)")
//                database.execSQL("CREATE INDEX IF NOT EXISTS `index_PathUserML_pathCollectionId` ON `${TABLE_NAME}` (`pathCollectionId`)")
//                database.execSQL("CREATE INDEX IF NOT EXISTS `index_PathUserML_pathId` ON `${TABLE_NAME}` (`pathId`)")
//                database.setTransactionSuccessful()
//                database.endTransaction()
//                database.execSQL("PRAGMA foreign_keys=on;")
//            }
//
//            private fun updateUserPathCollectionPrefs(database: SupportSQLiteDatabase) {
//                val TABLE_NAME = "UserPathCollectionPrefs"
//                val TABLE_TEMP = "TABLE_TEMP"
//                database.execSQL("PRAGMA foreign_keys=off;")
//                database.beginTransaction()
//                database.execSQL("CREATE TABLE IF NOT EXISTS `${TABLE_TEMP}` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `userId` INTEGER NOT NULL, `collectionId` INTEGER NOT NULL, `enabled` INTEGER NOT NULL DEFAULT 1, `displayOrder` INTEGER, `autoSort` INTEGER NOT NULL DEFAULT 0, FOREIGN KEY(`userId`) REFERENCES `PathUser`(`userId`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`collectionId`) REFERENCES `PathCollection`(`collectionId`) ON UPDATE NO ACTION ON DELETE CASCADE )")
//                database.execSQL("INSERT INTO `${TABLE_TEMP}` (`id`, `userId`, `collectionId`, `enabled`, `displayOrder`) SELECT `id`, `userId`, `collectionId`, `enabled`, `displayOrder` FROM `${TABLE_NAME}`")
//                database.execSQL("DROP TABLE `${TABLE_NAME}`")
//                database.execSQL("ALTER TABLE `${TABLE_TEMP}` RENAME TO `${TABLE_NAME}`")
//                database.execSQL("CREATE INDEX IF NOT EXISTS `index_UserPathCollectionPrefs_userId` ON `${TABLE_NAME}` (`userId`)")
//                database.execSQL("CREATE INDEX IF NOT EXISTS `index_UserPathCollectionPrefs_collectionId` ON `${TABLE_NAME}` (`collectionId`)")
//                database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_UserPathCollectionPrefs_userId_collectionId` ON `${TABLE_NAME}` (`userId`, `collectionId`)")
//                database.setTransactionSuccessful()
//                database.endTransaction()
//                database.execSQL("PRAGMA foreign_keys=on;")
//            }
//
//            private fun updatePathUserDetailView(database: SupportSQLiteDatabase) {
//                val VIEW_NAME = "PathUserDetail"
//                database.execSQL("DROP VIEW IF EXISTS `${VIEW_NAME}`")
//                database.execSQL("CREATE VIEW `${VIEW_NAME}` AS SELECT PU.userId, P.pathId, P.imageResource as defaultImageResource, P.defaultTitleStringResource, P2C.parentId, P2C.defaultPosition, PC.collectionId, COALESCE(PUML.enabled,1) as enabled, PUML.anchored, COALESCE(PUML.timesUsed,0) as timesUsed, COALESCE(PUML.position,P2C.defaultPosition,null) as position, UPC.title AS userTitle, UPC.imageResource AS userSharedImageUri, UPUI.imageResource AS userIndividualImageUri FROM PathUser AS PU INNER JOIN Path AS P INNER JOIN PathToCollections AS P2C ON P2C.pathId = P.pathId LEFT JOIN UserPathCustomizations AS UPC ON P.pathId = UPC.pathId LEFT JOIN PathUserML AS PUML ON PUML.userId = PU.userId AND PUML.pathId = P.pathId AND PUML.pathCollectionId = P2C.pathCollectionId LEFT JOIN PathCollection AS PC ON PC.collectionId = P2C.pathCollectionId LEFT JOIN UserPathUniqueImages AS UPUI ON UPUI.userId = PU.userId AND UPUI.pathId = P.pathId")
//            }
//        }

        private val MIGRATION_11_12 = object : Migration(11, 12) {
            val tablePath = "Path"
            val tablePathCollections = "PathCollection"

            override fun migrate(database: SupportSQLiteDatabase) {
                val tableTempPath = "TEMP_PATH"
                val tableTempCollections = "TEMP_COLLECTIONS"

                database.beginTransaction()
                database.execSQL("PRAGMA foreign_keys=off;")

                updateCollections(database, tableTempCollections)
                updatePaths(database, tableTempPath)

                database.execSQL("DROP TABLE `$tablePath`")
                database.execSQL("DROP TABLE `$tablePathCollections`")

                database.execSQL("ALTER TABLE `$tableTempPath` RENAME TO `$tablePath`")
                database.execSQL("ALTER TABLE `$tableTempCollections` RENAME TO `$tablePathCollections`")

                val prefix = "android.resource://" + BuildConfig.LIBRARY_PACKAGE_NAME + "/drawable"

                database.execSQL("UPDATE `$tablePath` SET imageUri = `replace`(imageUri, 'ic_', '$prefix/ic_') WHERE imageUri LIKE 'ic_%'")
                database.execSQL("UPDATE `$tablePathCollections` SET imageUri = `replace`(imageUri, 'ic_', '$prefix/ic_') WHERE imageUri LIKE 'ic_%'")

                // Create temporary index for migrating data
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_PathCollection_oldCollectionId` ON `$tablePathCollections` (`oldCollectionId`)")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_Path_oldPathId` ON `$tablePath` (`oldPathId`)")

                // Update collection index
                database.execSQL("UPDATE Path SET collectionId = (SELECT collectionId FROM PathCollection AS pc WHERE Path.collectionId = pc.oldCollectionId AND Path.userId = pc.userId)")
                database.execSQL("UPDATE Path SET parentId = (SELECT p.pathId FROM Path AS p WHERE Path.parentId = p.oldPathId AND p.collectionId = Path.collectionId AND p.userId = Path.userId)")

//                printTable(database, tablePathCollections)
//                printTable(database, tablePath)

                updateCollectionsFinal(database, tableTempCollections)
                updatePathsFinal(database, tableTempPath)

                // Drop the rest of the tables
                database.execSQL("DROP TABLE `PathToCollections`")
                database.execSQL("DROP VIEW `PathUserDetail`")
                database.execSQL("DROP TABLE `PathUserML`")
                database.execSQL("DROP VIEW `UserPathCollectionPrefsView`")
                database.execSQL("DROP TABLE `UserPathCollectionPrefs`")
                database.execSQL("DROP TABLE `UserPathCustomizations`")
                database.execSQL("DROP TABLE `UserPathUniqueImages`")

                database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_Path_pathId` ON `Path` (`pathId`)")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_Path_userId_collectionId_parentId` ON `Path` (`userId`, `collectionId`, `parentId`)")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_Path_collectionId` ON `Path` (`collectionId`)")

                database.execSQL("PRAGMA foreign_keys=on;")
                database.setTransactionSuccessful()
                database.endTransaction()
            }

            private fun printTable(database: SupportSQLiteDatabase, tableName: String) {
                Log.d("MIGRATE", "Printing table $tableName")
                val cursor = database.query("SELECT * FROM `$tableName`")
                Log.d("MIGRATE", DatabaseUtils.dumpCursorToString(cursor))
            }

            val pudView = "PathUserDetail"
            val upcView = "UserPathCollectionPrefsView"

            fun updatePaths(
                database: SupportSQLiteDatabase,
                TABLE_NAME: String,
            ) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `$TABLE_NAME` (`pathId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `oldPathId` INTEGER NOT NULL, `userId` INTEGER NOT NULL, `collectionId` INTEGER NOT NULL, `parentId` INTEGER, `defaultPosition` INTEGER, `name` TEXT, `imageUri` TEXT, `enabled` INTEGER NOT NULL DEFAULT 1, `anchored` INTEGER NOT NULL DEFAULT 0, `timesUsed` INTEGER NOT NULL DEFAULT 0, `position` INTEGER, FOREIGN KEY(`userId`) REFERENCES `PathUser`(`userId`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`collectionId`) REFERENCES `PathCollection`(`collectionId`) ON UPDATE NO ACTION ON DELETE CASCADE )")
                database.execSQL("INSERT INTO `$TABLE_NAME` (`oldPathId`,`userId`, `collectionId`, `parentId`, `defaultPosition`, `name`, `imageUri`, `enabled`, `anchored`, `timesUsed`, `position` ) SELECT u.pathId ,u.userId, u.collectionId, u.parentId, u.defaultPosition, COALESCE(u.userTitle,u.defaultTitleStringResource), COALESCE(u.userIndividualImageUri, u.userSharedImageUri, u.defaultImageResource), COALESCE(u.enabled,1), COALESCE(u.anchored,0), COALESCE(u.timesUsed,0), u.position FROM `$pudView` AS u WHERE u.collectionId < 9999 OR u.collectionId > 10000")
            }

            fun updateCollections(
                database: SupportSQLiteDatabase,
                TABLE_NAME: String,
            ) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `$TABLE_NAME` (`collectionId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `oldCollectionId` INTEGER, `userId` INTEGER NOT NULL, `name` TEXT, `imageUri` TEXT, `displayOrder` INTEGER, `enabled` INTEGER NOT NULL DEFAULT 1, `autoSort` INTEGER NOT NULL DEFAULT 0)")
                database.execSQL("INSERT INTO `$TABLE_NAME` (`oldCollectionId`, `userId`, `name`, `imageUri`, `displayOrder`, `enabled` ) SELECT c.collectionId, COALESCE(c.userId,1), COALESCE(c.userDisplayName,c.name), COALESCE(c.displayImage, c.userDisplayImage), c.displayOrder, COALESCE(c.enabled,1) AS enabled  FROM `$upcView` AS c")
            }

            /**
             * The last migration just gets rid of the oldCollectionId column
             */
            fun updateCollectionsFinal(
                database: SupportSQLiteDatabase,
                TABLE_NAME: String,
            ) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `$TABLE_NAME` (`collectionId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `userId` INTEGER NOT NULL, `name` TEXT, `imageUri` TEXT, `displayOrder` INTEGER, `enabled` INTEGER NOT NULL DEFAULT 1, `autoSort` INTEGER NOT NULL DEFAULT 0)")
                database.execSQL("INSERT INTO `$TABLE_NAME` (`collectionId`, `userId`, `name`, `imageUri`, `displayOrder`, `enabled`, `autoSort` ) SELECT `collectionId`, `userId`, `name`, `imageUri`, `displayOrder`, `enabled` , `autoSort` FROM `$tablePathCollections`")
                database.execSQL("DROP TABLE `$tablePathCollections`")

                database.execSQL("ALTER TABLE `$TABLE_NAME` RENAME TO `$tablePathCollections`")
            }

            fun updatePathsFinal(
                database: SupportSQLiteDatabase,
                TABLE_NAME: String,
            ) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `$TABLE_NAME` (`pathId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `userId` INTEGER NOT NULL, `collectionId` INTEGER NOT NULL, `parentId` INTEGER, `defaultPosition` INTEGER, `name` TEXT, `imageUri` TEXT, `enabled` INTEGER NOT NULL DEFAULT 1, `anchored` INTEGER NOT NULL DEFAULT 0, `timesUsed` INTEGER NOT NULL DEFAULT 0, `position` INTEGER, FOREIGN KEY(`userId`) REFERENCES `PathUser`(`userId`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`collectionId`) REFERENCES `PathCollection`(`collectionId`) ON UPDATE NO ACTION ON DELETE CASCADE )")
                database.execSQL("INSERT INTO `$TABLE_NAME` (`pathId`, `userId`, `collectionId`, `parentId`, `defaultPosition`, `name`, `imageUri`, `enabled`, `anchored`, `timesUsed`, `position` ) SELECT `pathId`, `userId`, `collectionId`, `parentId`, `defaultPosition`, `name`, `imageUri`, `enabled`, `anchored`, `timesUsed`, `position` FROM `$tablePath`")

                database.execSQL("DROP TABLE `$tablePath`")

                database.execSQL("ALTER TABLE `$TABLE_NAME` RENAME TO `$tablePath`")
            }
        }

        private val MIGRATION_12_13 = object : Migration(12, 13) {

            val tablePath = "Path"

            override fun migrate(database: SupportSQLiteDatabase) {
                inMigrationTransaction(database) {
                    updatePaths(database, "TEMP_PATH")
                }
            }

            fun updatePaths(
                database: SupportSQLiteDatabase,
                TABLE_NAME: String,
            ) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `$TABLE_NAME` (`pathId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `userId` INTEGER NOT NULL, `collectionId` INTEGER NOT NULL, `parentId` INTEGER, `defaultPosition` INTEGER, `name` TEXT, `imageUri` TEXT, `audioPromptUri` TEXT, `enabled` INTEGER NOT NULL DEFAULT 1, `anchored` INTEGER NOT NULL DEFAULT 0, `timesUsed` INTEGER NOT NULL DEFAULT 0, `position` INTEGER, FOREIGN KEY(`userId`) REFERENCES `PathUser`(`userId`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`collectionId`) REFERENCES `PathCollection`(`collectionId`) ON UPDATE NO ACTION ON DELETE CASCADE )")
                database.execSQL("INSERT INTO `$TABLE_NAME` (`pathId`, `userId`, `collectionId`, `parentId`, `defaultPosition`, `name`, `imageUri`, `enabled`, `anchored`, `timesUsed`, `position` ) SELECT u.pathId ,u.userId, u.collectionId, u.parentId, u.defaultPosition, u.name, u.imageUri, u.enabled, u.anchored, u.timesUsed, u.position FROM `$tablePath` AS u")

                // Make temp table the new table
                database.execSQL("DROP TABLE `$tablePath`")
                database.execSQL("ALTER TABLE `$TABLE_NAME` RENAME TO `$tablePath`")

                database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_Path_pathId` ON `Path` (`pathId`)")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_Path_userId_collectionId_parentId` ON `Path` (`userId`, `collectionId`, `parentId`)")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_Path_collectionId` ON `Path` (`collectionId`)")
            }
        }

        private val ALL_MIGRATIONS = arrayOf<Migration>(
//            MIGRATION_10_11,
            MIGRATION_11_12,
            MIGRATION_12_13,
        )

        private val DESTRUCTIVE_MIGRATION_VERSIONS = arrayOf(
            1,
            2,
            3,
            4,
            5,
            6,
            7,
            8,
            9,
            10,
        )

        @Deprecated("Only Repository Classes should access this class directly")
        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    @Suppress("DEPRECATION") AppDatabase::class.java,
                    "sc_database",
                ).let {
                    return@let buildDatabase(it)
                }

                INSTANCE = instance
                return instance
            }
        }

        /**
         * Strategy for building the database. This lets us test our code using the same settings
         * so we don't need to duplicate it
         */
        internal fun buildDatabase(it: Builder<AppDatabase>): AppDatabase {
            it.fallbackToDestructiveMigrationFrom(*DESTRUCTIVE_MIGRATION_VERSIONS.toIntArray())
            it.addMigrations(*ALL_MIGRATIONS)
            it.fallbackToDestructiveMigrationOnDowngrade() // for BETA cases
            // if(BuildConfig.DEBUG) {
            //    it.fallbackToDestructiveMigration()
            // }
            return it.build()
        }

        fun destroyInstance() {
            Log.d("db", "closing")
            INSTANCE?.close()
            INSTANCE = null
        }
    }
}
