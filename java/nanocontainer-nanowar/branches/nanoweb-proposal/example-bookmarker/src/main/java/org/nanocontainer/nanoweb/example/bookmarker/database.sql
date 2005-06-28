create table tb_bookmark (
	id integer generated by default as identity primary key,
	url varchar(500),
	name varchar(500),
	description longvarchar,
	primary key (id)
);

-- initial data

insert into tb_bookmark (id, url, name, description)
	values (1, 'http://www.foo.com', 'The best ever site', 'This site is nice to look for something that you realy dont know what is, and you can always find at google.');

insert into tb_bookmark (id, url, name, description)
	values (2, 'http://www.foo.com', 'The best ever site', 'This site is nice to look for something that you realy dont know what is, and you can always find at google.');

insert into tb_bookmark (id, url, name, description)
	values (3, 'http://www.foo.com', 'The best ever site', 'This site is nice to look for something that you realy dont know what is, and you can always find at google.');

insert into tb_bookmark (id, url, name, description)
	values (4, 'http://www.foo.com', 'The best ever site', 'This site is nice to look for something that you realy dont know what is, and you can always find at google.');

insert into tb_bookmark (id, url, name, description)
	values (5, 'http://www.foo.com', 'The best ever site', 'This site is nice to look for something that you realy dont know what is, and you can always find at google.');

