# Examen TP - ING3 - Hejer FESSI & Mohamed TLOH

Vous trouverez ci dessous une description de notre rendu pour l'examen de modèle d'architecture auto-hébergé.

Nous décrirons dans un premier temps la conception et le fonctionnement du service de suppression des données personnelles à la demande puis celui de hachage des données.

Nous avons utilisés les technologies suivantes pour ce projet :

- Scala version 2.11.11
- Spark core version 2.4.8
- Spark sql version 2.4.8
- Spray json version 1.3.6
- Scopt version 4.0.1


Le service récupère d'abords les arguments saisies par l'utilisateur, facilité par l'utilisation de scopt.

## Scopt

Scopt nous permet de spécifier plusieurs arguments en ligne de commande, le type attendu et si ces  arguments sont nécéssaires ou non. Cela nous évite de manipuler dans le code des variables args(i) que l'on pourrait confondre et que l'utilisateurs devrait saisir dans un ordre précis sans savoir à quoi ils correspondent sans une parfaite connaissance du code.
Dans une case class "Config" ont été précisé l'ensemble des options possibles, dans notre cas les arguments sont les suivants et ils sont tous "required", cela signifie que l'utilisateur doit nécéssairement les saisir à chaque fois. Les options retenues sont les suivantes :

- idClient
- filepath
- finalpath
- service

L'option service nous permet de préciser si nous voulons hacher les données d'un client ou les supprimer
Nous récupérons plus tard le contenu de ces options dans le main.

## Service n°1

Pour le premier service, le filepath nous permet de lire le dataset et d'en créer un dataframe conforme à la case class Client.
Une fois le dataset créé, nous en supprimons le client associé à l'id donné en argument avec l'option idClient. On donne en argument à cette option le filepath et le finalpath. Ainsi elle pourra créer un nouveau dataset à partir du premier duquel elle aura supprimer le client à enlever. Puis elle créer un nouveau fichier csv avec le fichier filtré, ce nouveau fichier sera sauvegardé dans le repertoir indiqué dans l'option finalpath.


## Service n°2

Dans le cas où l'utilisateur aurait saisie "hashData" pour l'argument service, le traitement est sensiblement le même. Dans un premier temps, un dataset est construit à partir du fichier renseigné dans l'argument filepath, puis ) partir de ce dataset, un nouveau dataset est créé avec seulement la ligne correspondant à l'id saisie dont nous aurons haché les valeurs, par la même occasion nous supprimons la ligne correspondant à cette id dans le premier dataset.
Nous avons donc le dataset originel sans la ligne à haché et un autre dataset avec seulement la ligne haché puis nous faison l'union de ces deux datasets que nous sauvegardons dans un nouveau fichier csv à l'emplacement indiqué dans finalPath.
