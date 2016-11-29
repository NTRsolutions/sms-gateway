DROP DATABASE IF EXISTS sg;
CREATE DATABASE IF NOT EXISTS sg;
USE sg;

DROP TABLE IF EXISTS servers;
CREATE TABLE IF NOT EXISTS servers(
  id INT NOT NULL AUTO_INCREMENT,
  name VARCHAR(10) NOT NULL,
  imei VARCHAR (20) NOT NULL,
  device_name VARCHAR (50) NOT NULL,
  device_hash TEXT NOT NULL,
  server_key VARCHAR (10) NOT NULL,
  fcm_id TEXT NOT NULL,
  country_id INT(4) NOT NULL DEFAULT 91,
  is_active TINYINT(4) NOT NULL DEFAULT 1,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

  PRIMARY KEY(id)
  );

CREATE TABLE users(
   id INT(11) NOT NULL AUTO_INCREMENT,
   email VARCHAR(100) NOT NULL,
   api_key VARCHAR (10) NOT NULL,
   is_active TINYINT(4)  NOT NULL  DEFAULT 1 ,
   created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

   PRIMARY KEY (id)
   );

CREATE TABLE requests(
  id INT NOT NULL AUTO_INCREMENT,
  message TEXT NOT NULL,
  server_id INT NOT NULL,
  user_id INT NOT NULL,
  is_active TINYINT(4) NOT NULL DEFAULT 1,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

  PRIMARY KEY(id),
  FOREIGN KEY (server_id) REFERENCES servers(id) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE
  );

CREATE TABLE recipients(
  id INT NOT NULL AUTO_INCREMENT,
  request_id INT NOT NULL,
  recipient VARCHAR (15) NOT NULL,
  is_active TINYINT(4) NOT NULL DEFAULT 1,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY(id),
  FOREIGN KEY (request_id) REFERENCES requests(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE request_statuses(
  id INT NOT NULL AUTO_INCREMENT,
  request_id INT NOT NULL,
  status ENUM('SENT','DELIVERED','FAILED') NOT NULL DEFAULT 'SENT',
  occured_at INT(11) NOT NULL ,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  is_active TINYINT(4) NOT NULL DEFAULT 1,
  PRIMARY KEY(id),
  FOREIGN KEY (request_id) REFERENCES requests(id) ON DELETE CASCADE ON UPDATE CASCADE
  );

CREATE TABLE IF NOT EXISTS preference (
  id int(11) NOT NULL AUTO_INCREMENT,
  _key varchar(100) NOT NULL,
  _value text NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY _key (_key)
);

INSERT INTO preference (id, _key, _value) VALUES
(1, 'gmail_username', 'gpixofficial@gmail.com'),
(2, 'gmail_password', 'thepassword'),
(3, 'admin_email', 'theapache64@gmail.com');