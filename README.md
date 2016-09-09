DUE

Installation
Initialisation java, tapper la commande: mvn -U clean

Se placer dans le repertoire /src
tapper commande: npm install

Packager l'application WAR
gradle -i -b build.gradle


Developpement
Client seulement
compilation partie client, crée un repertoire src/dist: grunt build --force

Serveur seulement
Compilation créé un war target/due-version.war: mvn -U -o clean package






