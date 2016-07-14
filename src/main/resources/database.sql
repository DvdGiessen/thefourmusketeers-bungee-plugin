CREATE TABLE "players" (
	"uuid" TEXT NOT NULL,
	"name" TEXT NOT NULL,
	PRIMARY KEY ( "uuid" ),
	UNIQUE ( "name" )
);

CREATE TABLE "servers" (
	"name" TEXT NOT NULL,
	PRIMARY KEY ( "name" )
);

CREATE TABLE "serverStatistics" (
	"server" TEXT NOT NULL,
	"time" INTEGER NOT NULL,
	"playerCount" INTEGER NOT NULL,
	PRIMARY KEY ( "server", "time" ),
	FOREIGN KEY ( "server" ) REFERENCES "servers" ( "name" ) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE "playerStatistics" (
	"player" TEXT NOT NULL,
	"connectTime" INTEGER NOT NULL,
	"disconnectTime" INTEGER DEFAULT NULL,
	"server" TEXT NOT NULL,
	PRIMARY KEY ( "player", "connectTime", "disconnectTime" ),
	FOREIGN KEY ( "player" ) REFERENCES "players" ( "uuid" ) ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN KEY ( "server" ) REFERENCES "servers" ( "name" ) ON DELETE CASCADE ON UPDATE CASCADE
);
