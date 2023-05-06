CREATE TABLE "songs"(
	"id" 				INTEGER PRIMARY KEY,
	"uri"				TEXT UNIQUE NOT NULL,
	"name"				TEXT ,
	"author" 			TEXT ,
	"description" 		Text,
	"is_autoplay_marker" 	Integer,
	UNIQUE("name", "author")
);

CREATE TABLE "directories"(
	"id" 			INTEGER PRIMARY KEY,
	"uri"			TEXT UNIQUE NOT NULL,
	"name_dir"		TEXT UNIQUE,
	"is_add_in_list_marker"	INTEGER,
	"is_default_dir" INTEGER
);

INSERT INTO "directories" ("uri", "name_dir",
 "is_add_in_list_marker", "is_default_dir")
    VALUES
	    ("/storage/emulated/0/Download", "Download", 1, 1),
	    ("/storage/emulated/0/Music", "Music", 1, 1),
	    ("/storage/emulated/0/Ringtone", "Ringtone", 0, 1);

INSERT INTO "songs" ("uri", "name",
     "author", "description", "is_autoplay_marker")
     VALUES
    	("/storage/emulated/0/Download/Luke-Bergs-Bliss.mp3", "First_name_music", NULL,
    	"This is first music what I add in DATABASE", 1),
    	("/storage/emulated/0/Download/Smile.mp3", NULL, "first author",
    	NULL, 1);