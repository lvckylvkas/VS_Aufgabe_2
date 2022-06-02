package com.verteiltesysteme.Aufgabe;

/**Entwickeln Sie einen Adapter, der Nachrichten von JMS und MQTT abonnieren kann. Es soll möglich sein, 
 * mit derselben Adapter-Instanz gleichzeitig sowohl mit einem JMS- als auch mit einem (anderen) MQTT-Broker verbunden zu sein. 
 * Die zu abonnierenden Topics (jeweils 1 Topic für jeden der beiden Server) und Server-Adressen sollen konfigurierbar sein. 

Wenn der Adapter eine Nachricht enthält, soll er prüfen, ob es sich um eine Nachricht handelt, in der ein Objekt vom Typ 
de.hs_mannheim.ffi.vs.syslog.model.SyslogMessage bzw. eines Untertyps enthalten ist. 
Dafür gibt es zwei Möglichkeiten: als textuelle Repräsentation (s. toString()-Methode) oder als serialisiertes Java-Objekt. 
Sie brauchen nur die Variante als Java-Objekt unterstützen.

Wird eine Nachricht vom Adapter empfangen, soll er das SyslogMessage-Objekt (oder das Objekt des entsprechenden Untertyps) 
an einen Websocket "forwarden", wenn das Feld sev mindestens WARNING entspricht (also im Fall EMERGENCY, ALERT, CRITICAL, ERROR und WARNING).
Die Adresse des Websockets soll konfigurierbar sein. **/

public class Adapter {
    
}
