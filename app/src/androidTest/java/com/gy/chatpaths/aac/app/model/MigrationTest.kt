package com.gy.chatpaths.aac.app.model

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import com.gy.chatpaths.aac.app.model.source.local.AppDatabase
import com.gy.chatpaths.aac.app.model.source.local.LocalCPDataSource
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test

@LargeTest
class MigrationTest {
    private val TEST_DB = "sc_database_test"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        com.gy.chatpaths.aac.app.model.source.local.AppDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory(),
    )

    fun populate11(db: SupportSQLiteDatabase) {
        db.execSQL(
            "INSERT INTO \"Path\" VALUES (1,'path_chat_words','ic_chat_words'),\n" +
                " (2,'path_i_want','ic_i_want'),\n" +
                " (3,'path_something_is_wrong','ic_something_is_wrong'),\n" +
                " (4,'path_something_to_eat_drink','ic_something_to_eat_drink'),\n" +
                " (5,'path_break_rest','ic_break_rest'),\n" +
                " (6,'path_hug','ic_hug'),\n" +
                " (7,'path_drink','ic_drink'),\n" +
                " (8,'path_eat','ic_eat'),\n" +
                " (9,'path_tablet','ic_tablet'),\n" +
                " (10,'path_mad','ic_mad'),\n" +
                " (11,'path_play_with_toys','ic_play_with_toys'),\n" +
                " (12,'path_read_a_book','ic_read_a_book'),\n" +
                " (13,'path_sick','ic_sick'),\n" +
                " (14,'path_more','ic_more'),\n" +
                " (15,'path_tired','ic_tired'),\n" +
                " (16,'path_something_hurts','ic_something_hurts'),\n" +
                " (17,'path_help','ic_help'),\n" +
                " (18,'path_all_done','ic_all_done'),\n" +
                " (19,'path_ear','ic_ear'),\n" +
                " (20,'path_foot','ic_foot'),\n" +
                " (21,'path_head','ic_head'),\n" +
                " (22,'path_hearing_aid','ic_hearing_aid'),\n" +
                " (23,'path_music','ic_music'),\n" +
                " (24,'path_nose','ic_nose'),\n" +
                " (25,'path_mouth_teeth_tongue','ic_mouth_teeth_tongue'),\n" +
                " (26,'path_mouth','ic_mouth'),\n" +
                " (27,'path_tooth','ic_tooth'),\n" +
                " (28,'path_tongue','ic_tongue'),\n" +
                " (29,'path_leg_foot_knees_toes','ic_leg_foot_knees_toes'),\n" +
                " (30,'path_leg','ic_leg'),\n" +
                " (31,'path_milk_bottle','ic_milk_bottle'),\n" +
                " (32,'path_bathroom','ic_bathroom'),\n" +
                " (33,'path_pasta','ic_pasta'),\n" +
                " (34,'path_sandwich','ic_sandwich'),\n" +
                " (35,'path_pancakes','ic_pancakes'),\n" +
                " (36,'path_dont_know','ic_dont_know'),\n" +
                " (37,'path_sad','ic_sad'),\n" +
                " (38,'path_cold','ic_cold'),\n" +
                " (39,'path_hot','ic_hot'),\n" +
                " (40,'path_chicken_nuggets','ic_chicken_nuggets'),\n" +
                " (41,'path_wet','ic_wet'),\n" +
                " (42,'path_pizza','ic_pizza'),\n" +
                " (43,'path_strawberry','ic_strawberry'),\n" +
                " (44,'path_tomato','ic_tomato'),\n" +
                " (45,'path_grapes','ic_grapes'),\n" +
                " (46,'path_eggplant','ic_eggplant'),\n" +
                " (47,'path_watermelon','ic_watermelon'),\n" +
                " (48,'path_bath','ic_bath'),\n" +
                " (49,'path_swim','ic_swim'),\n" +
                " (50,'path_lets_go','ic_lets_go'),\n" +
                " (51,'path_home','ic_home'),\n" +
                " (52,'path_somewhere_in_house_yard','ic_somewhere_in_house_yard'),\n" +
                " (53,'path_kitchen','ic_refrigerator'),\n" +
                " (54,'path_living_room','ic_living_room'),\n" +
                " (55,'path_hot_dog','ic_hot_dog'),\n" +
                " (56,'path_bathtub_shower','ic_bath'),\n" +
                " (57,'path_bedroom','ic_bed'),\n" +
                " (58,'path_somewhere_at_school','ic_school'),\n" +
                " (59,'path_classroom','ic_classroom'),\n" +
                " (60,'path_outside','ic_outside'),\n" +
                " (61,'path_table','ic_table'),\n" +
                " (62,'path_rug_carpet','ic_rug_carpet'),\n" +
                " (63,'path_gym','ic_gym'),\n" +
                " (64,'path_bathroom_school','ic_bathroom_school'),\n" +
                " (65,'path_another_room',NULL),\n" +
                " (66,'path_play_area',NULL),\n" +
                " (67,'path_food_red','ic_food_red'),\n" +
                " (68,'path_food_yellow_orange','ic_food_yellow_orange'),\n" +
                " (69,'path_food_green','ic_food_green'),\n" +
                " (70,'path_food_blue_purple','ic_food_blue_purple'),\n" +
                " (71,'path_food_white_brown','ic_food_white_brown'),\n" +
                " (72,'path_vegetable',NULL),\n" +
                " (73,'path_vegetable_red','ic_vegetable_red'),\n" +
                " (74,'path_vegetable_yellow_orange','ic_vegetable_yellow_orange'),\n" +
                " (75,'path_vegetable_green','ic_vegetable_green'),\n" +
                " (76,'path_vegetable_blue_purple','ic_eggplant'),\n" +
                " (77,'path_vegetable_brown',NULL),\n" +
                " (78,'path_fruit','ic_fruit_red'),\n" +
                " (79,'path_fruit_red','ic_fruit_red'),\n" +
                " (80,'path_fruit_yellow_orange','ic_fruit_yellow_orange'),\n" +
                " (81,'path_fruit_green','ic_fruit_green'),\n" +
                " (82,'path_fruit_blue_purple','ic_fruit_blue_purple'),\n" +
                " (83,'path_fruit_brown',NULL),\n" +
                " (84,'path_potato_red',NULL),\n" +
                " (85,'path_onion','ic_onion'),\n" +
                " (86,'path_radish',NULL),\n" +
                " (87,'path_pepper_red','ic_pepper_red'),\n" +
                " (88,'path_beets',NULL),\n" +
                " (89,'path_grapefruit',NULL),\n" +
                " (90,'path_cherry','ic_cherry'),\n" +
                " (91,'path_cranberry',NULL),\n" +
                " (92,'path_grapes_red','ic_grapes_red'),\n" +
                " (93,'path_raspberry',NULL),\n" +
                " (94,'path_apple','ic_apple'),\n" +
                " (95,'path_tomato_yellow','ic_tomato_yellow'),\n" +
                " (96,'path_potato_sweet','ic_potato_sweet'),\n" +
                " (97,'path_corn','ic_corn'),\n" +
                " (98,'path_summer_squash',NULL),\n" +
                " (99,'path_pumpkin','ic_pumpkin'),\n" +
                " (100,'path_pepper_yellow',NULL),\n" +
                " (101,'path_carrot','ic_carrot'),\n" +
                " (102,'path_butternut_squash',NULL),\n" +
                " (103,'path_tangerine',NULL),\n" +
                " (104,'path_pineapple','ic_pineapple'),\n" +
                " (105,'path_peach','ic_peach'),\n" +
                " (106,'path_mango',NULL),\n" +
                " (107,'path_orange','ic_orange'),\n" +
                " (108,'path_apricot',NULL),\n" +
                " (109,'path_nectarine',NULL),\n" +
                " (110,'path_lemon','ic_lemon'),\n" +
                " (111,'path_melon',NULL),\n" +
                " (112,'path_banana','ic_banana'),\n" +
                " (113,'path_zucchini',NULL),\n" +
                " (114,'path_spinach',NULL),\n" +
                " (115,'path_onion_green',NULL),\n" +
                " (116,'path_lettuce','ic_lettuce'),\n" +
                " (117,'path_cucumber','ic_cucumber'),\n" +
                " (118,'path_celery',NULL),\n" +
                " (119,'path_cabbage_green',NULL),\n" +
                " (120,'path_beans_green',NULL),\n" +
                " (121,'path_brussel_sprouts',NULL),\n" +
                " (122,'path_broccoli','ic_broccoli'),\n" +
                " (123,'path_asparagus',NULL),\n" +
                " (124,'path_peas',NULL),\n" +
                " (125,'path_lime','ic_lime'),\n" +
                " (126,'path_honeydew',NULL),\n" +
                " (127,'path_grapes_green','ic_grapes_green'),\n" +
                " (128,'path_avocado','ic_avocado'),\n" +
                " (129,'path_apple_green','ic_apple_green'),\n" +
                " (130,'path_olives_black',NULL),\n" +
                " (131,'path_rasins',NULL),\n" +
                " (132,'path_grape_juice','ic_grape_juice'),\n" +
                " (133,'path_plums',NULL),\n" +
                " (134,'path_blueberries',NULL),\n" +
                " (135,'path_blackberries',NULL),\n" +
                " (136,'path_pear_yellow','ic_pear_yellow'),\n" +
                " (137,'path_pear_green','ic_pear_green'),\n" +
                " (138,'path_garlic','ic_garlic'),\n" +
                " (139,'path_pretzel','ic_pretzel'),\n" +
                " (140,'path_food_warm','ic_food_warm'),\n" +
                " (141,'path_food_cold','ic_food_cold'),\n" +
                " (142,'path_ice_cream_cone','ic_ice_cream_cone'),\n" +
                " (143,'path_ice_cream_dish','ic_ice_cream_dish'),\n" +
                " (144,'path_bread','ic_bread'),\n" +
                " (145,'path_stew','ic_stew'),\n" +
                " (146,'path_soup','ic_soup'),\n" +
                " (147,'path_egg_cooked','ic_egg_cooked'),\n" +
                " (148,'path_cheese','ic_cheese'),\n" +
                " (149,'path_food_sweet','ic_food_sweet'),\n" +
                " (150,'path_doughnut','ic_doughnut'),\n" +
                " (151,'path_candy','ic_candy'),\n" +
                " (152,'path_candy_bar','ic_candy_bar'),\n" +
                " (153,'path_honey','ic_honey'),\n" +
                " (154,'path_cake','ic_cake'),\n" +
                " (155,'path_fries','ic_fries'),\n" +
                " (156,'path_lollypop','ic_lollypop'),\n" +
                " (157,'path_pita','ic_pita'),\n" +
                " (158,'path_salad','ic_salad'),\n" +
                " (159,'path_steak','ic_steak'),\n" +
                " (160,'path_windy','ic_windy'),\n" +
                " (161,'path_mushroom','ic_mushroom'),\n" +
                " (162,'path_waffle','ic_waffle'),\n" +
                " (163,'path_butter','ic_butter'),\n" +
                " (164,'path_placeholder','ic_hand_raise'),\n" +
                " (165,'path_something_to_say','ic_question'),\n" +
                " (166,'path_whats_wrong','ic_question'),\n" +
                " (167,'path_hungry','ic_hungry'),\n" +
                " (168,'path_uncomfortable','ic_hot'),\n" +
                " (169,'path_why_mad','ic_question'),\n" +
                " (170,'path_wanted_something','ic_i_want'),\n" +
                " (171,'path_dont_like','ic_dont_like'),\n" +
                " (172,'path_dont_like_something','ic_dont_like'),\n" +
                " (173,'path_kiwi','ic_kiwi'),\n" +
                " (174,'path_cookie','ic_cookie'),\n" +
                " (175,'path_milk_glass','ic_milk_glass'),\n" +
                " (176,'path_cereal','ic_cereal'),\n" +
                " (177,'path_school','ic_school'),\n" +
                " (178,'path_family','ic_family'),\n" +
                " (179,'path_mother','ic_mother'),\n" +
                " (180,'path_father','ic_father'),\n" +
                " (181,'path_sister','ic_sister'),\n" +
                " (182,'path_brother','ic_brother'),\n" +
                " (183,'path_baby','ic_baby'),\n" +
                " (184,'path_grandma','ic_grandma'),\n" +
                " (185,'path_grandpa','ic_grandpa'),\n" +
                " (186,'path_shower','ic_shower'),\n" +
                " (187,'path_cupcake','ic_cupcake'),\n" +
                " (188,'path_pie','ic_pie'),\n" +
                " (189,'path_window','ic_window'),\n" +
                " (190,'path_something_different','ic_different'),\n" +
                " (191,'path_wheelchair','ic_wheelchair_motorized'),\n" +
                " (192,'path_keyboard','ic_keyboard'),\n" +
                " (10000,'path_empty_string',NULL);\n",
        )
        db.execSQL(
            "INSERT INTO \"PathCollection\" VALUES (1,'col_title_smartchat',NULL,'ic_chat',NULL,2,0),\n" +
                " (2,'col_title_madchat',NULL,'ic_mad',NULL,3,0),\n" +
                " (3,'col_title_foodfinder',NULL,'ic_food_yellow_orange',NULL,50,0),\n" +
                " (4,'col_title_essentials',NULL,'ic_crescent_moon_right',NULL,1,0),\n" +
                " (6,'col_title_breakfast',NULL,'ic_egg_cooked',NULL,4,0),\n" +
                " (7,'col_title_lunch',NULL,'ic_sandwich',NULL,6,0),\n" +
                " (9,'col_title_dinner',NULL,'ic_stew',NULL,7,0),\n" +
                " (10,'col_title_dessert',NULL,'ic_cookie',NULL,8,0),\n" +
                " (11,'col_title_places_home',NULL,'ic_home',NULL,11,0),\n" +
                " (12,'col_title_places_school',NULL,'ic_school',NULL,12,0),\n" +
                " (13,'col_title_family',NULL,'ic_family',NULL,13,0),\n" +
                " (14,'col_title_fruits',NULL,'ic_apple',NULL,9,0),\n" +
                " (15,'col_title_vegetables',NULL,'ic_carrot',NULL,10,0),\n" +
                " (9999,'col_title_emptychat',NULL,'ic_hand_raise',NULL,5,1),\n" +
                " (10000,NULL,NULL,NULL,NULL,10000,1);\n",
        )
        db.execSQL(
            "INSERT INTO \"PathToCollections\" VALUES (1,1,1,NULL,1),\n" +
                " (2,1,2,NULL,2),\n" +
                " (3,1,3,NULL,3),\n" +
                " (4,1,4,2,NULL),\n" +
                " (5,1,5,2,NULL),\n" +
                " (6,1,6,2,NULL),\n" +
                " (7,1,7,4,NULL),\n" +
                " (8,1,8,4,NULL),\n" +
                " (9,1,9,2,NULL),\n" +
                " (10,1,10,3,0),\n" +
                " (11,1,11,2,NULL),\n" +
                " (12,1,12,2,NULL),\n" +
                " (13,1,13,3,NULL),\n" +
                " (14,1,14,1,1),\n" +
                " (15,1,15,3,1),\n" +
                " (16,1,16,3,2),\n" +
                " (17,1,17,1,2),\n" +
                " (18,1,18,1,0),\n" +
                " (19,1,19,16,NULL),\n" +
                " (20,1,20,29,NULL),\n" +
                " (21,1,22,3,NULL),\n" +
                " (22,1,23,2,NULL),\n" +
                " (23,1,21,16,NULL),\n" +
                " (24,1,24,16,NULL),\n" +
                " (25,1,25,16,NULL),\n" +
                " (26,1,26,25,NULL),\n" +
                " (27,1,27,25,NULL),\n" +
                " (28,1,28,25,NULL),\n" +
                " (29,1,29,16,NULL),\n" +
                " (30,1,30,29,NULL),\n" +
                " (31,1,31,7,NULL),\n" +
                " (32,1,32,1,3),\n" +
                " (33,1,32,2,NULL),\n" +
                " (34,1,32,52,NULL),\n" +
                " (35,1,64,58,NULL),\n" +
                " (36,1,33,8,NULL),\n" +
                " (37,1,34,8,NULL),\n" +
                " (38,1,35,8,NULL),\n" +
                " (39,1,36,1,20),\n" +
                " (40,1,37,3,NULL),\n" +
                " (41,1,38,3,NULL),\n" +
                " (42,1,39,3,NULL),\n" +
                " (43,1,40,8,NULL),\n" +
                " (44,1,41,3,NULL),\n" +
                " (45,1,42,8,0),\n" +
                " (46,1,43,8,NULL),\n" +
                " (47,1,44,8,NULL),\n" +
                " (48,1,45,8,NULL),\n" +
                " (49,1,46,8,NULL),\n" +
                " (50,1,47,8,NULL),\n" +
                " (51,1,48,2,NULL),\n" +
                " (52,1,49,2,NULL),\n" +
                " (53,1,50,NULL,4),\n" +
                " (54,1,51,50,0),\n" +
                " (55,1,52,50,1),\n" +
                " (56,1,53,52,NULL),\n" +
                " (57,1,54,52,NULL),\n" +
                " (58,1,55,8,NULL),\n" +
                " (59,1,56,52,NULL),\n" +
                " (60,1,57,52,NULL),\n" +
                " (61,1,58,50,2),\n" +
                " (62,1,59,58,NULL),\n" +
                " (63,1,60,58,NULL),\n" +
                " (64,1,61,58,NULL),\n" +
                " (65,1,62,58,NULL),\n" +
                " (66,1,63,58,NULL),\n" +
                " (69,1,145,8,NULL),\n" +
                " (70,1,146,8,NULL),\n" +
                " (71,1,147,8,NULL),\n" +
                " (72,1,159,8,NULL),\n" +
                " (73,1,157,8,NULL),\n" +
                " (74,1,155,8,NULL),\n" +
                " (75,1,148,8,NULL),\n" +
                " (76,1,165,NULL,0),\n" +
                " (77,1,190,1,4),\n" +
                " (78,1,192,2,NULL),\n" +
                " (79,1,191,2,NULL),\n" +
                " (500,2,166,NULL,0),\n" +
                " (501,2,16,NULL,1),\n" +
                " (502,2,19,16,NULL),\n" +
                " (503,2,21,16,NULL),\n" +
                " (504,2,24,16,NULL),\n" +
                " (505,2,25,16,NULL),\n" +
                " (506,2,29,16,0),\n" +
                " (507,2,22,NULL,5),\n" +
                " (508,2,15,NULL,2),\n" +
                " (509,2,10,NULL,3),\n" +
                " (510,2,37,NULL,NULL),\n" +
                " (511,2,38,168,NULL),\n" +
                " (512,2,39,168,NULL),\n" +
                " (513,2,41,168,NULL),\n" +
                " (514,2,167,NULL,4),\n" +
                " (515,2,168,NULL,NULL),\n" +
                " (516,2,169,10,0),\n" +
                " (517,2,170,10,NULL),\n" +
                " (518,2,172,10,NULL),\n" +
                " (519,2,32,10,NULL),\n" +
                " (700,3,67,NULL,NULL),\n" +
                " (701,3,68,NULL,NULL),\n" +
                " (702,3,69,NULL,NULL),\n" +
                " (703,3,70,NULL,NULL),\n" +
                " (704,3,71,NULL,NULL),\n" +
                " (705,3,79,67,NULL),\n" +
                " (706,3,73,67,NULL),\n" +
                " (707,3,80,68,NULL),\n" +
                " (708,3,74,68,NULL),\n" +
                " (709,3,81,69,NULL),\n" +
                " (710,3,75,69,NULL),\n" +
                " (711,3,82,70,NULL),\n" +
                " (712,3,76,70,NULL),\n" +
                " (713,3,44,73,NULL),\n" +
                " (716,3,87,73,NULL),\n" +
                " (719,3,47,79,NULL),\n" +
                " (720,3,43,79,NULL),\n" +
                " (721,3,90,79,NULL),\n" +
                " (723,3,92,79,NULL),\n" +
                " (725,3,94,79,NULL),\n" +
                " (726,3,95,74,NULL),\n" +
                " (727,3,96,74,NULL),\n" +
                " (728,3,97,74,NULL),\n" +
                " (729,3,85,74,NULL),\n" +
                " (731,3,99,74,NULL),\n" +
                " (733,3,101,74,NULL),\n" +
                " (736,3,104,80,NULL),\n" +
                " (737,3,105,80,NULL),\n" +
                " (739,3,107,80,NULL),\n" +
                " (742,3,110,80,NULL),\n" +
                " (744,3,112,80,NULL),\n" +
                " (745,3,136,80,NULL),\n" +
                " (749,3,116,75,NULL),\n" +
                " (750,3,117,75,NULL),\n" +
                " (755,3,122,75,NULL),\n" +
                " (758,3,125,81,NULL),\n" +
                " (760,3,127,81,NULL),\n" +
                " (761,3,128,81,NULL),\n" +
                " (762,3,129,81,NULL),\n" +
                " (763,3,137,81,NULL),\n" +
                " (764,3,46,76,NULL),\n" +
                " (767,3,132,82,NULL),\n" +
                " (769,3,45,82,NULL),\n" +
                " (772,3,96,71,NULL),\n" +
                " (773,3,138,71,NULL),\n" +
                " (774,3,139,71,NULL),\n" +
                " (775,3,144,71,NULL),\n" +
                " (776,3,161,71,NULL),\n" +
                " (777,3,140,NULL,NULL),\n" +
                " (778,3,141,NULL,NULL),\n" +
                " (779,3,35,140,NULL),\n" +
                " (780,3,162,140,NULL),\n" +
                " (781,3,33,140,NULL),\n" +
                " (782,3,42,140,NULL),\n" +
                " (783,3,40,140,NULL),\n" +
                " (784,3,55,140,NULL),\n" +
                " (785,3,145,140,NULL),\n" +
                " (786,3,146,140,NULL),\n" +
                " (787,3,147,140,NULL),\n" +
                " (788,3,159,140,NULL),\n" +
                " (789,3,157,140,NULL),\n" +
                " (790,3,155,140,NULL),\n" +
                " (791,3,142,141,NULL),\n" +
                " (792,3,143,141,NULL),\n" +
                " (793,3,31,141,NULL),\n" +
                " (794,3,148,141,NULL),\n" +
                " (795,3,149,NULL,NULL),\n" +
                " (796,3,150,149,NULL),\n" +
                " (797,3,142,149,NULL),\n" +
                " (798,3,143,149,NULL),\n" +
                " (800,3,151,149,NULL),\n" +
                " (801,3,152,149,NULL),\n" +
                " (802,3,153,149,NULL),\n" +
                " (803,3,154,149,NULL),\n" +
                " (804,3,156,149,NULL),\n" +
                " (805,3,163,68,NULL),\n" +
                " (806,3,148,68,NULL),\n" +
                " (807,3,173,81,NULL),\n" +
                " (900,4,8,NULL,0),\n" +
                " (901,4,7,NULL,1),\n" +
                " (902,4,32,NULL,2),\n" +
                " (903,4,14,NULL,3),\n" +
                " (904,4,18,NULL,4),\n" +
                " (920,6,112,NULL,NULL),\n" +
                " (921,6,35,NULL,NULL),\n" +
                " (922,6,162,NULL,NULL),\n" +
                " (923,6,176,NULL,NULL),\n" +
                " (924,6,147,NULL,NULL),\n" +
                " (925,6,150,NULL,NULL),\n" +
                " (926,6,175,NULL,NULL),\n" +
                " (950,7,34,NULL,NULL),\n" +
                " (951,7,42,NULL,NULL),\n" +
                " (952,7,40,NULL,NULL),\n" +
                " (953,7,78,NULL,NULL),\n" +
                " (954,7,55,NULL,NULL),\n" +
                " (980,9,96,NULL,NULL),\n" +
                " (981,9,40,NULL,NULL),\n" +
                " (982,9,33,NULL,NULL),\n" +
                " (983,9,42,NULL,NULL),\n" +
                " (984,9,78,NULL,NULL),\n" +
                " (985,9,55,NULL,NULL),\n" +
                " (986,9,147,NULL,NULL),\n" +
                " (987,9,159,NULL,NULL),\n" +
                " (988,9,145,NULL,NULL),\n" +
                " (1000,10,174,NULL,0),\n" +
                " (1001,10,154,NULL,1),\n" +
                " (1002,10,142,NULL,NULL),\n" +
                " (1003,10,143,NULL,NULL),\n" +
                " (1004,10,187,NULL,2),\n" +
                " (1005,10,188,NULL,3),\n" +
                " (1020,11,32,NULL,NULL),\n" +
                " (1021,11,57,NULL,NULL),\n" +
                " (1022,11,53,NULL,NULL),\n" +
                " (1023,11,54,NULL,NULL),\n" +
                " (1024,11,48,NULL,NULL),\n" +
                " (1025,11,186,NULL,NULL),\n" +
                " (1040,12,64,NULL,0),\n" +
                " (1041,12,59,NULL,1),\n" +
                " (1042,12,60,NULL,2),\n" +
                " (1043,12,61,NULL,NULL),\n" +
                " (1044,12,62,NULL,NULL),\n" +
                " (1045,12,63,NULL,NULL),\n" +
                " (1046,12,189,NULL,NULL),\n" +
                " (1070,13,179,NULL,NULL),\n" +
                " (1071,13,180,NULL,NULL),\n" +
                " (1072,13,181,NULL,NULL),\n" +
                " (1073,13,182,NULL,NULL),\n" +
                " (1074,13,183,NULL,NULL),\n" +
                " (1075,13,184,NULL,NULL),\n" +
                " (1076,13,185,NULL,NULL),\n" +
                " (1090,14,94,NULL,NULL),\n" +
                " (1091,14,112,NULL,5),\n" +
                " (1092,14,107,NULL,5),\n" +
                " (1093,14,45,NULL,5),\n" +
                " (1094,14,47,NULL,NULL),\n" +
                " (1095,14,90,NULL,NULL),\n" +
                " (1096,14,104,NULL,NULL),\n" +
                " (1097,14,105,NULL,NULL),\n" +
                " (1098,14,137,NULL,NULL),\n" +
                " (1099,14,128,NULL,NULL),\n" +
                " (1100,14,173,NULL,NULL),\n" +
                " (1101,14,44,NULL,4),\n" +
                " (1120,15,101,NULL,NULL),\n" +
                " (1121,15,96,NULL,NULL),\n" +
                " (1122,15,97,NULL,NULL),\n" +
                " (1123,15,87,NULL,NULL),\n" +
                " (1124,15,116,NULL,NULL),\n" +
                " (1125,15,117,NULL,NULL),\n" +
                " (9999,9999,164,NULL,NULL),\n" +
                " (10000,10000,10000,NULL,0);\n",
        )
        db.execSQL(
            "INSERT INTO \"PathUser\" VALUES (1,'default',NULL),\n" +
                " (2,'user1',NULL),\n" +
                " (3,'user2',NULL);\n",
        )
        db.execSQL(
            "INSERT INTO \"UserPathCollectionPrefs\" VALUES (1,1,1,1,NULL,0),\n" +
                " (2,1,2,1,NULL,0),\n" +
                " (3,1,3,1,NULL,0),\n" +
                " (4,1,4,1,NULL,0),\n" +
                " (5,1,6,1,NULL,0),\n" +
                " (6,1,7,1,NULL,0),\n" +
                " (7,1,9,1,NULL,0),\n" +
                " (8,1,10,1,NULL,0),\n" +
                " (9,1,11,1,NULL,0),\n" +
                " (10,1,12,1,NULL,0),\n" +
                " (11,1,13,1,NULL,0),\n" +
                " (12,1,14,1,NULL,0),\n" +
                " (13,1,15,1,NULL,0),\n" +
                " (14,2,1,0,NULL,0),\n" +
                " (15,2,2,0,NULL,0),\n" +
                " (16,2,3,0,NULL,0),\n" +
                " (17,2,4,1,NULL,0),\n" +
                " (18,2,6,0,NULL,0),\n" +
                " (19,2,7,0,NULL,0),\n" +
                " (20,2,9,0,NULL,0),\n" +
                " (21,2,10,0,NULL,0),\n" +
                " (22,2,11,0,NULL,0),\n" +
                " (23,2,12,0,NULL,0),\n" +
                " (24,2,13,0,NULL,0),\n" +
                " (25,2,14,0,NULL,0),\n" +
                " (26,2,15,0,NULL,0),\n" +
                " (65,3,1,1,NULL,0),\n" +
                " (67,3,2,1,NULL,0),\n" +
                " (69,3,3,1,NULL,0),\n" +
                " (71,3,4,1,NULL,0),\n" +
                " (73,3,6,1,NULL,0),\n" +
                " (75,3,7,1,NULL,0),\n" +
                " (77,3,9,1,NULL,0),\n" +
                " (79,3,10,1,NULL,0),\n" +
                " (81,3,11,1,NULL,0),\n" +
                " (83,3,12,1,NULL,0),\n" +
                " (85,3,13,1,NULL,0),\n" +
                " (87,3,14,1,NULL,0),\n" +
                " (89,3,15,1,NULL,0);",
        )
    }

    @Test
    fun migrateAll() {
        for (version in 10..com.gy.chatpaths.aac.app.model.source.local.AppDatabase.DATABASE_VERSION) {
            // Create earliest version of the database.
            val db = helper.createDatabase(TEST_DB, version)
            when (version) {
                // Oldest version with non-destructive upgrades
//                10 -> {
//                    db.execSQL("INSERT INTO Path (`pathId`, `defaultTitleStringResource`, `imageResource`) VALUES (1,null,null)")
//                    db.execSQL("INSERT INTO PathCollection (`collectionId`,`name`,`defaultOrder`, `hidden`) VALUES (1, 'test', 0, 0 )")
//                    db.execSQL("INSERT INTO PathToCollections (`id`,`pathCollectionId`,`pathId`, `parentId`) VALUES (1, 1,1,null )")
//                    db.execSQL("INSERT INTO PathUserML (`userId`, `pathCollectionId`, `pathId`) VALUES (1,1,1)")
//                    db.execSQL("INSERT INTO PathUser (`userId`, `name`) VALUES (1,'test'),(2,'test2')")
//                }
                11 -> {
                    populate11(db)
                }
            }
            db.close()

            // Open latest version of the database. Room will validate the schema
            // once all migrations execute.
            Room.databaseBuilder(
                InstrumentationRegistry
                    .getInstrumentation()
                    .targetContext,
                com.gy.chatpaths.aac.app.model.source.local.AppDatabase::class.java,
                TEST_DB,
            ).let {
                return@let com.gy.chatpaths.aac.app.model.source.local.AppDatabase.buildDatabase(it)
            }.apply {
                val data = com.gy.chatpaths.aac.app.model.source.local.LocalCPDataSource(this)
                when (version) {
//                    10 -> {
//                        runBlocking {
//                            assertThat(data.getPathCount()).isEqualTo(2)
//                            assertThat(data.getCollectionCount()).isEqualTo(2)
//                            assertThat(data.getPathCollections(1,true).size).isEqualTo(1)
//                            assertThat(data.getPathCollections(2,true).size).isEqualTo(1)
//                            assertThat(data.getPathCollections(1,true)).doesNotContain(data.getPathCollections(2,true))
//
//                            val collection1 = data.getPathCollection(1)
//                            assertThat(collection1).isNotNull()
//                            collection1?.apply {
//                                Log.d("test", "Collection1: $collection1")
//                                assertThat(userId).isEqualTo(1)
//                            }
//                            val collection2 = data.getPathCollection(2)
//                            assertThat(collection2).isNotNull()
//                            collection2?.apply {
//                                Log.d("test", "Collection2: $collection2")
//                                assertThat(userId).isEqualTo(2)
//                            }
//
//                            assertThat(data.getPathFromCollection(1,1,1)).isNotNull()
//
//                            assertThat(data.getPathCollection(2)).isNotNull()
//                            assertThat(data.getPathById(1)).isNotNull()
//                            data.getPathById(1)?.apply {
//                                assertThat(collectionId).isEqualTo(1)
//                            }
//                            assertThat(data.getPathById(2)).isNotNull()
//                            data.getPathById(2)?.apply {
//                                assertThat(collectionId).isEqualTo(2)
//                            }
//                        }
//                    }
                    11 -> {
                        /**
                         * @note This test should not validate translations, we have removed the code to do that post migration, but nobody will have this old database.
                         */
                        runBlocking {
                            assertThat(data.getCollectionCount()).isEqualTo(39)
                            assertThat(data.getPathCount()).isEqualTo(735)
                            assertThat(data.getPathCollections(1, true).size).isEqualTo(13)
                            assertThat(data.getPathCollections(2, true).size).isEqualTo(1)
                            assertThat(data.getPathCollections(3, true).size).isEqualTo(13)
                            assertThat(data.getPathCollections(1, true)).doesNotContain(data.getPathCollections(3, true))
                            val children = data.getChildrenOfParent(1, 1, null, true)
                            assertThat(children).isNotNull()
                            children?.apply {
                                assertThat(size).isEqualTo(5)
                                get(1).apply {
                                    assertThat(name).contains("chat_words")
                                    val c1 = data.getChildrenOfParent(1, collectionId, pathId, true)
                                    assertThat(c1).isNotNull()
                                    c1?.apply {
                                        // should be like 5 paths or so
                                        assertThat(size).isGreaterThan(2)
                                    }
                                }
                            }
                        }
                    }
                }
            }.openHelper.writableDatabase
                .apply {
//                    close()
                }
        }
    }
//
//    @Test
//    fun migrateFrom6() {
//        val version = 6
//        val db = helper.createDatabase(TEST_DB, version)
//        db.execSQL("INSERT INTO PathCollection (`collectionId`,`name`) VALUES (1, 'test' )")
//        db.close()
//
//
//        helper.runMigrationsAndValidate(TEST_DB, version, true,
//            AppDatabase.MIGRATION_11_12)
//
//    }
}
