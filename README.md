WebProgPraktikum
================

The Repository for the WebProgP Project "WebQuiz" was hosted by: Github

Used Technologies for the realization of this project:
======================================================

Font-Awesome
http://fortawesome.github.com/Font-Awesome/#icons-web-app
---------------------------------------------------------
Repository Technology: Git
---------------------------------------------------------
CSS Reset
http://meyerweb.com/eric/tools/css/reset/
---------------------------------------------------------
jQuery
http://jquery.com/
---------------------------------------------------------


==============================================================================
Netzwerk-Protokoll f�r das Multiplayer-Quiz
Version 5.1, Sommersemester 2013
==============================================================================

Achtung: Alle Zahlen werden in der Network-Byte-Order (Big-Endian) versendet!!!

Das Netzwerkprotokoll f�r das Multiplayer-Quiz setzt direkt auf TCP auf.
Jede Nachricht beginnt mit einem 3 Byte langem Header. Dieser ist f�r
Nachrichten vom Server zum Client und f�r Nachrichten vom Client zum Server
gleich aufgebaut:

0             7              15              23
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
| Type        |  Length                       |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

Type:                   uint8_t, gibt die Art der Nachricht an
Length:                 uint16_t, L�nge der nachfolgenden Zusatzdaten in Bytes



===================================
�bersicht �ber die Nachrichtentypen
===================================

+------+------------------+----------------------------------------+---------+
| Type | Name             | Beschreibung                           | Richtg. |
+------+------------------+----------------------------------------+---------+
|    1 | LoginRequest     | Anmeldung eines Clients am Server      | C ==> S |
+------+------------------+----------------------------------------+---------+
|    2 | LoginResponseOK  | Anmeldung am Server erfolgreich        | C <== S |
+------+------------------+----------------------------------------+---------+
|    3 | CatalogRequest   | Anforderung der Liste der Fragakata-   | C ==> S |
|      |                  | loge durch den Client                  |         |
+------+------------------+----------------------------------------+---------+
|    4 | CatalogResponse  | Name eines Fragekatalogs (mehrere      | C <== S |
|      |                  | Nachrichten dieses Typs ergeben die    |         |
|      |                  | vollst�ndige Liste)                    |         |
+------+------------------+----------------------------------------+---------+
|    5 | CatalogChange    | Spielleiter hat Katalogauswahl ge-     | C <=> S |
|      |                  | �ndert, wird an alle Clients weiter-   |         |
|      |                  | geleitet                               |         |
+------+------------------+----------------------------------------+---------+
|    6 | PlayerList       | Liste der Spielteilnehmer, wird ver-   | C <== S |
|      |                  | sendet bei: An-/Abmeldung, Spielstart  |         |
|      |                  | und �nderung des Punktestandes         |         |
+------+------------------+----------------------------------------+---------+
|    7 | StartGame        | Spielleiter m�chte Spiel starten, wird | C <=> S |
|      |                  | vom Server ausgewertet und an Clients  |         |
|      |                  | weitergeleitet                         |         |
+------+------------------+----------------------------------------+---------+
|    8 | QuestionRequest  | Anforderung einer Quizfrage durch      | C ==> S |
|      |                  | einen Client                           |         |
+------+------------------+----------------------------------------+---------+
|    9 | Question         | Reaktion auf QuestionRequest:          | C <== S |
|      |                  | Transport einer Quiz-Frage zum Client  |         |
+------+------------------+----------------------------------------+---------+
|   10 | QuestionAnswered | Quiz-Frage wurde beantwortet           | C ==> S |
+------+------------------+----------------------------------------+---------+
|   11 | QuestionResult   | Auswertung einer Antwort auf eine      | C <== S |
|      |                  | Quiz-Frage                             |         |
+------+------------------+----------------------------------------+---------+
|   12 | GameOver         | Alle Clients sind fertig, Mitteilung   | C <== S |
|      |                  | �ber Endplatzierung                    |         |
+------+------------------+----------------------------------------+---------+
|  255 | ErrorWarning     | Fehlermeldung an den Client            | C <== S |
+------+------------------+----------------------------------------+---------+



=======
Legende
=======

0             7              15              23              31
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
| Element ................................................... |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

Element hat variable L�nge (auch l�nger als K�stchengr��e m�glich).


0             7              15              23              31
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
| [Element]                                                   |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

Element wird nicht immer mitgeschickt.


0             7              15              23              31
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
| Element                                                     =
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
=                                                             |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

Element wird in der n�chsten Zeile des Diagramms fortgesetzt (in diesem
Beispiel: Element ist an Bitposition 0 bis 63).



================================
Aufbau der einzelnen Nachrichten
================================

LoginRequest
------------

0             7              15              23              31
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
| Type        | Length                        | Name ........ |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

Type:                   1
Length:                 L�nge des Namens (Length <= 31)
Name:                   Login-Name, UTF-8, nicht nullterminiert, maximal
                        31 Bytes


LoginResponseOK
---------------

0             7              15              23              31
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
| Type        | Length                        | Client-ID     |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

Type:                   2
Length:                 1
Client-ID:              uint8_t, ID des Clients (0 := Spielleiter)


CatalogRequest
--------------

0             7              15              23
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
| Type        | Length                        |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

Type:                   3
Length:                 0


CatalogResponse
---------------

0             7              15              23              31
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
| Type        | Length                        | [Filename] .. |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

Type:                   4
Length:                 L�nge des Dateinamens, oder 0 f�r Endemarkierung
Filename:               Dateiname eines Fragekataloges (UTF-8, nicht null-
                        terminiert), oder leer als Kennzeichnung f�r Ende
                        der Auflistung


CatalogChange
-------------

Diese Nachricht wird vom Client des Spielleiters an den Server gesendet,
wenn der Spielleiter einen Fragekatalog in der Liste des Vorbereitungsfensters
anklickt. Der Server leitet diese Nachricht daraufhin an alle Clients
au�er den des Spielleiters (!) weiter.

0             7              15              23              31
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
| Type        | Length                        | Filename .... |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

Type:                   5
Length:                 L�nge des Dateinamens
Filename:               Dateiname des gew�hlten Fragekataloges (UTF-8, nicht
                        nullterminiert)


PlayerList
----------

Die folgende Nachricht wird aus Platzgr�nden verk�rzt dargestellt. Das
Players-Feld ist immer 37*Spieleranzahl Bytes gro�.

0             7              15              23              31
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
| Type        | Length                        | Players ..... =
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
= ........................................................... |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

Type:                   6
Length:                 37*Spieleranzahl (maximal 4*37 = 148)
Players:                Liste aller derzeit angemeldeten Benutzer, siehe unten

Aufbau der Spielerliste "Players":

        32 Bytes        Spielername 1 (UTF-8, nullterminiert)
         4 Bytes        Punktestand Spieler 1, vorzeichenlos
         1 Byte         ID Spieler 1

        32 Bytes        Spielername 2 (UTF-8, nullterminiert)
         4 Bytes        Punktestand Spieler 2, vorzeichenlos
         1 Byte         ID Spieler 2
                    .
                    .
                    .

W�hrend der Spielphase muss der Server diese Liste absteigend nach
den Punktest�nden der Spieler sortieren.


StartGame
---------

0             7              15              23              31
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
| Type        | Length                        | [Filename] .. |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

Type:                   7
Length:                 L�nge des Dateinamens
Filename:               Dateiname des zu spielenden Fragekataloges (UTF-8, nicht
                        nullterminiert), kann beim Versand Server ==> Client
                        (nicht Client ==> Server!) auch leer gelassen werden


QuestionRequest
---------------

0             7              15              23
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
| Type        | Length                        |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

Type:                   8
Length:                 0


Question
--------

Die folgende Nachricht wird aus Platzgr�nden verk�rzt dargestellt. Wenn
Fragedaten versendet werden (also nicht die Nachricht verschickt wird, dass es
keine Fragen mehr gibt), dann ist das Data-Feld genau 769 Bytes gro�.

0             7              15              23              31
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
| Type        | Length                        | [Data] ...... =
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
= ............................................................|
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+


Type:                   9
Length:                 769 oder 0 (falls keine Fragen mehr)
Data:                   Eine Struktur, die wie unten angegeben aufgebaut ist,
                        oder leer falls Ende des aktuellen Kataloges erreicht

Aufbau des Data-Felds:
        256 Bytes       Text der Fragestellung (UTF-8, nullterminiert)
        128 Bytes       Antworttext 1 (UTF-8, nullterminiert)
        128 Bytes       Antworttext 2 (UTF-8, nullterminiert)
        128 Bytes       Antworttext 3 (UTF-8, nullterminiert)
        128 Bytes       Antworttext 4 (UTF-8, nullterminiert)
          1 Byte        Zeitbegrenzung in Sekunden


QuestionAnswered
----------------

0             7              15              23              31
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
| Type        | Length                        | Selection     |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

Type:                   10
Length:                 1
Selection:              uint8_t, Index der vom Benutzer gew�hlten Antwort-
                        m�glichkeit (0 <= Selection <= 3)


QuestionResult
--------------

0             7              15              23              31
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
| Type        | Length                        | TimedOut      |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
| Correct     |
+-+-+-+-+-+-+-+

Type:                   11
Length:                 2
Selection:              uint8_t, wenn Timeout f�r Frage erreicht wurde
                        ungleich 0, sonst 0
Correct:                uint8_t, Index der richtigen Antwort (0 <= Correct <= 3)


GameOver
--------

0             7              15              23              31
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
| Type        | Length                        | Rank          |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

Type:                   12
Length:                 1
Rank:                   uint8_t, Endposition des Benutzers in der Rangliste
                        (1 <= Rank <= 4)


ErrorWarning
------------

0             7              15              23              31
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
| Type        | Length                        | Subtype       |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
| [Message] ................................................. |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

Type:                   255
Length:                 1 + L�nge(Message)
Subtype:                uint8_t
                        0 -> Warnung
                        1 -> fataler Fehler, Client muss sich beenden
Message:                Beschreibung des Fehlers im Textformat (UTF-8), nicht
                        nullterminiert

Eine ErrorWarning wird in folgenden F�llen gesendet:

Warnung:
* Katalog kann nicht geladen werden
* Spiel kann nicht gestartet werden, weil noch zu wenig Teilnehmer (nur
  w�hrend Vorbereitung, sonst fatal)

fatal:
* Login nicht m�glich (z.B. Server voll, Spiel l�uft schon oder Name
  bereits vergeben)
* Spielleiter verl�sst den Server
* Spielabbruch wegen weniger als 2 Teilnehmern in der Spielphase


vim: set expandtab softtabstop=8:
