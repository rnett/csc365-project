drop table if exists planets;
drop table if exists stars;

create table stars (
	starName varchar(30) primary key,
    hipName varchar(30),
    class varchar(20),
    type varchar(20),
    color varchar(20),
    mass real,
    radius real,
    temp real,
    goldilocksInner real,
    goldilocksOuter real,
    planets int,
    distance real
);

create table planets (
	starName varchar(30) not null,
    letter varchar(5) not null,
    orbitalRadius real,
    orbitalPeriod real,
    orbitalEccentricity real,
    orbitalInclination real,
    mass real,
    radius real,
    density real,
    goldilocks bit,
    primary key (starName, letter),
    foreign key (starName) references stars(starName)
);