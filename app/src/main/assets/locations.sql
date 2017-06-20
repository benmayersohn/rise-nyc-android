BEGIN TRANSACTION;
CREATE TABLE "locations" (
	`_id`	INTEGER,
	`borough`	TEXT,
	`day`	TEXT,
	`place`	TEXT,
	`img`	TEXT,
	`desc`	TEXT,
	`title`	TEXT,
	PRIMARY KEY(`_id`)
);
INSERT INTO `locations` (borough,day,place,img,desc,title,_id) VALUES ('Manhattan','M','Washington Square Park','hiit20161107','hiit.txt','HIIT',1);
INSERT INTO `locations` (borough,day,place,img,desc,title,_id) VALUES ('Brooklyn','T','Grand Army Plaza','badass20160614','badass.txt','Brooklyn Body Circuit',2);
INSERT INTO `locations` (borough,day,place,img,desc,title,_id) VALUES ('Queens','T','MacDonald Park','warriors20160405','warriors.txt','Queens Warriors',3);
INSERT INTO `locations` (borough,day,place,img,desc,title,_id) VALUES ('Manhattan','W','Bryant Park','bootcamp20150902','bootcamp.txt','Core Body Boot Camp',4);
INSERT INTO `locations` (borough,day,place,img,desc,title,_id) VALUES ('Queens','R','MacDonald Park','kings20160914','kings.txt','Kings of Queens',5);
INSERT INTO `locations` (borough,day,place,img,desc,title,_id) VALUES ('Manhattan','R','Flatiron Plaza','flow20150710','flow.txt','Form and Flow',6);
INSERT INTO `locations` (borough,day,place,img,desc,title,_id) VALUES ('Manhattan','F','Williamsburg Bridge','hills20160226','hills.txt','Hills',7);
COMMIT;
