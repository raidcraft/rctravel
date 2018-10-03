-- apply changes
create table rc_travel_stations (
  id                            integer auto_increment not null,
  name                          varchar(255),
  group_name                    varchar(255),
  price                         integer not null,
  x                             integer not null,
  y                             integer not null,
  z                             integer not null,
  pitch                         integer not null,
  yaw                           integer not null,
  world                         varchar(255),
  x_min                         integer not null,
  y_min                         integer not null,
  z_min                         integer not null,
  x_max                         integer not null,
  y_max                         integer not null,
  z_max                         integer not null,
  constraint pk_rc_travel_stations primary key (id)
);

