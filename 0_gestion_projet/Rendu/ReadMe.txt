Editeur d'images GEMMS

-- Introduction --

GEMMS est une application d'édition d'images réalisée entièrement en Java avec la librairie JavaFX 8. Elle a été développée dans le cadre du projet de semestre de la deuxième année d'informatique logicielle à la Haute-Ecole d'Ingénierie et de Gestion (HEIG-VD). Son but est d'être le plus simple et intuitif possible, permettant d'éditer des images rapidement et sans apprentissage préalable.

-- Installation --

Exécuter le fichier GEMMS.jar (ou via la ligne de commande, 'java -jar GEMMS.jar') ou compiler les sources :

Compiler le projet nécessite [Apache Maven](https://maven.apache.org/download.cgi) ainsi que la version 1.8 du [Java JDK](http://www.oracle.com/technetwork/pt/java/javase/downloads/jdk8-downloads-2133151.html).

Le fichier `pom.xml` contient les informations utilisées par Maven pour compiler le projet. La commande `mvn clean install` permet de compiler le projet avec le fichier `pom.xml` du répertoire courant.

Le programme exécutable est `/target/GEMMS-1.0-SNAPSHOT.jar`.
		
-- Lancement de l'exécutable --

Pour lancer l'exécutable compilé ou celui founi, utiliser la commande `java -jar <nom de l'executable>.jar`

-- Auteurs --

  Guillaume Milani
  Edward Ransome
  Mathieu Monteverde
  Michaël Spierer
  Sathiya Kirushnapillai
