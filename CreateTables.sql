drop table if exists planets;
drop table if exists stars;

create table stars (
	starName varchar(30) primary key,
    hipName varchar(30),
    class varchar(20),
    type enum('unknown', 'supergiant', 'bright giant', 'giant', 'subgiant', 'main sequence'),
    color enum('unknown', 'blue', 'blue white', 'white', 'yellow white', 'yellow', 'light orange', 'orange red'),
    starMass real,
    starRadius real,
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
    planetMass real,
    planetRadius real,
    density real,
    goldilocks bit,
    primary key (starName, letter),
    foreign key (starName) references stars(starName)
);
