

#  Random Hand Cards - Test Technique ASA TECH

Ce projet est une application web Spring Boot qui simule la création d'un jeu de 52 cartes, son mélange aléatoire, et la distribution d'une main de 10 cartes triées et non triées.

##  1. Prérequis et Lancement

L'application est conteneurisée dans un "Fat JAR" autonome incluant le serveur web et les ressources statiques.

**Prérequis :**
* **Java 21**
* **Git** (pour cloner le dépôt)

**Cloner le projet :**
```bash
git clone https://github.com/CHROQUIHAMZA/random-hand-cards.git
cd random-hand-cards
```

**Compiler et packager l'application :**
```bash
./mvnw clean package
```

**Lancer l'application :**
```bash
java -jar target/random-hand-cards-1.0.0.jar
```

**Accéder à l'application :**
Ouvrez votre navigateur sur : [http://localhost:8080/random-hand-cards](http://localhost:8080/random-hand-cards)

**Lancer la suite de tests automatisés :**
```bash
./mvnw test
```

---

## 2. Architecture et Choix Techniques

Le projet respecte une architecture **MVC (Model-View-Controller)** stricte afin de garantir une séparation claire des responsabilités :
* **Couche Controller (`CardController`) :** Gère uniquement le routage HTTP, l'injection des modèles de données et la redirection vers les vues.
* **Couche Service (`CardService`) :** Contient 100 % de la logique métier (création du paquet complet, mélange via `Collections.shuffle`, et algorithme de tri via un `Comparator` personnalisé).
* **Vues (Thymeleaf + Tailwind CSS) :** Responsables uniquement de l'affichage de l'interface utilisateur, sans embarquer aucune logique métier.
* **Ressources statiques :** Les images fournies ont été intentionnellement placées dans le sous-dossier `static/img/cards/` afin de maintenir une arborescence propre et d'isoler les assets du domaine métier des éléments d'interface globaux.

### Justification des dépendances principales

* **Spring Boot (3.4.0) (Web + Thymeleaf) :** Framework retenu pour sa rapidité de mise en place (serveur embarqué, routage, DI) et son intégration native avec Thymeleaf pour le rendu de vues côté serveur, ainsi qu'avec `@ControllerAdvice` pour la gestion centralisée des erreurs.
* **Lombok :** Utilisé uniquement pour l'injection de dépendances (`@RequiredArgsConstructor` sur `CardController`). Les entités du domaine (`Card`, `ApiError`...) restent des `record` Java natifs, sans annotation Lombok.

### Structure du projet
```
src/main/java/com/asatech/random_hand_cards/
├── model/       → Card, Suit, Rank
├── dto/         → CardDrawResult, ApiError
├── service/     → CardService (logique métier)
├── controller/  → CardController
└── exception/   → GlobalExceptionHandler

src/main/resources/
├── static/img/cards/  → Images des 52 cartes (.png)
└── templates/
    ├── cards.html      → Vue principale (paquet, main aléatoire, main triée)
    └── error.html      → Vue affichée par le GlobalExceptionHandler

src/test/java/com/asatech/random_hand_cards/
├── service/     → CardServiceTest, CardImageTest
└── controller/  → CardControllerTest
```

---

## 3. Modélisation des Données

### L'utilisation des `Record`
L'entité principale `Card` a été modélisée sous forme de **Record**. Ce choix est justifié par :
1. **L'immuabilité :** Une carte à jouer (ex: l'As de Cœur) ne change jamais de valeur au cours de son cycle de vie. Le `record` garantit cette propriété nativement (absence de setters).
2. **L'égalité par valeur :** Le `record` génère automatiquement les méthodes `equals()` et `hashCode()` basées sur le contenu (Rang et Couleur) et non sur l'adresse mémoire. Cela rend les algorithmes de vérification et les tests unitaires mathématiquement fiables.

### Enumérations (`Enum`)
Les couleurs (`Suit`) et les valeurs (`Rank`) sont gérées par des énumérations pour garantir un typage fort.

L'ordre de déclaration dans l'enum `Suit` (`CARREAU`, `COEUR`, `PIQUE`, `TREFLE`) reprend fidèlement l'ordre énoncé dans le cahier des charges ("4 couleurs : Carreau, Cœur, Pique, Trèfle"). Ce choix n'est pas anodin : en Java, l'ordre naturel (`Comparable`) d'un enum correspond à son ordre de déclaration. En s'alignant sur l'ordre du sujet, le tri par défaut des couleurs (`Comparator.comparing(Card::suit)`) reflète directement et sans ambiguïté l'énoncé métier, sans nécessiter de logique de tri personnalisée supplémentaire.

Le même principe s'applique à l'enum `Rank`, dont l'ordre de déclaration (`AS`, `DEUX`, ..., `ROI`) respecte l'ordre naturel des valeurs listées dans le cahier des charges.

### Structures de données (`List` vs `Set`)
Bien que les 52 cartes soient uniques, l'utilisation d'une `List` a été privilégiée face à un `Set`. Le domaine métier exige un brassage aléatoire (`Collections.shuffle`) et un tirage séquentiel par index (piocher les 10 premières cartes sur le dessus du paquet), des opérations nécessitant une séquence ordonnée.

---

##  4. Programmation Défensive et Robustesse

Bien que le point d'entrée web actuel (`GET /random-hand-cards`) ne prenne aucun paramètre d'URL utilisateur, une stricte **programmation défensive** a été appliquée dans la couche `CardService`.

Le Service agit comme une API interne robuste. Si les méthodes internes (`drawHand`, `shuffleDeck`) sont réutilisées à l'avenir avec des paramètres illogiques (ex: demande de 100 cartes sur un paquet de 52, ou liste nulle), le système valide ces entrées et lève proactivement des `IllegalArgumentException`.

---

## 5. Gestion Centralisée des Erreurs

Les exceptions levées par la logique métier ou par le système sont interceptées par un **GlobalExceptionHandler** (`@ControllerAdvice`). Les erreurs sont standardisées via un objet `ApiError` respectant les conventions de Spring Boot (`timestamp`, `status`, `error`, `message`, `path`).

* **Erreurs Métier (400 Bad Request) :** Si les paramètres ou règles du jeu sont violés.
* **Page Introuvable (404 Not Found) :** Si l'utilisateur tente d'accéder à une URL inexistante, interceptée de manière élégante via NoResourceFoundException.
* **Erreurs Serveur (500 Internal Server Error) :** En cas de défaillance technique imprévue.

La réponse est ensuite redirigée vers une vue `error.html` générique pour ne jamais exposer la stacktrace ou les détails d'implémentation à l'utilisateur final.

---

##  6. Stratégie de Tests

Le projet dispose d'une couverture de test automatisée assurant la fiabilité des flux :
* **Tests Unitaires (Couche Service) :** Validation de l'algorithme de tri, de la stricte conservation des 52 cartes après brassage (via `containsExactlyInAnyOrderElementsOf` d'AssertJ), et vérification de la programmation défensive.
* **Tests d'Intégration (Couche Web) :** Utilisation de `@WebMvcTest` et `@MockitoBean` pour valider le routage du contrôleur, la présence des attributs dans le modèle Thymeleaf, et le déclenchement du `GlobalExceptionHandler`.
* **Test d'Intégrité des Ressources :** Vérification itérative garantissant que chaque combinaison possible générée en Java possède bien son fichier image physique `.png` correspondant, évitant les erreurs 404 au runtime.
