package fr.umontpellier.iut.rails;

import java.util.*;
import java.util.stream.Collectors;

public class Joueur {

    /**
     * Les couleurs possibles pour les joueurs (pour l'interface graphique)
     */
    public static enum Couleur {
        JAUNE, ROUGE, BLEU, VERT, ROSE;
    }

    /**
     * Jeu auquel le joueur est rattaché
     */
    private Jeu jeu;
    /**
     * Nom du joueur
     */
    private String nom;
    /**
     * CouleurWagon du joueur (pour représentation sur le plateau)
     */
    private Couleur couleur;
    /**
     * Nombre de gares que le joueur peut encore poser sur le plateau
     */
    private int nbGares;
    /**
     * Nombre de wagons que le joueur peut encore poser sur le plateau
     */
    private int nbWagons;
    /**
     * Liste des missions à réaliser pendant la partie
     */
    private List<Destination> destinations;
    /**
     * Liste des cartes que le joueur a en main
     */
    private List<CouleurWagon> cartesWagon;
    /**
     * Liste temporaire de cartes wagon que le joueur est en train de jouer pour
     * payer la capture d'une route ou la construction d'une gare
     */
    private List<CouleurWagon> cartesWagonPosees;
    /**
     * Score courant du joueur (somme des valeurs des routes capturées)
     */
    private int score;

    /**
     * Liste des cartes destinations longue
     */
    private static ArrayList<Destination> destinationLongue = Destination.makeDestinationsLonguesEurope();

    public Joueur(String nom, Jeu jeu, Joueur.Couleur couleur) {
        this.nom = nom;
        this.jeu = jeu;
        this.couleur = couleur;
        nbGares = 3;
        nbWagons = 45;
        cartesWagon = new ArrayList<>();
        cartesWagonPosees = new ArrayList<>();
        destinations = new ArrayList<>();
        score = 12; // chaque gare non utilisée vaut 4 points
    }


    public String getNom() {
        return nom;
    }

    public int getScore() {
        return score;
    }

    public Couleur getCouleur() {
        return couleur;
    }

    public int getNbWagons() {
        return nbWagons;
    }

    public Jeu getJeu() {
        return jeu;
    }

    public List<CouleurWagon> getCartesWagonPosees() {
        return cartesWagonPosees;
    }

    public List<CouleurWagon> getCartesWagon() {
        return cartesWagon;
    }

    public List<Destination> getDestinations() {
        return destinations;
    }

    public int getNbGares() {
        return nbGares;
    }

    public int getScore() {
        return score;
    }

    public void setNbGares(int i) {
        nbGares += i;
    }

    public void setScore(int i) {
        score += i;
    }

    public void setNbWagons(int i) {
        nbWagons -= i;
    }


    /**
     * @param nomVille
     * @return vrai si le nom donné correspond à une ville
     */
    public boolean estVille(String nomVille) {
        boolean estVille = false;
        for (int i = 0; i < jeu.getVilles().size(); i++) {
            if (nomVille.equals(jeu.getVilles().get(i).getNom())) {
                estVille = true;
            }
        }
        return estVille;
    }


    /**
     * @return la liste des noms des cartes wagons dans la main du joueur
     */
    public ArrayList<String> getCarteWagonString() {
        ArrayList<String> carteWagonString = new ArrayList<>();
        for (CouleurWagon carteWagon : cartesWagon) {
            carteWagonString.add(carteWagon.name());
        }
        return carteWagonString;
    }

    /**
     * @return la liste des noms des destinations
     */
    public List<String> getDestinationString(List<Destination> destinations) {
        ArrayList<String> destinationString = new ArrayList<>();
        for (Destination destination : destinations) {
            destinationString.add(destination.toString());
        }
        return destinationString;

    }

    public int nbCouleurWagonListe(List<CouleurWagon> liste, CouleurWagon couleur) {
        int compteur = 0;
        for (int i = 0; i < liste.size(); i++) {
            if (liste.get(i).equals(couleur) || liste.get(i).equals(CouleurWagon.LOCOMOTIVE)) {
                compteur++;
            }
        }
        return compteur;
    }

    /**
     * Attend une entrée de la part du joueur (au clavier ou sur la websocket) et
     * renvoie le choix du joueur.
     * <p>
     * Cette méthode lit les entrées du jeu ({@code Jeu.lireligne()}) jusqu'à ce
     * qu'un choix valide (un élément de {@code choix} ou de {@code boutons} ou
     * éventuellement la chaîne vide si l'utilisateur est autorisé à passer) soit
     * reçu.
     * Lorsqu'un choix valide est obtenu, il est renvoyé par la fonction.
     * <p>
     * Si l'ensemble des choix valides ({@code choix} + {@code boutons}) ne comporte
     * qu'un seul élément et que {@code canPass} est faux, l'unique choix valide est
     * automatiquement renvoyé sans lire l'entrée de l'utilisateur.
     * <p>
     * Si l'ensemble des choix est vide, la chaîne vide ("") est automatiquement
     * renvoyée par la méthode (indépendamment de la valeur de {@code canPass}).
     * <p>
     * Exemple d'utilisation pour demander à un joueur de répondre à une question
     * par "oui" ou "non" :
     * <p>
     * {@code
     * List<String> choix = Arrays.asList("Oui", "Non");
     * String input = choisir("Voulez vous faire ceci ?", choix, new ArrayList<>(), false);
     * }
     * <p>
     * <p>
     * Si par contre on voulait proposer les réponses à l'aide de boutons, on
     * pourrait utiliser :
     * <p>
     * {@code
     * List<String> boutons = Arrays.asList("1", "2", "3");
     * String input = choisir("Choisissez un nombre.", new ArrayList<>(), boutons, false);
     * }
     *
     * @param instruction message à afficher à l'écran pour indiquer au joueur la
     *                    nature du choix qui est attendu
     * @param choix       une collection de chaînes de caractères correspondant aux
     *                    choix valides attendus du joueur
     * @param boutons     une collection de chaînes de caractères correspondant aux
     *                    choix valides attendus du joueur qui doivent être
     *                    représentés par des boutons sur l'interface graphique.
     * @param peutPasser  booléen indiquant si le joueur a le droit de passer sans
     *                    faire de choix. S'il est autorisé à passer, c'est la
     *                    chaîne de caractères vide ("") qui signifie qu'il désire
     *                    passer.
     * @return le choix de l'utilisateur (un élément de {@code choix}, ou de
     * {@code boutons} ou la chaîne vide)
     */
    public String choisir(String instruction, Collection<String> choix, Collection<String> boutons, boolean peutPasser) {
        // on retire les doublons de la liste des choix
        HashSet<String> choixDistincts = new HashSet<>();
        choixDistincts.addAll(choix);
        choixDistincts.addAll(boutons);

        // Aucun choix disponible
        if (choixDistincts.isEmpty()) {
            return "";
        } else {
            // Un seul choix possible (renvoyer cet unique élément)
            if (choixDistincts.size() == 1 && !peutPasser) return choixDistincts.iterator().next();
            else {
                String entree;
                // Lit l'entrée de l'utilisateur jusqu'à obtenir un choix valide
                while (true) {
                    jeu.prompt(instruction, boutons, peutPasser);
                    entree = jeu.lireLigne();
                    // si une réponse valide est obtenue, elle est renvoyée
                    if (choixDistincts.contains(entree) || (peutPasser && entree.equals(""))) return entree;
                }
            }
        }
    }

    /**
     * Affiche un message dans le log du jeu (visible sur l'interface graphique)
     *
     * @param message le message à afficher (peut contenir des balises html pour la
     *                mise en forme)
     */
    public void log(String message) {
        jeu.log(message);
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add(String.format("=== %s (%d pts) ===", nom, score));
        joiner.add(String.format("  Gares: %d, Wagons: %d", nbGares, nbWagons));
        joiner.add("  Destinations: " + destinations.stream().map(Destination::toString).collect(Collectors.joining(", ")));
        joiner.add("  Cartes wagon: " + CouleurWagon.listToString(cartesWagon));
        return joiner.toString();
    }

    /**
     * @return une chaîne de caractères contenant le nom du joueur, avec des balises
     * HTML pour être mis en forme dans le log
     */
    public String toLog() {
        return String.format("<span class=\"joueur\">%s</span>", nom);
    }

    /**
     * Renvoie une représentation du joueur sous la forme d'un objet Java simple
     * (POJO)
     */
    public Object asPOJO() {
        HashMap<String, Object> data = new HashMap<>();
        data.put("nom", nom);
        data.put("couleur", couleur);
        data.put("score", score);
        data.put("nbGares", nbGares);
        data.put("nbWagons", nbWagons);
        data.put("estJoueurCourant", this == jeu.getJoueurCourant());
        data.put("destinations", destinations.stream().map(Destination::asPOJO).collect(Collectors.toList()));
        data.put("cartesWagon", cartesWagon.stream().sorted().map(CouleurWagon::name).collect(Collectors.toList()));
        data.put("cartesWagonPosees", cartesWagonPosees.stream().sorted().map(CouleurWagon::name).collect(Collectors.toList()));
        return data;
    }

    /**
     * @return la carte destination pioché aléatoirement
     */
    public Destination destinationLongue() {
        Destination destinationLongueInitiale;
        int index = jeu.getRandomInt(destinationLongue.size());
        destinationLongueInitiale = destinationLongue.get(index);
        destinationLongue.remove(destinationLongueInitiale);
        return destinationLongueInitiale;
    }


    /**
     * Distribution initiale des cartes wagons
     */
    public void distributionInitialeWagon() {
        for (int i = 0; i < 4; i++) {
            cartesWagon.add(jeu.piocherCarteWagon());
        }
    }

    public boolean appartientACouleurWagon(String couleur) {
        boolean appartientCouleur = false;
        ArrayList<CouleurWagon> couleurWagon = CouleurWagon.getCouleursSimples();
        couleurWagon.add(CouleurWagon.LOCOMOTIVE);
        for (int i = 0; i < couleurWagon.size(); i++) {
            if (couleur.equals(couleurWagon.get(i).name())) {
                appartientCouleur = true;
            }
        }
        return appartientCouleur;
    }

    public boolean poseCarteValideRoute(Route r) {
        int longueur = r.getLongueur();
        CouleurWagon couleurRoute = r.getCouleur();
        int compteur = 0;
        boolean valide = false;
        if (!couleurRoute.equals(CouleurWagon.GRIS)) {
            for (int i = 0; i < cartesWagonPosees.size(); i++) {
                if (cartesWagonPosees.get(i).equals(couleurRoute) || cartesWagonPosees.get(i).equals(CouleurWagon.LOCOMOTIVE)) {
                    compteur++;
                }
            }
            if (compteur == longueur) {
                valide = true;
            } else {
                valide = false;
            }
        } else {
            int nbCouleur = 0;
            Collections.sort(cartesWagonPosees);
            for (int i = 0; i < cartesWagonPosees.size(); i++) {
                if (nbCouleurWagonListe(cartesWagonPosees, cartesWagonPosees.get(i)) == r.getLongueur()) {
                    valide = true;
                }
            }
        }

        return valide;
    }

    public boolean poseCarteValideVille(Ville v) {
        boolean valide = false;
        int longueur;
        if (nbGares == 3) {
            longueur = 1;
        } else if (nbGares == 2) {
            longueur = 2;
        } else {
            longueur = 3;
        }
        int nbCouleur = 0;
        Collections.sort(cartesWagonPosees);
        for (int i = 0; i < cartesWagonPosees.size(); i++) {
            if (nbCouleurWagonListe(cartesWagonPosees, cartesWagonPosees.get(i)) == longueur) {
                valide = true;
            }
        }
        return valide;
    }

    public void cartesPoseesTri(Route r) {
        CouleurWagon c;
        if (r.getCouleur() != CouleurWagon.GRIS) {
            c = r.getCouleur();
        } else {
            c = r.trouveCouleurWagonQuandGrisPosees(this);
        }
        ArrayList<CouleurWagon> liste = CouleurWagon.getCouleursSimples();
        for (CouleurWagon couleurWagon : liste) {
            for (int i = 0; i < this.getCartesWagonPosees().size(); i++) {
                if (cartesWagonPosees.get(i).equals(couleurWagon)) {
                    if (couleurWagon != c) {
                        cartesWagonPosees.remove(i);
                        cartesWagon.add(couleurWagon);
                    }
                }
            }
        }
    }

    public void cartesPoseesTriGare() {
        Ville v = new Ville("Gridania");
        Ville v1 = new Ville("Limsa Lominsa");
        Route r = new Route(v, v1, 0, CouleurWagon.GRIS);
        if (nbGares == 1) {
            r.setLongueur(3);
        } else if (nbGares == 2) {
            r.setLongueur(2);
        } else if (nbGares == 3) {
            r.setLongueur(1);
        }
        cartesPoseesTri(r);
    }

    public boolean ajouterCarte(int ajoute, CouleurWagon c) {
        ArrayList<String> liste = new ArrayList<>();
        for (int i = 0; i < this.getCartesWagon().size(); i++) {
            if (cartesWagon.get(i).equals(CouleurWagon.LOCOMOTIVE) || cartesWagon.get(i).equals(c)) {
                liste.add(cartesWagon.get(i).name());
            }
        }
        int a = 0;
        while (a != ajoute) {
            String choix = choisir("Quelle carte ajouter ?", new ArrayList<>(), liste, true);
            if (choix.equals("")) {
                return false;
            }
            else{
                for(int j = 0; j < cartesWagon.size(); j++){
                    if (choix.equals(cartesWagon.get(j).name())) {
                        liste.remove(cartesWagon.get(j).name());
                        cartesWagonPosees.add(cartesWagon.get(j));
                        cartesWagon.remove(cartesWagon.get(j));
                        a++;
                    }
                }
            }
        }
        return true;
    }

    /**
     * return vrai si pour une couleur et une route donnée ça suffit pour poser
     */
    public boolean carteValide(CouleurWagon c, Route r) {
        boolean verif = false;
        int cpt = 0;
        if (r.getCouleur() == CouleurWagon.GRIS) {
            for (int i = 0; i < cartesWagon.size(); i++) {
                if (cartesWagon.get(i).equals(CouleurWagon.LOCOMOTIVE) || cartesWagon.get(i).equals(c)) {
                    cpt++;
                }
            }
            for (int j = 0; j < cartesWagonPosees.size(); j++) {
                if (cartesWagonPosees.get(j).equals(CouleurWagon.LOCOMOTIVE) || cartesWagonPosees.get(j).equals(c)) {
                    cpt++;
                }
            }
        } else {
            if (c.equals(r.getCouleur())) {
                for (int i = 0; i < cartesWagon.size(); i++) {
                    if (cartesWagon.get(i).equals(CouleurWagon.LOCOMOTIVE) || cartesWagon.get(i).equals(c)) {
                        cpt++;
                    }
                }
                for (int j = 0; j < cartesWagonPosees.size(); j++) {
                    if (cartesWagonPosees.get(j).equals(CouleurWagon.LOCOMOTIVE) || cartesWagonPosees.get(j).equals(c)) {
                        cpt++;
                    }
                }
            }
        }
        if (cpt >= r.getLongueur()) {
            verif = true;
        }
        return verif;
    }

    public void cartesPoseesTriV2(Route r, CouleurWagon c) {
        int i = 0;
        while (i < cartesWagonPosees.size()) {
            if (cartesWagonPosees.get(i).equals(c) || cartesWagonPosees.get(i).equals(CouleurWagon.LOCOMOTIVE)) {
                i++;
            } else {
                cartesWagon.add(cartesWagonPosees.get(i));
                cartesWagonPosees.remove(cartesWagonPosees.get(i));
            }
        }
    }

    /**
     * Propose une liste de cartes destinations, parmi lesquelles le joueur doit en
     * garder un nombre minimum n.
     * <p>
     * Tant que le nombre de destinations proposées est strictement supérieur à n,
     * le joueur peut choisir une des destinations qu'il retire de la liste des
     * choix, ou passer (en renvoyant la chaîne de caractères vide).
     * <p>
     * Les destinations qui ne sont pas écartées sont ajoutées à la liste des
     * destinations du joueur. Les destinations écartées sont renvoyées par la
     * fonction.
     *
     * @param destinationsPossibles liste de destinations proposées parmi lesquelles
     *                              le joueur peut choisir d'en écarter certaines
     * @param n                     nombre minimum de destinations que le joueur
     *                              doit garder
     * @return liste des destinations qui n'ont pas été gardées par le joueur
     */
    public List<Destination> choisirDestinations(List<Destination> destinationsPossibles, int n) {
        ArrayList<Destination> listeDestinationNonGarder = new ArrayList<>();
        List<String> nomListeDestinationPossible = getDestinationString(destinationsPossibles);
        ArrayList<Destination> listeDestinationGarder = new ArrayList<>();
        boolean aPasser = false;
        int cpt = 0;
        while (destinationsPossibles.size() > n && !aPasser) {
            String choix = choisir("Choisissez la destination a enlever :", new ArrayList(), nomListeDestinationPossible, true);
            if (choix.equals("")) {
                aPasser = true;
            } else {
                for (int i = 0; i < destinationsPossibles.size(); i++) {
                    if (choix.equals(destinationsPossibles.get(i).getNom())) {
                        listeDestinationNonGarder.add(destinationsPossibles.get(i));
                        nomListeDestinationPossible.remove(choix);
                        destinationsPossibles.remove(destinationsPossibles.get(i));
                        cpt++;
                    }
                }
            }
        }
        while (!destinationsPossibles.isEmpty()) {
            destinations.add(destinationsPossibles.get(0));
            destinationsPossibles.remove(0);
        }
        return listeDestinationNonGarder;
    }


    public ArrayList<CouleurWagon> choisirCartePosee() {
        ArrayList<String> carteWagonString = getCarteWagonString();
        carteWagonString.add("Fini");
        ArrayList<CouleurWagon> cartePosee = new ArrayList<>();
        String choixCarteAJouer = choisir("Choisir les cartes que vous voulez jouer", new ArrayList<>(), carteWagonString, false);
        int i = 0;
        boolean trouve = false;
        while (!choixCarteAJouer.equals("Fini")) {
            while (i < cartesWagon.size() && !trouve) {
                if (choixCarteAJouer.equals(cartesWagon.get(i).name())) {
                    cartePosee.add(cartesWagon.get(i));
                    cartesWagon.remove(i);
                    trouve = true;
                } else {
                    i++;
                }
            }
            carteWagonString = getCarteWagonString();
            carteWagonString.add("Fini");
            choixCarteAJouer = choisir("Choisir les cartes que vous voulez jouer", new ArrayList<>(), carteWagonString, false);
            i = 0;
            trouve = false;
        }
        return cartePosee;
    }

    public CouleurWagon choisirCartePoseeV2() {
        ArrayList<String> carteWagonString = getCarteWagonString();
        CouleurWagon cartePosee = null;
        String choixCarteAJouer = choisir("Choisir les cartes que vous voulez jouer", new ArrayList<>(), carteWagonString, false);
        int i = 0;
        boolean trouve = false;
        while (i < cartesWagon.size() && !trouve) {
            if (choixCarteAJouer.equals(cartesWagon.get(i).name())) {
                cartePosee = cartesWagon.get(i);
                cartesWagon.remove(i);
                trouve = true;
            } else {
                i++;
            }
        }
        i = 0;
        trouve = false;
        return cartePosee;
    }

    /**
     * Exécute un tour de jeu du joueur.
     * <p>
     * Cette méthode attend que le joueur choisisse une des options suivantes :
     * - le nom d'une carte wagon face visible à prendre ;
     * - le nom "GRIS" pour piocher une carte wagon face cachée s'il reste des
     * cartes à piocher dans la pile de pioche ou dans la pile de défausse ;
     * - la chaîne "destinations" pour piocher des cartes destination ;
     * - le nom d'une ville sur laquelle il peut construire une gare (ville non
     * prise par un autre joueur, le joueur a encore des gares en réserve et assez
     * de cartes wagon pour construire la gare) ;
     * - le nom d'une route que le joueur peut capturer (pas déjà capturée, assez de
     * wagons et assez de cartes wagon) ;
     * - la chaîne de caractères vide pour passer son tour
     * <p>
     * Lorsqu'un choix valide est reçu, l'action est exécutée (il est possible que
     * l'action nécessite d'autres choix de la part de l'utilisateur, comme "choisir les cartes wagon à défausser pour capturer une route" ou
     * "construire une gare", "choisir les destinations à défausser", etc.)
     */
    public void jouerTour() {
        //Liste nom carte dans main joueur qu'il peut poser
        ArrayList<String> carteWagonString = getCarteWagonString();
        carteWagonString.add("Fini");
        //Liste pour le choix de piocher
        ArrayList<String> listeCarteAPrendre = jeu.getNomWagonVisible();
        listeCarteAPrendre.add("GRIS");
        //Boutons pour piocher destinations
        String destination = "destinations";
        //Liste de toutes les villes possibles pour poser gare
        ArrayList<String> villePossiblesString = new ArrayList<>();
        for (Ville ville : jeu.getVilles()) {
            if (ville.verifierGare(this)) {
                villePossiblesString.add(ville.getNom());
            }
        }
        //Liste de toutes les routes possibles
        ArrayList<String> routePossiblesString = new ArrayList<>();
        for (Route route : jeu.getRoutes()) {
            if (route.verifierTrajet(this)) {
                routePossiblesString.add(route.getNom());
            }
        }
        ArrayList<String> choixNonBouton = new ArrayList<>();
        for (int i = 0; i < villePossiblesString.size(); i++) {
            choixNonBouton.add(villePossiblesString.get(i));
        }

        for (int j = 0; j < routePossiblesString.size(); j++) {
            choixNonBouton.add(routePossiblesString.get(j));
        }

        ArrayList<String> choixBouton = new ArrayList<>();
        for (int i = 0; i < listeCarteAPrendre.size(); i++) {
            choixBouton.add(listeCarteAPrendre.get(i));
        }
        choixBouton.add(destination);
        //choixBouton.add("Poser carte pour capturer");
        int nbPiocher = 0;
        boolean aJouer = false;

        String choix = choisir("Que voulez-vous faire?", choixNonBouton, choixBouton, true);
        while (nbPiocher < 2 && !aJouer) {
            if (choix.equals("")) {
                jeu.log("Vous avez passez");
                aJouer = true;
            } else if (choix.equals("GRIS")) {
                CouleurWagon wagonPiocher = jeu.piocherCarteWagon();
                this.cartesWagon.add(wagonPiocher);
                jeu.log("Vous avez piocher: " + wagonPiocher);
                nbPiocher++;
                List<String> piocheApresGris = jeu.getNomWagonVisibleSimple();
                piocheApresGris.add("GRIS");
                choix = choisir("Quelle carte voulez-vous piocher?", new ArrayList<>(), piocheApresGris, true);
                if (choix.equals("GRIS")) {
                    wagonPiocher = jeu.piocherCarteWagon();
                    this.cartesWagon.add(wagonPiocher);
                    jeu.log("Vous avez piocher: " + wagonPiocher);
                } else {
                    int i = 0;
                    boolean trouve = false;
                    while (i < jeu.getCartesWagonVisibles().size() && !trouve) {
                        if (choix.equals(jeu.getCartesWagonVisibles().get(i).name())) {
                            cartesWagon.add(jeu.getCartesWagonVisibles().get(i));
                            jeu.retirerCarteWagonVisible(jeu.getCartesWagonVisibles().get(i));
                            trouve = true;
                        } else {
                            i++;
                        }
                    }
                }
                nbPiocher++;

            } else if (appartientACouleurWagon(choix)) {
                if (choix.equals("LOCOMOTIVE")) {
                    if (nbPiocher == 1) {
                        jeu.log("Vous ne pouvez pas piocher cette locomotive");
                        choix = choisir("Quelle carte voulez-vous piocher?", new ArrayList<>(), jeu.getNomWagonVisibleSimple(), true);
                    } else {
                        int i = 0;
                        boolean trouve = false;
                        while (i < jeu.getCartesWagonVisibles().size() && !trouve) {
                            if (choix.equals(jeu.getCartesWagonVisibles().get(i).name())) {
                                cartesWagon.add(jeu.getCartesWagonVisibles().get(i));
                                jeu.retirerCarteWagonVisible(jeu.getCartesWagonVisibles().get(i));
                                trouve = true;
                            } else {
                                i++;
                            }
                        }
                        nbPiocher = nbPiocher + 2;
                        aJouer = true;
                    }
                } else {
                    nbPiocher++;
                    int i = 0;
                    boolean trouve = false;
                    while (i < jeu.getCartesWagonVisibles().size() && !trouve) {
                        if (choix.equals(jeu.getCartesWagonVisibles().get(i).name())) {
                            cartesWagon.add(jeu.getCartesWagonVisibles().get(i));
                            jeu.retirerCarteWagonVisible(jeu.getCartesWagonVisibles().get(i));
                            trouve = true;
                        } else {
                            i++;
                        }
                    }
                    List<String> piocheApresVisible = jeu.getNomWagonVisibleSimple();
                    piocheApresVisible.add("GRIS");
                    choix = choisir("Quelle carte voulez-vous piocher?", new ArrayList<>(), piocheApresVisible, true);
                    if (choix.equals("GRIS")) {
                        CouleurWagon wagonPiocher = jeu.piocherCarteWagon();
                        this.cartesWagon.add(wagonPiocher);
                        jeu.log("Vous avez piocher: " + wagonPiocher);
                    } else {
                        i = 0;
                        trouve = false;
                        while (i < jeu.getCartesWagonVisibles().size() && !trouve) {
                            if (choix.equals(jeu.getCartesWagonVisibles().get(i).name())) {
                                cartesWagon.add(jeu.getCartesWagonVisibles().get(i));
                                jeu.retirerCarteWagonVisible(jeu.getCartesWagonVisibles().get(i));
                                trouve = true;
                            } else {
                                i++;
                            }
                        }
                    }
                    nbPiocher++;
                }
            } else if (choix.equals("destinations")) {
                ArrayList<Destination> destinationsPiochees = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    Destination d = jeu.piocherDestination();
                    destinationsPiochees.add(d);
                }
                List<Destination> nonPrises = choisirDestinations(destinationsPiochees, 1);
                jeu.defausserCarteDestination(nonPrises);
                while (!nonPrises.isEmpty()) {
                    nonPrises.remove(0);
                }
                aJouer = true;
            } else if (estVille(choix)) {
                jeu.log("Vous avez choisi la ville : " + choix);
                Ville villeChoisi = null;
                for (int i = 0; i < jeu.getVilles().size(); i++) {
                    if (choix.equals(jeu.getVilles().get(i).getNom())) {
                        villeChoisi = jeu.getVilles().get(i);
                    }
                }
                while (!poseCarteValideVille(villeChoisi)) {
                    cartesWagonPosees.add(choisirCartePoseeV2());
                }
                cartesPoseesTriGare();
                villeChoisi.poserGare(this);
                aJouer = true;


            } else {
                jeu.log("Vous avez choisi la route: " + choix);
                Route routeChoisi = null;
                for (int i = 0; i < jeu.getRoutes().size(); i++) {
                    if (choix.equals(jeu.getRoutes().get(i).getNom())) {
                        routeChoisi = jeu.getRoutes().get(i);
                    }
                }
                System.out.println(routeChoisi);
                CouleurWagon couleurWagonChoisi = routeChoisi.capturerRoute(this);
                cartesPoseesTriV2(routeChoisi, couleurWagonChoisi);
                //routeChoisi.capturerRouteV2(this);
                //rajout après la mise en commentaire
                if(!routeChoisi.poserTrain(this)){
                    cartesWagon.addAll(cartesWagonPosees);
                    cartesWagonPosees.clear();
                }
                aJouer = true;
            }
        }
    }
}









