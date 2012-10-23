knowledge-base
==============

Getting started
===============

create user and database in PostgreSQL

	CREATE ROLE dbusername LOGIN NOSUPERUSER INHERIT NOCREATEDB NOCREATEROLE NOREPLICATION;
	CREATE DATABASE dbusername WITH OWNER = kb ENCODING = 'UTF8';
    
configure database connection settings in src/main/java/hibernate.cfg.xml

    <property name="hibernate.connection.url">jdbc:postgresql://HOSTNAME/DATABASE</property>
    <property name="hibernate.connection.username">DBUSERNAME</property>
    <property name="hibernate.connection.password">DBPASSWORD</property>

  
start web container

    mvn jetty:run
    
browse http://127.0.0.1:8080

load simple data from data.xml
