DROP DATABASE IF EXISTS servicea;
CREATE DATABASE servicea CHARACTER SET utf8;
USE servicea;
GRANT USAGE ON *.* TO 'saadmin'@'%';
DROP USER saadmin;
CREATE USER saadmin IDENTIFIED BY 'qwerty';
GRANT ALL PRIVILEGES ON *.* TO 'saadmin'@'%' IDENTIFIED BY 'qwerty' WITH GRANT OPTION;

CREATE TABLE emails (
  id INT NOT NULL auto_increment,
  email VARCHAR(255) NOT NULL default '',
  name VARCHAR(255) NOT NULL default '',
  surname VARCHAR(255) NOT NULL default '',
  PRIMARY KEY  (id),
  UNIQUE KEY (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE logins (
  id INT NOT NULL auto_increment,
  login VARCHAR(255) NOT NULL default '',
  passwd VARCHAR(255) NOT NULL default '',
  `group` VARCHAR(255) NOT NULL default '',
  emails_ref INT NOT NULL,
  PRIMARY KEY  (id),
  UNIQUE KEY (login),
  FOREIGN KEY (emails_ref) REFERENCES emails (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE news (
  id INT  NOT NULL auto_increment,
  `date` DATETIME NOT NULL,
  author INT NOT NULL,
  title VARCHAR(255) NOT NULL default '',
  text TEXT NOT NULL default '',
  PRIMARY KEY  (id),
  FOREIGN KEY (author) REFERENCES logins (id),
  FULLTEXT (title, text)
) DEFAULT CHARSET=utf8;

CREATE TABLE comments (
  id INT NOT NULL auto_increment,
  `date` DATETIME NOT NULL,
  author INT NOT NULL,
  title VARCHAR(255) NOT NULL default '',
  text TEXT NOT NULL default '',
  news_ref INT NOT NULL,
  PRIMARY KEY  (id),
  FOREIGN KEY (author) REFERENCES logins (id),
  FOREIGN KEY (news_ref) REFERENCES news (id)
) DEFAULT CHARSET=utf8;

CREATE TABLE sessions (
	id INT NOT NULL auto_increment,
	session_id VARCHAR(255) NOT NULL,
	user_id INT NOT NULL,
	expire_time DATETIME NOT NULL,
	PRIMARY KEY  (id),
	UNIQUE KEY (session_id),
	FOREIGN KEY (user_id) REFERENCES logins (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE stat_browser ( 
	id INT NOT NULL AUTO_INCREMENT, 
	browser VARCHAR(255) NOT NULL, 
	counter INT NOT NULL, 
	PRIMARY KEY (id), 
	UNIQUE KEY (browser) 
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

